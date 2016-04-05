package com.example.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.Util.FileUtil;
import com.example.asus_cp.iweibo.R;
import com.example.modle.UserInfo;

import java.util.List;

/**
 * 用户选择下拉列表的adapter
 * Created by asus-cp on 2016-03-22.
 */
public class UserSelectSpinnerAdapter extends BaseAdapter {
    private Context context;
    private List<UserInfo>userInfos;

    public UserSelectSpinnerAdapter(Context context, List<UserInfo> userInfos) {
        this.context = context;
        this.userInfos = userInfos;
    }

    public UserSelectSpinnerAdapter() {

    }

    @Override
    public int getCount() {
        return userInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return userInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder=null;
        ImageView userHeadImageView=null;
        TextView userNameTextView=null;
        if(convertView==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.spinner_user_select_item,null);
            //userHeadImageView= (ImageView) convertView.findViewById(R.id.img_spin_user_head);
            userNameTextView= (TextView) convertView.findViewById(R.id.text_spin_user_name);
            viewHolder=new ViewHolder();
            //viewHolder.imageView=userHeadImageView;
            viewHolder.textView=userNameTextView;
            convertView.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolder)convertView.getTag();
            //userHeadImageView=viewHolder.imageView;
            userNameTextView=viewHolder.textView;
        }
        Bitmap bitmap= FileUtil.getBitmapFromByteArray(userInfos.get(position).getUserImage());
        //userHeadImageView.setImageBitmap(bitmap);
        userNameTextView.setText(userInfos.get(position).getUserName());
        return convertView;
    }

    public class ViewHolder{
        //private ImageView imageView;
        private  TextView textView;

    }
}
