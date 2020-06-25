package server.MQTTUtil;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttCalback implements MqttCallback {

  @Override
  public void connectionLost(Throwable cause) {
    System.out.println("Connextion to MQTT Broker lost!");
  }

  @Override
  public void messageArrived(String topic, MqttMessage message) throws Exception {
    System.out.println("Message received:\n\t "+ topic + new String(message.getPayload()));
  }

  @Override
  public void deliveryComplete(IMqttDeliveryToken token) {

  }
}
