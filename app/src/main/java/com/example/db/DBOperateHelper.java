package com.example.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**这个类主要是当纯粹用是sql语句时，才有用
 * Created by asus-cp on 2016-03-21.
 */
public class DBOperateHelper {
    private String tag="DBOperateHelper";
    private DBCreateHelper createHelper;
    private DBOperateHelper(){
        createHelper=DBCreateHelper.getDBCreateHelper();
    }
    public static DBOperateHelper operateHelper=new DBOperateHelper();
    public static DBOperateHelper getDBOperateHelper(){
        return operateHelper;
    }
    /**
     * 增删改的通用方法
     */
    public void zsg(String sql, String[] params){
        SQLiteDatabase db=null;
        try{
            db=createHelper.getWritableDatabase();
            db.execSQL(sql,params);
        }catch (Exception e){
            Log.d(tag,e.toString());
        }finally {
            db.close();
        }
    }
    /**
     * 查询的通用方法
     */
    public Object query(String sql,String[]params,HandleCursor handleCursor){
        SQLiteDatabase db=null;
        Object object=null;
        try{
            db=createHelper.getWritableDatabase();
            Cursor cursor=db.rawQuery(sql, params);
            object=handleCursor.handleCursor(cursor);
        }catch (Exception e){
            Log.d(tag,e.toString());
        }finally {
            db.close();
        }
        return object;
    }
}
