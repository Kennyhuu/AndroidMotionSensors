package Subcribe;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

public class Subscriber {

  public static void main(String[] args) throws MqttException {

    System.out.println("== START SUBSCRIBER ==");

    MqttClient client=new MqttClient("tcp://192.168.178.108:1883", MqttClient.generateClientId());
    client.setCallback( new MqttCalback() );
    client.connect();

    client.subscribe("iot_data");

  }

}
