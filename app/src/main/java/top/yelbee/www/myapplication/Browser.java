package top.yelbee.www.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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

import java.util.ArrayList;

import hugo.weaving.DebugLog;
import top.yelbee.www.library.FilterMenu;
import top.yelbee.www.library.FilterMenuLayout;


public class Browser extends AppCompatActivity implements View.OnClickListener {

    //ImageView index_bottom_poweroff;

    LinearLayout index_view;
    EditText index_title_edit;
    WebView  index_webView;
    /*
    LinearLayout index_bottom_menu_goback;
    LinearLayout index_bottom_menu_nogoback;
    LinearLayout index_bottom_menu_goforward;
    LinearLayout index_bottom_menu_nogoforward;
    LinearLayout index_bottom_menu_gohome;
    LinearLayout index_bottom_menu_nogohome;
    */

    ImageView index_title_refresh;
    ProgressBar index_title_progress;


    View index_background;






    LinearLayout search_view;
    EditText search_title_edit;
    Button search_title_cancel;
    ImageView search_title_url_clear;
    Button search_title_go;

    WebViewClient homeWebViewClient;
    WebChromeClient homeWebChromeClient;

    //主页地址
    private String home_url = "http://www.baidu.com";

    //引擎图像变换
    public ImageView index_title_top_earth;
    private String prefixx;
    private String trans;

    //语音引擎配置如下
    private VoiceWakeuper mIvw;
    private String resultString;  //�������ѽ��
    private String recoString;  //����ʶ����
    private static final int MAX=60;
    private static final int MIN=-20;
    private int curThresh= MIN;
    private String threshStr= "threshold: ";
    private String mEngineType= SpeechConstant.TYPE_CLOUD;
    private String mCloudGrammerId= null;

    //识别结果回传变量
    private String goUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_browser);

        //语音唤醒启动
        start_waker();

        //语音引擎配置初始化
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=5aa9c8f7");  //Ѷ��ע��
        mIvw=VoiceWakeuper.createWakeuper(this, null);    //(Context arg0, InitListener arg1)

        //引擎图像变换
        index_title_top_earth = (ImageView)findViewById(R.id.index_title_top_earth) ;

        index_title_top_earth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("preifx","");
                intent.setClass(Browser.this,engine_drag.class);
                startActivityForResult(intent , 1);
            }
        });

        //index_bottom_poweroff = (ImageView)findViewById(R.id.index_bottom_poweroff);

        index_view = (LinearLayout)findViewById(R.id.index_view);
        index_title_edit = (EditText) index_view.findViewById(R.id.index_title_edit);
        index_webView = (WebView)index_view.findViewById(R.id.index_webView);
        /*
        index_bottom_menu_goback = (LinearLayout)findViewById(R.id.index_bottom_menu_goback);
        index_bottom_menu_nogoback = (LinearLayout)findViewById(R.id.index_bottom_menu_nogoback);
        index_bottom_menu_goforward = (LinearLayout)findViewById(R.id.index_bottom_menu_goforward);
        index_bottom_menu_nogoforward = (LinearLayout)findViewById(R.id.index_bottom_menu_nogoforward);
        index_bottom_menu_gohome = (LinearLayout)findViewById(R.id.index_bottom_menu_gohome);
        index_bottom_menu_nogohome = (LinearLayout)findViewById(R.id.index_bottom_menu_nogohome);
        */
        index_title_refresh = (ImageView)index_view.findViewById(R.id.index_title_refresh);
        index_title_progress = (ProgressBar)index_view.findViewById(R.id.index_title_progress);
        index_background = (View)findViewById(R.id.index_background);



        //index_bottom_poweroff.setOnClickListener(this);

        //index_bottom_menu_gohome.setOnClickListener(this);
        index_title_edit.setOnClickListener(this);
        /*
        index_bottom_menu_goback.setOnClickListener(this);
        index_bottom_menu_goforward.setOnClickListener(this);
        */
        index_title_refresh.setOnClickListener(this);
        index_background.setOnClickListener(this);




        search_view = (LinearLayout)findViewById(R.id.search_view);
        search_title_edit = (EditText)search_view.findViewById(R.id.search_title_edit);
        search_title_cancel = (Button)search_view.findViewById(R.id.search_title_cancel);
        search_title_go = (Button)search_view.findViewById(R.id.search_title_go);
        search_title_url_clear = (ImageView)search_view.findViewById(R.id.search_title_url_clear);
        search_title_edit.addTextChangedListener(search_title_edit_changed);
        search_title_cancel.setOnClickListener(this);
        search_title_url_clear.setOnClickListener(this);
        search_title_go.setOnClickListener(this);

        FilterMenuLayout layout = (FilterMenuLayout) findViewById(R.id.filter_menu);
        attachMenu(layout);


        initHome();

    }
    private FilterMenu attachMenu(FilterMenuLayout layout){
        return new FilterMenu.Builder(this)
                .addItem(R.mipmap.ic_action_io)
                .addItem(R.mipmap.ic_action_back)
                .addItem(R.mipmap.ic_action_voice)
                .attach(layout)
                .withListener(listener)
                .build();
    }

    FilterMenu.OnMenuChangeListener listener = new FilterMenu.OnMenuChangeListener() {
        @DebugLog
        @Override
        public void onMenuItemClick(View view, int position) {
            switch (position) {
                case 0:
                    finish();
                    Intent intent= new Intent(Browser.this, MainActivity.class);
                    startActivity(intent);
                    break;
                case 1:
                    index_webView.goBack();
                    break;
                case 3:
                    break;


            }
        }

        @Override
        public void onMenuCollapse() {

        }

        @Override
        public void onMenuExpand() {

        }



    };


    TextWatcher search_title_edit_changed = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            //是否显示清除
            if(search_title_edit.getText().toString().length()>0){
                search_title_url_clear.setVisibility(View.VISIBLE);
                search_title_go.setVisibility(View.VISIBLE);
                search_title_cancel.setVisibility(View.GONE);
            }else{
                search_title_url_clear.setVisibility(View.GONE);
                search_title_go.setVisibility(View.GONE);
                search_title_cancel.setVisibility(View.VISIBLE);
            }

            //是否显示前往
            //是否显示取消


        }
    };


    private void initHome(){
        WebSettings webSettings = index_webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setBuiltInZoomControls(true);

        homeWebViewClient = new WebViewClient(){

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                index_title_progress.setVisibility(View.VISIBLE);
            }
/*
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if(url.equals(home_url+"/")){
                    index_bottom_menu_gohome.setVisibility(View.GONE);
                    index_bottom_menu_nogohome.setVisibility(View.VISIBLE);
                }else{
                    index_bottom_menu_gohome.setVisibility(View.VISIBLE);
                    index_bottom_menu_nogohome.setVisibility(View.GONE);
                }
                if(index_webView.canGoForward()){
                    index_bottom_menu_goforward.setVisibility(View.VISIBLE);
                    index_bottom_menu_nogoforward.setVisibility(View.GONE);
                }else{
                    index_bottom_menu_goforward.setVisibility(View.GONE);
                    index_bottom_menu_nogoforward.setVisibility(View.VISIBLE);
                }
                if(index_webView.canGoBack()){
                    index_bottom_menu_goback.setVisibility(View.VISIBLE);
                    index_bottom_menu_nogoback.setVisibility(View.GONE);
                }else{
                    index_bottom_menu_goback.setVisibility(View.GONE);
                    index_bottom_menu_nogoback.setVisibility(View.VISIBLE);
                }
            }*/
        };

        homeWebChromeClient = new WebChromeClient(){

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if(newProgress==100){
                    index_title_progress.setVisibility(View.GONE);
                }else{
                    index_title_progress.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }
        };





        index_webView.setWebChromeClient(homeWebChromeClient);
        index_webView.setWebViewClient(homeWebViewClient);
        index_webView.loadUrl(home_url);
    }


    @Override
    public void onClick(View v) {
        InputMethodManager inputMethodManager=(InputMethodManager)search_title_edit.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        switch (v.getId()){
            case R.id.index_title_edit:
                index_view.setVisibility(View.GONE);
                search_view.setVisibility(View.VISIBLE);
                search_title_edit.requestFocus();
                inputMethodManager.toggleSoftInput(0,InputMethodManager.SHOW_FORCED);
                break;
            case R.id.search_title_cancel:
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                index_view.setVisibility(View.VISIBLE);
                search_view.setVisibility(View.GONE);
                search_title_edit.clearFocus();
                break;
            case R.id.search_title_go:
                if(goUrl.equals("")){
                    goUrl = search_title_edit.getText().toString();
                }else{
                if(goUrl.indexOf("http://")<0&&goUrl.indexOf(".com")<0){
                    //goUrl="http://"+goUrl;
                    //goUrl="http://www.baidu.com/s?wd="+goUrl;  //原理待查
                    //goUrl=engine_drag.+goUrl;
                    goUrl=prefixx+goUrl;
                    search_title_edit.setText(goUrl);
                }
                else{
                    //goUrl="http://"+goUrl;
                    search_title_edit.setText(goUrl);
                }
                search_title_cancel.callOnClick();
                index_webView.loadUrl(goUrl);}
                break;
            case R.id.index_bottom_menu_goback:
                index_webView.goBack();
                break;
            case R.id.index_bottom_menu_goforward:
                index_webView.goForward();
                break;
            case R.id.index_bottom_menu_gohome:
                index_webView.loadUrl(home_url);
                break;

            case R.id.search_title_url_clear:
                search_title_edit.setText("");
                break;
            case R.id.index_title_refresh:
                index_webView.reload();
                break;


            case R.id.index_bottom_poweroff:
                finish();
                //onDestroy();
                //System.exit(0);
                break;
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && index_webView.canGoBack()) {
            index_webView.goBack();
            return true;
        }
        return false;
    }

    /*//prefix传值方法
    public void set_prefix(String prefix){
        this.prefix = prefix;
    }

    public String get_prefix(){
        return this.prefix;
    }*/

    protected void onActivityResult(int requestCode , int resultCode , Intent data){
        super.onActivityResult(requestCode , resultCode , data);
        if((requestCode==1)&&(resultCode==2)){
            Bundle b = data.getExtras();
            prefixx = b.getString("prefix");
            trans = b.getString("trans");
            if (trans.equals("baidu")) {
                index_title_top_earth.setImageResource(R.mipmap.baidu_icon);
            }
            else if(trans.equals("google")){
                index_title_top_earth.setImageResource(R.mipmap.google_icon);
            }
            }

        }

    private void start_waker() {
        // TODO Auto-generated method stub
        mIvw=VoiceWakeuper.getWakeuper();    //�ǿ��жϣ���ֹ��ָ��
        if(mIvw!=null) {
            resultString="";
            recoString="";
            //ed1.setText(resultString);
            //��ȡ����������Դ·��
            final String resPath= ResourceUtil.generateResourcePath(this, ResourceUtil.RESOURCE_TYPE.assets, "5aa9c8f7"+".jet");
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
                goUrl = final_stream;     //结果回传变量
                search_title_go.performClick();
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

    private String parseData(String resultString) {
        //����gson����,�ǵ�Ҫ����һ��gson.jar��������ʹ��.
        Gson gson = new Gson();
        //���� 1.String���͵�json���� ���� 2.���json���ݶ�Ӧ��bean��
        bean xfBean = gson.fromJson(resultString, bean.class);
        //����һ������,�������bean����Ķ���.
        ArrayList<bean.WS> ws = xfBean.ws;
        //����һ������,������Ŵ�ÿ���������õ�������,ʹ��StringBuilderЧ�ʸ���
        StringBuilder stringBuilder = new StringBuilder();
        //ʹ�ø߼�forѭ��,ȡ���ض����Ե���������,װ��StringBuilder��
        for ( bean.WS w: ws) {
            String text = w.cw.get(0).w;
            stringBuilder.append(text);
        }
        //�������ڵ�����תΪ�ַ������س�ȥ.
        return stringBuilder.toString();
    }
    }

