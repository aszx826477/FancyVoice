package top.yelbee.www.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * 初次安装软件时的引导界面
 */

public class FirstWelcomeActivity extends Activity {
    private CustomVideoView videoview;
    private TextView clickin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        set_statusbar_visible();
        setContentView(R.layout.activity_welcome_first);

        clickin = (TextView) findViewById(R.id.clickin);
        clickin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FirstWelcomeActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        videoview = (CustomVideoView) findViewById(R.id.videoview);
        videoview.setVideoURI(Uri.parse("android.resource://"+getPackageName()+"/"+ R.raw.milkyway));

        //播放
        videoview.start();
        //循环播放
        videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                videoview.start();
            }
        });
        //设置slogan字体
        set_slogan_font();

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

    //设置slogan的字体
    public void set_slogan_font() {
        TextView slogan1 = (TextView)findViewById(R.id.slogan1);
        TextView slogan2 = (TextView)findViewById(R.id.slogan2);
        AssetManager assets = getAssets();
        Typeface fromAsset = Typeface.createFromAsset(assets, "fff.ttf");
        slogan1.setTypeface(fromAsset);
        slogan2.setTypeface(fromAsset);
    }


}
