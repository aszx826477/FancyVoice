package top.yelbee.www.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Bob on 2018/3/5.
 */

public class url_testj extends AppCompatActivity {
    private EditText url1;
    private Button goer;
    private String URI="";
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.url_test);
        url1 = (EditText)findViewById(R.id.url1);
        goer = (Button)findViewById(R.id.goer);
        goer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = url1.getText().toString();
                String URL = "http://www.baidu.com/s?wd=";//URL是根据使用百度搜索某个关键字得到的url截取得到的
                //URI = URI.parse(URL + content);
                //Intent intent = new Intent();
                //intent.setData(URI);
            }
        });
    }
}
