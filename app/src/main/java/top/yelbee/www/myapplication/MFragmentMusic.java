package top.yelbee.www.myapplication;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



/**
 * 依附在MainActivity中的第二个fragment
 * 功能：实现歌词识别器
 */

public class MFragmentMusic extends Fragment implements View.OnClickListener{
    View view;

    CardView music_select1;
    CardView music_select2;
    CardView music_select3;
    CardView music_select4;
    CardView music_select5;
    CardView music_select6;
    CardView music_select7;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.music_fragment, container, false);
        InitView();
        return view;
    }



    private void InitView() {
       music_select1 = (CardView) view.findViewById(R.id.music_select1);
       music_select2 = (CardView) view.findViewById(R.id.music_select2);
       music_select3 = (CardView) view.findViewById(R.id.music_select3);
       music_select4 = (CardView) view.findViewById(R.id.music_select4);
       music_select5 = (CardView) view.findViewById(R.id.music_select5);
       music_select6 = (CardView) view.findViewById(R.id.music_select6);
       music_select7 = (CardView) view.findViewById(R.id.music_select7);


       music_select1.setOnClickListener(this);
       music_select2.setOnClickListener(this);
       music_select3.setOnClickListener(this);
       music_select4.setOnClickListener(this);
       music_select5.setOnClickListener(this);
       music_select6.setOnClickListener(this);
       music_select7.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getContext(), MusicPlay.class);
        Bundle bundle = new Bundle();
        switch(v.getId()) {
            case R.id.music_select1:
                bundle.putString("music_name","music_moonlight.wav");
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.music_select2:
                bundle.putString("music_name","music_redbean.wav");
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.music_select3:
                bundle.putString("music_name","music_sing_guitar.wav");
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.music_select4:
                bundle.putString("music_name","sing_for_test.wav");
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.music_select5:
                bundle.putString("music_name", "sing_onlyone.wav");
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.music_select6:
                bundle.putString("music_name","talking_english.wav");
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.music_select7:
                bundle.putString("music_name","music_sing_guitar.wav");
                intent.putExtras(bundle);
                startActivity(intent);
                break;
        }
    }


}
