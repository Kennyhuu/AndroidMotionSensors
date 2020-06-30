package com.example.androidmotionsensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class Gyroscope extends SensorBase{

    Gyroscope(Context context) {
        super(context);
    }

    @Override
    Sensor setSensorType() {
        if(sensorManager!=null)
            return sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        else
            return null;
    }
}
