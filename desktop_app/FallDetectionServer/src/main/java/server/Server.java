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
		new UserConnection(dataOb, new DataProcessor(this));
	}
	
	protected void emergency(){
		if(!alerted){
			boolean userOK = userinterface.checkUser();
			if(userOK)
				return;
			emergencyservice.callHelp();
			alerted=false;
		}
		
	}
}
