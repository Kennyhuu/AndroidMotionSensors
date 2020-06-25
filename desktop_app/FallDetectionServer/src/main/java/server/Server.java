package server;

public class Server {
	private UserInterface userinterface;
	private EmergencyService emergencyservice;
	private UserConnection userconnection;
	
	public Server(UserInterface ui, EmergencyService es, DataObserver dataOb){
		userinterface=ui;
		emergencyservice=es;
		userconnection = new UserConnection(dataOb, new DataProcessor(this));
		start();
	}

	public void start(){
		userconnection.start();
	}
	
	protected void emergency(){
		boolean userOK = userinterface.checkUser();
		if(userOK) return;
		emergencyservice.callHelp();
	}
}
