package top.yelbee.www.myapplication;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by 雷华平 on 2017/7/29.
 */

public class MPagerAdapter extends FragmentPagerAdapter {

    //这里可以根据需要设置不同的fragment。
    //从而可以实现每个page有不同的界面

    private int count=3;
    private MFragment1 f1;
    private MFragment2 f2;
    private MFragment3 f3;

    public MPagerAdapter(FragmentManager fm) {
        super(fm);
        f1 = new MFragment1();
        f2 = new MFragment2();
        f3 = new MFragment3();

    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment= null;
        switch (position){
            case 0:
                fragment=f1;
                break;
            case 1:
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
