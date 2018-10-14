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
* 最新版本: 趣声 V1.3 （稳定版）
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

