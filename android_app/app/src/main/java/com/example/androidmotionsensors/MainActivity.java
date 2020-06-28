package com.example.androidmotionsensors;

import android.graphics.Color;
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
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    public TextView tv1;
    public TextView tv2;
    public GraphView graphView;
    public GraphView graphView2;
    public GraphView graphView3;
    public LineGraphSeries<DataPoint> series;
    public LineGraphSeries<DataPoint> series2;
    public LineGraphSeries<DataPoint> series3;
    public Button button;
    private boolean mqttstarted = false;
    private Accelerometer accelerometer;
    private Gyroscope gyroscope;
    int counter=0;
    public MqttAndroidClient client;
    final String clientId = "ExampleAndroidClient";
    final String subscriptionTopic = "iot_data";
    private byte[] gyroValues = new byte[3];
    private byte[] accellValues = new byte[3];
    private byte[] sensorData = new byte[6];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

// Create Graph and add to URL
        //tv1 = (TextView) findViewById(R.id.AccelerometerTextView);
        //tv2 = (TextView) findViewById(R.id.GyroTextView);
        graphView= findViewById(R.id.graph);
        series  = new LineGraphSeries<>(getDataPoint());
        series2 = new LineGraphSeries<>(getDataPoint());
        series3 = new LineGraphSeries<>(getDataPoint());
        graphView = createGraph(R.id.graph,series,series2,series3);
        //graphView2 = createGraph(R.id.graph2,series2);


// Create button and add listener to URL
        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createMqttConnection();
            }
        });


        accelerometer = new Accelerometer(this);
        gyroscope = new Gyroscope(this);
        accelerometer.setListener(new Accelerometer.Listener() {
            @Override
            public void onTranslation(float tx, float ty, float tz) {
                //String text = " X :" + tx + " Y : " + ty + " Z :" + tz;
                //().getDecorView().setBackgroundColor(Color.RED);
                if(tx>1.0f || tx<-1.0f ||ty>1.0f || ty<-1.0f || tz>1.0f || tz<-1.0f) {
                    sensorData[0] = (byte) tx;
                    sensorData[1] = (byte) ty;
                    sensorData[2] = (byte) tz;
                    //doMqttPublish(text,"phone/Accelerometer");
                    doMqttPublishByte(sensorData, "phone");
                }
                series.appendData(new DataPoint(counter, tx), false, 100);
                series2.appendData(new DataPoint(counter, ty), false, 100);
                series3.appendData(new DataPoint(counter, tz), false, 100);
                counter++;

            }
        });

        gyroscope.setListener(new Gyroscope.Listener() {
            @Override
            public void onRotation(float rx, float ry, float rz) {
                //String text = " X : " + rx + " Y : " + ry + " Z : " + rz;
                if(rx>1.0f || rx<-1.0f ||ry>1.0f || ry<-1.0f || rz>1.0f || rz<-1.0f) {
                    sensorData[3] = (byte) rx;
                    sensorData[4] = (byte) ry;
                    sensorData[5] = (byte) rz;
                    doMqttPublishByte(sensorData, "phone");
                    //tv2.setText(text);
                }
            }
        });
    }

    private DataPoint[] getDataPoint() {
        return new DataPoint[]{
        new DataPoint(0,0)
        };
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

    public GraphView createGraph(int id, LineGraphSeries series, LineGraphSeries series2, LineGraphSeries<DataPoint> series3){
        GraphView graphView = findViewById(id);
        graphView.getViewport().setMaxY(5);
        graphView.getViewport().setMinX(0);
        graphView.getViewport().setMinY(-5);
        graphView.getViewport().setXAxisBoundsManual(false);
        graphView.getViewport().setYAxisBoundsManual(true);
        series.setColor(Color.BLUE);
        series2.setColor(Color.RED);
        series3.setColor(Color.GREEN);
        graphView.addSeries(series);
        graphView.addSeries(series2);
        graphView.addSeries(series3);
        return graphView;
    }
    public void createMqttConnection(){
        // Create MQTT Client
        String clientId = MqttClient.generateClientId();
        client =
                new MqttAndroidClient(this.getApplicationContext(), "tcp://192.168.178.108:1883",
                        clientId);

        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    setMqttstarted(true);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    setMqttstarted(false);
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
// if you want to publish as a string
    public void doMqttPublish(String payload,String topic){
        if(isMqttstarted() == true){
            //String topic = "phone/Gyros";
            //String payload = "the payload";
            byte[] encodedPayload;
            try {
                encodedPayload = payload.getBytes("UTF-8");
                MqttMessage message = new MqttMessage(encodedPayload);
                client.publish(topic, message);
            } catch (UnsupportedEncodingException | MqttException e) {
                e.printStackTrace();
            }
        }
    }

    public void doMqttPublishByte(byte[] payload,String topic){
        if(isMqttstarted() == true){
            //String topic = "phone/Gyros";
            //String payload = "the payload";
            try {
                //encodedPayload = payload.getBytes("UTF-8");
                MqttMessage message = new MqttMessage(payload);
                client.publish(topic, message);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isMqttstarted() {
        return mqttstarted;
    }

    public void setMqttstarted(boolean mqttstarted) {
        this.mqttstarted = mqttstarted;
    }
}
