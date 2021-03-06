package com.example.modle;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 用户信息的实体类
 * Created by asus-cp on 2016-03-21.
 */
public class UserInfo implements Parcelable{
    private String userId;
    private String userName;
    private byte[] userImage;
    private String accessToken;
    private String refreshToken;
    private long authTime;//授权时间
    private long expiresIn;//授权的有效期

    public UserInfo(String userId, String userName, byte[] userImage,
                    String accessToken, String refreshToken, long authTime,
                    long expiresIn) {
        this.userId = userId;
        this.userName = userName;
        this.userImage = userImage;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.authTime = authTime;
        this.expiresIn = expiresIn;
    }

    public UserInfo() {

    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public byte[] getUserImage() {
        return userImage;
    }

    public void setUserImage(byte[] userImage) {
        this.userImage = userImage;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public long getAuthTime() {
        return authTime;
    }

    public void setAuthTime(long authTime) {
        this.authTime = authTime;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(userName);
        dest.writeByteArray(userImage);
        dest.writeString(accessToken);
        dest.writeString(refreshToken);
        dest.writeLong(authTime);
        dest.writeLong(expiresIn);
    }
    public static final Parcelable.Creator<UserInfo> CREATOR=new Creator<UserInfo>() {
        @Override
        public UserInfo createFromParcel(Parcel source) {
            UserInfo userInfo=new UserInfo();
            userInfo.userId=source.readString();
            userInfo.userName=source.readString();
            userInfo.userImage=source.createByteArray();//注意这里的用createByteArray（），不能用readByteArray
            userInfo.accessToken=source.readString();
            userInfo.refreshToken=source.readString();
            userInfo.authTime=source.readLong();
            userInfo.expiresIn=source.readLong();
            return userInfo;
        }

        @Override
        public UserInfo[] newArray(int size) {
            return new UserInfo[size];
        }
    };
}
