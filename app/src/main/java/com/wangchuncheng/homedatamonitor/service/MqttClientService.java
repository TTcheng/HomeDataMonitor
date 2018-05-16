package com.wangchuncheng.homedatamonitor.service;


import android.content.Context;
import android.os.Message;
import android.util.Log;

import com.wangchuncheng.homedatamonitor.entity.HomeData;
import com.wangchuncheng.homedatamonitor.utils.MqttMessageHandler;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.List;
import java.util.UUID;

public class MqttClientService {
    private final int qos = 1;
    private final String broker = "tcp://homedata.mqtt.iot.bj.baidubce.com:1883";
    private final String userName = "homedata/monitor";
    private final String password = "r2MFyMnO6OGn0uOj0KX5RJ9a/40LiY6VNPtOzXy7cjY=";
    private final String pubTopic = "req_homedata";
    private final String subTopic = "pub_homedata";
    private final String clientId = "monitor_mqtt_java_" + UUID.randomUUID().toString();

    private MqttAndroidClient mqttClient;
    private MqttConnectOptions connOpts;

    public MqttClientService(Context context) {
        connOpts = new MqttConnectOptions();
        connOpts.setUserName(userName);
        connOpts.setPassword(password.toCharArray());
        connOpts.setAutomaticReconnect(true);
        connOpts.setCleanSession(false);
        mqttClient = new MqttAndroidClient(context, broker, clientId, new MemoryPersistence(), MqttAndroidClient.Ack.AUTO_ACK);

        mqttClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                Log.d("MQTT service:", "连接成功");
            }

            MqttMessageHandler mHandler = MqttMessageHandler.getHandler();

            @Override
            public void connectionLost(Throwable cause) {
                Log.e("MQTT service:", "Connection lost!!!!!!!!!");
                Message msg = new Message();
                msg.what = 0;//error code
                msg.obj = "Connection lost，startting reconnect";
                mHandler.sendMessage(msg);
                connect();//重连
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                final String mqttmessage = new String(message.getPayload());
                System.out.println("MQTT message received: " + mqttmessage);
                HomeData homeData = HomeData.parseHomeData(mqttmessage);

                Message msg = new Message();
                msg.what = 1;
                msg.obj = homeData;
                mHandler.sendMessage(msg);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
        connect();
    }

    public void connect() {
        try {
            mqttClient.connect(connOpts, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(true);
                    disconnectedBufferOptions.setBufferSize(100);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(false);
                    mqttClient.setBufferOpts(disconnectedBufferOptions);
                    System.out.println("连接建立成功");
                    try {
                        System.out.println("Subscribe to topic :" + subTopic);
                        mqttClient.subscribe(subTopic, qos);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e("MQTT Service:", "Failed to connect to: " + broker);
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
            System.out.println("连接建立失败");
        }
    }

    private void pub(String msg, String topic) {
        MqttMessage message = new MqttMessage(msg.getBytes());
        message.setQos(qos);
        message.setRetained(false);
        try {
            mqttClient.publish(topic, message);
        } catch (MqttException e) {
            e.printStackTrace();
            System.out.println("发布失败！");
        }
    }

    public int publishRequestData(String homeID) {
        if (!isConnected()) {
            connect();
            System.out.println("mqtt client is null 开始重连！");
        }
        pub("request_" + homeID + "_20", pubTopic);//request_homeId_limit
        return 0;
    }

    public int publishHomeData(List<HomeData> homeDataList) {
        if (mqttClient != null) {

        } else {
            connect();
            System.out.println("mqtt client is null 开始重连！");
        }
        for (int i = 0; i < homeDataList.size(); i++) {
            pub(homeDataList.get(i).toString(), pubTopic);
        }
        return 0;
    }

    public boolean isConnected() {
        return mqttClient.isConnected();
    }
}
