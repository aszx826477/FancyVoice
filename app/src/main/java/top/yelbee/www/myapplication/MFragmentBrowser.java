package top.yelbee.www.myapplication;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.huawei.hiai.asr.AsrConstants;
import com.huawei.hiai.asr.AsrListener;
import com.huawei.hiai.asr.AsrRecognizer;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechEvent;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.VoiceWakeuper;
import com.iflytek.cloud.WakeuperListener;
import com.iflytek.cloud.WakeuperResult;
import com.iflytek.cloud.util.ResourceUtil;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import top.yelbee.www.myapplication.View.ScrollWebView;

import static android.transition.TransitionManager.beginDelayedTransition;


/**
 * 依附在MainActivity中的第一个fragment
 * 功能：实现语音浏览器
 */


public class MFragmentBrowser extends Fragment implements View.OnClickListener {
    //fragment的view
    View view;

    //MainActivity
    MainActivity mainActivity;

    //顶部导航栏
    ImageView search_case;
    MaterialEditText url_text;
    ProgressBar pro_bar;

    //浏览器底部操作栏
    ImageView index_bottom_hide;
    ImageView index_bottom_left;
    ImageView index_bottom_right;
    ImageView index_bottom_home;
    ImageView index_bottom_speak;
    LinearLayout index_bottom_bar;
    ImageView auto_scroll;

    //记录主导航栏的状态
    boolean nav_bar_expanded = true;


    //webview
    private String home_url = "http://www.baidu.com";
    ScrollWebView index_webView;
    WebViewClient homeWebViewClient;
    WebChromeClient homeWebChromeClient;
    LinearLayout web_container;



    //语音模块变量
    public VoiceWakeuper mIvw_browser;
    private static final int MAX = 60;
    private static final int MIN = -20;
    private String mEngineType = SpeechConstant.TYPE_CLOUD;
    private int curThresh= MIN;

    //搜索引擎弹出窗
    private popup_engine engine_select;
    private ImageView baidu1;
    private ImageView Google1;
    private Drawable baidu_draw;
    private Drawable Google_draw;


    //语音指令句柄
    private Handler mHandler = new Handler();

    //ScrollWebView触底判断符
    private boolean bottom_flag = false;

    //放大倍数控制变量
    private int scale_flag = 1;

    private static String TAG = "Browser HiAI";

    private AsrRecognizer mAsrRecognizer;

    @Override
    public void onDestroy() {
        super.onDestroy();
        VoiceWakeuper.getWakeuper().destroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.browser_fragment, container, false);
        init();
        init_web_home();

        /*
        //语音模块注册
        SpeechUtility.createUtility(getContext(),SpeechConstant.APPID + "=5aa9c8f7");  //Ѷ��ע��
        mIvw_browser = VoiceWakeuper.createWakeuper(getContext(), null);    //(Context arg0, InitListener arg1)
        start_waker();*/

        initHiAIEngine();
        return view;
    }

    /**
     * 创建监听器
     */
    private AsrListener mMyAsrListener = new AsrListener() {
        @Override
        public void onInit(Bundle bundle) {
        }

        @Override
        public void onBeginningOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            Toast.makeText(getActivity(), "speak...", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getActivity(), "end...", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(int i) {
            Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onResults(Bundle results) {
            String mResult = getOnResult(results, AsrConstants.RESULTS_RECOGNITION);
            if (mAsrRecognizer != null) {
                mAsrRecognizer.stopListening();
            }
            //根据语音识别的结果进行相应的操作
            matching(mResult);
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

    /**
     * 初始化HiAI Engine
     */
    void initHiAIEngine() {
        Log.d(TAG, "initEngine() ");
        mAsrRecognizer = AsrRecognizer.createAsrRecognizer(getActivity());
        /** 初始化引擎*/
        Intent initIntent = new Intent();
        initIntent.putExtra(AsrConstants.ASR_AUDIO_SRC_TYPE, AsrConstants.ASR_SRC_TYPE_RECORD);
        mAsrRecognizer.init(initIntent, mMyAsrListener);
        // mAsrRecognizer.destroy();
        Log.d(TAG, "initHiAIEngine_finish");


    }

    /**
     * 启动HiAI Engine进行语音识别
     */
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
        if (mAsrRecognizer != null) {
            mAsrRecognizer.startListening(paramIntent);
        }

    }


    public void init() {
        //mainActivity;
        mainActivity = (MainActivity) getActivity();


        //顶部导航栏
        search_case = (ImageView) view.findViewById(R.id.browser_search);
        url_text = (MaterialEditText) view.findViewById(R.id.browser_url);
        pro_bar = (ProgressBar) view.findViewById(R.id.browser_pro_bar);

        //浏览器底部操作栏
        index_bottom_hide = (ImageView) view.findViewById(R.id.index_bottom_hide);
        index_bottom_left = (ImageView) view.findViewById(R.id.index_bottom_left);
        index_bottom_right = (ImageView) view.findViewById(R.id.index_bottom_right);
        index_bottom_home = (ImageView) view.findViewById(R.id.index_bottom_home);
        index_bottom_speak = (ImageView) view.findViewById(R.id.index_bottom_search);
        index_bottom_bar = (LinearLayout) view.findViewById(R.id.index_bottom_bar);
        //index_bottom_bar.setVisibility(view.GONE);

        //搜索引擎图标及图片src
        Google1 = (ImageView) view.findViewById(R.id.google);
        baidu1 = (ImageView) view.findViewById(R.id.baidu);
        baidu_draw = getResources().getDrawable(R.mipmap.browser_baidu);
        Google_draw = getResources().getDrawable(R.mipmap.browser_google);

        //其他
        web_container = (LinearLayout) view.findViewById(R.id.web_container);
        index_webView = (ScrollWebView) view.findViewById(R.id.index_webView);

        index_webView.setOnScrollChangeListener(new ScrollWebView.OnScrollChangeListener() {

            @Override
            public void onScrollChanged(int l, int t, int oldl, int oldt) {
                //滑动中
/*
                if(index_bottom_bar.getVisibility() == View.GONE) {
                    index_bottom_bar.setVisibility(view.VISIBLE);
                }*/

                if((mainActivity.navigation).getVisibility() == View.VISIBLE){
                    nav_bar_expanded = mainActivity.bottom_bar_disappear();
                }


            }

            @Override
            public void onPageTop(int l, int t, int oldl, int oldt) {
/*
                //expanded = true;
                index_bottom_bar.setVisibility(view.GONE);
                mainActivity.bottom_bar_appear();
*/
            }

            @Override
            public void onPageEnd(int l, int t, int oldl, int oldt) {
                //滑动到底部
                bottom_flag=true;

            }
        });

        //顶部导航栏设置监听器
        search_case.setOnClickListener(this);

        //设置监听器---底部操作栏
        index_bottom_hide.setOnClickListener(this);
        index_bottom_left.setOnClickListener(this);
        index_bottom_right.setOnClickListener(this);
        index_bottom_home.setOnClickListener(this);
        index_bottom_speak.setOnClickListener(this);

    }




    public void init_web_home() {
        WebSettings webSettings = index_webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);    //隐藏缩放控制

        homeWebViewClient = new WebViewClient() {

            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }
        };

        homeWebChromeClient = new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if(newProgress == 100){
                    pro_bar.setVisibility(View.GONE);//加载完网页进度条消失
                }
                else{
                    pro_bar.setVisibility(View.VISIBLE);//开始加载网页时显示进度条
                    pro_bar.setProgress(newProgress);//设置进度值
                }
            }
        };

        index_webView.setWebChromeClient(homeWebChromeClient);
        index_webView.setWebViewClient(homeWebViewClient);
        index_webView.loadUrl(home_url);
    }

    /**
     * 点击事件重写
     */
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.index_bottom_hide:
                if(nav_bar_expanded) {
                    nav_bar_expanded = mainActivity.bottom_bar_disappear();
                } else {
                    nav_bar_expanded = mainActivity.bottom_bar_appear();
                }
                break;

            case R.id.browser_search:
                String goUrl;
                String str = url_text.getText().toString();

                String regEx1 = "http://*";
                Pattern pattern1 = Pattern.compile(regEx1);
                Matcher matcher1 = pattern1.matcher(str);

                String regEx2 = "www*";
                Pattern pattern2 = Pattern.compile(regEx2);
                Matcher matcher2 = pattern2.matcher(str);
                if(matcher1.find()) {
                    goUrl = str;
                } else if (matcher2.find()){
                    goUrl = "http://" + str;
                    url_text.setText(goUrl);
                } else {
                    goUrl = "http://www.baidu.com/s?wd=" + str;
                    url_text.setText(goUrl);
                }
                index_webView.loadUrl(goUrl);
                break;

            case R.id.index_bottom_left:
                if (index_webView.canGoBack()) {
                    index_webView.goBack();
                }
                break;
            case R.id.index_bottom_right:
                index_webView.goForward();
                break;

            case R.id.index_bottom_home:
                index_webView.loadUrl(home_url);
                break;

            case R.id.index_bottom_search:
                //showPopFromBottom(view);
                startHiAIEngine();
                break;

        }
    }

    /*
    //语音模块函数
    private void start_waker() {
        // TODO Auto-generated method stub
        mIvw_browser=VoiceWakeuper.getWakeuper();
        if(mIvw_browser!=null) {
            //resultString="";
            //recoString="";
            //ed1.setText(resultString);

            final String resPath= ResourceUtil.generateResourcePath(getContext(), ResourceUtil.RESOURCE_TYPE.assets, "5aa9c8f7"+".jet");

            mIvw_browser.setParameter(SpeechConstant.KEEP_ALIVE, "1");
            mIvw_browser.setParameter(SpeechConstant.PARAMS, null);
            mIvw_browser.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
            mIvw_browser.setParameter(ResourceUtil.IVW_RES_PATH, resPath);
            mIvw_browser.setParameter(SpeechConstant.IVW_SST, "oneshot");  //ʶ��ģʽ��one_shot
            mIvw_browser.setParameter(SpeechConstant.RESULT_TYPE, "json");
            mIvw_browser.setParameter(SpeechConstant.IVW_THRESHOLD, "0:"+curThresh);
            mIvw_browser.setParameter(SpeechConstant.IVW_SHOT_WORD, "0");  //��Ϊ��Ϊ
            mIvw_browser.setParameter(SpeechConstant.ASR_PTT, "0");
            mIvw_browser.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
            mIvw_browser.startListening(mWakeupListener);
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
            Toast.makeText(getContext(), "I'm listening...", Toast.LENGTH_SHORT).show();
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
                matching(final_stream);
                //matching(final_stream, getWindow().getDecorView());
                start_waker();
            }

        }

        @Override
        public void onError(SpeechError arg0) {
            // TODO Auto-generated method stub
            if(arg0.getErrorCode()==10118) {
                Toast.makeText(getContext(), "It takes me quite long to expect your answer!?", Toast.LENGTH_SHORT).show();
                start_waker();
            }
        }

        @Override
        public void onBeginOfSpeech() {
            // TODO Auto-generated method stub
            Toast.makeText(getContext(), "u r welcome to begin", Toast.LENGTH_SHORT).show();
        }
    };*/

    /**
    * 致谢JDK 8，语音指令：
    */
    public void matching(String str){
        String strip_str = str.substring(0,str.length()-2);
        switch (strip_str){
            case "前进":
               index_webView.goForward();
               break;

            case "后退":
                index_webView.goBack();
                break;

            case "阅读":
                alert("阅读模式");
                bottom_flag=false;
                mHandler.post(ScrollRunnable);
                break;

            case "引擎":
                alert("引擎选择");
                showPopFromBottom(view);
                break;

            case "放大":
                alert("放大");
                scale_flag++;
                index_webView.setInitialScale(scale_flag*100);
                index_webView.goBack();
                break;

            case "缩小":
                alert("缩小");
                scale_flag--;
                index_webView.setInitialScale(scale_flag*100);
                index_webView.goBack();
                break;

            default :
                index_webView.loadUrl("http://www.baidu.com/s?wd="+str);
                break;
        }
    }
/*
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
    }*/

    //PopupWindow设置
    public void showPopFromBottom(View view) {
        engine_select = new popup_engine(getContext());
        //showAtLocation(View parent, int gravity, int x, int y)
        engine_select.showAtLocation(view.findViewById(R.id.fragment1), Gravity.CENTER, 0, 0);
    }

    //Toast替换
    public void alert(String str){
        Toast.makeText(getContext(),str,Toast.LENGTH_SHORT).show();
    }

    private Runnable ScrollRunnable = new Runnable() {
        @Override
        public void run() {
            /**
             * getMeasuredHeight() retrieve the actual height of view(whether visible), getHeight
             * retrieve the height of the view visible
             **/
            int off = web_container.getMeasuredHeight() - index_webView.getHeight();
            if (off > 0) {
                index_webView.scrollBy(0, 1);
                Log.i("measured:",String.valueOf(web_container.getMeasuredHeight()));
                Log.i("webview:",String.valueOf(index_webView.getHeight()));
                Log.i("scrollY:",String.valueOf(index_webView.getScrollY()));
                if (bottom_flag==true) {
                    //alert("bottom!");
                    //Thread.currentThread().interrupt();
                    //mHandler.removeCallbacks(ScrollRunnable);
                    //return;
                } else {
                    mHandler.postDelayed(this, 2);
                }
            }
        }
    };
}



