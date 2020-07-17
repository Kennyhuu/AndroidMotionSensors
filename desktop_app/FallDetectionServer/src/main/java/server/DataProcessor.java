package server;

import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

class DataProcessor {
	
	private Server server;
	private LinkedList<Float> acc;
	private LinkedList<Float> gyro;
	private Float thAccDiff,thGyroDiff,thAngle, thAccLongTerm, thGyroLongTerm;
	
	private TimerTask timerTaskNoMessage;
	private Timer timerNoMessage;
	
	protected DataProcessor(Server s){
		server=s;
		
		acc = new LinkedList<>();
		gyro = new LinkedList<>();
		
		thAccDiff = 3.924f;
		thGyroDiff = 60f;
		thAngle = 35f;
		thAccLongTerm = 24.525f;
		thGyroLongTerm = 340f;
		
	}
	
	protected void calc(MovementData data){
		//reset no-message-timer
		/*timerNoMessage.cancel();
		timerNoMessage=new Timer();
		timerNoMessage.schedule(timerTaskNoMessage, 1000 *10);*/
			
		
		// CALC FOR PRESENTATION
		//-----------------------------------
		boolean demo = true;
		if(demo) {
			if((float) Math.sqrt(data.accX*data.accX+data.accY*data.accY+data.accZ*data.accZ)>9.81*2 ){
				server.emergency(data);
			}
			return;
		}
		//-----------------------------------
		
		
		//first element - oldest measurement
		//last  element - newest measurement
		acc.add(
				(float) Math.sqrt(data.accX * data.accX + data.accY * data.accY + data.accZ * data.accZ));
		gyro.add(
				(float) Math.sqrt(data.posX * data.posX + data.posY * data.posY + data.posZ * data.posZ));
		
		if(acc.size()==251){
			//shift time window
			acc.remove();
			gyro.remove();
			
			//we check for fall
			// 1. is the user stationary?
			Float accMax, accMin, gyroMax, gyroMin;
			accMax=acc.getFirst();
			accMin=acc.getFirst();
			gyroMax=gyro.getFirst();
			gyroMin=gyro.getFirst();
			
			//previous 1 sec is the last 50 elements
			for(int i=200;i<250;i++){
				if(acc.get(i)>accMax) accMax=acc.get(i);
				if(acc.get(i)<accMin) accMin=acc.get(i);
				if(gyro.get(i)>gyroMax) gyroMax=gyro.get(i);
				if(gyro.get(i)<gyroMin) gyroMin=gyro.get(i);
			}
			
			if(accMax-accMin<thAccDiff && gyroMax-gyroMin<thGyroDiff){
				// 2. is the user lying?
				if(Math.abs(data.accY)<9.81f && (float)(Math.acos(data.accY/9.81f)/Math.PI*180)>thAngle){
					// 3. did the user had a fast movement before?
					//previous 5 sec is the last 250 elements, aka all the elements
					for(int i=0;i<250;i++){
						if(acc.get(i)>accMax) accMax=acc.get(i);
						if(gyro.get(i)>gyroMax) gyroMax=gyro.get(i);
					}
					
					if(accMax>thAccLongTerm && gyroMax>thGyroLongTerm)
						//emergency
						server.emergency();
				}
			}	
		}
	}

	protected void resetData() {
		acc.clear();
		gyro.clear();
	}
}
