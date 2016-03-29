package com.example.Util;

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by asus-cp on 2016-03-22.
 */
public class HttpDownLoader {
    private static String tag="HttpDownLoader";
    public static String GET="GET";
    public static void download(URL url, File file){
        FileOutputStream out = null;
        InputStream in = null;
        HttpURLConnection conn = null;
        try {
            conn =(HttpURLConnection)url.openConnection();
            Log.d(tag, conn.toString());
            conn.setRequestMethod(GET);
            conn.setConnectTimeout(10 * 000);
            in = conn.getInputStream();
            out =new FileOutputStream(file);
            Log.d(tag, in.toString());
            Log.d(tag,"执行到这儿了吗");
            int len=0;
            byte[] buf=new byte[1024];
            while((len= in.read(buf))!=-1){
                out.write(buf, 0, len);
                Log.d(tag, "循环中"+len);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            conn.disconnect();
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
