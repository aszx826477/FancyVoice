# FancyVoice
FancyVoice(Chinese Name:趣声) is an Android Application using voice-recognize technology from iFLY(website:http://www.xfyun.cn/). It has three mainly kinds of modules, Speech-Control Browser, Speech-Control Notebook and Game Centre based on voice-recognize. With BottomNavigationView on the bottom of layout companied by Viewpager, following **Material Design** rules, you must find yourself immerse in exploring this application for great pleasure as well as for its colorful, creative design.

<div align=center>
    <img width="250" height="180" src="https://github-1252789527.cos.ap-shanghai.myqcloud.com/FancyVoice/logo.png"/>
</div>

## 1 Environment
* Language: Chinese
* Platform: Android API:25(minAPI:21)
* Version: FancyVoice V1.0 (underdeveloped)
### 1.1 Gradle details
You could refer to the following configure to gradle

```
/*app/build.gradle*/
apply plugin: 'com.android.application'

dependencies {
    implementation files('libs/gson-2.8.0.jar')
    implementation files('libs/Msc.jar')
    implementation 'com.rengwuxian.materialedittext:library:2.1.4'
}
```


## 2 Function
### 2.1 Speech-Control Browser
You can use *Voice Conmmand* to control the browser if you are indolent. Or this is a kind of function for disabled individuals who cannot use their arms.
<div align=center>
    <img width="150" height="300" src="https://github-1252789527.cos.ap-shanghai.myqcloud.com/FancyVoice/browser1.jpg"/>
    <img width="150" height="300" src="https://github-1252789527.cos.ap-shanghai.myqcloud.com/FancyVoice/browser2.jpg"/>
</div>

### 2.2 Lyric-recognition Music Player
This is a tool not only play music but also recognize the song lyric and directly extract them from the audio.
<div align=center>
    <img width="150" height="300" src="https://github-1252789527.cos.ap-shanghai.myqcloud.com/FancyVoice/music_player1.jpg"/>
    <img width="150" height="300" src="https://github-1252789527.cos.ap-shanghai.myqcloud.com/FancyVoice/music_player2.jpg"/>
</div>

### 2.3 Speech-Control Notebook
Just click the button and then assert without any restraint. All your voice would be converted into texts.
<div align=center>
    <img width="150" height="300" src="https://github-1252789527.cos.ap-shanghai.myqcloud.com/FancyVoice/notebook1.jpg"/>
    <img width="150" height="300" src="https://github-1252789527.cos.ap-shanghai.myqcloud.com/FancyVoice/notebook2.jpg"/>
</div>

### 2.4 Game Centre
You need to speak “华为华为” to initialize the iFLY speech-recognize engine before giving any answer to microphone of cellphone. Following the instruction given by a description, you'll have limitless opportunities to use your acute brain, deduction and logic thinking to crack the right answer in each stage.
<div align=center>
  <img width="150" height="300" src="https://github-1252789527.cos.ap-shanghai.myqcloud.com/FancyVoice/game.jpg"/>
</div>

### 2.5 Other Modules
Other Modules include ***Me*** and ***About Softeware***
*Me* is a page for containing other voice-recognition tools;
*About Software* is a Copyright Statement and contains developers' information.
<div align=center>
  <img width="150" height="300" src="https://github-1252789527.cos.ap-shanghai.myqcloud.com/FancyVoice/me.jpg"/>
  <img width="150" height="300" src="https://github-1252789527.cos.ap-shanghai.myqcloud.com/FancyVoice/about_software.jpg"/>
</div>

