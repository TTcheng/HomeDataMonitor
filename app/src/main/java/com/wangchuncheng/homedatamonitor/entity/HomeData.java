package com.wangchuncheng.homedatamonitor.entity;

import java.io.Serializable;

public class HomeData implements Serializable {
    private String homeId;      //房间号    101 ~ 2012
    private double temperature; //温度    -40~80℃
    private double humidity;    //湿度    相对湿度0~100％RH
    private long pointtime;

    public static final double TEMPERATURE_RANGE = 120;//-40-80℃
    public static final double HUMIDITY_RANGE = 100;//0-100%RH

    public HomeData() {
    }

    public HomeData(String homeId, double temperature, double humidity, long pointtime) {
        this.homeId = homeId;
        this.temperature = temperature;
        this.humidity = humidity;
        this.pointtime = pointtime;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public long getPointtime() {
        return pointtime;
    }

    public void setPointtime(long pointtime) {
        this.pointtime = pointtime;
    }

    public String getHomeId() {
        return homeId;
    }

    public void setHomeId(String homeId) {
        this.homeId = homeId;
    }

    public double getTemperaturePercentage() {
        return (temperature / TEMPERATURE_RANGE)*100;
    }

    public double getHumidityPercentage() {
        return (humidity / HUMIDITY_RANGE)*100;
    }


    public static HomeData parseHomeData(String s) {
        String homeDataString = s.replace(" ", "");//去掉空格
        homeDataString = homeDataString.replace("{", " ");//将{换为空格
        homeDataString = homeDataString.replace("}", " ");//将}换为空格
        homeDataString = homeDataString.replace("'", "");//去掉单引号'
        String[] values = homeDataString.split(" ")[1].split(",");//先空格分隔，在逗号分隔，获得数据

        HomeData homeData = new HomeData();
        for (int i = 0; i < values.length; i++) {   //i==1,skip HomeData{
            switch (i) {
                case 0:
                    homeData.setHomeId(values[i].split("=")[1]);
                    break;
                case 1:
                    homeData.setTemperature(Double.parseDouble(values[i].split("=")[1]));
                    break;
                case 2:
                    homeData.setHumidity(Double.parseDouble(values[i].split("=")[1]));
                    break;
                case 3:
                    homeData.setPointtime(Long.parseLong(values[i].split("=")[1]));
                    break;
                default:
                    break;
            }
        }
        return homeData;
    }

    @Override
    public String toString() {
        return "HomeData{" +
                "homeId='" + homeId + '\'' +
                ", temperature=" + temperature +
                ", humidity=" + humidity +
                ", pointtime=" + pointtime +
                '}';
    }
}
