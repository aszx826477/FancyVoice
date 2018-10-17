package top.yelbee.www.myapplication.Controller;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Administrator on 2018/10/15.
 */

public class AsrError {

    public void errorCodeHandle(Context context, int code) {
        switch (code) {
            case 0:
                Toast.makeText(context, "成功", Toast.LENGTH_SHORT).show();
                break;
            case 1:
                Toast.makeText(context, "网络超时", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                Toast.makeText(context, "其他网络错误", Toast.LENGTH_SHORT).show();
                break;
            case 3:
                Toast.makeText(context, "录音相关错误", Toast.LENGTH_SHORT).show();
                break;
            case 4:
                Toast.makeText(context, "服务器相关错诶", Toast.LENGTH_SHORT).show();
                break;
            case 5:
                Toast.makeText(context, "其他客户端错误", Toast.LENGTH_SHORT).show();
                break;
            case 6:
                Toast.makeText(context, "没有声音", Toast.LENGTH_SHORT).show();
                break;
            case 7:
                Toast.makeText(context, "未匹配到识别结果", Toast.LENGTH_SHORT).show();
                break;
            case 8:
                Toast.makeText(context, "识别引擎忙", Toast.LENGTH_SHORT).show();
                break;
            case 9:
                Toast.makeText(context, "客户端权限不足", Toast.LENGTH_SHORT).show();
                break;
            case 10:
                Toast.makeText(context, "入参错误", Toast.LENGTH_SHORT).show();
                break;
            case 11:
                Toast.makeText(context, "未知错误", Toast.LENGTH_SHORT).show();
                break;
            case 12:
                Toast.makeText(context, "服务端权限不足", Toast.LENGTH_SHORT).show();
                break;
            case 13:
                Toast.makeText(context, "引擎模型路径获取失败", Toast.LENGTH_SHORT).show();
                break;
            case 14:
                Toast.makeText(context, "请在设置中打开HiAI设置项", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(context, "无法识别错误码", Toast.LENGTH_SHORT).show();
                break;
        }

    }
}
