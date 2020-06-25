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
    final String serverUri = "tcp://localhost:1883";
    final String clientId = "ExampleAndroidClient";
    final String subscriptionTopic = "iot_data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
// Create Graph and add to URL
        //tv1 = (TextView) findViewById(R.id.AccelerometerTextView);
        //tv2 = (TextView) findViewById(R.id.GyroTextView);
        graphView= findViewById(R.id.graph);
        series  = new LineGraphSeries<>(getDataPoint());
        //series2 = new LineGraphSeries<>(getDataPoint());
        //series3 = new LineGraphSeries<>(getDataPoint());


        graphView = createGraph(R.id.graph,series);
        //graphView2 = createGraph(R.id.graph2,series2);
        //graphView3 = createGraph(R.id.graph3,series3);


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
                String text = "Accelerometer X :" + tx + " Y : " + ty + " Z :" + tz;
                if(tx>1.0f){
                    getWindow().getDecorView().setBackgroundColor(Color.RED);
                    doMqttPublish(text);

                }else if(tx< -1.0f){
                    getWindow().getDecorView().setBackgroundColor(Color.BLUE);
                    doMqttPublish(text);
                }
                //tv1.setText(text);
                series.appendData(new DataPoint(counter,tx),false,100);
                //series2.appendData(new DataPoint(counter,ty),false,100);
                //series3.appendData(new DataPoint(counter,tz),false,100);
                counter++;

            }
        });

        gyroscope.setListener(new Gyroscope.Listener() {
            @Override
            public void onRotation(float rx, float ry, float rz) {
                String text = "Gyroscope X : " + rx + " Y : " + ry + " Z : " + rz;
                if(rz > 1.0f){
                    getWindow().getDecorView().setBackgroundColor(Color.GREEN);
                    doMqttPublish(text);
                }else if(rz < -1.0f){
                    getWindow().getDecorView().setBackgroundColor(Color.YELLOW);
                    doMqttPublish(text);
                }
                //tv2.setText(text);

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

    public GraphView createGraph(int id,LineGraphSeries series){
        GraphView graphView = findViewById(id);
        graphView.getViewport().setMaxY(5);
        graphView.getViewport().setMinX(0);
        graphView.getViewport().setMinY(-5);
        graphView.getViewport().setXAxisBoundsManual(false);
        graphView.getViewport().setYAxisBoundsManual(true);
        graphView.addSeries(series);
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

    public void doMqttPublish(String payload){
        if(isMqttstarted() == true){
            String topic = "phone/Gyros";
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

    public boolean isMqttstarted() {
        return mqttstarted;
    }

    public void setMqttstarted(boolean mqttstarted) {
        this.mqttstarted = mqttstarted;
    }
}
