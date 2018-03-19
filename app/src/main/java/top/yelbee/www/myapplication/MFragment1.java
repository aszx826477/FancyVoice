package top.yelbee.www.myapplication;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
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
    ImageView index_bottom_microphone;
    ImageView index_bottom_home;
    ImageView index_bottom_search;
    LinearLayout index_bottom_bar;

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

    //滑动监听坐标记录
    float mPosX;
    float mPosY;
    float mCurPosX;
    float mCurPosY;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment1, container, false);
        init();
        init_web_home();
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
                Toast.makeText(getContext(),"clear!",Toast.LENGTH_SHORT).show();
                text.setText("");
            }
        });

        //explore层覆盖
        explore_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //animate();
                Toast.makeText(getContext(),"explore!",Toast.LENGTH_SHORT).show();
                String goUrl = text.getText().toString();
                if(goUrl.indexOf("http://")<0){
                    goUrl="http://"+goUrl;
                    text.setText(goUrl);
                }
                //search_title_cancel.callOnClick();
                index_webView.loadUrl(goUrl);
            }
        });

        //浏览器底部操作栏
        index_bottom_left = (ImageView) view.findViewById(R.id.index_bottom_left);
        index_bottom_right = (ImageView) view.findViewById(R.id.index_bottom_right);
        index_bottom_microphone = (ImageView) view.findViewById(R.id.index_bottom_microphone);
        index_bottom_home = (ImageView) view.findViewById(R.id.index_bottom_home);
        index_bottom_search = (ImageView) view.findViewById(R.id.index_bottom_search);
        index_bottom_bar = (LinearLayout) view.findViewById(R.id.index_bottom_bar);

        //其他
        index_webView = (ScrollWebView) view.findViewById(R.id.index_webView);
        index_webView.setOnScrollChangeListener(new ScrollWebView.OnScrollChangeListener() {

            @Override
            public void onScrollChanged(int l, int t, int oldl, int oldt) {
                //滑动中
                mainActivity.bottom_bar_disappear();
            }

            @Override
            public void onPageTop(int l, int t, int oldl, int oldt) {
                //滑动到顶部
                animate();
               /* if(!expanded){
                    animate();
                }*/
                mainActivity.bottom_bar_appear();
            }

            @Override
            public void onPageEnd(int l, int t, int oldl, int oldt) {
                //滑动到底部
            }
        });


        //设置监听器---底部操作栏
        index_bottom_left.setOnClickListener(this);
        index_bottom_right.setOnClickListener(this);
        index_bottom_microphone.setOnClickListener(this);
        index_bottom_home.setOnClickListener(this);
        index_bottom_search.setOnClickListener(this);
        
        //web_view监听器
        //未使用
        //index_webView.setOnTouchListener(this);

        //动态改变layout_width（触控bug的修复）
        //FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)iv.getLayoutParams();
        //params.width = width_trans(getActivity() , 0);


        //入口animate(),修复初始化bug
        animate();
    }

    public void animate() {

        if (!expanded) {
            iv.setImageDrawable(searchToBar);
            searchToBar.start();
            iv.animate().translationX(0f).setDuration(duration).setInterpolator(interp);
            explore_icon.animate().translationX(0f).setDuration(duration).setInterpolator(interp);
            text.animate().translationX(0f).setDuration(duration).setInterpolator(interp);
            tick.animate().translationX(0f).setDuration(duration).setInterpolator(interp);
            //text.animate().alpha(1f).setStartDelay(duration - 100).setDuration(100).setInterpolator(interp);
            //tick.animate().alpha(1f).setStartDelay(duration - 150).setDuration(100).setInterpolator(interp);
        } else {
            iv.setImageDrawable(barToSearch);
            barToSearch.start();
            iv.animate().translationX(offset).setDuration(duration).setInterpolator(interp);
            text.animate().translationX(offset).setDuration(duration).setInterpolator(interp);
            explore_icon.animate().translationX(offset).setDuration(duration).setInterpolator(interp);
            tick.animate().translationX(offset).setDuration(duration).setInterpolator(interp);
            //text.setAlpha(0f);
        }
        //动画一次改变一次boolean
        expanded = !expanded;
    }

    //width_trans(context , width)
    public int width_trans(Context context , float dipValue){
        Resources r = getContext().getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP , dipValue , r.getDisplayMetrics());
    }


    public void init_web_home() {
        WebSettings webSettings = index_webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(false);

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
     *
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
            case R.id.index_bottom_microphone:
                break;
            case R.id.index_bottom_home:
                index_webView.loadUrl(home_url);
                break;
            case R.id.index_bottom_search:
                break;

        }
    }

    /**
     * 设置上下滑动作监听器
     * 未使用
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch(event.getAction()) {
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
                    /*if(expanded){
                        animate();
                    }*/


                } else if (mCurPosY - mPosY < 0 && (Math.abs(mCurPosY - mPosY) > 25)) {
                    //向上滑动
                    mainActivity.bottom_bar_disappear();
                }
                break;
        }

        return false;
    }
}