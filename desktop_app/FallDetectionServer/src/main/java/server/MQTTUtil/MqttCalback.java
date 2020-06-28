package server.MQTTUtil;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import server.MovementData;

public class MqttCalback implements MqttCallback {

  @Override
  public void connectionLost(Throwable cause) {
    System.out.println("Connextion to MQTT Broker lost!");
  }

  @Override
  public void messageArrived(String topic, MqttMessage message) throws Exception {
    //System.out.println("Message received:\n\t "+ topic + new String(message.getPayload()));
    byte[] payload = message.getPayload();
    //System.out.println("Message received:\n\t"+ new String(message.getPayload()));
    System.out.println("Accel " +"  :  "+payload[0]+ "------"  + payload[1]+ "-------"+ payload[2]);
    System.out.println("Gyros " +"  :  "+payload[3]+ "------"  + payload[4]+ "-------"+ payload[5]);
  }

  @Override
  public void deliveryComplete(IMqttDeliveryToken token) {

  }
}
