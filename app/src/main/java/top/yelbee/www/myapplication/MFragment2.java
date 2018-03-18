package top.yelbee.www.myapplication;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hugo.weaving.DebugLog;
import top.yelbee.www.library.FilterMenu;
import top.yelbee.www.library.FilterMenuLayout;

/**
 * 依附在MainActivity中的第二个fragment
 * 功能：实现语音备忘录
 */

public class MFragment2 extends Fragment implements
        AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    View view;
    FloatingActionButton fbutton;

    private ListView listview;
    private SimpleAdapter simple_adapter;
    private List<Map<String, Object>> dataList;

    private TextView tv_content;

    private NotebookDB DbHelper;
    private SQLiteDatabase DB;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment2, container, false);
        InitView();
        return view;
    }



    //在activity显示的时候更新listview
    @Override
    public void onStart() {
        super.onStart();
        RefreshNotesList();
    }

    //刷新listview
    public void RefreshNotesList() {
        //如果dataList已经有的内容，全部删掉
        //并且更新simp_adapter
        int size = dataList.size();
        if (size > 0) {
            dataList.removeAll(dataList);
            simple_adapter.notifyDataSetChanged();
        }
        //从数据库读取信息
        Cursor cursor = DB.query("note", null, null, null, null, null, null);
        getActivity().startManagingCursor(cursor);//不知道是不是这么改
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex("title"));
            String date = cursor.getString(cursor.getColumnIndex("date"));
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("tv_content", name);
            map.put("tv_date", date);
            dataList.add(map);
        }
        //不知道改成什么
        simple_adapter = new SimpleAdapter(getActivity(), dataList, R.layout.notebook_item,
                new String[]{"tv_content", "tv_date"}, new int[]{
                R.id.tv_content, R.id.tv_date});
        listview.setAdapter(simple_adapter);
    }

    private void InitView() {
        tv_content = (TextView) view.findViewById(R.id.tv_content);
        listview = (ListView) view.findViewById(R.id.listview);
        dataList = new ArrayList<Map<String, Object>>();
        DbHelper = new NotebookDB(getActivity());//不知道改成什么
        DB = DbHelper.getReadableDatabase();

        fbutton = (FloatingActionButton) view.findViewById(R.id.notebook_plus);
        fbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NotebookEdit.class);
                Bundle bundle = new Bundle();
                bundle.putString("info", "");
                bundle.putInt("enter_state", 0);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        listview.setOnItemClickListener(this);
        listview.setOnItemLongClickListener(this);

    }

    // 点击listview中某一项的点击监听事件
    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        //获取listview中此个item中的内容
        String content = listview.getItemAtPosition(arg2) + "";
        //获取title
        String title = content.substring(content.indexOf("=") + 1,
                content.indexOf(","));
        //根据title在SQLite中查找content
        Cursor cursor = DB.query("note", null, null, null, null, null, null);
        getActivity().startManagingCursor(cursor);//不知道是不是这么改
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex("title"));
            if(name.equals(title)) {
                String content1 = cursor.getString(cursor.getColumnIndex("content"));
                Intent myIntent = new Intent(getActivity(), NotebookEdit.class);//不知道改成什么
                Bundle bundle = new Bundle();
                bundle.putString("info_title", title);
                bundle.putString("info_content", content1);
                bundle.putInt("enter_state", 1);
                myIntent.putExtras(bundle);
                startActivity(myIntent);
                break;
            }

        }

    }

    // 点击listview中某一项长时间的点击事件
    @Override
    public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int arg2,
                                   long arg3) {
        Builder builder = new Builder(getActivity());//不知道改成什么
        builder.setTitle("删除该日志");
        builder.setMessage("确认删除吗？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //获取listview中此个item中的内容
                //删除该行后刷新listview的内容
                String content = listview.getItemAtPosition(arg2) + "";
                String title = content.substring(content.indexOf("=") + 1,
                        content.indexOf(","));

                DB.delete("note", "title = ?", new String[]{title});
                RefreshNotesList();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.create();
        builder.show();
        return true;
    }
}
