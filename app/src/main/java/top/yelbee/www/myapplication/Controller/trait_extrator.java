package top.yelbee.www.myapplication.Controller;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaExtractor;
import android.media.MediaFormat;

/**
 * Created by Bob on 2018/7/1.
 */

public class trait_extrator {
    private static AssetManager assetMg;
    private static MediaExtractor mex;

    //extract bit-trait
    public static int bit_trait(Context context, String audio_name) {
        assetMg = context.getAssets();
        mex = new MediaExtractor();
        try{
            AssetFileDescriptor fileDescriptor = assetMg.openFd(audio_name);
            mex.setDataSource(fileDescriptor);
        }catch(Exception e) {
            e.printStackTrace();
        }
        MediaFormat mf = mex.getTrackFormat(0);
        int bitRate = mf.getInteger(MediaFormat.KEY_BIT_RATE);

        return bitRate;
    }

    //extract frequency-trait
    public static int frequency_trait(Context context, String audio_name) {
        assetMg = context.getAssets();
        mex = new MediaExtractor();
        try{
            AssetFileDescriptor fileDescriptor = assetMg.openFd(audio_name);
            mex.setDataSource(fileDescriptor);
        }catch(Exception e) {
            e.printStackTrace();
        }
        MediaFormat mf = mex.getTrackFormat(0);
        int sampleRate = mf.getInteger(MediaFormat.KEY_SAMPLE_RATE);

        return sampleRate;
    }
}
