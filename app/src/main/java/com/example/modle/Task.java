package com.example.modle;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by asus-cp on 2016-03-19.
 * 任务实体，包括任务id以及任务所包含的参数,这个要序列化，以方便传递
 */
public class Task implements Parcelable{
    private int taskId;
    private Bundle taskParams;
    public static final int DAYIN=1;
    public static final int SLEEP=2;
    public static final int GET_LOGIN_USER_INFO=3;//获取用户名和用户头像，并刷新ui，另外获取到的数据存储到数据库


    public Task(int taskId, Bundle taskParams) {
        this.taskId = taskId;
        this.taskParams = taskParams;
    }

    public Task() {

    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public Bundle getTaskParams() {
        return taskParams;
    }

    public void setTaskParams(Bundle taskParams) {
        this.taskParams = taskParams;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(taskId);
        dest.writeBundle(taskParams);
    }
    public static final Parcelable.Creator<Task> CREATOR=new Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel source) {
            Task task=new Task();
            task.taskId=source.readInt();
            task.taskParams=source.readBundle();
            return task;
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };
}
