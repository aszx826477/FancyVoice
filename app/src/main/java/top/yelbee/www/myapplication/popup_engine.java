package top.yelbee.www.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * Created by Bob on 2018/4/29.
 */

public class popup_engine extends PopupWindow {
    private Context mContext;
    private View view;
    private ImageView baidu, google;

    public popup_engine(Context mContext) {
        //布局加载器将xml parse into View
        //Activit以外场景获取布局加载器的方式
        this.mContext = mContext;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.view = inflater.inflate(R.layout.engine_select, null);
        google = (ImageView)view.findViewById(R.id.google);
        baidu = (ImageView)view.findViewById(R.id.baidu);

        // 设置按钮监听
        //google.setOnClickListener(itemsOnClick);
        //baidu.setOnClickListener(itemsOnClick);
        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                baidu.setColorFilter(Color.GRAY);
                google.setColorFilter(null);
                //dismiss();
                alert("Google is selected!");
            }
        });

        baidu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                google.setColorFilter(Color.GRAY);
                baidu.setColorFilter(null);
                //dismiss();
                alert("baidu is selected");
            }
        });

        // 设置外部可点击 ,来自父类PopupWindow的引用构造方法
        this.setOutsideTouchable(true);

        // mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        this.view.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                int height = view.findViewById(R.id.pop_layout).getTop();

                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });

        // 设置视图
        this.setContentView(this.view);
        // 设置弹出窗体的宽和高
        this.setHeight(RelativeLayout.LayoutParams.MATCH_PARENT);
        this.setWidth(RelativeLayout.LayoutParams.MATCH_PARENT);

        // 设置弹出窗体可点击（获取焦点）
        this.setFocusable(true);

        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        // 设置弹出窗体的背景
        this.setBackgroundDrawable(dw);

        // 设置弹出窗体显示时的动画，从底部向上弹出
        this.setAnimationStyle(R.style.engine_anim);
    }
    public void alert(String str){
        Toast.makeText(mContext,str,Toast.LENGTH_SHORT).show();
    }
    }
