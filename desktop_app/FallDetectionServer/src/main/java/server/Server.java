package server;

public class Server {
	private UserInterface userinterface;
	private EmergencyService emergencyservice;
	private boolean alerted;
	//private UserConnection userconnection;
	
	public Server(UserInterface ui, EmergencyService es, DataObserver dataOb){
		userinterface=ui;
		emergencyservice=es;
		alerted=false;
		//userconnection = new UserConnection(dataOb, new DataProcessor(this));
		//userconnection.start();
		new UserConnection(dataOb, new DataProcessor(this), this);
	}
	
	protected void emergency(){
		if(alerted) return;
		alerted=true;
		new Thread(){
			@Override
			public void run(){
				boolean userOK = userinterface.checkUser();
				if(userOK)
					return;
				emergencyservice.callHelp();
				alerted=false;
			}
		}.start();
	}
	
	protected void noNewMessage(){
		userinterface.noNewMessage();
	}

	protected void conenctionLost() {
		userinterface.connectionLost();
	}
}
