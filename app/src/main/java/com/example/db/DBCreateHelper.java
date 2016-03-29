package com.example.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.Util.MyApplication;
import com.example.contants.DBConstant;

/**
 * Created by asus-cp on 2016-03-21.
 */
public class DBCreateHelper extends SQLiteOpenHelper{
    public static DBCreateHelper createHelper=new DBCreateHelper(MyApplication.getContext(),DBConstant.DB_NAME,null,
            DBConstant.DB_VERSION);
    private DBCreateHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    public static DBCreateHelper getDBCreateHelper(){
        return createHelper;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DBConstant.UserTable.CREATE_TABLE_USER);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
