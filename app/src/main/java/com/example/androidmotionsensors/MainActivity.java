package com.example.androidmotionsensors;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class MainActivity extends AppCompatActivity {

    public TextView tv1;
    public TextView tv2;
    public GraphView graphView;
    public GraphView graphView2;
    public GraphView graphView3;
    public LineGraphSeries<DataPoint> series;
    public LineGraphSeries<DataPoint> series2;
    public LineGraphSeries<DataPoint> series3;
    private Accelerometer accelerometer;
    private Gyroscope gyroscope;
    int counter=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //tv1 = (TextView) findViewById(R.id.AccelerometerTextView);
        //tv2 = (TextView) findViewById(R.id.GyroTextView);
        graphView=(GraphView) findViewById(R.id.graph);
        series  = new LineGraphSeries<>(getDataPoint());
        series2 = new LineGraphSeries<>(getDataPoint());
        //series3 = new LineGraphSeries<>(getDataPoint());

        graphView = createGraph(R.id.graph,series);
        graphView2 = createGraph(R.id.graph2,series2);
        //graphView3 = createGraph(R.id.graph3,series3);



        accelerometer = new Accelerometer(this);
        gyroscope = new Gyroscope(this);
        accelerometer.setListener(new Accelerometer.Listener() {
            @Override
            public void onTranslation(float tx, float ty, float tz) {
                String text = "Accelerometer X :" + String.valueOf(tx) + " Y : " + String.valueOf(ty) + " Z :" + String.valueOf(tz);
                if(tx>1.0f){
                    getWindow().getDecorView().setBackgroundColor(Color.RED);

                }else if(tx< -1.0f){
                    getWindow().getDecorView().setBackgroundColor(Color.BLUE);
                }
                //tv1.setText(text);
                series.appendData(new DataPoint(counter,tx),false,100);
                series2.appendData(new DataPoint(counter,ty),false,100);
                //series3.appendData(new DataPoint(counter,tz),false,100);
                counter++;

            }
        });

        gyroscope.setListener(new Gyroscope.Listener() {
            @Override
            public void onRotation(float rx, float ry, float rz) {
                String text = "Gyroscope X : " + String.valueOf(rx) + " Y : " + String.valueOf(ry) + " Z : " + String.valueOf(rz);
                if(rz > 1.0f){
                    getWindow().getDecorView().setBackgroundColor(Color.GREEN);
                }else if(rz < -1.0f){
                    getWindow().getDecorView().setBackgroundColor(Color.YELLOW);
                }
                //tv2.setText(text);

            }
        });
    }

    private DataPoint[] getDataPoint() {
        DataPoint[] dp = new DataPoint[]{
        new DataPoint(0,0)
        };
        return dp;
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
        GraphView graphView = (GraphView) findViewById(id);
        graphView.getViewport().setMaxY(5);
        graphView.getViewport().setMinX(0);
        graphView.getViewport().setMinY(-5);
        graphView.getViewport().setXAxisBoundsManual(false);
        graphView.getViewport().setYAxisBoundsManual(true);
        graphView.addSeries(series);
        return graphView;
    }

}
