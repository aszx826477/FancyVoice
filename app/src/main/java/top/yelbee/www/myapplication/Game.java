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

public class Game extends Activity {
	ImageView exit_case, game_speak;

	private String TAG= "ivw";

	//animation is as following:
	private ExplosionField mExplosionField;
	private ImageView spongebob;
	private Vibrator mVib;
	private TextView topic;
	private TextView content;


	private String lag="zh_cn";

	// 语音听写对象
	private SpeechRecognizer mIat;
	int ret = 0;// 函数调用返回值

	public static Game instance;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//修复Bug需要判断为非空才能销毁语音唤醒器
		if(VoiceWakeuper.getWakeuper() != null) {
			VoiceWakeuper.getWakeuper().destroy();      //onDestroy mIvw
		}

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		set_statusbar_visible();
		setContentView(R.layout.game_main);

		init_view();

		//animation is as following
		mExplosionField = ExplosionField.attach2Window(this);
		mVib=(Vibrator)getApplication().getSystemService(Service.VIBRATOR_SERVICE);
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

	public void init_view() {
		instance = this;
		SpeechUtility.createUtility(this, SpeechConstant.APPID + "=5a881329");
		mIat = SpeechRecognizer.createRecognizer(this, mInitListener);//语音对象实例化

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
				mIat.destroy();
				finish();
			}
		});

		set_topic();
		set_content();
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
			//String text = JsonParser.parseIatResult(results.getResultString());
			final String final_stream = parseData(results.getResultString());
			//焦点检测
			matching(final_stream, getWindow().getDecorView());

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

	public void matching(String str, View v) {
		if (v instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup) v;
            for (int i = 0; i < parent.getChildCount(); i++) {
                matching(str, parent.getChildAt(i));
            }
        } 
		else {
			if(str.equals("开始")&&(v.getId()== R.id.topic)) {
			mExplosionField.explode(v);
		    mVib.vibrate(500);
		    new Thread(new thread_sponge()).start();

		}
			}
	}

	
	public void set_topic() {
		topic=(TextView)findViewById(R.id.topic);
		AssetManager assets = getAssets();
		Typeface fromAsset = Typeface.createFromAsset(assets, "fff.ttf");
		topic.setTypeface(fromAsset);
	}	
	
	public void set_content() {
		content=(TextView)findViewById(R.id.content);
		AssetManager assets = getAssets();
		Typeface fromAsset = Typeface.createFromAsset(assets, "zw.ttf");
		content.setTypeface(fromAsset);
	}
    public class thread_sponge implements Runnable {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            try{
                Thread.sleep(2000);     // sleep 1000ms
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }catch (Exception e) {
            }
        }

    }
    final Handler handler=new Handler() {
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case 1:
                    Intent intent=new Intent(Game.this, spongebob_act.class); startActivity(intent);
                    break;
                default:

                    break;


            }
            super.handleMessage(msg);
        }
    };

//	public void set_name() {
//		name=(TextView)findViewById(R.id.name);
//		AssetManager assets = getAssets();
//		Typeface fromAsset = Typeface.createFromAsset(assets, "zw.ttf");
//		name.setTypeface(fromAsset);
//	}
//
//	public void set_sponge() {
//		spongetip=(TextView)findViewById(R.id.spongetip);
//		AssetManager assets = getAssets();
//		Typeface fromAsset = Typeface.createFromAsset(assets, "zw.ttf");
//		spongetip.setTypeface(fromAsset);
//	}
//
//	public void set_sherlock() {
//		sherlocktip=(TextView)findViewById(R.id.sherlocktip);
//		AssetManager assets = getAssets();
//		Typeface fromAsset = Typeface.createFromAsset(assets, "zw.ttf");
//		sherlocktip.setTypeface(fromAsset);
//	}
//
//	public void set_celebration() {
//		cele_tip1=(TextView)findViewById(R.id.cele_tip1);
//		cele_tip2=(TextView)findViewById(R.id.cele_tip2);
//		AssetManager assets = getAssets();
//		Typeface fromAsset = Typeface.createFromAsset(assets, "kkk.ttf");
//		cele_tip1.setTypeface(fromAsset);
//		cele_tip2.setTypeface(fromAsset);
//	}




//	final Handler handler=new Handler() {
//		 public void handleMessage(Message msg) {
//		 	switch(msg.what) {
//			 case 1:
//					 setContentView(R.layout.game_spongebob);
//					 set_sponge();
//					 break;
//
//			 case 2:
//				     setContentView(R.layout.game_sherlock);
//				     set_sherlock();
//				     break;
//
//			 case 3:
//				 	 setContentView(R.layout.game_name);
//				 	 set_name();
//				 	 break;
//
//				case 4:
//				 	 	setContentView(R.layout.game_celebration);
//				 	 	set_celebration();
//				 	 	break;
//
//				 case 5:
//				 	finish();
//				 	break;
//
//				 default:
//				 	break;
//			 }
//			 super.handleMessage(msg);
//		 }
//	 };

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

	//重写系统的返回键，保证退出时销毁讯飞的语音引擎
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			mIat.destroy();
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
