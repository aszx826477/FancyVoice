package top.yelbee.www.myapplication.Controller;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import top.yelbee.www.myapplication.Controller.pcm2wav.pcm2wav;
import top.yelbee.www.myapplication.Controller.wav_util.SSRC;

public class UtilsMusic {
    private static String dirPath = Environment.getExternalStorageDirectory() + "/audio_test";
    private static String new_path = dirPath + "/wav_trans.wav";
    public static pcm2wav pcm2wav_util = new pcm2wav();
    private static int freq_temp;
    /**
     * FUNCTION_1:读取asset目录下文件,不进行SSRC变换。
     *
     * @return content
     */
    public static byte[] readAudioFile(Context context, String filename) {
        try {
            InputStream fis = context.getAssets().open(filename);
            //InputStream new_ins = new FileInputStream(SampleChangedFile);
            byte[] data = new byte[fis.available()];
            fis.read(data);
            fis.close();

            return data;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * FUNCTION_2:读取asset目录下音频文件，进行SSRC变换。
     * @return 二进制文件数据（未经处理的PCM编码）
     */

    public static byte[] newreadAudioFile(Context context, String in_file, String out_file){
        freq_temp = trait_extrator.frequency_trait(context,in_file);

        try{
            InputStream fis = context.getAssets().open(in_file);
            File file = new File(dirPath+"/"+out_file);
            File new_file = new File(new_path);
            if(!file.exists()){
                //先得到文件的上级目录，并创建上级目录，在创建文件
                file.getParentFile().mkdir();
                try {
                    //创建文件
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(!new_file.exists()){
                //先得到文件的上级目录，并创建上级目录，在创建文件
                new_file.getParentFile().mkdir();
                try {
                    //创建文件
                    new_file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            OutputStream fos = new FileOutputStream(file);

            /**
             * 此处重点比较以下两个方法的调用
             * 1.在构造方法中完成类中普通方法的作用时，可以直接通过new实例化为对象即可
             * 2.构造方法不能申明为静态static
             * 3.静态域static-field（方法）中不能调用一个非静态域non-satic field中的方法或变量,否则将肯能在调用剁成中被对象object改变为属性，违反static原则
             * 4.构造方法可以直接在方法中实例化 new
             * */
            new SSRC(fis,fos,freq_temp,16000,2,2,1,Integer.MAX_VALUE,0,0,true);
            //new pcm2wav(dirPath+"/"+out_file , new_path);
            fis.close();
            fos.close();

            //写入wav头文件
            /**此处一定要实例化，由于pcm2wav中的pcm2wav1方法没有this.mBufferSize
             * 将会导致 byte[] data = new byte[mBufferSize];产生void-array
             * 进而使(in.read(data)==-1)无法写入out.write(data);
             */

            pcm2wav_util.pcm2wav1(dirPath+"/"+out_file , new_path);

            InputStream fis_real = new FileInputStream(new_file);
            //data--the buffer into which the data is read
            byte[] data = new byte[fis_real.available()];
            fis_real.read(data);
            fis_real.close();

            return data;
        }catch (IOException e){
            e.printStackTrace();
        }

        return null;
    }

}
