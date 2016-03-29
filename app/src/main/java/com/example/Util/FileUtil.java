package com.example.Util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * Created by asus-cp on 2016-03-21.
 */
public class FileUtil {
    private static String tag="FileUtil";
    /**
     * 把手机中的文件转换成bitmap
     */
    public static Bitmap getBitMapFromFile(File file){
        if(!file.exists()){
            return null;
        }
        try {
            return BitmapFactory.decodeFile(file.getCanonicalPath());
        } catch (IOException e) {
            Log.d(tag,e.toString());
        }
        return null;
    }
    /**
     * 把bitmap转换成字节数组
     */
    public static byte[] getByteArrayFromBitmap(Bitmap bitmap){
        ByteArrayOutputStream out=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,out);
        try {
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return out.toByteArray();
    }
    /**
     * 将字节数组转换成bitmap
     */
    public static Bitmap getBitmapFromByteArray(byte[] b){
        if(b!=null){
            return BitmapFactory.decodeByteArray(b,0,b.length);
        }
        return null;
    }
}
