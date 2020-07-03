package server;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

class UserConnection implements MqttCallback{
	
	private DataObserver observer;
	private DataProcessor processor;
	private Server server;
	
	private TimerTask timerTaskNoMessage;
	private Timer timerNoMessage;
	
	protected UserConnection(DataObserver dataOb, DataProcessor dp, Server s){
		observer=dataOb;
		processor=dp;
		server=s;
		timerTaskNoMessage = new TimerTask(){
			@Override
			public void run() {
				server.noNewMessage();
			}
		};
		MqttClient client = null;
		try {
			String ipAdresse = Inet4Address.getLocalHost().getHostAddress();
			String portNr = "1883";
			String mqttAdresse = "tcp://"+ipAdresse+":"+portNr;
			client = new MqttClient(mqttAdresse, MqttClient.generateClientId());
			client.setCallback(this);
			client.connect();
			client.subscribe("phone/data");
			timerNoMessage=new Timer();
			timerNoMessage.schedule(timerTaskNoMessage, 1000 * 2);

		} catch (MqttException | UnknownHostException e) {
			e.printStackTrace();
		}
	}

	protected void start(){
		
	}

	@Override
	public void connectionLost(Throwable cause) {
		// TODO on conn lost notify Server->UI->connLost()
		// need a new function to alert user on lack of messages
		System.out.println("Connextion to MQTT Broker lost!");
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		//System.out.println("Message received:\n\t "+ topic + new String(message.getPayload()));
		
		ByteBuffer buffer = ByteBuffer.wrap(message.getPayload());
		MovementData data = new MovementData(buffer.getFloat(), buffer.getFloat(), buffer.getFloat(),
				buffer.getFloat(), buffer.getFloat(), buffer.getFloat());
		processor.calc(data);
		observer.newData(new MovementData(data));
		
		timerNoMessage.cancel();
		timerNoMessage=new Timer();
		timerNoMessage.schedule(timerTaskNoMessage, 1000 *2);
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {}
	
}
