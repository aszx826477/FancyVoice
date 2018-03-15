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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_browser);

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
                String goUrl = search_title_edit.getText().toString();
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
                index_webView.loadUrl(goUrl);
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
    }

