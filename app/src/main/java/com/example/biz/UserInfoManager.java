package com.example.biz;

import com.example.dao.IUserInfoService;
import com.example.daoiml.UserInfoService;
import com.example.modle.UserInfo;

import java.util.List;

/**
 * 直接和ui进行交互的层
 * Created by asus-cp on 2016-03-22.
 */
public class UserInfoManager {
    private IUserInfoService userInfoService;//面向接口编程,userInfoService是接口的一种实现
    public UserInfoManager(){
        userInfoService=new UserInfoService();
    }
    /**
     * 添加用户信息
     */
    public void addUserInfo(UserInfo userInfo){
        userInfoService.insert(userInfo);
    }
    /**
     * 获取所有的用户信息
     */
    public List<UserInfo> getAllUserInfo(){
        return userInfoService.query();
    }
}
