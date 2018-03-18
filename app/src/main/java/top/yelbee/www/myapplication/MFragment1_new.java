package top.yelbee.www.myapplication;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
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
import android.widget.TextView;
import android.widget.Toast;


/**
 * 依附在MainActivity中的第一个fragment
 * 功能：实现语音浏览器
 */


public class MFragment1_new extends Fragment implements View.OnClickListener, View.OnTouchListener {
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
    WebView index_webView;
    WebViewClient homeWebViewClient;
    WebChromeClient homeWebChromeClient;
/*
    //浏览器顶部搜索栏
    LinearLayout index_view;
    EditText index_title_edit;
    ImageView index_title_refresh;
    LinearLayout search_view;
    Button search_title_go;
    Button search_title_cancel;
    ImageView search_title_url_clear;
    EditText search_title_edit;
*/
    //其他
    /*
    View index_background;
    ProgressBar index_title_progress;
*/
    //滑动监听坐标记录
    float mPosX;
    float mPosY;
    float mCurPosX;
    float mCurPosY;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment1_new, container, false);
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
        animate();
        tick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animate();
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
/*
        //浏览器顶部搜索栏
        index_view = (LinearLayout) view.findViewById(R.id.index_view);
        index_title_edit = (EditText) index_view.findViewById(R.id.index_title_edit);
        index_title_refresh = (ImageView) index_view.findViewById(R.id.index_title_refresh);

        search_view = (LinearLayout) view.findViewById(R.id.search_view);
        search_title_go = (Button) search_view.findViewById(R.id.search_title_go);
        search_title_cancel = (Button) search_view.findViewById(R.id.search_title_cancel);
        search_title_url_clear = (ImageView) search_view.findViewById(R.id.search_title_url_clear);
        search_title_edit = (EditText) search_view.findViewById(R.id.search_title_edit);
*/
        //其他
        index_webView = (WebView) view.findViewById(R.id.index_webView);
        /*
        index_background = (View) view.findViewById(R.id.index_background);

        index_title_progress = (ProgressBar) index_view.findViewById(R.id.index_title_progress);
*/
        //设置监听器---底部操作栏
        index_bottom_left.setOnClickListener(this);
        index_bottom_right.setOnClickListener(this);
        index_bottom_microphone.setOnClickListener(this);
        index_bottom_home.setOnClickListener(this);
        index_bottom_search.setOnClickListener(this);
        /*
        //设置监听器---顶部搜索栏
        index_title_edit.setOnClickListener(this);
        index_title_refresh.setOnClickListener(this);
        search_title_go.setOnClickListener(this);
        search_title_cancel.setOnClickListener(this);
        search_title_url_clear.setOnClickListener(this);
*/
        /*
        //文本监听器
        search_title_edit.addTextChangedListener(search_title_edit_changed);
*/
        //web_view监听器
        index_webView.setOnTouchListener(this);
    }

    public void animate() {

        if (!expanded) {
            iv.setImageDrawable(searchToBar);
            searchToBar.start();
            //iv.animate().translationX(0f).setDuration(duration).setInterpolator(interp);
            text.animate().alpha(1f).setStartDelay(duration - 100).setDuration(100).setInterpolator(interp);
            //tick.animate().alpha(1f).setStartDelay(duration - 150).setDuration(100).setInterpolator(interp);
        } else {
            iv.setImageDrawable(barToSearch);
            barToSearch.start();
            //iv.animate().translationX(offset).setDuration(duration).setInterpolator(interp);
            text.setAlpha(0f);
        }
        expanded = !expanded;
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
                /*
                index_title_progress.setVisibility(View.VISIBLE);*/
            }
        };

        homeWebChromeClient = new WebChromeClient() {
            /*
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    index_title_progress.setVisibility(View.GONE);
                } else {
                    index_title_progress.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }
            */
        };

        index_webView.setWebChromeClient(homeWebChromeClient);
        index_webView.setWebViewClient(homeWebViewClient);
        index_webView.loadUrl(home_url);
    }
/*
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
            if (search_title_edit.getText().toString().length() > 0) {
                search_title_url_clear.setVisibility(View.VISIBLE);
                search_title_go.setVisibility(View.VISIBLE);
                search_title_cancel.setVisibility(View.GONE);
            } else {
                search_title_url_clear.setVisibility(View.GONE);
                search_title_go.setVisibility(View.GONE);
                search_title_cancel.setVisibility(View.VISIBLE);
            }

            //是否显示前往
            //是否显示取消


        }
    };
*/
    /**
     *
     * 点击事件重写
     */
    @Override
    public void onClick(View v) {
        /*
        InputMethodManager inputMethodManager=(InputMethodManager)search_title_edit.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
*/
        switch (v.getId()) {
            /*
            case R.id.index_title_edit:
                index_view.setVisibility(View.GONE);
                search_view.setVisibility(View.VISIBLE);
                search_title_edit.requestFocus();
                inputMethodManager.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
                break;
            case R.id.search_title_cancel:
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                index_view.setVisibility(View.VISIBLE);
                search_view.setVisibility(View.GONE);
                search_title_edit.clearFocus();
                break;
            case R.id.search_title_go:
                String goUrl = search_title_edit.getText().toString();
                if(goUrl.indexOf("http://")<0){
                    goUrl="http://"+goUrl;
                    search_title_edit.setText(goUrl);
                }
                search_title_cancel.callOnClick();
                index_webView.loadUrl(goUrl);
                break;
            case R.id.search_title_url_clear:
                search_title_edit.setText("");
                break;
            case R.id.index_title_refresh:
                index_webView.reload();
                break;
                */
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


                } else if (mCurPosY - mPosY < 0 && (Math.abs(mCurPosY - mPosY) > 25)) {
                    //向上滑动
                    mainActivity.bottom_bar_disappear();
                }
                break;
        }

        return false;
    }
}