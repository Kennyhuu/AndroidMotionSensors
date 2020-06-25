package server;

import java.util.ArrayList;

public class DataProcessor {
	
	private Server server;
	private ArrayList<MovementData> dataList;
	
	protected DataProcessor(Server s){
		server=s;
		dataList = new ArrayList<MovementData>();
	}
	
	protected void calc(MovementData newData){
		// TODO use server and dataList for prediction of emergency
	}
}
