package Publish;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttPublish {

  MqttClient client;
  public MqttPublish(){
    try {
      client = new MqttClient("tcp://localhost:1883",MqttClient.generateClientId());
    } catch (MqttException e) {
      e.printStackTrace();
    }

  }
  public void doPublish(String messageString){
    System.out.println("== START PUBLISHER ==");
    try {
      //MqttClient client = new MqttClient("tcp://localhost:1883", MqttClient.generateClientId());
      client.connect();
      MqttMessage message = new MqttMessage();
      message.setPayload(messageString.getBytes());
      client.publish("iotdata", message);

      System.out.println("\tMessage '"+ messageString +"' to 'iot_data'");

      client.disconnect();

      System.out.println("== END PUBLISHER ==");
    } catch (MqttException e) {
      e.printStackTrace();
    }
  }
}
