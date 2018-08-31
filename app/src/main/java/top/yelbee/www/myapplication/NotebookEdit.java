package top.yelbee.www.myapplication;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUtility;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

import top.yelbee.www.myapplication.Datebase.NotebookDB;

import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.VoiceWakeuper;

public class NotebookEdit extends Activity implements CompoundButton.OnCheckedChangeListener,View.OnClickListener {
    private FloatingActionButton fbutton;
    private ImageView exit_case;
    private ImageView save_case;

    private TextView tv_date;
    private EditText et_title;
    private TextView title;
    private EditText et_content;
    private NotebookDB DBHelper;
    public int enter_state = 0;//用来区分是新建一个note还是更改原来的note

    public String last_content1;//用来获取edittext(title)的内容
    public String last_content2;//用来获取edittext(content)内容
    public String dateString;   //用来获取正在编辑备忘录的时间

    private SQLiteDatabase DB;

    private SwitchCompat lag_sel;
    private TextView lag_set1;
    private TextView lag_set2;
    private String lag="zh_cn";
    //语音听写
    private static String TAG = "IatDemo";
    // 语音听写对象
    private SpeechRecognizer mIat;
    // 听写结果内容
    private EditText mResultText;
    // 用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
    int ret = 0;// 函数调用返回值
    private Button test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //修复Bug需要判断为非空才能销毁语音唤醒器
        if(VoiceWakeuper.getWakeuper() != null) {
            VoiceWakeuper.getWakeuper().destroy();      //onDestroy mIvw
        }
        set_statusbar_visible();
        setContentView(R.layout.notebook_edit);
        InitView();

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

    private void InitView() {
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=5a881329");
        mIat = SpeechRecognizer.createRecognizer(this, mInitListener);//语音对象实例化

        //语言选择控件
        lag_sel = (SwitchCompat) findViewById(R.id.lag_sel);
        lag_sel.setOnCheckedChangeListener(this);
        lag_set1 = (TextView)findViewById(R.id.lag_set1);
        lag_set2 = (TextView)findViewById(R.id.lag_set2);

        title = (TextView)findViewById(R.id.title);
        et_title = (EditText)findViewById(R.id.et_title);

        tv_date = (TextView) findViewById(R.id.tv_date);
        et_content = (EditText) findViewById(R.id.et_content);
        et_title = (EditText) findViewById(R.id.et_title);
        DBHelper = new NotebookDB(this);

        //获取此时时刻时间
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateString = sdf.format(date);
        tv_date.setText(dateString);

        //接收内容和id
        Bundle myBundle = this.getIntent().getExtras();
        last_content1 = myBundle.getString("info_title");
        last_content2 = myBundle.getString("info_content");
        enter_state = myBundle.getInt("enter_state");

        //还原原项目信息
        et_title.setText(last_content1);
        et_content.setText(last_content2);

        //语音按钮和保存
        exit_case = (ImageView) findViewById(R.id.notebook_edit_quit);
        fbutton = (FloatingActionButton) findViewById(R.id.notebook_speak);
        save_case = (ImageView) findViewById(R.id.notebook_save);

        fbutton.setOnClickListener(this);
        exit_case.setOnClickListener(this);
        save_case.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.notebook_edit_quit:
                finish();
                break;
            case R.id.notebook_speak:
                // 设置参数
                setParam();
                ret = mIat.startListening(mRecognizerListener);
                if (ret != ErrorCode.SUCCESS) {
                    Toast.makeText(getApplicationContext(),"error"+ret,Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(),"start: "+ret,Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.notebook_save:
                SQLiteDatabase db = DBHelper.getReadableDatabase();

                //获取edit_title内容
                String title = et_title.getText().toString().trim();
                // 获取edit_text内容
                String content = et_content.getText().toString();

                // 添加一个新的日志
                if (enter_state == 0) {
                    if (!content.equals("")) {
                        //向数据库添加信息
                        ContentValues values = new ContentValues();
                        values.put("title", title);
                        values.put("content", content);
                        values.put("date", dateString);
                        db.insert("note", null, values);
                        finish();
                    } else {
                        Toast.makeText(NotebookEdit.this, "请输入你的内容！", Toast.LENGTH_SHORT).show();
                    }
                }
                // 查看并修改一个已有的日志
                else {
                    ContentValues values = new ContentValues();
                    values.put("title", title);
                    values.put("content", content);
                    values.put("date", dateString);
                    db.update("note", values, "title = ?", new String[]{last_content1});
                    finish();
                }
                break;
        }
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

        if(et_title.hasFocus()){
            mIat.setParameter(SpeechConstant.ASR_PTT, "0");
        }
        else{
            mIat.setParameter(SpeechConstant.ASR_PTT, "1");
        }

        if (lag.equals("en_us")) {
            // 设置语言
            mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
            mIat.setParameter(SpeechConstant.ACCENT, null);
        }else {
            // 设置语言
            mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
            // 设置语言区域
            mIat.setParameter(SpeechConstant.ACCENT, lag);
        }
        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat.setParameter(SpeechConstant.VAD_BOS,"4000");

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat.setParameter(SpeechConstant.VAD_EOS,"1000");


    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (lag_sel.isChecked()) {
            lag = "en_us";
            lag_set1.setText("title");
            lag_set2.setText("content_edit(eng)");
            Toast.makeText(getApplicationContext(), "English is selected", Toast.LENGTH_SHORT).show();
        } else if (!lag_sel.isChecked()) {
            lag = "zh_cn";
            lag_set1.setText("标题");
            lag_set2.setText("内容编辑(中文)");
            Toast.makeText(getApplicationContext(), "中文已选择", Toast.LENGTH_SHORT).show();
        }
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
            //String text = JsonParser.parseIatResult(results.getResultString());
            final String final_stream = parseData(results.getResultString());
            //焦点检测
            if(et_content.hasFocus()){
                et_content.append(final_stream);//待检测光标位置
            }
            else if(et_title.hasFocus()){
                et_title.append(final_stream);
            }

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