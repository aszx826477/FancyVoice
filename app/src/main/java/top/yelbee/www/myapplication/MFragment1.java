package top.yelbee.www.myapplication;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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

import java.util.ArrayList;


/**
 * 依附在MainActivity中的第一个fragment
 * 功能：实现语音浏览器
 */


public class MFragment1 extends Fragment implements View.OnClickListener, View.OnTouchListener {
    //fragment的view
    View view;

    //MainActivity
    MainActivity mainActivity;

    //浏览器底部操作栏
    ImageView index_bottom_left;
    ImageView index_bottom_right;
    ImageView index_bottom_home;
    ImageView index_bottom_search;
    LinearLayout index_bottom_bar;
    ImageView auto_scroll;

    //顶部搜索栏（fancy_title）
    private ImageView iv;
    private TextView text;
    private ImageView tick;
    private ImageView explore_icon;
    private AnimatedVectorDrawable searchToBar;
    private AnimatedVectorDrawable barToSearch;
    private float offset;
    private Interpolator interp;
    private int duration;
    private boolean expanded = false;

    //webview
    private String home_url = "http://www.baidu.com";
    ScrollWebView index_webView;
    WebViewClient homeWebViewClient;
    WebChromeClient homeWebChromeClient;
    LinearLayout web_container;

    //滑动监听坐标记录
    float mPosX;
    float mPosY;
    float mCurPosX;
    float mCurPosY;
    long mStarttime;

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
    private boolean bottom_flag=false;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment1, container, false);
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

        //fancy title初始化
        iv = (ImageView) view.findViewById(R.id.search);
        text = (TextView) view.findViewById(R.id.text);
        tick = (ImageView) view.findViewById(R.id.tick);
        explore_icon = (ImageView) view.findViewById(R.id.explore_icon);
        searchToBar = (AnimatedVectorDrawable) getResources().getDrawable(R.drawable.anim_search_to_bar);
        barToSearch = (AnimatedVectorDrawable) getResources().getDrawable(R.drawable.anim_bar_to_search);
        interp = AnimationUtils.loadInterpolator(getContext(), android.R.interpolator.linear_out_slow_in);
        duration = getResources().getInteger(R.integer.duration_bar);

        //右移参数设置
        offset = -275f * (int) getResources().getDisplayMetrics().scaledDensity;
        iv.setTranslationX(offset);
        text.setTranslationX(offset);
        explore_icon.setTranslationX(offset);
        tick.setTranslationX(offset);
        //animate();

        tick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //animate();
                Toast.makeText(getContext(), "clear!", Toast.LENGTH_SHORT).show();
                text.setText("");
            }
        });

        //explore层覆盖,expanded监听事件切换
        explore_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expanded) {
                    Toast.makeText(getContext(), "explore!", Toast.LENGTH_SHORT).show();
                    String goUrl = text.getText().toString();
                    if (goUrl.indexOf("http://") < 0) {
                        goUrl = "http://" + goUrl;
                        text.setText(goUrl);
                    }
                    index_webView.loadUrl(goUrl);
                    //anim_shrink();
                } else {
                    anim_stretch();
                }
            }
        });


        //浏览器底部操作栏
        index_bottom_left = (ImageView) view.findViewById(R.id.index_bottom_left);
        index_bottom_right = (ImageView) view.findViewById(R.id.index_bottom_right);
        index_bottom_home = (ImageView) view.findViewById(R.id.index_bottom_home);
        index_bottom_search = (ImageView) view.findViewById(R.id.index_bottom_search);
        index_bottom_bar = (LinearLayout) view.findViewById(R.id.index_bottom_bar);
        auto_scroll = (ImageView) view.findViewById(R.id.auto_scroll);

        //搜索引擎图标及图片src
        Google1 = (ImageView) view.findViewById(R.id.google);
        baidu1 = (ImageView) view.findViewById(R.id.baidu);
        baidu_draw = getResources().getDrawable(R.drawable.baidu);
        Google_draw = getResources().getDrawable(R.drawable.google);

        //其他
        web_container = (LinearLayout) view.findViewById(R.id.web_container);
        index_webView = (ScrollWebView) view.findViewById(R.id.index_webView);
        index_webView.setOnScrollChangeListener(new ScrollWebView.OnScrollChangeListener() {

            @Override
            public void onScrollChanged(int l, int t, int oldl, int oldt) {
                //滑动中
                if((mainActivity.navigation).getVisibility()==View.VISIBLE){
                    mainActivity.bottom_bar_disappear();
                }

                if (expanded) {
                    anim_shrink();
                }
            }

            @Override
            public void onPageTop(int l, int t, int oldl, int oldt) {
                //滑动到顶部搜索栏展开
                if (!expanded) {
                    anim_stretch();
                }

                //bottom_bar_appear动画
                mainActivity.bottom_bar_appear();
            }

            @Override
            public void onPageEnd(int l, int t, int oldl, int oldt) {
                //滑动到底部
                bottom_flag=true;

            }
        });


        //设置监听器---底部操作栏
        index_bottom_left.setOnClickListener(this);
        index_bottom_right.setOnClickListener(this);
        //index_bottom_microphone.setOnClickListener(this);
        index_bottom_home.setOnClickListener(this);
        index_bottom_search.setOnClickListener(this);

        animate();
    }

    public void animate() {

        if (!expanded) {
            anim_stretch();
        } else {
            anim_shrink();
        }
    }

    //搜索栏伸展动画
    public void anim_stretch() {
        iv.setImageDrawable(searchToBar);
        searchToBar.start();
        iv.animate().translationX(0f).setDuration(duration).setInterpolator(interp);
        explore_icon.animate().translationX(0f).setDuration(duration).setInterpolator(interp);
        text.animate().translationX(0f).setDuration(duration).setInterpolator(interp);
        tick.animate().translationX(0f).setDuration(duration).setInterpolator(interp);
        expanded = !expanded;
    }

    //搜索栏收缩动画
    public void anim_shrink() {
        iv.setImageDrawable(barToSearch);
        barToSearch.start();
        iv.animate().translationX(offset).setDuration(duration).setInterpolator(interp);
        text.animate().translationX(offset).setDuration(duration).setInterpolator(interp);
        explore_icon.animate().translationX(offset).setDuration(duration).setInterpolator(interp);
        tick.animate().translationX(offset).setDuration(duration).setInterpolator(interp);
        expanded = !expanded;
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

            case R.id.index_bottom_left:
                if (index_webView.canGoBack()) {
                    index_webView.goBack();
                }
                break;
            case R.id.index_bottom_right:
                index_webView.goForward();
                break;

            case R.id.index_bottom_home:
                //index_webView.loadUrl(home_url);

                mHandler.post(ScrollRunnable);

                //auto_scroll.setClickable(false);
                break;

            case R.id.index_bottom_search:
                showPopFromBottom(view);
                break;

            case R.id.auto_scroll:
                mHandler.post(ScrollRunnable);
                //auto_scroll.setClickable(false);
        }
    }

    /**
     * 设置上下滑动作监听器(motion_event)
     * 未使用
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPosX = event.getX();
                mPosY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                mCurPosX = event.getX();
                mCurPosY = event.getY();
                break;
            case MotionEvent.ACTION_UP:

                if (mCurPosY - mPosY > 0 && (Math.abs(mCurPosY - mPosY) > 25)) {
                    //向下滑動
                    mainActivity.bottom_bar_appear();

                } else if (mCurPosY - mPosY < 0 && (Math.abs(mCurPosY - mPosY) > 25)) {
                    //向上滑动
                    mainActivity.bottom_bar_disappear();
                }
                break;

        }

        return false;
    }
    //语音模块函数
    private void start_waker() {
        // TODO Auto-generated method stub
        mIvw=VoiceWakeuper.getWakeuper();    //�ǿ��жϣ���ֹ��ָ��
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
            case "上":

                break;

            case "下":

                break;

            case "前进":

                break;

            case "后退":

                break;

            case "放大":

                break;

            case "阅读":
                mHandler.post(ScrollRunnable);
                break;

            case "引擎":

                break;

            default :
                if (expanded) {
                    Toast.makeText(getContext(),"exploring "+str , Toast.LENGTH_SHORT).show();
                    String goUrl = "http://www.baidu.com/s?wd="+str;
                    text.setText(goUrl);
                    index_webView.loadUrl(goUrl);
                } else {
                    anim_stretch();
                    Toast.makeText(getContext(),"exploring "+str , Toast.LENGTH_SHORT).show();
                    String goUrl = "http://www.baidu.com/s?wd="+str;
                    text.setText(goUrl);
                    index_webView.loadUrl(goUrl);
                }
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
                index_webView.scrollBy(0, 5);
                Log.i("measured:",String.valueOf(web_container.getMeasuredHeight()));
                Log.i("webview:",String.valueOf(index_webView.getHeight()));
                Log.i("scrollY:",String.valueOf(index_webView.getScrollY()));
                if (bottom_flag==true) {
                    alert("bottom!");
                    //Thread.currentThread().interrupt();
                    mHandler.removeCallbacks(ScrollRunnable);
                } else {
                    mHandler.postDelayed(this, 10);
                }
            }
        }
    };
}



