package com.example.contants;

/**
 * Created by asus-cp on 2016-03-21.
 */
public interface DBConstant {

        public static String DB_NAME="weibo";
        public static int DB_VERSION=1;
        public interface  UserTable{
                public static String USER_TABLE="user";
                public static String USER_ID="userId";
                public static String USER_NAME="userName";
                public static String USER_IMAGE="userImage";
                public static String ACCESS_TOKEN="accessToken";
                public static String REFRESH_TOKEN="refreshToken";
                public static String AUTH_TIME="authTime";
                public static String EXPIREIN="expiresIn";
                public static String CREATE_TABLE_USER="create table if not exists user("
                        +"_id integer primary key autoincrement,"
                        +"userId text unique not null,"
                        +"userName text not null,"
                        +"userImage blob,"
                        +"accessToken text unique not null,"
                        +"refreshToken text not null,"
                        +"authTime text,"
                        +"expiresIn text"
                        +")";
                public static String UPDATE_USER="update user set userName=?,accessToken=?," +
                        "refreshToken=?,authTime=?,expiresIn=? where userId=?";
                public static String DELETE_USER="delete from user where userId=?";
                public static String QUERY_USER="select * from user where userId=?";
                public static String QUERY_USERS="select * from user";
        }


}
