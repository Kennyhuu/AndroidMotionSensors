package Subcribe;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

public class Subscriber {

  public static void main(String[] args) throws MqttException {

    System.out.println("== START SUBSCRIBER ==");
    try {
      System.out.println("Your Ip-Adresse is :" + Inet4Address.getLocalHost().getHostAddress());
    } catch (UnknownHostException e) {
      e.printStackTrace();
    }

    MqttClient client=new MqttClient("tcp://192.168.178.108:1883", MqttClient.generateClientId());
    client.setCallback( new MqttCalback() );
    client.connect();

    client.subscribe("iot_data");

  }

}
