package com.example.androidmotionsensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

class Accelerometer extends SensorBase{

    Accelerometer(Context context){
        super(context);
    }

    @Override
    Sensor setSensorType() {
        if(sensorManager!=null)
            return sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        else
            return null;
    }

}
