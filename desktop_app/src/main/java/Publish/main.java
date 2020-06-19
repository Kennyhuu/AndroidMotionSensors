package Publish;

import Publish.MqttPublish;

public class main {

  //MqttClient client;
  public static void main(String[] args) {
    MqttPublish mqttPublish = new MqttPublish();
    mqttPublish.doPublish("Hit other form for publish");
    }

}
