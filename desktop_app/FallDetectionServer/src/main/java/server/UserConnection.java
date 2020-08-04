package server;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

class UserConnection implements MqttCallbackExtended{
	
	private DataObserver observer;
	private DataProcessor processor;
	private Server server;
	private MqttClient client;
	
	private boolean noMessage;
	private TimerTask timerTaskNoMessage;
	private Timer timerNoMessage;
	private final static Logger LOGGER = Logger.getLogger(UserConnection.class.getName());

  private boolean alreadyExecuted =false;


	private MovementData newData;

	protected UserConnection(DataObserver dataOb, DataProcessor dp, Server s){
		observer=dataOb;
		processor=dp;
		server=s;
		timerTaskNoMessage = new TimerTask(){
			@Override
			public void run() {
				if(client.isConnected() && noMessage){
          LOGGER.info("No-message thread executed.");
					processor.resetData();
					server.noNewMessage();
				}
				noMessage=true;
			}
		};
		noMessage=true;
		timerNoMessage=new Timer();
		client = null;
		try {
			String ipAdresse = Inet4Address.getLocalHost().getHostAddress();
			String portNr = "1883";
			String mqttAdresse = "tcp://"+ipAdresse+":"+portNr;
			client = new MqttClient(mqttAdresse, MqttClient.generateClientId(),new MemoryPersistence());
			client.setCallback(this);
			MqttConnectOptions conOpt = new MqttConnectOptions();
	        conOpt.setCleanSession(true);
	        conOpt.setAutomaticReconnect(true);
	        conOpt.setConnectionTimeout(60);
	        conOpt.setPassword(new String("sensor").toCharArray());
	        conOpt.setUserName("phone");
			client.connect(conOpt);
		  recordData();
			timerNoMessage.schedule(timerTaskNoMessage, 1000 *10, 1000 * 10);
		} catch (MqttException | UnknownHostException e) {
			LOGGER.warning(e.toString());
		}
	}

	protected void start(){
		
	}

	@Override
	public void connectionLost(Throwable cause) {
		//server.conenctionLost();
		LOGGER.info("Connection lost");
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		ByteBuffer buffer = ByteBuffer.wrap(message.getPayload());
		MovementData data = new MovementData(buffer.getFloat(), buffer.getFloat(), buffer.getFloat(),
				buffer.getFloat(), buffer.getFloat(), buffer.getFloat());
		newData = data;
		processor.calc(data);
		observer.newData(new MovementData(data));
		noMessage=false;
	}

	private void recordData(){
		if(!alreadyExecuted){
			ScheduledExecutorService executorService = Executors.newScheduledThreadPool(10);
			executorService.scheduleAtFixedRate(new Runnable() {
				@Override
				public void run() {
					if (server.isAlerted()){
						server.recordDataIntoCsv(newData, "Fall Detected wait for User Answer");
					}else{
						server.recordDataIntoCsv(newData, "----");
					}
				}
			}, 5, 200, TimeUnit.MILLISECONDS);
			alreadyExecuted = true;
		}
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {}

	@Override
	public void connectComplete(boolean reconnect, String serverURI) {
		LOGGER.info("Connection established");
		try {
			client.subscribe("phone/data");
		} catch (MqttException e) {
			System.out.println(e.toString());
		}
	}
}