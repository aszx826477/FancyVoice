package top.yelbee.www.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * 依附在MainActivity中的第三个fragment
 * 功能：语音小游戏、软件版本介绍
 */

public class MFragment3 extends Fragment implements View.OnClickListener {

    View view;
    TextView start_game;
    TextView start_about;
    Button exit_software;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment3,container,false);
        init();
        return view;
    }

    public void init() {
        start_game = (TextView) view.findViewById(R.id.start_game);
        start_about = (TextView) view.findViewById(R.id.start_about);
        exit_software = (Button) view.findViewById(R.id.exit_software);

        start_game.setOnClickListener(this);
        start_about.setOnClickListener(this);
        exit_software.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_game:
                Intent intent1 = new Intent(getActivity(), Game.class);
                startActivity(intent1);
                break;
            case R.id.start_about:
                Intent intent2 = new Intent(getActivity(), AboutSoftware.class);
                startActivity(intent2);
                break;
            case R.id.exit_software:
                getActivity().finish();
                break;

        }
    }

}
