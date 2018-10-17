# 趣声
<div align=center>
    <img width="250" height="180" src="https://github-1252789527.cos.ap-shanghai.myqcloud.com/FancyVoice/logo.png"/>
</div>

趣声是一款以智能语音技术为核心，具有获取信息、记录信息、娱乐休闲功能的安卓应用。结合了讯飞ASR(网站：http://www.xfyun.cn/) 和华为HiAI Engine(网站：https://developer.huawei.com/consumer/cn/devservice/doc/31403) ，在高效率、信息化、快速化的时代需求下，趣声可以为日常生活的诸多场景，提供智能语音技术服务。其创新点有三：一是以人为本，以完全解放人类双手为目标，将残障人士纳入软件使用的范围；二是模块化设计，使得软件具有可拓展性并可建立以趣声为中心的生态；三是实现了多方语音互操作领域的创新。

目前趣声拥有五个模块，分别是语音浏览器、语音游戏、歌词识别、语音记事本、视频通话字幕辅助

| 模块        | 功能   |  接口调用 |
| --------   | -----  | -----  |
| 智能语音浏览器           | 支持语音指令对浏览器进行操作                            | HuaWei HiAI ASR |
| 语音游戏                 |   结合传统游戏模式和语音交互，说出答案                  | iFLY TEC |
| 语音记事本                 |   语音转换为文字记录                                 |  HuaWei HiAI ASR |
| 智能歌词识别播放器         |可简单过滤掉背景声干扰，提取出歌声中的歌词               | iFLY TEC |
| 视频通话字幕辅助         |双方可建立实时视频通话，实时转写通话内容，以字幕形式呈现    | iFLY TEC |

## 1 环境
* 语言：中文
* 平台： Android API:25(minAPI:21)
* 最新版本: 趣声 V1.3.1 (2018-10-17)
### 1.1 Gradle相关
在Gradle需要进行如下的配置

```
/*app/build.gradle*/
apply plugin: 'com.android.application'

dependencies {
    implementation files('libs/gson-2.8.0.jar')
    implementation files('libs/Msc.jar')
    implementation 'com.rengwuxian.materialedittext:library:2.1.4'
}
```


## 2 功能
### 2.1 语音浏览器
<div align=center>
    <img width="150" height="300" src="https://github-1252789527.cos.ap-shanghai.myqcloud.com/FancyVoice/browser1.jpg"/>
    <img width="150" height="300" src="https://github-1252789527.cos.ap-shanghai.myqcloud.com/FancyVoice/browser2.jpg"/>
</div>


### 2.2 歌词识别器
<div align=center>
    <img width="150" height="300" src="https://github-1252789527.cos.ap-shanghai.myqcloud.com/FancyVoice/music_player1.jpg"/>
    <img width="150" height="300" src="https://github-1252789527.cos.ap-shanghai.myqcloud.com/FancyVoice/music_player2.jpg"/>
</div>

### 2.3 语音备忘录

<div align=center>
    <img width="150" height="300" src="https://github-1252789527.cos.ap-shanghai.myqcloud.com/FancyVoice/notebook1.jpg"/>
    <img width="150" height="300" src="https://github-1252789527.cos.ap-shanghai.myqcloud.com/FancyVoice/notebook2.jpg"/>
</div>

### 2.4 语音游戏

<div align=center>
  <img width="150" height="300" src="https://github-1252789527.cos.ap-shanghai.myqcloud.com/FancyVoice/game.jpg"/>
</div>

### 2.5 视频通话字幕辅助

<div align=center>
    <img width="150" height="300" src="https://github-1252789527.cos.ap-shanghai.myqcloud.com/FancyVoice/call1.jpg"/>
    <img width="150" height="300" src="https://github-1252789527.cos.ap-shanghai.myqcloud.com/FancyVoice/call2.jpg"/>
    <img width="150" height="300" src="https://github-1252789527.cos.ap-shanghai.myqcloud.com/FancyVoice/call3.jpg"/>
</div>

### 2.5 其他模块

<div align=center>
  <img width="150" height="300" src="https://github-1252789527.cos.ap-shanghai.myqcloud.com/FancyVoice/me.jpg"/>
</div>

## 版本信息
### 2018-10-15 趣声V1.3(Stable Version) 杨荣锋、李指明

HiAI使用条件比较苛刻，需要处理器麒麟970，且EMUI版本需要8.1，而且暂时无法支持多线程

修复了以下问题

| **\#**   | **问题**                                                                                 | **原因及解决方案**                                                                                                                                                                                                                    | **是否Fixed** |
|----------|------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------------|
| 1        | 游戏出现speaking之后闪现error                                                            | 讯飞需要在Browser中就进行注册                                                                                                                                                                                                         | T             |
| 2        | 游戏多重Activity                                                                         | 新的关卡onCreate时，finish之前一个Activity                                                                                                                                                                                            | T             |
| 3        | 采用系统返回键退出Activity会出现error                                                    | 左上角的返回键和系统的返回键，功能要统一 ，重写系统的返回功能                                                                                                                                                                         | T             |
| 4        | 在游戏中途退出，打开其他界面进行语音识别会出现error                                      | 在游戏的任意关卡退出，都需要销毁科大讯飞的引擎                                                                                                                                                                                        | T             |
| 5        | 音乐播放器使用系统的物理返回按键，音乐不会暂停，而且再次打开其他的音乐，音乐会交织在一起 | 左上角的返回键和系统的返回键，功能要统一 ，重写系统的返回功能 在finish()前添加pause()                                                                                                                                                 | T             |
| **6\***  | **HiAI会出现error的情况**                                                                | **原因一：切换不同fragment的时候，没有销毁HiAI Engine，释放掉资源，错误码显示8，表示识别引擎忙，在切换fragment时destory HiAIASR引擎的实例（注意先判断HiAI引擎是否为空） 原因二：没有打开HiAI设置，错误码显示14，在设置打开HiAI即可**  | **T**         |
| **7\***  | **浏览器出现net::ERR_UNKNOWN_URL_SCHEME**                                                | **自定义协议的问题，需要跳转到相应的APP**                                                                                                                                                                                             | **T**         |
| 8        | 播放器的开始和暂停按钮不协调                                                             | 调整即可                                                                                                                                                                                                                              | T             |
| **9\***  | **游戏多线程ASR崩溃**                                                                    | **改用单线程，多Activity**                                                                                                                                                                                                            | **T**         |
| **10\*** | **语音指令会多一个句号，导致不匹配**                                                     | **substring，length() - 2**                                                                                                                                                                                                           | **T**         |
| 11       | 备忘录保存以后点击进不去 长摁条目，选择删除无法删除                                      | 数据库查询的问题                                                                                                                                                                                                                      | T             |

添加了新功能：

增加了语音浏览器的导航界面（提升用户的使用体验），一是在第一次启动时，fragment从底部飞入；而是在“我的”里边的指令说明

### 2018-10-15 趣声V1.3.1 杨荣锋

增加对HiAI Engine ASR的返回报文的处理结果，针对以下三种结果

ASR_FAILRE/ASR_UNCONFIDENCE/NO VOICE DETECTED

