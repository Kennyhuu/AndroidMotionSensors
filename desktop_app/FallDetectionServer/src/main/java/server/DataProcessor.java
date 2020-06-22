package server;

import java.util.ArrayList;

class DataProcessor {
	
	private Server server;
	private ArrayList<MovementData> dataList;
	
	protected DataProcessor(Server s){
		server=s;
		dataList = new ArrayList<>();
	}
	
	protected void calc(MovementData newData){
		// TODO use server and dataList for prediction of emergency
	}
}
