package top.yelbee.www.myapplication;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.huawei.hiai.asr.AsrConstants;
import com.huawei.hiai.asr.AsrListener;
import com.huawei.hiai.asr.AsrRecognizer;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechEvent;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.VoiceWakeuper;
import com.iflytek.cloud.WakeuperListener;
import com.iflytek.cloud.WakeuperResult;
import com.iflytek.cloud.util.ResourceUtil;
import com.iflytek.cloud.util.ResourceUtil.RESOURCE_TYPE;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Timer;

import top.yelbee.www.myapplication.View.ExplosionField;

public class celebration_act extends Activity {

    //最后一个庆祝不需要调用讯飞的ASR引擎，免得引起不必要的BUG

    ImageView exit_case, game_speak;

    private String TAG= "ivw";

    //animation is as following:
    private ExplosionField mExplosionField;
    private Vibrator mVib;

    private TextView sherlocktip;
    private TextView cele_tip1;
    private TextView cele_tip2;


    private String lag="zh_cn";

    // 语音听写对象
    private SpeechRecognizer mIat;
    int ret = 0;// 函数调用返回值


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //修复Bug需要判断为非空才能销毁语音唤醒器
        if(VoiceWakeuper.getWakeuper() != null) {
            VoiceWakeuper.getWakeuper().destroy();      //onDestroy mIvw
        }

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        set_statusbar_visible();
        setContentView(R.layout.game_celebration);

        init_view();

        //animation is as following
        mExplosionField = ExplosionField.attach2Window(this);
        mVib=(Vibrator)getApplication().getSystemService(Service.VIBRATOR_SERVICE);
    }

    public void init_view() {
        name_act.instance.finish();

        game_speak = (ImageView) findViewById(R.id.game_speak);
        game_speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setParam();
                ret = mIat.startListening(mRecognizerListener);
                if (ret != ErrorCode.SUCCESS) {
                    Toast.makeText(getApplicationContext(),"error"+ret,Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(),"start: "+ret,Toast.LENGTH_SHORT).show();
                }
                Log.d("msc_threadmsc_thread","msc_thread");
            }
        });

        exit_case = (ImageView) findViewById(R.id.game_quit);
        exit_case.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        set_celebration();
    }

    /*
    语音听写参数设置
    */
    public void setParam()
    {
        // 清空参数
        mIat.setParameter(SpeechConstant.PARAMS, null);
        // 设置引擎
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        // 设置返回结果格式
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");

        mIat.setParameter(SpeechConstant.ASR_PTT, "0");


        // 设置语言
        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        // 设置语言区域
        mIat.setParameter(SpeechConstant.ACCENT, lag);

        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat.setParameter(SpeechConstant.VAD_BOS,"4000");

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat.setParameter(SpeechConstant.VAD_EOS,"1000");
    }

    /**
     * 听写监听器。
     */
    private RecognizerListener mRecognizerListener = new RecognizerListener() {

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            Toast.makeText(getApplicationContext(),"speak...",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(SpeechError error) {
            Toast.makeText(getApplicationContext(),"No hearing anything",Toast.LENGTH_SHORT).show();
            //Log.e("111",String.valueOf(error.getErrorCode()));
        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            Toast.makeText(getApplicationContext(),"end...",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onResult(RecognizerResult results, boolean isLast) {

        }

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            Log.d(TAG, "返回音频数据："+data.length);
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}
        }
    };

    public void set_celebration() {
        cele_tip1=(TextView)findViewById(R.id.cele_tip1);
        cele_tip2=(TextView)findViewById(R.id.cele_tip2);
        AssetManager assets = getAssets();
        Typeface fromAsset = Typeface.createFromAsset(assets, "kkk.ttf");
        cele_tip1.setTypeface(fromAsset);
        cele_tip2.setTypeface(fromAsset);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private String parseData(String resultString) {

        Gson gson = new Gson();

        bean xfBean = gson.fromJson(resultString, bean.class);

        ArrayList<bean.WS> ws = xfBean.ws;

        StringBuilder stringBuilder = new StringBuilder();

        for ( bean.WS w: ws) {
            String text = w.cw.get(0).w;
            stringBuilder.append(text);
        }

        return stringBuilder.toString();
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


}
