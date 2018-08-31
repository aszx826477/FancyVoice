package top.yelbee.www.myapplication;

import android.app.Activity;
import android.app.Service;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
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
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechEvent;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.VoiceWakeuper;
import com.iflytek.cloud.WakeuperListener;
import com.iflytek.cloud.WakeuperResult;
import com.iflytek.cloud.util.ResourceUtil;
import com.iflytek.cloud.util.ResourceUtil.RESOURCE_TYPE;

import java.util.ArrayList;
import java.util.Timer;

import top.yelbee.www.myapplication.View.ExplosionField;


public class Game extends Activity {
	ImageView exit_case;

	private String TAG= "ivw";
	private EditText ed1;
	private EditText ed2;
	private Button bt1;
	private VoiceWakeuper mIvw;
	private String resultString;  //�������ѽ��
	private String recoString;  //����ʶ����
	private static final int MAX=60;
	private static final int MIN=-20;
	private int curThresh= MIN;
	private String threshStr= "threshold: ";
	private String mEngineType= SpeechConstant.TYPE_CLOUD;
	private String mCloudGrammerId= null;
	//animation is as following:
	private ExplosionField mExplosionField;
	private ImageView spongebob;
	private Vibrator mVib;
	private TextView topic;
	private TextView content;
	private TextView name;
	private TextView spongetip;
	private TextView sherlocktip;
	private TextView cele_tip1;
	private TextView cele_tip2;
	private Timer tm1;
	private int sign=0;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		set_statusbar_visible();
		setContentView(R.layout.game_main);

		SpeechUtility.createUtility(this, SpeechConstant.APPID + "=5aa9c8f7");
		mIvw= VoiceWakeuper.createWakeuper(this, null);    //(Context arg0, InitListener arg1)

		init_view();

		start_waker(); 
		
		//animation is as following
		mExplosionField = ExplosionField.attach2Window(this);
		mVib=(Vibrator)getApplication().getSystemService(Service.VIBRATOR_SERVICE);
	}

	public void init_view() {
		exit_case = (ImageView) findViewById(R.id.game_quit);
		exit_case.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		set_topic();
		set_content();
	}

	private void start_waker() {
		// TODO Auto-generated method stub
		mIvw = VoiceWakeuper.getWakeuper();
		if(mIvw!=null) {
			resultString="";
			recoString="";
			//ed1.setText(resultString);

			final String resPath= ResourceUtil.generateResourcePath(this, RESOURCE_TYPE.assets, "5aa9c8f7"+".jet");

			mIvw.setParameter(SpeechConstant.KEEP_ALIVE, "1");
			mIvw.setParameter(SpeechConstant.PARAMS, null);
			mIvw.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
			mIvw.setParameter(ResourceUtil.IVW_RES_PATH, resPath);
			mIvw.setParameter(SpeechConstant.IVW_SST, "oneshot");  //ʶ��ģʽ��one_shot
			mIvw.setParameter(SpeechConstant.RESULT_TYPE, "json");
			mIvw.setParameter(SpeechConstant.IVW_THRESHOLD, "0:"+curThresh);
			mIvw.setParameter(SpeechConstant.IVW_SHOT_WORD, "0");  //��Ϊ��Ϊ
			mIvw.setParameter(SpeechConstant.ASR_PTT, "0");
			mIvw.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
			mIvw.startListening(mWakeupListener);
		}
	}
	
	private WakeuperListener mWakeupListener = new WakeuperListener() {
		
		@Override
		public void onVolumeChanged(int arg0) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onResult(WakeuperResult arg0) {
			// TODO Auto-generated method stub
			//String text=arg0.getResultString();
			Toast.makeText(getApplicationContext(), "I'm listening...", Toast.LENGTH_SHORT).show();
			//ed1.setText(text);
			//ed2.setText(null);
		}
		
		@Override
		public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {    //knot to fix
			// TODO Auto-generated method stub
			//RecognizerResult result=((RecognizerResult)arg3.get(SpeechEvent.KEY_EVENT_IVW_RESULT));
			//recoString= result.getResultString();
			//ed2.setText(recoString);
			 if (SpeechEvent.EVENT_IVW_RESULT==arg0) {
				    //ed2.setText(null);
		            RecognizerResult result=(RecognizerResult) arg3.get(SpeechEvent.KEY_EVENT_IVW_RESULT);
		            String final_stream = parseData(result.getResultString());
		            //ed2.append(final_stream);
		            //ed2.setText(final_stream);
		            //matching(final_stream,findViewById(R.id.root));		  
		            matching(final_stream, getWindow().getDecorView());
		            start_waker();
		        }  
			 
		}
		
		@Override
		public void onError(SpeechError arg0) {
			// TODO Auto-generated method stub
			if(arg0.getErrorCode()==10118) {
				Toast.makeText(getApplicationContext(), "It takes me quite long to expect your answer!?", Toast.LENGTH_SHORT).show();
				start_waker();
			}
		}
		
		@Override
		public void onBeginOfSpeech() {
			// TODO Auto-generated method stub
			Toast.makeText(getApplicationContext(), "u r welcome to begin", Toast.LENGTH_SHORT).show();
		}
	};
	
	private void reset(View root) {
        if (root instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup) root;
            for (int i = 0; i < parent.getChildCount(); i++) {
                reset(parent.getChildAt(i));
            }
        } else {
            root.setScaleX(1);
            root.setScaleY(1);
            root.setAlpha(1);
        }
    }

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
		    new Thread(new thread_sponge()).start();         // start thread
		}	
			else if(str.equals("海绵宝宝")&&(v.getId()== R.id.spongebob)) {
				mExplosionField.explode(v);
			    mVib.vibrate(500);
			    new Thread(new thread_sherlock()).start();
			}
			else if(str.equals("福尔摩斯")&&(v.getId()== R.id.sherlock)) {
				mExplosionField.explode(v);
			    mVib.vibrate(500);
			    new Thread(new thread_name()).start();
			}
			else if(str.equals("波比")&&(v.getId()== R.id.ming)) {
				mExplosionField.explode(v);
			    mVib.vibrate(500);
			    new Thread(new thread_celebration()).start();
			}
			else if(str.equals("结束")&&(v.getId()== R.id.cele)) {
				mExplosionField.explode(v);
				mVib.vibrate(500);
				new Thread (new thread_finish()).start();
				finish();
			}
			}
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
	
	public void set_name() {
		name=(TextView)findViewById(R.id.name);
		AssetManager assets = getAssets();
		Typeface fromAsset = Typeface.createFromAsset(assets, "zw.ttf");
		name.setTypeface(fromAsset);
	}
	
	public void set_sponge() {
		spongetip=(TextView)findViewById(R.id.spongetip);
		AssetManager assets = getAssets();
		Typeface fromAsset = Typeface.createFromAsset(assets, "zw.ttf");
		spongetip.setTypeface(fromAsset);
	}
	
	public void set_sherlock() {
		sherlocktip=(TextView)findViewById(R.id.sherlocktip);
		AssetManager assets = getAssets();
		Typeface fromAsset = Typeface.createFromAsset(assets, "zw.ttf");
		sherlocktip.setTypeface(fromAsset);
	}

	public void set_celebration() {
		cele_tip1=(TextView)findViewById(R.id.cele_tip1);
		cele_tip2=(TextView)findViewById(R.id.cele_tip2);
		AssetManager assets = getAssets();
		Typeface fromAsset = Typeface.createFromAsset(assets, "kkk.ttf");
		cele_tip1.setTypeface(fromAsset);
		cele_tip2.setTypeface(fromAsset);
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
	 
	 public class thread_sherlock implements Runnable {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try{  
		            Thread.sleep(2000);     // sleep 2000ms
		            Message message = new Message();  
		            message.what = 2;
		            handler.sendMessage(message);  
		        }catch (Exception e) {  
		        } 
			}

		}

	public class thread_name implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try{
				Thread.sleep(2000);     // sleep 1000ms
				Message message = new Message();
				message.what = 3;
				handler.sendMessage(message);
			}catch (Exception e) {
			}
		}

	}
	public class thread_celebration implements Runnable {
		@Override
		public void run() {
			try{
				Thread.sleep(2000);
				Message message = new Message();
				message.what = 4;
				handler.sendMessage(message);
			}catch(Exception e) {

			}
		}
	}

	public class thread_finish implements Runnable {
		@Override
		public void run() {
			try{
				Thread.sleep(2000);
				Message message = new Message();
				message.what = 5;
				handler.sendMessage(message);
			}catch(Exception e){

			}
		}
	}

	 final Handler handler=new Handler() {
		 public void handleMessage(Message msg) {
		 	switch(msg.what) {
			 case 1:
					 setContentView(R.layout.game_spongebob);
					 set_sponge();
					 break;

			 case 2:
				     setContentView(R.layout.game_sherlock);
				     set_sherlock();
				     break;

			 case 3:
				 	 setContentView(R.layout.game_name);
				 	 set_name();
				 	 break;

				case 4:
				 	 	setContentView(R.layout.game_celebration);
				 	 	set_celebration();
				 	 	break;

				 case 5:
				 	finish();
				 	break;

				 default:

				 	break;


			 }
			 super.handleMessage(msg);
		 }
	 };

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
