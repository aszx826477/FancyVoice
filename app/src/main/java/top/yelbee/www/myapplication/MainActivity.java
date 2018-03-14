package top.yelbee.www.myapplication;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.media.MediaPlayer;

import top.yelbee.www.library.RoundSpinView;
import top.yelbee.www.library.RoundSpinView.onRoundSpinViewListener;

public class MainActivity extends Activity implements onRoundSpinViewListener{
    private CustomVideoView videoview;

    public RoundSpinView rsv_test;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rsv_test = (RoundSpinView)this.findViewById(R.id.rsv_test);
        rsv_test.setOnRoundSpinViewListener(this);


        videoview = (CustomVideoView) findViewById(R.id.videoview);
        videoview.setVideoURI(Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.milkyway));

        //播放
        videoview.start();
        //videoview.seekTO();
        //循环播放
        videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                videoview.start();
            }
        });

    }


    @Override
    public void onSingleTapUp(int position) {
        switch (position) {
            case 0:
                Intent intent0 = new Intent(MainActivity.this, Browser.class);
                startActivity(intent0);
                finish();
                break;
            case 1:
                Intent intent1 = new Intent(MainActivity.this, Game.class);
                startActivity(intent1);
                finish();
                break;
            case 2:
                //finish();
                Intent intent2 = new Intent(MainActivity.this, Notebook.class);
                startActivity(intent2);
                finish();
                break;
            case 3:
                break;
            case 4:
                Intent intent3 = new Intent(MainActivity.this, url_testj.class);
                startActivity(intent3);
                finish();
            default:
                break;
        }
    }



}
