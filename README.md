# FancyVoice
FancyVoice(Chinese Name:趣声) is an Android Application using voice-recognize technology from iFLY(website:http://www.xfyun.cn/). It has three mainly kinds of modules, Speech-Control Browser, Speech-Control Notebook and Game Centre based on voice-recognize. With BottomNavigationView on the bottom of layout companied by Viewpager, following **Material Design** rules, you must find yourself immerse in exploring this application for great pleasure as well as for its colorful, creative design.
<div align=center><img width="250" height="180" src="http://opm54c01s.bkt.clouddn.com/18-3-16/3554926.jpg"/></div>

## 1 Environment
* Language: Chinese
* Platform: Android API:25(minAPI:15)
* Version: FancyVoice V1.0 (underdeveloped)
### 1.1 Gradle details
You could refer to the following configure to gradle

```
/*app/build.gradle*/
apply plugin: 'com.android.application'
apply plugin: 'com.jakewharton.hugo'
dependencies {
    compile project(':library')
    implementation files('libs/gson-2.8.0.jar')
    implementation files('libs/Msc.jar')
}
```
and
```
/*library/build.gradle*/
apply plugin: 'com.android.library'
```

## 2 Function
### 2.1 Speech-Control Browser
<div align=center><img width="150" height="300" src="http://opm54c01s.bkt.clouddn.com/18-3-16/61169555.jpg"/></div>

### 2.2 Speech-Control Notebook
<div align=center><img width="150" height="300" src="http://opm54c01s.bkt.clouddn.com/18-3-16/96617817.jpg"/></div>

### 2.3 Game Centre
You need to speak “华为华为” to initialize the iFLY speech-recognize engine before giving any answer to microphone of cellphone. Following the instruction given by a description, you'll have limitless opportunities to use your acute brain, deduction and logic thinking to crack the right answer in each stage.
<div align=center>
  <img width="150" height="300" src="http://opm54c01s.bkt.clouddn.com/18-3-16/13284193.jpg"/>
  <img width="150" height="300" src="http://opm54c01s.bkt.clouddn.com/18-3-16/76865400.jpg"/>
</div>
