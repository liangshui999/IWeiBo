package com.example.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.Util.ImageAsynLoader;
import com.example.Util.MyStringUtil;
import com.example.asus_cp.activity.PicOperateActivity;
import com.example.asus_cp.iweibo.R;
import com.sina.weibo.sdk.openapi.legacy.StatusesAPI;
import com.sina.weibo.sdk.openapi.models.Status;

import java.util.List;

/**
 * Created by asus-cp on 2016-03-25.
 */
public class WeiBoListAdapter extends BaseAdapter  {
    private String tag="WeiBoListAdapter";
    private Context context;
    private List<Status> statuses;
    private LayoutInflater inflater;
    private ListView listView;
    private ImageAsynLoader loader;
    private MyStringUtil myStringUtil;
    private StatusesAPI statusesAPI;
    public static final String URL_KEY_MYLISTENER="urlKeyMylistener";
    private int width;
    private int height;

    public WeiBoListAdapter(Context context, List<Status> statuses,StatusesAPI statusesAPI,int width,int height) {
        this.context = context;
        this.statuses = statuses;
        this.statusesAPI=statusesAPI;
        this.width=width;
        this.height=height;
        for(int i=0;i<statuses.size();i++){
            statuses.get(i).text=statuses.get(i).text+"........."+i;
            statuses.get(i).user.screen_name=statuses.get(i).user.screen_name+"....."+i;
            Log.d(tag,"名称="+statuses.get(i).user.screen_name+".........."+"内容="+statuses.get(i).text);
        }
        inflater = LayoutInflater.from(context);
        loader=new ImageAsynLoader(width,height);
        myStringUtil=new MyStringUtil(statusesAPI);

    }

    public WeiBoListAdapter() {

    }

    @Override
    public int getCount() {
        return statuses.size();
    }

    @Override
    public Object getItem(int position) {
        return statuses.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(listView==null){
            listView= (ListView) parent;
        }
        View v = convertView;
        ViewHolder viewHolder=null;
        if (v == null) {
            v = inflater.inflate(R.layout.item_home_listview_layout, null);
            viewHolder=new ViewHolder();
            viewHolder.imgListviewUserHead= (ImageView) v.findViewById(R.id.img_listview_user_head);
            viewHolder.textListviewUserName= (TextView) v.findViewById(R.id.text_listview_userName);
            viewHolder.imgListviewVCertification= (ImageView) v.findViewById(R.id.img_listview_v_certification);
            viewHolder.textReleaseTime= (TextView) v.findViewById(R.id.text_release_time);
            viewHolder.textFromWhere= (TextView) v.findViewById(R.id.text_from_where);
            viewHolder.textWeiboContent= (TextView) v.findViewById(R.id.text_weibo_content);
            viewHolder.imgContentPictrue= (ImageView) v.findViewById(R.id.img_content_pictrue);
            viewHolder.textZhuanfaContent= (TextView) v.findViewById(R.id.text_zhuanfa_content);
            viewHolder.imgZhuanfaPictrue= (ImageView) v.findViewById(R.id.img_zhuanfa_pictrue);
            v.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolder)v.getTag();
        }
        Status status=statuses.get(position);
        final String userHeadUrl=status.user.profile_image_url;
        ImageView userHeadImageView=viewHolder.imgListviewUserHead;
        userHeadImageView.setTag(userHeadUrl);
        userHeadImageView.setImageResource(R.mipmap.p1);//因为复用的问题，先设置一个默认值，把复用的给覆盖掉，免得是乱的
        Log.d(tag, "userHeadUrl=" + userHeadUrl + "............" + "position=" + position);
        Bitmap userHeadBitamp=loader.getBitmap(userHeadUrl,new ImageAsynLoader.ImageCallBak() {
            @Override
            public void refresh(Bitmap bitmap,String url) { //注意这里的url很重要，确保当初让我下载的url
                ImageView imageView= (ImageView) listView.findViewWithTag(url);
                if(imageView!=null && bitmap!=null){
                    imageView.setImageBitmap(bitmap);
                    Log.d(tag, "url=" + url + ".,,,,,,," + "position=" + position);
                }


            }
        });
        if(userHeadBitamp!=null){
           viewHolder.imgListviewUserHead.setImageBitmap(userHeadBitamp);
        }



        viewHolder.textListviewUserName.setText(status.user.screen_name);
        //判断是否是加v用户
        if(status.user.verified){
            viewHolder.imgListviewVCertification.setVisibility(View.VISIBLE);
            viewHolder.imgListviewVCertification.setImageResource(R.mipmap.v);
            viewHolder.textListviewUserName.setTextColor(context.getResources().getColor(R.color.userName));
        }else{
            viewHolder.imgListviewVCertification.setVisibility(View.GONE);
        }

        if(status.created_at!=null){
//            viewHolder.textReleaseTime.setText(MyDateUtil.getFormatString(MyDateUtil.transformStringToDate(status.created_at)));
        }

        //Log.d(tag, "发布时间=" + status.created_at);
        viewHolder.textFromWhere.setText(getSubString(status.source));

        SpannableString content=myStringUtil.setHuiZong(status.text);
        viewHolder.textWeiboContent.setText(content);//问题居然出在这句话上面
        //有配图
        String peiturl=status.thumbnail_pic;
        if(peiturl!=null && !peiturl.equals("")){ //注意所谓的不返回，其实返回的是""，
            viewHolder.imgContentPictrue.setVisibility(View.VISIBLE);
            viewHolder.imgContentPictrue.setOnClickListener(new MyOnclickListener(status.original_pic));
            viewHolder.imgContentPictrue.setTag(peiturl);
            viewHolder.imgContentPictrue.setImageResource(R.mipmap.ic_launcher);
            Bitmap zwptBitmap=loader.getBitmap(peiturl, new ImageAsynLoader.ImageCallBak() {
                @Override
                public void refresh(Bitmap bitmap,String url) {
                    ImageView imageView= (ImageView) listView.findViewWithTag(url);
                    if(imageView!=null&&bitmap!=null){
                        imageView.setImageBitmap(bitmap);
                    }

                }
            });
            if(zwptBitmap!=null){
                viewHolder.imgContentPictrue.setImageBitmap(zwptBitmap);
            }
        }else{
            viewHolder.imgContentPictrue.setVisibility(View.GONE);//这句话很重要，因为哥们复用的上面的，就算哥们没有图，上面的有，图也会显示出来的
        }

        //有转发微博
        Status zf=status.retweeted_status;
        if(zf!=null){
            viewHolder.textZhuanfaContent.setVisibility(View.VISIBLE);
            viewHolder.textZhuanfaContent.setText(zf.text);
            //转发有配图
            String zfUrl=zf.thumbnail_pic;
            if(zfUrl!=null && !zfUrl.equals("")){ //注意这个判断的后半部分
                viewHolder.imgZhuanfaPictrue.setVisibility(View.VISIBLE);
                viewHolder.imgZhuanfaPictrue.setOnClickListener(new MyOnclickListener(status.original_pic));
                viewHolder.imgZhuanfaPictrue.setTag(zfUrl);
                viewHolder.imgZhuanfaPictrue.setImageResource(R.mipmap.ic_launcher);
                Bitmap zfpeituBitmap=loader.getBitmap(zfUrl, new ImageAsynLoader.ImageCallBak() {
                    @Override
                    public void refresh(Bitmap bitmap, String url) {
                        ImageView imageView= (ImageView) listView.findViewWithTag(url);
                        if(imageView!=null && bitmap!=null){
                            imageView.setImageBitmap(bitmap);
                        }

                    }
                });
                if(zfpeituBitmap!=null){
                    viewHolder.imgZhuanfaPictrue.setImageBitmap(zfpeituBitmap);
                }
            }else{
                viewHolder.imgZhuanfaPictrue.setVisibility(View.GONE);//因为哥们是复用的上面的，有可能上面正好有图，哥们这儿也跟着有了
            }
        }else{
            //哥们复用的上面的，上面如果有的发，哥们这儿也复用了它的，但哥们这儿没有转发内容，所以哥们将转发内容隐藏
            viewHolder.textZhuanfaContent.setVisibility(View.GONE);
            viewHolder.imgZhuanfaPictrue.setVisibility(View.GONE);
        }
        return v;

    }

    /**
     * 分割字符串，求取需要的子字符串，主要用于微博来源的提取
     */
    public String getSubString(String s){
        int start=s.indexOf(">");
        int end=s.indexOf("</");
        String s1=s.substring(start+1,end);
        return  s1;
    }

     class ViewHolder {

        ImageView imgListviewUserHead;
        TextView textListviewUserName;
        ImageView imgListviewVCertification;
        TextView textReleaseTime;
        TextView textFromWhere;
         TextView textWeiboContent;
        ImageView imgContentPictrue;
        TextView textZhuanfaContent;
        ImageView imgZhuanfaPictrue;
         TextView textZhuanfa;
        TextView textComments;
        TextView textLike;
        ViewHolder() {

        }
    }

    /**
     * 自定义的监听器
     */
    class MyOnclickListener implements View.OnClickListener{

        private String url;
        MyOnclickListener(String url){
            this.url=url;
        }

        @Override
        public void onClick(View v) {
            Intent intent=new Intent(context, PicOperateActivity.class);
            intent.putExtra(URL_KEY_MYLISTENER,url);
            context.startActivity(intent);
        }
    }
}
