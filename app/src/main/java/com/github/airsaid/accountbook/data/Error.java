package com.github.airsaid.accountbook.data;

import com.avos.avoscloud.AVException;

/**
 * @author Airsaid
 * @github https://github.com/airsaid
 * @date 2017/3/16
 * @desc 错误信息类，对 AVException 进行封装，用于获取 code 错误码，或者错误信息 message。
 */
public class Error{

    private AVException e;
    public int code;
    public String message;

    public Error(){

    }

    public Error(AVException e){
        this.e = e;
        this.code = e.getCode();
    }

    public int getCode(){
        return code;
    }

    public Throwable getCause(){
        return e.getCause();
    }

    public String getLocalizedMessage(){
        return e.getLocalizedMessage();
    }

    public String getMessage(){
        if(message != null){
            return message;
        }
        switch (getCode()){
            case 0:
                return "请求失败了，网络好像不太好呢o(>﹏<)o";
            case 1:
                return "服务器内部错误或参数错误";
            case 100:
                return "服务器连接失败";
            case 101:
                return "查询不到数据";
            case 104:
                return "缺少请求参数";
            case 107:
                return "解析数据失败";
            case 109:
                return "您无权限执行该操作";
            case 111:
                return "数据存储失败，类型不匹配。";
            case 113:
                return "缺少必填数据";
            case 117:
                return "更新失败";
            case 122:
                return "无效的文件名称";
            case 123:
                return "ACL 格式错误";
            case 124:
                return "请求超时";
            case 125:
                return "电子邮箱地址无效";
            case 126:
                return "用户不存在";
            case 127:
                return "手机号码无效";
            case 139:
                return "昵称不合法，只能以英文字母、数字或下划线组成。";
            case 140:
                return "服务器过载，请稍后再试。";
            case 141:
                return "云引擎调用超时";
            case 142:
                return "云引擎校验错误";
            case 145:
                return "本应用没有启用支付功能";
            case 150:
                return "转换数据到图片失败";
            case 154:
                return "您的请求已超过应用阈值限制，请稍后再试。";
            case 160:
                return "服务器错误（请联系客服: airsaid@163.com）";
            case 200:
                return "手机号不能为空";
            case 201:
                return "密码不能为空";
            case 202:
                return "手机号已经存在";
            case 203:
                return "电子邮箱地址已经被占用";
            case 204:
                return "请输入电子邮箱地址";
            case 205:
                return "找不到电子邮箱地址对应的用户";
            case 206:
                return "登录超时，请重新登录后操作。";
            case 207:
                return "请通过注册创建用户，当前不允许第三方登录。";
            case 208:
                return "第三方帐号已经绑定到一个用户，不可绑定到其他用户。";
            case 210:
                return "手机号和密码不匹配";
            case 211:
                return "找不到该用户";
            case 212:
                return "请输入手机号码";
            case 213:
                return "手机号码对应的用户不存在";
            case 214:
                return "手机号码已经被注册";
            case 215:
                return "未验证的手机号码";
            case 216:
                return "未验证的邮箱地址";
            case 217:
                return "无效的用户名，不允许空白用户名。";
            case 218:
                return "无效的密码，不允许空白密码。";
            case 219:
                return "登录失败次数超过限制，请稍候再试，或者通过忘记密码重设密码。";
            case 251:
                return "无效的账户连接";
            case 252:
                return "无效的微信授权信息";
            case 301:
                return "数据存储失败";
            case 303:
                return "数据存储失败";
            case 304:
                return "数据操作错误";
            case 403:
                return "用户没有登录，无法修改用户信息。";
            case 429:
                return "系统繁忙，请稍后再试。";
            case 430:
                return "系统繁忙，请稍后再试。";
            case 431:
                return "系统繁忙，请稍后再试。";
            case 502:
                return "服务器维护中";
            case 503:
                return "服务器维护中";
            case 511:
                return "该操作暂时不可用，请稍后再试。";
            case 524:
                return "服务器连接失败，请稍后再试。";
            case 600:
                return "无效的短信签名";
            case 601:
                return "发送短信过于频繁";
            case 602:
                return "短信验证码发送失败";
            case 603:
                return "无效的短信验证码";
        }


        return "请求失败";
    }

}
