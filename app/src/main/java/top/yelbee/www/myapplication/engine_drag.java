package top.yelbee.www.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * Created by Bob on 2018/3/5.
 */

public class engine_drag extends Activity{
    private EditText url1;
    private Button goer;
    private String URI="";
    private LinearLayout layout;
    private ImageView google;
    private ImageView baidu;

    //prefix传值
    public String prefix = "";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.drag);
        google = (ImageView)findViewById(R.id.google);
        baidu = (ImageView)findViewById(R.id.baidu);
        layout=(LinearLayout)findViewById(R.id.pop_layout);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "提示：点击窗口外部关闭窗口！",
                        Toast.LENGTH_SHORT).show();
            }
        });

        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Google is selected",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("prefix" , "http://www.google.com/search?hl=zh-CN&q=");
                bundle.putString("trans" , "google");
                intent.putExtras(bundle);
                setResult(2 , intent);
                finish();
;            }
        });

        baidu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"百度搜索已选择",Toast.LENGTH_SHORT).show();
                //prefix="http://www.baidu.com/s?wd=";
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("prefix" , "http://www.baidu.com/s?wd=");
                bundle.putString("trans" , "baidu");
                intent.putExtras(bundle);
                setResult(2 , intent);
                finish();
            }
        });
    }
    public boolean onTouchEvent(MotionEvent event){
        finish();
        return true;
    }



}
