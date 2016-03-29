package com.example.dao;

import com.example.modle.UserInfo;

import java.util.List;

/**
 * 管理用户信息的接口
 * Created by asus-cp on 2016-03-21.
 */
public interface IUserInfoService {
    public void insert(UserInfo userInfo);//向数据库中插入一条用户信息
    public void update(String userId,UserInfo userInfo);//根据userId更新用户信息
    public void delete(String userId);//根据删除一条用户信息
    public UserInfo query(String userId);//根据用户id查询一条用户信息
    public List<UserInfo> query();//查询所有的用户信息
}
