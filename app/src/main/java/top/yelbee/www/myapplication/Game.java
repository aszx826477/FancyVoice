package top.yelbee.www.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import java.util.ArrayList;
import java.util.Timer;

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

import android.app.Service;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class Game extends Activity {
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
	private Timer tm1;
	private int sign=0;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.game_main);
		SpeechUtility.createUtility(this, SpeechConstant.APPID + "=5aa9c8f7");  //Ѷ��ע��
		mIvw=VoiceWakeuper.createWakeuper(this, null);    //(Context arg0, InitListener arg1)
		set_topic();
		set_content();
		//set_name();

		start_waker(); 
		
		//animation is as following
		   mExplosionField = ExplosionField.attach2Window(this);
	       mVib=(Vibrator)getApplication().getSystemService(Service.VIBRATOR_SERVICE); 
	}
	
	private void start_waker() {
		// TODO Auto-generated method stub
		mIvw=VoiceWakeuper.getWakeuper();    //�ǿ��жϣ���ֹ��ָ��
		if(mIvw!=null) {
			resultString="";
			recoString="";
			//ed1.setText(resultString);

			final String resPath=ResourceUtil.generateResourcePath(this, RESOURCE_TYPE.assets, "5aa9c8f7"+".jet");

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
			if(str.equals("开始")&&(v.getId()==R.id.topic)) {
			mExplosionField.explode(v);
		    mVib.vibrate(500);	   
		    new Thread(new thread_name()).start();         // start thread  
		}	
			else if(str.equals("波比")&&(v.getId()==R.id.ming)) {
				mExplosionField.explode(v);
			    mVib.vibrate(500);
			    new Thread(new thread_sponge()).start();
			}
			else if(str.equals("海绵宝宝")&&(v.getId()==R.id.spongebob)) {
				mExplosionField.explode(v);
			    mVib.vibrate(500);
			    new Thread(new thread_sherlock()).start();
			}
			else if(str.equals("福尔摩斯")&&(v.getId()==R.id.sherlock)) {
				mExplosionField.explode(v);
			    mVib.vibrate(500);
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
		Typeface fromAsset = Typeface.createFromAsset(assets, "kkk.ttf");
		content.setTypeface(fromAsset);
	}	
	
	public void set_name() {
		name=(TextView)findViewById(R.id.name);
		AssetManager assets = getAssets();
		Typeface fromAsset = Typeface.createFromAsset(assets, "kkk.ttf");
		name.setTypeface(fromAsset);
	}
	
	public void set_sponge() {
		spongetip=(TextView)findViewById(R.id.spongetip);
		AssetManager assets = getAssets();
		Typeface fromAsset = Typeface.createFromAsset(assets, "kkk.ttf");
		spongetip.setTypeface(fromAsset);
	}
	
	public void set_sherlock() {
		sherlocktip=(TextView)findViewById(R.id.sherlocktip);
		AssetManager assets = getAssets();
		Typeface fromAsset = Typeface.createFromAsset(assets, "kkk.ttf");
		sherlocktip.setTypeface(fromAsset);
	}
	
	public class thread_name implements Runnable {

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
	
	 public class thread_sponge implements Runnable {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try{  
		            Thread.sleep(2000);     // sleep 1000ms
		            Message message = new Message();  
		            message.what = 2;  
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
		            message.what = 3;  
		            handler.sendMessage(message);  
		        }catch (Exception e) {  
		        } 
			}

		} 

	
	 final Handler handler=new Handler() {
		 public void handleMessage(Message msg) {
			 switch(msg.what) {
			 case 1: 
					 setContentView(R.layout.game_name);
					 set_name();
					 break;
					 
			 case 2:
				     setContentView(R.layout.game_spongebob);
				     set_sponge();
				     break;
				     
			 case 3:
				 	 setContentView(R.layout.game_sherlock);
				 	 set_sherlock();
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
}
