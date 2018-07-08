package top.yelbee.www.myapplication;


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
    private VoiceWakeuper mIvw;
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

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.browser_fragment, container, false);
        init();
        init_web_home();

        //语音模块注册
        SpeechUtility.createUtility(getContext(),SpeechConstant.APPID + "=5aa9c8f7");  //Ѷ��ע��
        mIvw = VoiceWakeuper.createWakeuper(getContext(), null);    //(Context arg0, InitListener arg1)
        start_waker();
        return view;
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
                showPopFromBottom(view);
                break;

        }
    }

    //语音模块函数
    private void start_waker() {
        // TODO Auto-generated method stub
        mIvw=VoiceWakeuper.getWakeuper();
        if(mIvw!=null) {
            //resultString="";
            //recoString="";
            //ed1.setText(resultString);

            final String resPath= ResourceUtil.generateResourcePath(getContext(), ResourceUtil.RESOURCE_TYPE.assets, "5aa9c8f7"+".jet");

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
    };
/**
* 致谢JDK 8，语音指令：
*/
public void matching(String str){
    switch (str){
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
            break;

        case "缩小":
            alert("缩小");
            scale_flag--;
            index_webView.setInitialScale(scale_flag*100);
            break;

        default :
            index_webView.loadUrl("http://www.baidu.com/s?wd="+str);
            break;
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



