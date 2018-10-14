package top.yelbee.www.myapplication.Controller;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import top.yelbee.www.myapplication.MFragmentBrowser;
import top.yelbee.www.myapplication.MFragmentMusic;
import top.yelbee.www.myapplication.MFragmentMe;

/**
 * Created by 雷华平 on 2017/7/29.
 */

public class MPagerAdapter extends FragmentPagerAdapter {

    //这里可以根据需要设置不同的fragment。
    //从而可以实现每个page有不同的界面

    private int count=3;
    private MFragmentBrowser f1;
    private MFragmentMusic f2;
    private MFragmentMe f3;

    public MPagerAdapter(FragmentManager fm) {
        super(fm);
        f1 = new MFragmentBrowser();
        f2 = new MFragmentMusic();
        f3 = new MFragmentMe();

    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment= null;
        switch (position){
            case 0:
                fragment=f1;
                break;
            case 1:
                //MFragmentBrowser.instance.onDestroy();
                fragment=f2;
                break;
            case 2:
                fragment=f3;
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return count;
    }
}
