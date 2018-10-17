package top.yelbee.www.myapplication;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.hiai.asr.AsrConstants;
import com.huawei.hiai.asr.AsrListener;
import com.huawei.hiai.asr.AsrRecognizer;


import java.text.SimpleDateFormat;

import java.util.Date;


import top.yelbee.www.myapplication.Datebase.NotebookDB;
import top.yelbee.www.myapplication.Controller.AsrError;


import com.iflytek.cloud.VoiceWakeuper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



public class NotebookEdit extends Activity implements  View.OnClickListener {
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


    //语音听写
    private static String TAG = "1111111111111111111111111";

    private AsrRecognizer mAsrRecognizer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //修复Bug需要判断为非空才能销毁语音唤醒器
        if (VoiceWakeuper.getWakeuper() != null) {
            VoiceWakeuper.getWakeuper().destroy();      //onDestroy mIvw
        }

        set_statusbar_visible();
        setContentView(R.layout.notebook_edit);

        InitView();
        Log.d(TAG, "杨荣锋1");
        initHiAIEngine();
        Log.d(TAG, "杨荣锋2");


    }

    /**
     * 创建监听器
     */
    private AsrListener mMyAsrListener = new AsrListener() {
        @Override
        public void onInit(Bundle bundle) {
            Log.d(TAG, "onInit() called with: params = [" + bundle + "]");
        }

        @Override
        public void onBeginningOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            Toast.makeText(getApplicationContext(), "speak...", Toast.LENGTH_SHORT).show();
            //Log.d(TAG, "onBeginningOfSpeech() called");
        }

        @Override
        public void onRmsChanged(float v) {
        }

        @Override
        public void onBufferReceived(byte[] bytes) {
        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            Toast.makeText(getApplicationContext(), "end...", Toast.LENGTH_SHORT).show();
            //Log.d(TAG, "onEndOfSpeech: ");
        }

        @Override
        public void onError(int i) {
            //Toast.makeText(getApplicationContext(), "error code = " + i, Toast.LENGTH_SHORT).show();
            AsrError asrError = new AsrError();
            asrError.errorCodeHandle(getApplicationContext(), i);
        }

        @Override
        public void onResults(Bundle results) {
            /*
            //String text = JsonParser.parseIatResult(results.getResultString());
            final String final_stream = parseData(bundle.getResultString());
            //焦点检测
            if (et_content.hasFocus()) {
                et_content.append(final_stream);//待检测光标位置
            } else if (et_title.hasFocus()) {
                et_title.append(final_stream);
            }*/
            Log.d(TAG, "onResults() called with: results = [" + results + "]");
            String mResult = getOnResult(results, AsrConstants.RESULTS_RECOGNITION);


            if (mAsrRecognizer != null) {
                mAsrRecognizer.stopListening();
            }

            //焦点检测
            if (mResult.equals("ASR_FAILURE") || mResult.equals("ASR_UNCONFIDENT") || mResult.equals("NO SPEECH DETECTED")) {
                Toast.makeText(getApplicationContext(), "识别不出声音", Toast.LENGTH_SHORT).show();
            } else if (et_content.hasFocus()) {
                et_content.append(mResult);//待检测光标位置
            } else {
                et_title.append(mResult);
            }

        }

        @Override
        public void onPartialResults(Bundle bundle) {
        }

        @Override
        public void onEnd() {
        }

        @Override
        public void onEvent(int i, Bundle bundle) {
        }
    };
    private String getOnResult(Bundle partialResults, String key) {

        String json = partialResults.getString(key);
        final StringBuilder sb = new StringBuilder();
        try {
            JSONObject result = new JSONObject(json);
            JSONArray items = result.getJSONArray("result");
            for (int i = 0; i < items.length(); i++) {
                String word = items.getJSONObject(i).getString("word");
                sb.append(word);

            }

        } catch (JSONException exp) {

        }
        return sb.toString();
    }





/*
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            Log.d(TAG, "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                Log.d(TAG, "SpeechRecognizer init() code = " + code);
            }
        }
    };
    */


    private void InitView() {
        /*
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=5a881329");
        mIat = SpeechRecognizer.createRecognizer(this, mInitListener);//语音对象实例化
        */



        title = (TextView) findViewById(R.id.title);
        et_title = (EditText) findViewById(R.id.et_title);

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
        switch (v.getId()) {
            case R.id.notebook_edit_quit:
                mAsrRecognizer.destroy();
                finish();
                break;
            case R.id.notebook_speak:

                startHiAIEngine();

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
                        mAsrRecognizer.destroy();
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
                    mAsrRecognizer.destroy();
                    finish();
                }
                break;
        }
    }

    /**
     * 启动HiAI Engine
     */
    void initHiAIEngine() {
        Log.d(TAG, "initEngine() ");
        mAsrRecognizer = AsrRecognizer.createAsrRecognizer(this);
        /** 初始化引擎*/
        Intent initIntent = new Intent();
        initIntent.putExtra(AsrConstants.ASR_AUDIO_SRC_TYPE, AsrConstants.ASR_SRC_TYPE_RECORD);
        mAsrRecognizer.init(initIntent, mMyAsrListener);
        // mAsrRecognizer.destroy();
        Log.d(TAG, "initHiAIEngine_finish");


    }

    void startHiAIEngine() {
        /** 设置引擎参数开始识别 */
        /** 用户可以不设置参数,使用默认参数*/
        Intent paramIntent = new Intent();
        /** 设置前端静音检测时间*/
        paramIntent.putExtra(AsrConstants.ASR_VAD_FRONT_WAIT_MS, 4000);
        /** 设置后端静音检测时间*/
        paramIntent.putExtra(AsrConstants.ASR_VAD_END_WAIT_MS, 1000);
        /** 设置超时时间*/
        //paramIntent.putExtra(AsrConstants.ASR_TIMEOUT_THRESHOLD_MS, 20000);

        mAsrRecognizer.startListening(paramIntent);
        Log.d(TAG, "test");
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