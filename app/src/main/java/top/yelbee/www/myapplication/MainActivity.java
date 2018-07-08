package top.yelbee.www.myapplication;


import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import top.yelbee.www.myapplication.Controller.MPagerAdapter;
import top.yelbee.www.myapplication.View.NoScrollViewPager;


public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener{

    public static SharedPreferences preferences;
    private NoScrollViewPager mViewPager;
    public MPagerAdapter mPagerAdapter;
    public BottomNavigationView navigation;
    public LinearLayout index_bottom_bar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        set_statusbar_visible();
        setContentView(R.layout.activity_main);
        requestPermission();
        init();
        shared_preferences_operate();
    }

    private void requestPermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO},1);
        }else {

        }
    }

    public void shared_preferences_operate() {
        //采用SharedPreferences存储程序的打开次数
        preferences = getSharedPreferences("count",0);
        int count = preferences.getInt("count", 0);


        //判断程序与第几次运行，如果是第一次运行则跳转到引导页面
        if (count == 0) {
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(),FirstWelcomeActivity.class);
            startActivity(intent);
            this.finish();
        }
        SharedPreferences.Editor editor = preferences.edit();
        //存入数据
        editor.putInt("count", ++count);
        //提交修改
        editor.commit();
    }



    //初始化方法，包括实例化，以及设置底部导航栏的监听器
    public void init() {
        mViewPager = (NoScrollViewPager) findViewById(R.id.viewpage_container);
        mPagerAdapter = new MPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setCurrentItem(0);
        mViewPager.addOnPageChangeListener(this);
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        //index_bottom_bar = (LinearLayout) findViewById(R.id.index_bottom_bar);


        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_browser:
                        mViewPager.setCurrentItem(0);
                        return true;
                    case R.id.navigation_notebook:
                        mViewPager.setCurrentItem(1);
                        return true;
                    case R.id.navigation_about:
                        mViewPager.setCurrentItem(2);
                        return true;
                }
                return false;
            }
        });
        navigation.setItemIconTintList(null);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if(state==2){
            switch(mViewPager.getCurrentItem()){
                case 0:
                    navigation.setSelectedItemId(R.id.navigation_browser);
                    break;
                case 1:
                    navigation.setSelectedItemId(R.id.navigation_notebook);
                    break;
                case 2:
                    navigation.setSelectedItemId(R.id.navigation_about);
                    break;
            }
        }
    }

    //底部导航栏消失
    public boolean bottom_bar_disappear() {
        //navigation.setVisibility(View.GONE);
        // 初始化需要加载的动画资源
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.drag_down);

        // 将TextView执行Animation动画
        navigation.startAnimation(animation);
        navigation.setVisibility(View.GONE);
        return false;
    }
    //底部导航栏出现
    public boolean bottom_bar_appear() {
        //navigation.setVisibility(View.VISIBLE);
        // 初始化需要加载的动画资源
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.drag_up);

        // 将TextView执行Animation动画
        navigation.startAnimation(animation);
        navigation.setVisibility(View.VISIBLE);
        return true;
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
