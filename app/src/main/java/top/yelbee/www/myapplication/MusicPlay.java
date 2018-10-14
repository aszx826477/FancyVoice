package top.yelbee.www.myapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUtility;

import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

import top.yelbee.www.myapplication.Controller.trait_extrator;
import top.yelbee.www.myapplication.Controller.UtilsMusic;

public class MusicPlay extends AppCompatActivity {
    FloatingActionButton play, pause, smart;
    TextView musicName, musicLength, musicCur;
    private SeekBar seekBar;
    MediaPlayer mediaPlayer = new MediaPlayer();                //音乐播放器类
    AudioManager audioManager;                                  //音频管理类
    Timer timer;                                                //定时器类用于schedule一个Timertask,以此启动一个线程控制进度条和时间tv
    boolean isSeekBarChanging;                                  //互斥变量，防止进度条与定时器冲突。
    boolean playerpause;                                        //播放暂停标志位
    int currentPosition;                                        //当前音乐播放的进度
    SimpleDateFormat format;                                    //时间格式 00:00

    //测试音频文件地址
    static String tempPath = Environment.getExternalStorageDirectory() + "/Music/a million on my soul.mp3";
    AnimatedVectorDrawable s2p;                                 //animated_vector 控制容器变量
    AnimatedVectorDrawable p2s;
    TextView lyrics;                                            //歌词控件tv
    Handler handler;                                            //Handler
    Drawable play_src;                                          //悬浮按钮图片资源
    Drawable pause_src;
    TextView bit_display;                                       //位长及采样频率显示控件
    TextView freq_display;

    //音频流识别模块
    private static String TAG = "IatDemo";                      //语音听写
    private static com.iflytek.cloud.SpeechRecognizer mIat;     // 语音听写对象
    private int bit_temp;
    private int freq_temp;

    String music_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        set_statusbar_visible();
        setContentView(R.layout.music_play);
        requestPermission();

        //接收音乐的名称
        Bundle bundle = this.getIntent().getExtras();
        music_name = bundle.getString("music_name");

        //bit_temp = trait_extrator.bit_trait(ScrollingActivity.this,"voa_four.wav");
        freq_temp = trait_extrator.frequency_trait(MusicPlay.this, music_name);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Resources res = MusicPlay.this.getResources();
        play_src = res.getDrawable(R.drawable.music_play);
        pause_src = res.getDrawable(R.drawable.music_pause);

        //Activity.this-----context
        //必须在onCreate方法中创建Activity (此处为builer.create()----AD)
        LayoutInflater layoutInflater = LayoutInflater.from(MusicPlay.this);
        View pb_layout = layoutInflater.inflate(R.layout.music_progress_circle,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(MusicPlay.this)
                .setView(pb_layout);
        builder.setCancelable(false);
        final AlertDialog ad = builder.create();
        ad.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        //Toolbar控件设置
        //返回音乐列表
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.pause();
                finish();
            }
        });

        //获取控件id
        final FloatingActionButton play = (FloatingActionButton) findViewById(R.id.play);
        final FloatingActionButton pause = (FloatingActionButton) findViewById(R.id.pause);
        final FloatingActionButton smart = (FloatingActionButton) findViewById(R.id.smart);


        //时间显示格式 00:00
        format = new SimpleDateFormat("mm:ss");

        //获取当前音乐播放进度
        currentPosition = mediaPlayer.getCurrentPosition();

        //seekbar监听
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new MySeekBar());

        //位长及采样频率显示控件注册
        bit_display = (TextView) findViewById(R.id.bit_display);
        freq_display = (TextView) findViewById(R.id.freq_display);
        freq_display.setText(String.valueOf(freq_temp)+" Hz ");


        //播放器控制键监听注册
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition());
                    mediaPlayer.start();
                    play.setImageDrawable(pause_src);

                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if(!isSeekBarChanging){
                                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                                runOnUiThread(skb_refresh);     //UI线程动态改变显时(将显时Runnable在UI——thread中运行)
                            }
                        }
                    }, 0, 50);
                }else if(mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                    play.setImageDrawable(play_src);
                }
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.seekTo(0);
                seekBar.setProgress(0);
            }
        });

        //smart于 onCreate 方法内部注册单击事件
        smart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recongnition_thread();
                ad.show();
            }
        });

        //查看UI组件所在的线程名
        Log.i("tag", "onCreate()-->"+Thread.currentThread().getName());

        //分秒单位控制
        musicLength = (TextView) findViewById(R.id.music_length);
        musicCur = (TextView) findViewById(R.id.music_cur);

        //歌词控件tv
        lyrics = (TextView) findViewById(R.id.lyrics);
        AssetManager assets = getAssets();
        Typeface tf1 = Typeface.createFromAsset(assets, "fff.ttf");
        lyrics.setTypeface(tf1);
        //initView();
        initMediaPlayer();      //初始化MediaPlayer
        //initPlayerParameter();  //初始化MediaPlayer其他参数

        //悬浮按钮动画添加
        s2p = (AnimatedVectorDrawable) getResources().getDrawable(R.drawable.music_s2p_anivector);
        p2s = (AnimatedVectorDrawable) getResources().getDrawable(R.drawable.music_p2s_anivector);

        //音频流识别入口注册
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=5aa9c8f7");
        mIat = com.iflytek.cloud.SpeechRecognizer.createRecognizer(this, mInitListener);//语音对象实例化
        // 设置参数
        setParam();
        // 设置音频来源为外部文件
        mIat.setParameter(SpeechConstant.AUDIO_SOURCE, "-1");

        //Handler--instantiation
        handler =new Handler(){
            @Override
            //当有消息发送出来的时候就执行Handler的这个方法
            public void handleMessage(Message msg){
                super.handleMessage(msg);
                switch(msg.what) {
                    case 0:
                        mIat.stopListening();
                        ad.dismiss();
                        break;

                    default:
                        break;

                }
                mIat.stopListening();
            }
        };

    }

    //播放器初始化
    private void initMediaPlayer() {
        try {
            AssetManager assetMg = this.getApplicationContext().getAssets();
            AssetFileDescriptor fileDescriptor = assetMg.openFd(music_name);
            mediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(), fileDescriptor.getLength());
            //mediaPlayer.setDataSource(ScrollingActivity.this.getAssets().openFd("yh.mp3"));//指定音频文件的路径
            mediaPlayer.prepare();//让mediaplayer进入准备状态
            mediaPlayer.setLooping(true);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer mp) {
                    seekBar.setMax(mediaPlayer.getDuration());
                    musicLength.setText(format.format(mediaPlayer.getDuration()) + "");
                    musicCur.setText("00:00");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //权限申请结果分类
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initMediaPlayer();
                } else {
                    Toast.makeText(MusicPlay.this, "denied access", Toast.LENGTH_SHORT).show();
                    //finish();
                }
                break;
            default:
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //seekbar_ui时间刷新Runnable
    private Runnable skb_refresh = new Runnable() {
        @Override
        public void run() {
                musicCur.setText(format.format(mediaPlayer.getCurrentPosition()) + "");
        }
    };

    //seekbar监听，监听 isSeekBarChanging 标志位
    public class MySeekBar implements SeekBar.OnSeekBarChangeListener {

        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
        }

        /*滚动时,应当暂停后台定时器*/
        public void onStartTrackingTouch(SeekBar seekBar) {
            isSeekBarChanging = true;
        }
        /*滑动结束后，重新设置值*/
        public void onStopTrackingTouch(SeekBar seekBar) {
            isSeekBarChanging = false;
            mediaPlayer.seekTo(seekBar.getProgress());
        }
    }

    private InitListener mInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            Log.d(TAG, "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                Log.d(TAG, "SpeechRecognizer init() code = " + code);
            }
        }
    };

    /**
     语音听写参数设置
     */
    public void setParam()
    {
        // 清空参数
        mIat.setParameter(SpeechConstant.PARAMS, null);
        // 设置引擎
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        // 设置返回结果格式
        mIat.setParameter( SpeechConstant.RESULT_TYPE, "plain" );

        mIat.setParameter(SpeechConstant.ASR_PTT, "1");

        // 设置语言
        if(music_name.equals("talking_english.wav")) {
            mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
        }else {
            mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        }
        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat.setParameter(SpeechConstant.VAD_BOS,"6000");

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat.setParameter(SpeechConstant.VAD_EOS,"3000");
        //设置是否显示标点0表示不显示，1表示显示
        mIat.setParameter(SpeechConstant.ASR_PTT, "0");

    }
    /**
     * 听写监听器。
     */
    private RecognizerListener mRecognizerListener = new RecognizerListener() {

        @Override
        public void onBeginOfSpeech() {
        }

        @Override
        public void onError(SpeechError error) {
            if(error.getErrorCode()==10118) {
                Toast.makeText(getApplicationContext(), "It takes me quite long to expect your answer!?", Toast.LENGTH_SHORT).show();
                //mIat.startListening(mRecognizerListener);
            }
        }

        @Override
        public void onEndOfSpeech() {

        }

        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
            lyrics.append(results.getResultString());
        }

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            Log.d(TAG, "返回音频数据："+data.length);
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
        }
    };

    /**
    *后台线程内构建音频流识别耗时线程
    * */
    private void recongnition_thread() {
        Log.i("tag", "processThread()-->"+ Thread.currentThread().getName());
        new Thread(){
            @Override
            public void run(){
                Log.i("tag", "run()-->"+ Thread.currentThread().getName());
                //在新线程里执行长耗时方法
                recongnition_sequence();
                //执行完毕后给handler发送一个 消息标志位
                Message lyrics = new Message();
                lyrics.what = 0;
                handler.sendMessage(lyrics);
            }
        }.start();

    }
    /**
     * 耗时音频流识别方法
     * */
    private void recongnition_sequence() {
        mIat.startListening(mRecognizerListener);
        //byte[] audioData = Util.readFile(MainActivity.this,"tpoex.wav");
        //由于此处Util中的newreadAudioFile声明为static修饰为类的静态方法，故可直接import包后直接为类调用
        //不再需要实例化为对象
        byte[] audioData = UtilsMusic.newreadAudioFile(MusicPlay.this, music_name,"out.pcm");          //添加动态权限申请
        //byte[] audioData = Util.readAudioFile(ScrollingActivity.this , "htys2_16.wav");
        mIat.writeAudio(audioData, 0, audioData.length);
    }

    private void requestPermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO},1);
        }else {

        }
    }

    //让系统状态栏成半透明状态，沉浸式设计
    public void set_statusbar_visible() {
        this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);//设置状态栏颜色透明
            //window.setNavigationBarColor(Color.TRANSPARENT);//设置导航栏颜色透明
        }
    }

    //重写系统的返回键功能，修复返回不能暂停音乐的Bug
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            mediaPlayer.pause();
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}