package com.wangchuncheng.homedatamonitor.utils;

import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.wangchuncheng.homedatamonitor.MainActivity;
import com.wangchuncheng.homedatamonitor.entity.HomeData;

import java.lang.ref.WeakReference;

public class MqttMessageHandler extends Handler {
    private static MqttMessageHandler mHandler = new MqttMessageHandler();

    public static MqttMessageHandler getHandler() {
        return mHandler;
    }

    //对Activity的弱引用
    private WeakReference<MainActivity> mActivity;

    private MqttMessageHandler() {
    }

    public void initHandler(MainActivity activity) {
        mActivity = new WeakReference<MainActivity>(activity);
    }

    private final int HOMEDATA_ARRIVED = 1;
    private final int ERROR_CODE = 0;

    @Override
    public void handleMessage(Message msg) {
        MainActivity activity = mActivity.get();
        if (activity == null) {
            super.handleMessage(msg);
            return;
        }
        switch (msg.what) {
            case HOMEDATA_ARRIVED:
                activity.validate((HomeData) msg.obj);
                break;
            case ERROR_CODE:
                Toast.makeText(activity, (CharSequence) msg.obj, Toast.LENGTH_SHORT).show();
            default:
                super.handleMessage(msg);
                break;
        }
    }
}

