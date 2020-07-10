package com.example.androidmotionsensors;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.ByteBuffer;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    public GraphView graphView;
    public LineGraphSeries<DataPoint> series;
    public TextView textView;
    public Button button;
    private boolean mqttstarted = false;
    private Accelerometer accelerometer;
    private Gyroscope gyroscope;
    public MqttAndroidClient client;
    final String serverUri = "tcp://192.168.0.241:1883";// Horva
//    final String serverUri = "tcp://192.168.178.108:1883";// Khiem
    final String clientId = "ExampleAndroidClient";
    final String topic = "phone/data";
    private MovementData currentData;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        // Create Graph and add to URL -- removed for better computing
        //graphView= findViewById(R.id.graph);
        //series  = new LineGraphSeries<>();
        //series.appendData(new DataPoint(0,0),false,100);
        //graphView = createGraph(R.id.graph,series);

        //
        textView= findViewById(R.id.textView3);

        // Create button and add listener to URL
        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mqttstarted){
                    createMqttConnection();
                }
                else {
                    disconnectMqttConnection();
                }

            }
        });


        accelerometer = new Accelerometer(this);
        accelerometer.setListener(this);
        gyroscope = new Gyroscope(this);
        gyroscope.setListener(this);
        currentData =new MovementData();
    }

    protected void onResume() {
        super.onResume();
        accelerometer.register();
        gyroscope.register();
    }
    protected void onPause(){
        super.onPause();
        accelerometer.unregister();
        gyroscope.unregister();
    }

    public GraphView createGraph(int id,LineGraphSeries<DataPoint> series){
        GraphView graphView = findViewById(id);
        graphView.getViewport().setMaxY(5);
        graphView.getViewport().setMinX(0);
        graphView.getViewport().setMinY(-5);
        graphView.getViewport().setXAxisBoundsManual(false);
        graphView.getViewport().setYAxisBoundsManual(true);
        graphView.addSeries(series);
        return graphView;
    }

    private void createMqttConnection(){
        client = new MqttAndroidClient(this.getApplicationContext(), serverUri, clientId);
        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    textView.setText("Connected");
                    button.setEnabled(false);
                    // We are connected
                    mqttstarted=true;
                    new Timer().schedule(new TimerTask(){
                        @Override
                        public void run() {
                            doMqttPublish();
                        }},20L,200L);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    textView.setText("Not Connected");
                    button.setEnabled(true);
                    mqttstarted=false;
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void disconnectMqttConnection(){
        try {
            IMqttToken disconToken = client.disconnect();
            disconToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // we are now successfully disconnected
                    textView.setText("Disconnected");
                    mqttstarted=false;
                    button.setEnabled(true);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    // something went wrong, but probably we are disconnected anyway
                    textView.setText("Connected");
                    mqttstarted=true;
                    button.setEnabled(false);
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void doMqttPublish(){
        if(mqttstarted){
            try {
                ByteBuffer buffer = ByteBuffer.allocate(6*4);
                synchronized (currentData){
                    buffer.putFloat(currentData.accX).putFloat(currentData.accY).putFloat(currentData.accZ).putFloat(currentData.posX).putFloat(currentData.posY).putFloat(currentData.posZ);
                }
                MqttMessage message = new MqttMessage(buffer.array());
                client.publish(topic, message);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor.getType()==Sensor.TYPE_LINEAR_ACCELERATION){
            synchronized (currentData){
                currentData.accX = sensorEvent.values[0];
                currentData.accY = sensorEvent.values[1];
                currentData.accZ = sensorEvent.values[2];
            }
        }
        else if(sensorEvent.sensor.getType()==Sensor.TYPE_GYROSCOPE){
            synchronized (currentData) {
                currentData.posX = sensorEvent.values[0];
                currentData.posY = sensorEvent.values[1];
                currentData.posZ = sensorEvent.values[2];
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
