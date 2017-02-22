package com.github.airsaid.accountbook.widget.slideback;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.view.AccessibilityDelegateCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * 处理向右滑动的逻辑，移动整个View，请参数{@link android.support.v4.widget.SlidingPaneLayout}
 *
 * @author lihong
 * @since 2016-3-10
 */
class SlideFrameLayout extends ViewGroup {
    /**
     * 滑动的监听器
     */
    public interface SlidingListener {
        /**
         * 当View滑动时调用
         *
         * @param panel view
         * @param slideOffset [0, 1]
         */
        void onPanelSlide(View panel, float slideOffset);

        /**
         * @param panel    panel
         * @param settling settling
         */
        void continueSettling(View panel, boolean settling);
    }

    /**
     * dips per second
     */
    private static final int MIN_FLING_VELOCITY = 400;

    /**
     * 边缘的阴影drawable
     */
    private Drawable mShadowDrawable;

    /**
     * 是否可以滑动
     */
    private boolean mCanSlide = true;

    /**
     * 可滑动的View
     */
    private View mSlideableView;

    /**
     * 滑动的偏移比例
     */
    private float mSlideOffset;

    /**
     * 滑动的范围
     */
    private int mSlideRange;

    /**
     * 是否不能拖拽
     */
    private boolean mIsUnableToDrag;

    /**
     * 初始的X
     */
    private float mInitialMotionX;

    /**
     * 初始的Y
     */
    private float mInitialMotionY;

    /**
     * 可以响应滑动事件的边缘大小，如果按下的坐标的x大于这个值的话，作不会响应滑动事件
     */
    private float mEdgeSize;

    /**
     * 滑动的listener
     */
    private SlidingListener mSlidingListener;

    /**
     * ViewDragHelper
     */
    private final ViewDragHelper mDragHelper;

    /**
     * Panel是否是打开的状态
     */
    private boolean mPreservedOpenState;

    /**
     * 是否是第一次发生布局
     */
    private boolean mFirstLayout = true;

    /**
     * Rect
     */
    private final Rect mTmpRect = new Rect();

    /**
     * Priview view
     */
    private final PreviewView mPreviousSnapshotView;

    /**
     * Runnable集合
     */
    private final ArrayList<DisableLayerRunnable> mPostedRunnables = new ArrayList<>();

    /**
     * 是否绘制左侧阴影
     */
    private boolean mDrawShadow = false;

    /**
     * SlidingPanelLayoutImpl
     */
    private static final SlidingPanelLayoutImpl IMPL;

    static {
        final int deviceVersion = Build.VERSION.SDK_INT;
        if (deviceVersion >= 17) {
            IMPL = new SlidingPanelLayoutImplJBMR1();
        } else if (deviceVersion >= 16) {
            IMPL = new SlidingPanelLayoutImplJB();
        } else {
            IMPL = new SlidingPanelLayoutImplBase();
        }
    }

    /**
     * 构造方法
     */
    public SlideFrameLayout(Context context) {
        this(context, null);
    }

    /**
     * 构造方法
     */
    public SlideFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * 构造方法
     */
    public SlideFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setWillNotDraw(false);
        ViewCompat.setAccessibilityDelegate(this, new AccessibilityDelegate());
        ViewCompat.setImportantForAccessibility(this, ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_YES);

        mDragHelper = ViewDragHelper.create(this, 0.5f, new DragHelperCallback());
        mDragHelper.setMinVelocity(dip2px(MIN_FLING_VELOCITY));
        setEdgeSize(dip2px(20));

        mPreviousSnapshotView = new PreviewView(context);
        addView(mPreviousSnapshotView, new LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT));
    }

    /**
     * 设置滑动的监听器
     *
     * @param listener
     */
    public void setSlidingListener(SlidingListener listener) {
        mSlidingListener = listener;
    }

    /**
     * 设置滑动边缘的大小，用来响应滑动事件
     *
     * @param offset offset
     */
    public void setEdgeSize(int offset) {
        mEdgeSize = offset;
    }

    /**
     * 是否可以滑动
     *
     * @return true/false
     */
    public boolean isSlideable() {
        return mCanSlide;
    }

    /**
     * 设置是否可以滑动，默认为true
     *
     * @param b true/false
     */
    public void setSlideable(boolean b) {
        mCanSlide = b;
    }

    /**
     * 设置边缘的阴影的资源
     *
     * @param resId
     */
    public void setShadowResource(int resId) {
        mShadowDrawable = getResources().getDrawable(resId);
    }

    /**
     * 调用滑动的监听器
     *
     * @param panel
     */
    private void dispatchOnPanelSlide(View panel) {
        if (mSlidingListener != null) {
            mSlidingListener.onPanelSlide(panel, mSlideOffset);
        }

        // 决定是否需要绘制阴影
        if (mSlideOffset > 0 && mSlideOffset < 1) {
            // 拖拽过程中
            mDrawShadow = true;
        } else {
            mDrawShadow = false;
        }
    }

    /**
     * @param panel
     */
    private void updateObscuredViewsVisibility(View panel) {
        final int startBound = getPaddingLeft();
        final int endBound = getWidth() - getPaddingRight();
        final int topBound = getPaddingTop();
        final int bottomBound = getHeight() - getPaddingBottom();
        final int left;
        final int right;
        final int top;
        final int bottom;
        if (panel != null && viewIsOpaque(panel)) {
            left = panel.getLeft();
            right = panel.getRight();
            top = panel.getTop();
            bottom = panel.getBottom();
        } else {
            left = right = top = bottom = 0;
        }

        for (int i = 0, childCount = getChildCount(); i < childCount; i++) {
            final View child = getChildAt(i);

            if (child == panel) {
                // There are still more children above the panel but they won't be affected.
                break;
            }

            final int clampedChildLeft = Math.max(startBound, child.getLeft());
            final int clampedChildTop = Math.max(topBound, child.getTop());
            final int clampedChildRight = Math.min(endBound, child.getRight());
            final int clampedChildBottom = Math.min(bottomBound, child.getBottom());
            final int vis;
            if (clampedChildLeft >= left && clampedChildTop >= top &&
                clampedChildRight <= right && clampedChildBottom <= bottom) {
                vis = INVISIBLE;
            } else {
                vis = VISIBLE;
            }
            child.setVisibility(vis);
        }
    }

    /**
     * 设置所有的child都可见
     */
    private void setAllChildrenVisible() {
        for (int i = 0, childCount = getChildCount(); i < childCount; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() == INVISIBLE) {
                child.setVisibility(VISIBLE);
            }
        }
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     *
     * @param dpValue dp 的单位
     * @return px(像素)的单位
     */
    private int dip2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale);
    }

    /**
     * 判断指定的View是否是不透明
     *
     * @param v
     * @return
     */
    private static boolean viewIsOpaque(View v) {
        if (ViewCompat.isOpaque(v)) {
            return true;
        }

        // View#isOpaque didn't take all valid opaque scrollbar modes into account
        // before API 18 (JB-MR2). On newer devices rely solely on isOpaque above and return false
        // here. On older devices, check the view's background drawable directly as a fallback.
        if (Build.VERSION.SDK_INT >= 18) {
            return false;
        }

        final Drawable bg = v.getBackground();
        if (bg != null) {
            return bg.getOpacity() == PixelFormat.OPAQUE;
        }
        return false;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mFirstLayout = true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mFirstLayout = true;

        ArrayList<DisableLayerRunnable> runnables = new ArrayList<>(mPostedRunnables);
        for (DisableLayerRunnable dlr : runnables) {
            dlr.run();
        }

        mPostedRunnables.clear();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode != MeasureSpec.EXACTLY) {
            if (isInEditMode()) {
                // Don't crash the layout editor. Consume all of the space if specified
                // or pick a magic number from thin air otherwise.
                // It will crash on a real device.
                if (widthMode == MeasureSpec.AT_MOST) {
                    widthMode = MeasureSpec.EXACTLY;
                } else if (widthMode == MeasureSpec.UNSPECIFIED) {
                    widthMode = MeasureSpec.EXACTLY;
                    widthSize = 300;
                }
            } else {
                throw new IllegalStateException("Width must have an exact value or MATCH_PARENT");
            }
        } else if (heightMode == MeasureSpec.UNSPECIFIED) {
            if (isInEditMode()) {
                // Don't crash the layout editor. Pick a magic number from thin air instead.
                // It will crash on a real device.
                if (heightMode == MeasureSpec.UNSPECIFIED) {
                    heightMode = MeasureSpec.AT_MOST;
                    heightSize = 300;
                }
            } else {
                throw new IllegalStateException("Height must not be UNSPECIFIED");
            }
        }

        int layoutHeight = 0;
        int maxLayoutHeight = -1;
        switch (heightMode) {
            case MeasureSpec.EXACTLY:
                layoutHeight = maxLayoutHeight = heightSize - getPaddingTop() - getPaddingBottom();
                break;
            case MeasureSpec.AT_MOST:
                maxLayoutHeight = heightSize - getPaddingTop() - getPaddingBottom();
                break;
            default:
                break;
        }

        float weightSum = 0;
        boolean canSlide = false;
        final int widthAvailable = widthSize - getPaddingLeft() - getPaddingRight();
        int widthRemaining = widthAvailable;
        final int childCount = getChildCount();

        // We'll find the current one below.
        mSlideableView = null;

        // First pass. Measure based on child LayoutParams width/height.
        // Weight will incur a second pass.
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            final LayoutParams lp = (LayoutParams) child.getLayoutParams();

            if (child.getVisibility() == GONE) {
                continue;
            }

            if (lp.weight > 0) {
                weightSum += lp.weight;

                // If we have no width, weight is the only contributor to the final size.
                // Measure this view on the weight pass only.
                if (lp.width == 0) {
                    continue;
                }
            }

            int childWidthSpec;
            final int horizontalMargin = lp.leftMargin + lp.rightMargin;
            if (lp.width == LayoutParams.WRAP_CONTENT) {
                childWidthSpec = MeasureSpec.makeMeasureSpec(widthAvailable - horizontalMargin,
                    MeasureSpec.AT_MOST);
            } else if (lp.width == LayoutParams.FILL_PARENT) {
                childWidthSpec = MeasureSpec.makeMeasureSpec(widthAvailable - horizontalMargin,
                    MeasureSpec.EXACTLY);
            } else {
                childWidthSpec = MeasureSpec.makeMeasureSpec(lp.width, MeasureSpec.EXACTLY);
            }

            int childHeightSpec;
            if (lp.height == LayoutParams.WRAP_CONTENT) {
                childHeightSpec = MeasureSpec.makeMeasureSpec(maxLayoutHeight, MeasureSpec.AT_MOST);
            } else if (lp.height == LayoutParams.FILL_PARENT) {
                childHeightSpec = MeasureSpec.makeMeasureSpec(maxLayoutHeight, MeasureSpec.EXACTLY);
            } else {
                childHeightSpec = MeasureSpec.makeMeasureSpec(lp.height, MeasureSpec.EXACTLY);
            }

            child.measure(childWidthSpec, childHeightSpec);
            final int childWidth = child.getMeasuredWidth();
            final int childHeight = child.getMeasuredHeight();

            if (heightMode == MeasureSpec.AT_MOST && childHeight > layoutHeight) {
                layoutHeight = Math.min(childHeight, maxLayoutHeight);
            }

            widthRemaining -= childWidth;
            canSlide |= lp.slideable = widthRemaining < 0;
            if (lp.slideable) {
                mSlideableView = child;
            }
        }

        // Resolve weight and make sure non-sliding panels are smaller than the full screen.
        if (canSlide || weightSum > 0) {
            final int fixedPanelWidthLimit = widthAvailable;

            for (int i = 0; i < childCount; i++) {
                final View child = getChildAt(i);

                if (child.getVisibility() == GONE) {
                    continue;
                }

                final LayoutParams lp = (LayoutParams) child.getLayoutParams();

                if (child.getVisibility() == GONE) {
                    continue;
                }

                final boolean skippedFirstPass = lp.width == 0 && lp.weight > 0;
                final int measuredWidth = skippedFirstPass ? 0 : child.getMeasuredWidth();
                if (canSlide && child != mSlideableView) {
                    if (lp.width < 0 && (measuredWidth > fixedPanelWidthLimit || lp.weight > 0)) {
                        // Fixed panels in a sliding configuration should
                        // be clamped to the fixed panel limit.
                        final int childHeightSpec;
                        if (skippedFirstPass) {
                            // Do initial height measurement if we skipped measuring this view
                            // the first time around.
                            if (lp.height == LayoutParams.WRAP_CONTENT) {
                                childHeightSpec = MeasureSpec.makeMeasureSpec(maxLayoutHeight,
                                    MeasureSpec.AT_MOST);
                            } else if (lp.height == LayoutParams.FILL_PARENT) {
                                childHeightSpec = MeasureSpec.makeMeasureSpec(maxLayoutHeight,
                                    MeasureSpec.EXACTLY);
                            } else {
                                childHeightSpec = MeasureSpec.makeMeasureSpec(lp.height,
                                    MeasureSpec.EXACTLY);
                            }
                        } else {
                            childHeightSpec = MeasureSpec.makeMeasureSpec(
                                child.getMeasuredHeight(), MeasureSpec.EXACTLY);
                        }
                        final int childWidthSpec = MeasureSpec.makeMeasureSpec(
                            fixedPanelWidthLimit, MeasureSpec.EXACTLY);
                        child.measure(childWidthSpec, childHeightSpec);
                    }
                } else if (lp.weight > 0) {
                    int childHeightSpec;
                    if (lp.width == 0) {
                        // This was skipped the first time; figure out a real height spec.
                        if (lp.height == LayoutParams.WRAP_CONTENT) {
                            childHeightSpec = MeasureSpec.makeMeasureSpec(maxLayoutHeight,
                                MeasureSpec.AT_MOST);
                        } else if (lp.height == LayoutParams.FILL_PARENT) {
                            childHeightSpec = MeasureSpec.makeMeasureSpec(maxLayoutHeight,
                                MeasureSpec.EXACTLY);
                        } else {
                            childHeightSpec = MeasureSpec.makeMeasureSpec(lp.height,
                                MeasureSpec.EXACTLY);
                        }
                    } else {
                        childHeightSpec = MeasureSpec.makeMeasureSpec(
                            child.getMeasuredHeight(), MeasureSpec.EXACTLY);
                    }

                    if (canSlide) {
                        // Consume available space
                        final int horizontalMargin = lp.leftMargin + lp.rightMargin;
                        final int newWidth = widthAvailable - horizontalMargin;
                        final int childWidthSpec = MeasureSpec.makeMeasureSpec(
                            newWidth, MeasureSpec.EXACTLY);
                        if (measuredWidth != newWidth) {
                            child.measure(childWidthSpec, childHeightSpec);
                        }
                    } else {
                        // Distribute the extra width proportionally similar to LinearLayout
                        final int widthToDistribute = Math.max(0, widthRemaining);
                        final int addedWidth = (int) (lp.weight * widthToDistribute / weightSum);
                        final int childWidthSpec = MeasureSpec.makeMeasureSpec(
                            measuredWidth + addedWidth, MeasureSpec.EXACTLY);
                        child.measure(childWidthSpec, childHeightSpec);
                    }
                }
            }
        }

        final int measuredWidth = widthSize;
        final int measuredHeight = layoutHeight + getPaddingTop() + getPaddingBottom();

        setMeasuredDimension(measuredWidth, measuredHeight);
        mCanSlide = canSlide;

        if (mDragHelper.getViewDragState() != ViewDragHelper.STATE_IDLE && !canSlide) {
            // Cancel scrolling in progress, it's no longer relevant.
            mDragHelper.abort();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT);

        final int width = r - l;
        final int paddingStart = getPaddingLeft();
        final int paddingEnd = getPaddingRight();
        final int paddingTop = getPaddingTop();

        final int childCount = getChildCount();
        int xStart = paddingStart;
        int nextXStart = xStart;

        if (mFirstLayout) {
            mSlideOffset = mCanSlide && mPreservedOpenState ? 1.f : 0.f;
        }

        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);

            if (child.getVisibility() == GONE) {
                continue;
            }

            final LayoutParams lp = (LayoutParams) child.getLayoutParams();

            final int childWidth = child.getMeasuredWidth();
            int offset = 0;

            if (lp.slideable) {
                final int margin = lp.leftMargin + lp.rightMargin;
                final int range = Math.min(nextXStart, width - paddingEnd) - xStart - margin;
                mSlideRange = range;
                final int lpMargin = lp.leftMargin;
                final int pos = (int) (range * mSlideOffset);
                xStart += pos + lpMargin;
                mSlideOffset = (float) pos / mSlideRange;
            } else {
                xStart = nextXStart;
            }

            final int childLeft = xStart - offset;
            final int childRight = childLeft + childWidth;

            final int childTop = paddingTop;
            final int childBottom = childTop + child.getMeasuredHeight();
            child.layout(childLeft, paddingTop, childRight, childBottom);

            nextXStart += child.getWidth();
        }

        if (mFirstLayout) {
            updateObscuredViewsVisibility(mSlideableView);
        }

        mFirstLayout = false;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // Recalculate sliding panes and their details
        if (w != oldw) {
            mFirstLayout = true;
        }
    }

    @Override
    public void requestChildFocus(View child, View focused) {
        super.requestChildFocus(child, focused);
        if (!isInTouchMode() && !mCanSlide) {
            mPreservedOpenState = child == mSlideableView;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mEdgeSize > 0) {
            if (ev.getAction() == MotionEvent.ACTION_DOWN && ev.getX() > mEdgeSize) {
                int dragState = mDragHelper.getViewDragState();
                // FIXBUG by lihong 2016/07/10
                // 如果正在拖放的话，截断所有事件
                if (dragState == ViewDragHelper.STATE_DRAGGING
                    || dragState == ViewDragHelper.STATE_SETTLING) {
                    return true;
                }

                mDragHelper.abort();
                mIsUnableToDrag = true;
                return false;
            }
        }

        final int action = MotionEventCompat.getActionMasked(ev);
        // Preserve the open state based on the last view that was touched.
        if (!mCanSlide && action == MotionEvent.ACTION_DOWN && getChildCount() > 1) {
            // After the first things will be slideable.
            final View secondChild = getChildAt(1);
            if (secondChild != null) {
                mPreservedOpenState = !mDragHelper.isViewUnder(secondChild,
                    (int) ev.getX(), (int) ev.getY());
            }
        }

        if (!mCanSlide || (mIsUnableToDrag && action != MotionEvent.ACTION_DOWN)) {
            mDragHelper.cancel();
            return super.onInterceptTouchEvent(ev);
        }

        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            mDragHelper.cancel();
            return false;
        }

        boolean interceptTap = false;

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                mIsUnableToDrag = false;
                final float x = ev.getX();
                final float y = ev.getY();
                mInitialMotionX = x;
                mInitialMotionY = y;

                if (mDragHelper.isViewUnder(mSlideableView, (int) x, (int) y) &&
                    isDimmed(mSlideableView)) {
                    interceptTap = true;
                }
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                final float x = ev.getX();
                final float y = ev.getY();
                final float adx = Math.abs(x - mInitialMotionX);
                final float ady = Math.abs(y - mInitialMotionY);
                final int slop = mDragHelper.getTouchSlop();
                if (adx > slop && ady > adx) {
                    mDragHelper.cancel();
                    mIsUnableToDrag = true;
                    return false;
                }
            }
        }

        final boolean interceptForDrag = mDragHelper.shouldInterceptTouchEvent(ev);

        return interceptForDrag || interceptTap;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mEdgeSize > 0) {
            if (ev.getAction() == MotionEvent.ACTION_DOWN && ev.getX() > mEdgeSize) {
                return false;
            }
        }

        if (!mCanSlide) {
            return super.onTouchEvent(ev);
        }

        mDragHelper.processTouchEvent(ev);

        final int action = ev.getAction();
        boolean wantTouchEvents = true;

        switch (action & MotionEventCompat.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                final float x = ev.getX();
                final float y = ev.getY();
                mInitialMotionX = x;
                mInitialMotionY = y;
                break;
            }

            case MotionEvent.ACTION_UP: {
                break;
            }
        }

        return wantTouchEvents;
    }

    /**
     * 当Panel被拖拽时调用
     *
     * @param newLeft
     */
    private void onPanelDragged(int newLeft) {
        if (mSlideableView == null) {
            // This can happen if we're aborting motion during layout because everything now fits.
            mSlideOffset = 0;
            return;
        }

        final LayoutParams lp = (LayoutParams) mSlideableView.getLayoutParams();

        final int paddingStart = getPaddingLeft();
        final int lpMargin = lp.leftMargin;
        final int startBound = paddingStart + lpMargin;

        mSlideOffset = (float) (newLeft - startBound) / mSlideRange;

        dispatchOnPanelSlide(mSlideableView);
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        final LayoutParams lp = (LayoutParams) child.getLayoutParams();
        boolean result;
        final int save = canvas.save(Canvas.CLIP_SAVE_FLAG);

        if (mCanSlide && !lp.slideable && mSlideableView != null) {
            // Clip against the slider; no sense drawing what will immediately be covered.
            canvas.getClipBounds(mTmpRect);
            mTmpRect.right = Math.min(mTmpRect.right, mSlideableView.getLeft());

            canvas.clipRect(mTmpRect);
        }

        if (Build.VERSION.SDK_INT >= 11) {
            result = super.drawChild(canvas, child, drawingTime);
        } else {
            if (child.isDrawingCacheEnabled()) {
                child.setDrawingCacheEnabled(false);
            }
            result = super.drawChild(canvas, child, drawingTime);
        }

        canvas.restoreToCount(save);

        return result;
    }

    /**
     * 刷新
     *
     * @param v
     */
    private void invalidateChildRegion(View v) {
        IMPL.invalidateChildRegion(this, v);
    }

    /**
     * Smoothly animate mDraggingPane to the target X position within its range.
     *
     * @param slideOffset position to animate to
     * @param velocity    initial velocity in case of fling, or 0.
     */
    boolean smoothSlideTo(float slideOffset, int velocity) {
        if (!mCanSlide) {
            // Nothing to do.
            return false;
        }

        final LayoutParams lp = (LayoutParams) mSlideableView.getLayoutParams();

        int startBound = getPaddingLeft() + lp.leftMargin;
        int x = (int) (startBound + slideOffset * mSlideRange);

        if (mDragHelper.smoothSlideViewTo(mSlideableView, x, mSlideableView.getTop())) {
            setAllChildrenVisible();
            ViewCompat.postInvalidateOnAnimation(this);
            return true;
        }
        return false;
    }

    @Override
    public void computeScroll() {
        boolean settling = mDragHelper.continueSettling(true);
        if (mSlidingListener != null) {
            mSlidingListener.continueSettling(this, settling);
        }

        if (settling) {
            if (!mCanSlide) {
                mDragHelper.abort();
                return;
            }

            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    public void draw(Canvas c) {
        super.draw(c);

        drawShadow(c);
    }

    /**
     * Draw the left side shadow drawable
     *
     * @param canvas canvas
     */
    protected void drawShadow(Canvas canvas) {
        // 不能滑动时，没必要绘制阴影
        if (!mCanSlide) {
            return;
        }

        if (!mDrawShadow) {
            return;
        }

        if (mShadowDrawable == null) {
            return;
        }

        final View shadowView = getChildCount() > 1 ? getChildAt(1) : null;
        if (shadowView == null) {
            // No need to draw a shadow if we don't have one.
            return;
        }

        final int top = shadowView.getTop();
        final int bottom = shadowView.getBottom();

        final int shadowWidth = mShadowDrawable.getIntrinsicWidth();
        final int right = shadowView.getLeft();
        final int left = right - shadowWidth;

        mShadowDrawable.setBounds(left, top, right, bottom);
        mShadowDrawable.draw(canvas);
    }

    boolean isDimmed(View child) {
        if (child == null) {
            return false;
        }
        final LayoutParams lp = (LayoutParams) child.getLayoutParams();
        return mCanSlide && lp.dimWhenOffset && mSlideOffset > 0;
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams();
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof MarginLayoutParams
            ? new LayoutParams((MarginLayoutParams) p)
            : new LayoutParams(p);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams && super.checkLayoutParams(p);
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    /**
     * ViewDragHelper.Callback implementation
     */
    private class DragHelperCallback extends ViewDragHelper.Callback {
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            if (mIsUnableToDrag) {
                return false;
            }

            return ((LayoutParams) child.getLayoutParams()).slideable;
        }

        @Override
        public void onViewDragStateChanged(int state) {
            if (mDragHelper.getViewDragState() == ViewDragHelper.STATE_IDLE) {
                if (mSlideOffset == 0) {
                    updateObscuredViewsVisibility(mSlideableView);
                    mPreservedOpenState = false;
                } else {
                    mPreservedOpenState = true;
                }
            }
        }

        @Override
        public void onViewCaptured(View capturedChild, int activePointerId) {
            // Make all child views visible in preparation for sliding things around
            setAllChildrenVisible();
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            onPanelDragged(left);
            invalidate();
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            final LayoutParams lp = (LayoutParams) releasedChild.getLayoutParams();

            int left = getPaddingLeft() + lp.leftMargin;
            if (xvel > 0 || (xvel == 0 && mSlideOffset > 0.5f)) {
                left += mSlideRange;
            }
            mDragHelper.settleCapturedViewAt(left, releasedChild.getTop());
            invalidate();
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return mSlideRange;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            final LayoutParams lp = (LayoutParams) mSlideableView.getLayoutParams();

            int startBound = getPaddingLeft() + lp.leftMargin;
            int endBound = startBound + mSlideRange;
            final int newLeft = Math.min(Math.max(left, startBound), endBound);
            return newLeft;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            // Make sure we never move views vertically.
            // This could happen if the child has less height than its parent.
            return child.getTop();
        }

        @Override
        public void onEdgeDragStarted(int edgeFlags, int pointerId) {
            mDragHelper.captureChildView(mSlideableView, pointerId);
        }
    }

    /**
     * Layout parameter
     */
    public static class LayoutParams extends MarginLayoutParams {
        private static final int[] ATTRS = new int[]{
            android.R.attr.layout_weight
        };

        /**
         * The weighted proportion of how much of the leftover space this child should consume after measurement.
         */
        public float weight = 0;

        /**
         * True if this pane is the slideable pane in the layout.
         */
        boolean slideable;
        /**
         * True if this view should be drawn dimmed
         * when it's been offset from its default position.
         */
        boolean dimWhenOffset;

        Paint dimPaint;

        public LayoutParams() {
            super(FILL_PARENT, FILL_PARENT);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(LayoutParams source) {
            super(source);
            this.weight = source.weight;
        }

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);

            final TypedArray a = c.obtainStyledAttributes(attrs, ATTRS);
            this.weight = a.getFloat(0, 0);
            a.recycle();
        }

    }

    interface SlidingPanelLayoutImpl {
        void invalidateChildRegion(SlideFrameLayout parent, View child);
    }

    static class SlidingPanelLayoutImplBase implements SlidingPanelLayoutImpl {
        public void invalidateChildRegion(SlideFrameLayout parent, View child) {
            ViewCompat.postInvalidateOnAnimation(parent, child.getLeft(), child.getTop(),
                child.getRight(), child.getBottom());
        }
    }

    static class SlidingPanelLayoutImplJB extends SlidingPanelLayoutImplBase {
        /*
         * Private API hacks! Nasty! Bad!
         *
         * In Jellybean, some optimizations in the hardware UI renderer
         * prevent a changed Paint on a View using a hardware layer from having
         * the intended effect. This twiddles some internal bits on the view to force
         * it to recreate the display list.
         */
        private Method mGetDisplayList;
        private Field mRecreateDisplayList;

        SlidingPanelLayoutImplJB() {
            try {
                mGetDisplayList = View.class.getDeclaredMethod("getDisplayList", (Class[]) null);
            } catch (NoSuchMethodException e) {
            }
            try {
                mRecreateDisplayList = View.class.getDeclaredField("mRecreateDisplayList");
                mRecreateDisplayList.setAccessible(true);
            } catch (NoSuchFieldException e) {
            }
        }

        @Override
        public void invalidateChildRegion(SlideFrameLayout parent, View child) {
            if (mGetDisplayList != null && mRecreateDisplayList != null) {
                try {
                    mRecreateDisplayList.setBoolean(child, true);
                    mGetDisplayList.invoke(child, (Object[]) null);
                } catch (Exception e) {
                }
            } else {
                // Slow path. REALLY slow path. Let's hope we don't get here.
                child.invalidate();
                return;
            }
            super.invalidateChildRegion(parent, child);
        }
    }

    static class SlidingPanelLayoutImplJBMR1 extends SlidingPanelLayoutImplBase {
        @Override
        public void invalidateChildRegion(SlideFrameLayout parent, View child) {
            ViewCompat.setLayerPaint(child, ((LayoutParams) child.getLayoutParams()).dimPaint);
        }
    }

    class AccessibilityDelegate extends AccessibilityDelegateCompat {
        private final Rect mTmpRect = new Rect();

        @Override
        public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfoCompat info) {
            final AccessibilityNodeInfoCompat superNode = AccessibilityNodeInfoCompat.obtain(info);
            super.onInitializeAccessibilityNodeInfo(host, superNode);
            copyNodeInfoNoChildren(info, superNode);
            superNode.recycle();

            info.setClassName(SlideFrameLayout.class.getName());
            info.setSource(host);

            final ViewParent parent = ViewCompat.getParentForAccessibility(host);
            if (parent instanceof View) {
                info.setParent((View) parent);
            }

            // This is a best-approximation of addChildrenForAccessibility()
            // that accounts for filtering.
            final int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = getChildAt(i);
                if (!filter(child) && (child.getVisibility() == View.VISIBLE)) {
                    // Force importance to "yes" since we can't read the value.
                    ViewCompat.setImportantForAccessibility(
                        child, ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_YES);
                    info.addChild(child);
                }
            }
        }

        @Override
        public void onInitializeAccessibilityEvent(View host, AccessibilityEvent event) {
            super.onInitializeAccessibilityEvent(host, event);

            event.setClassName(SlideFrameLayout.class.getName());
        }

        @Override
        public boolean onRequestSendAccessibilityEvent(ViewGroup host, View child,
                                                       AccessibilityEvent event) {
            if (!filter(child)) {
                return super.onRequestSendAccessibilityEvent(host, child, event);
            }
            return false;
        }

        public boolean filter(View child) {
            return false;
        }

        /**
         * This should really be in AccessibilityNodeInfoCompat, but there unfortunately seem to be a few elements that
         * are not easily cloneable using the underlying API. Leave it private here as it's not general-purpose useful.
         */
        private void copyNodeInfoNoChildren(AccessibilityNodeInfoCompat dest,
                                            AccessibilityNodeInfoCompat src) {
            final Rect rect = mTmpRect;

            src.getBoundsInParent(rect);
            dest.setBoundsInParent(rect);

            src.getBoundsInScreen(rect);
            dest.setBoundsInScreen(rect);

            dest.setVisibleToUser(src.isVisibleToUser());
            dest.setPackageName(src.getPackageName());
            dest.setClassName(src.getClassName());
            dest.setContentDescription(src.getContentDescription());

            dest.setEnabled(src.isEnabled());
            dest.setClickable(src.isClickable());
            dest.setFocusable(src.isFocusable());
            dest.setFocused(src.isFocused());
            dest.setAccessibilityFocused(src.isAccessibilityFocused());
            dest.setSelected(src.isSelected());
            dest.setLongClickable(src.isLongClickable());

            dest.addAction(src.getActions());

            dest.setMovementGranularities(src.getMovementGranularities());
        }
    }

    private class DisableLayerRunnable implements Runnable {
        final View mChildView;

        DisableLayerRunnable(View childView) {
            mChildView = childView;
        }

        @Override
        public void run() {
            if (mChildView.getParent() == SlideFrameLayout.this) {
                ViewCompat.setLayerType(mChildView, ViewCompat.LAYER_TYPE_NONE, null);
                invalidateChildRegion(mChildView);
            }

            mPostedRunnables.remove(this);
        }
    }

    /**
     * 移动上一个界面的截图
     *
     * @param snapshotView 上一个界面view
     * @param translateX 平移位移
     */
    public void offsetPreviousSnapshot(View snapshotView, float translateX) {
        if (null != mPreviousSnapshotView) {
            mPreviousSnapshotView.setHostView(snapshotView);
            mPreviousSnapshotView.invalidate();
            mPreviousSnapshotView.setTranslationX(translateX);
        }
    }

    /**
     * Preview custom view
     */
    private static class PreviewView extends View {
        private View mHostView;

        public PreviewView(Context context) {
            super(context);
        }

        /**
         * 设置要绘制的view
         *
         * @param view view
         */
        public void setHostView(View view) {
            mHostView = view;
        }

        @Override
        public void draw(Canvas canvas) {
            super.draw(canvas);

            if (mHostView != null) {
                mHostView.draw(canvas);
            }
        }

        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();

            mHostView = null;
        }
    }
}
