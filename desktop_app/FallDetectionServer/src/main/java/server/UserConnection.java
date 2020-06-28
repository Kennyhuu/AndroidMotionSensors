package server;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import server.MQTTUtil.MqttCalback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

public class UserConnection {
	
	private DataObserver observer;
	private DataProcessor processor;
	
	protected UserConnection(DataObserver dataOb, DataProcessor dp){
		observer=dataOb;
		processor=dp;
	}

	
	protected void start(){
		MqttCallback accelCallback = new MqttCalback();
		MqttClient accelMqtt = createMqttSubscriber("phone/Accelerometer",accelCallback);
		MqttCallback gyrosCallball = new MqttCalback();
		MqttClient gyrosMqtt = createMqttSubscriber("phone/Gyros",gyrosCallball);
	}

	public MqttClient createMqttSubscriber(String topic, MqttCallback callback){
		System.out.println("== START SUBSCRIBER ==");
		MqttClient client = null;
		try {
			String ipAdresse = Inet4Address.getLocalHost().getHostAddress();
			String portNr = "1883";
			String mqttAdresse = "tcp://"+ipAdresse+":"+portNr;
			System.out.println("Your Ip-Adresse is :" + mqttAdresse);
			client = new MqttClient(mqttAdresse, MqttClient.generateClientId());
			callback = new MqttCalback();
			client.setCallback( callback );
			client.connect();

			client.subscribe(topic);

		} catch (MqttException | UnknownHostException e) {
			e.printStackTrace();
		}
		return client;


	}
}
