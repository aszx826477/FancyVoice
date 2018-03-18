package top.yelbee.www.myapplication;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;


public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener{

    public static SharedPreferences preferences;
    private NoScrollViewPager mViewPager;
    public MPagerAdapter mPagerAdapter;
    public BottomNavigationView navigation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        shared_preferences_operate();
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
    public void bottom_bar_disappear() {
        navigation.setVisibility(View.GONE);
    }
    //底部导航栏出现
    public void bottom_bar_appear() {
        navigation.setVisibility(View.VISIBLE);
    }



}
