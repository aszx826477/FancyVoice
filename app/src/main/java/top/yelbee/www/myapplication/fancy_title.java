package top.yelbee.www.myapplication;

import android.app.Activity;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class fancy_title extends Activity {

    private ImageView iv;
    private TextView text;
    private ImageView tick;
    private ImageView explore_icon;
    private AnimatedVectorDrawable searchToBar;
    private AnimatedVectorDrawable barToSearch;
    private float offset;
    private Interpolator interp;
    private int duration;
    private boolean expanded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fancy_switch);
        iv = (ImageView) findViewById(R.id.search);
        text = (TextView) findViewById(R.id.text);
        tick = (ImageView) findViewById(R.id.tick);
        explore_icon = (ImageView) findViewById(R.id.explore_icon);
        searchToBar = (AnimatedVectorDrawable) getResources().getDrawable(R.drawable.anim_search_to_bar);
        barToSearch = (AnimatedVectorDrawable) getResources().getDrawable(R.drawable.anim_bar_to_search);
        interp = AnimationUtils.loadInterpolator(this, android.R.interpolator.linear_out_slow_in);
        duration = getResources().getInteger(R.integer.duration_bar);
        // iv is sized to hold the search+bar so when only showing the search icon, translate the
        // whole view left by half the difference to keep it centered
        //offset = -71f * (int) getResources().getDisplayMetrics().scaledDensity;
        //iv.setTranslationX(offset);

        //clear层覆盖
        tick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text.setText("");
            }
        });

        //explore层覆盖
        explore_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"explore!",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void animate() {

        if (!expanded) {
            iv.setImageDrawable(searchToBar);
            searchToBar.start();
            //iv.animate().translationX(0f).setDuration(duration).setInterpolator(interp);
            text.animate().alpha(1f).setStartDelay(duration - 100).setDuration(100).setInterpolator(interp);
            //tick.animate().alpha(1f).setStartDelay(duration - 150).setDuration(100).setInterpolator(interp);
        } else {
            iv.setImageDrawable(barToSearch);
            barToSearch.start();
            //iv.animate().translationX(offset).setDuration(duration).setInterpolator(interp);
            text.setAlpha(0f);
        }
        expanded = !expanded;
    }
}
