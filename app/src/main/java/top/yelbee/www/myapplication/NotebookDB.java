package top.yelbee.www.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NotebookDB extends SQLiteOpenHelper {

    //SQLite建表语句
    public static final String CreateNote = "create table note ("
            + "id integer primary key autoincrement, "
            + "title text , "
            + "content text , "
            + "date text)";

    public NotebookDB(Context context) {
        super(context, "note", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CreateNote);
    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub

    }


}