package com.example.androidmotionsensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

abstract class SensorBase {
    SensorManager sensorManager;
    Sensor sensor;
    SensorEventListener sensorEventListener;

    SensorBase(Context context){
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        assert sensorManager != null;
        sensor=setSensorType();
        assert sensor != null;
        sensorEventListener=null;
    }

    abstract Sensor setSensorType();

    void register(){
        if(sensorManager!=null && sensor!=null && sensorEventListener!=null)
        sensorManager.registerListener(sensorEventListener, sensor,SensorManager.SENSOR_DELAY_NORMAL);
    }

    void unregister(){
        sensorManager.unregisterListener(sensorEventListener);
    }

    void setListener(SensorEventListener sel){
        sensorEventListener=sel;
    }

}
