package server;

import com.opencsv.CSVWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;
//import sun.rmi.runtime.Log;

public class Server {
	private final static Logger LOGGER = Logger.getLogger(Server.class.getName());
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
				if(!userOK){
				  LOGGER.info("User need Help");
          emergencyservice.callHelp();
        }
				alerted=false;
			}
		}.start();
	}

  protected void emergency(MovementData data){
    if(alerted) return;
    alerted=true;
    new Thread(){
      @Override
      public void run(){
				LOGGER.info("User did fall down :" + data.accX + " " +data.accY +" "+data.accZ+" "+data.posX+" "+data.posY+"  "+data.posZ);
        boolean userOK = userinterface.checkUser();
        if(!userOK){
          LOGGER.info("User need Help");
          emergencyservice.callHelp();
        }
        alerted=false;
        recordDataIntoCsv(data,userOK);
			}
    }.start();
  }

  protected void recordDataIntoCsv(MovementData data, boolean userOK){
		File file = new File("src\\main\\java\\resource\\data.csv");
		String absolutePath = file.getAbsolutePath();
		LOGGER.info("Path : "+ absolutePath);
		try {
			CSVWriter csvWriter = new CSVWriter(new FileWriter(absolutePath,true));
			//DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
			LocalDateTime now = LocalDateTime.now();
			String accX = String.valueOf(data.accX);
			String accY = String.valueOf(data.accY);
			String accZ = String.valueOf(data.accZ);
			String posX = String.valueOf(data.posX);
			String posY = String.valueOf(data.posY);
			String posZ = String.valueOf(data.posZ);
			String stringBuilder = now+","+accX+","+accY+","+accZ+","+posX+","+posY+","+posZ+","+ userOK;
			String [] record = stringBuilder.split(",");
			csvWriter.writeNext(record);
			csvWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
  protected void noNewMessage(){
		userinterface.noNewMessage();
	}

	protected void conenctionLost() {
		userinterface.connectionLost();
	}
}
