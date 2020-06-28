package server;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

class UserConnection {
	
	private DataObserver observer;
	private DataProcessor processor;
	
	protected UserConnection(DataObserver dataOb, DataProcessor dp){
		observer=dataOb;
		processor=dp;
	}
}
