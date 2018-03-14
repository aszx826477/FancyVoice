package top.yelbee.www.myapplication;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hugo.weaving.DebugLog;
import top.yelbee.www.library.FilterMenu;
import top.yelbee.www.library.FilterMenuLayout;

public class Notebook extends Activity implements
        OnItemClickListener, OnItemLongClickListener {

    private ListView listview;
    private SimpleAdapter simple_adapter;
    private List<Map<String, Object>> dataList;

    private TextView tv_content;

    private NotebookDB DbHelper;
    private SQLiteDatabase DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notebook);

        FilterMenuLayout layout = (FilterMenuLayout) findViewById(R.id.filter_menu);
        attachMenu(layout);

        InitView();
    }

    private FilterMenu attachMenu(FilterMenuLayout layout){
        return new FilterMenu.Builder(this)
                .addItem(R.mipmap.ic_action_io)
                .addItem(R.mipmap.ic_action_add)
                .addItem(R.mipmap.ic_action_voice)
                .attach(layout)
                .withListener(listener)
                .build();
    }

    FilterMenu.OnMenuChangeListener listener = new FilterMenu.OnMenuChangeListener() {
        @DebugLog
        @Override
        public void onMenuItemClick(View view, int position) {
            switch (position) {
                case 0:
                    finish();
                    Intent intent1 = new Intent(Notebook.this, MainActivity.class);
                    startActivity(intent1);
                    break;
                case 1:
                    Intent intent = new Intent(Notebook.this, NotebookEdit.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("info", "");
                    bundle.putInt("enter_state", 0);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    break;


            }
        }

        @Override
        public void onMenuCollapse() {

        }

        @Override
        public void onMenuExpand() {

        }



    };
        //在activity显示的时候更新listview
    @Override
    protected void onStart() {
        super.onStart();
        RefreshNotesList();
    }


    private void InitView() {
        tv_content = (TextView) findViewById(R.id.tv_content);
        listview = (ListView) findViewById(R.id.listview);
        dataList = new ArrayList<Map<String, Object>>();
        DbHelper = new NotebookDB(this);
        DB = DbHelper.getReadableDatabase();

        listview.setOnItemClickListener(this);
        listview.setOnItemLongClickListener(this);

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
        startManagingCursor(cursor);
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex("title"));
            String date = cursor.getString(cursor.getColumnIndex("date"));
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("tv_content", name);
            map.put("tv_date", date);
            dataList.add(map);
        }
        simple_adapter = new SimpleAdapter(this, dataList, R.layout.notebook_item,
                new String[]{"tv_content", "tv_date"}, new int[]{
                R.id.tv_content, R.id.tv_date});
        listview.setAdapter(simple_adapter);
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
        startManagingCursor(cursor);
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex("title"));
            if(name.equals(title)) {
                String content1 = cursor.getString(cursor.getColumnIndex("content"));
                Intent myIntent = new Intent(Notebook.this, NotebookEdit.class);
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
        Builder builder = new Builder(this);
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