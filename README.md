# AccountBook
一个数据存储使用的 Leancloud、架构采用 MVP 的记账本 APP。（目前正在不断完善中）

## Preview

<img src="/image/preview1.png" alt="screenshot" title="screenshot" width="270" height="486" />  <img src="/image/preview2.png" alt="screenshot" title="screenshot" width="270" height="486" />
<img src="/image/preview3.png" alt="screenshot" title="screenshot" width="270" height="486" />  <img src="/image/preview4.png" alt="screenshot" title="screenshot" width="270" height="486" />
<img src="/image/preview5.png" alt="screenshot" title="screenshot" width="270" height="486" />  <img src="/image/preview6.png" alt="screenshot" title="screenshot" width="270" height="486" />
<img src="/image/preview7.png" alt="screenshot" title="screenshot" width="270" height="486" />  <img src="/image/preview8.png" alt="screenshot" title="screenshot" width="270" height="486" />
<img src="/image/preview9.png" alt="screenshot" title="screenshot" width="270" height="486" />


## Download

扫码下载：
<img src="/image/qr.png" alt="screenshot" title="screenshot" width="120" height="120" />

网址下载：
 [点我下载](https://fir.im/ddaccount)


## Version

### V1.0.5(5/24)
- 增加了统计功能。
- 更新了应用图标。

### V1.0.4(5/4)
- 增加自定义分类功能。
- 修复拍照功能 Bug。

### V1.0.3(4/28)
- 帐薄页增加总支出/收入显示。
- 增加版本自动检测更新功能。
- 增加异常上报功能。

### V1.0.2(4/26)
- 加入消息模块。
- 优化删除帐薄逻辑。
- 账目数据增加编辑功能。
- 加入共享账簿时，修改为同意后才可加入。


## TODO
- [x] 修改当帐薄中有其他共享用户时，不可删除帐薄，只能退出账簿。没有其他共享用户时才可删除帐薄。
- [x] 多人共享账簿下账目信息中增加显示是谁记账。
- [x] 加入共享账簿时，修改为同意后才可加入。
- [x] 引入 Bugly，增加异常上报功能。
- [x] 帐薄页增加总支出/收入显示。
- [x] 账目数据增加编辑功能。
- [x] 账目数据增加分页功能。
- [x] 增加应用检测更新功能。
- [x] 增加自定义分类功能。
- [x] 增加统计功能。
- [ ] 对应用进行瘦身。
- [ ] 增加密码锁功能。
- [ ] 增加换肤功能。


## Thanks
- [可萌记账](http://sj.qq.com/myapp/detail.htm?apkName=net.ffrj.pinkwallet)
- [iconfont](http://www.iconfont.cn/)
- [leancloud](https://leancloud.cn/)

## Statement
- 应用内部分页面图片来自可萌记账，非常感谢！如构成侵权请及时通知我修改或删除。

- 由于应用当中用到了 leancloud 和 bugly 服务，其 appid、appkey 由于私密原因已经隐藏。
请在 local.properties 文件中配置如下信息，配置后即可正常运行项目：
```
leancloud.appid=你在 leancloud 平台申请的 appid
leancloud.appkey=你在 leancloud 平台申请的 appkey
bugly.appid=你在 bugly 平台申请的 appid
```

## About me

- **QQ 群：** 5707887
- **Blog：**[http://blog.csdn.net/airsaid](http://blog.csdn.net/airsaid)
- **Email：** airsaid1024@gmail.com

## License
```
Copyright (C) 2017 Airsaid

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
