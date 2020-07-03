package server;

import java.util.LinkedList;

class DataProcessor {
	
	private Server server;
	private LinkedList<Float> acc,accLongTerm;
	private LinkedList<Float> gyro, gyroLongTerm;
	private Float accMax, accMin, gyroMax, gyroMin;
	private Float accMaxLongTerm, gyroMaxLongTerm;
	private Float thAccDiff,thGyroDiff,thAngle, thAccLongTerm, thGyroLongTerm;
	
	protected DataProcessor(Server s){
		server=s;
		acc = new LinkedList<>();
		gyro = new LinkedList<>();
		accLongTerm = new LinkedList<>();
		gyroLongTerm = new LinkedList<>();
		accMax=0f;
		accMin=-1f;
		gyroMax=0f;
		gyroMin=-1f;
		accMaxLongTerm = 0f;
		gyroMaxLongTerm = 0f;
		
		thAccDiff = 3.924f;
		thGyroDiff = 60f;
		thAngle = 35f;
		thAccLongTerm = 24.525f;
		thGyroLongTerm = 340f;
		
	}
	
	protected void calc(MovementData data){
		Float accCurrent = (float) Math.sqrt(data.accX*data.accX+data.accY*data.accY+data.accZ*data.accZ);
		Float gyroCurrent = (float) Math.sqrt(data.posX*data.posX+data.posY*data.posY+data.posZ*data.posZ);
		
		
		//compare new value to maxima/minima
		if(accCurrent>accMax) accMax =accCurrent;
		if(gyroCurrent>gyroMax) gyroMax = gyroCurrent;
		if(accMin<0 || accMin<accCurrent) accMin=accCurrent;
		if(gyroMin<0 || gyroMin<gyroCurrent) gyroMin=gyroCurrent;
		if(accCurrent>accMaxLongTerm) accMaxLongTerm =accCurrent;
		if(gyroCurrent>gyroMaxLongTerm) gyroMaxLongTerm = gyroCurrent;
		
		acc.add(accCurrent);
		gyro.add(gyroCurrent);
		accLongTerm.add(accCurrent);
		gyroLongTerm.add(gyroCurrent);
		
		if(acc.size()==51){
			//shift time window on short term
			Float accLast = acc.remove();
			Float gyroLast = gyro.remove();
			
			//did we remove the maxima/minima?
			if(accMax.equals(accLast)){
				accMax = acc.getFirst();
				for(int i=0;i<acc.size();i++)
					if(accMax<acc.get(i)) accMax=acc.get(i);
			}
			else if(accMin.equals(accLast)) {
				accMin = acc.getFirst();
				for(int i=0;i<acc.size();i++)
					if(accMin>acc.get(i)) accMin=acc.get(i);
			}
			if(gyroMax.equals(gyroLast)) {
				gyroMax = gyro.getFirst();
				for(int i=0;i<gyro.size();i++)
					if(gyroMax<gyro.get(i)) gyroMax=gyro.get(i);
			}
			else if(gyroMin.equals(gyroLast)) {
				gyroMin = gyro.getFirst();
				for(int i=0;i<gyro.size();i++)
					if(gyroMin>gyro.get(i)) gyroMin=gyro.get(i);
			}
			
			if(accLongTerm.size()==251){
				//shift time window on long term
				Float accLastLongTerm = accLongTerm.remove();
				Float gyroLastLongTerm = gyroLongTerm.remove();
				
				//did we remove the maxima/minima?
				if(accMaxLongTerm.equals(accLastLongTerm)){
					accMaxLongTerm = accLongTerm.getFirst();
					for(int i=0;i<accLongTerm.size();i++)
						if(accMaxLongTerm<accLongTerm.get(i)) accMaxLongTerm=accLongTerm.get(i);
				}
				if(gyroMaxLongTerm.equals(gyroLastLongTerm)){
					gyroMaxLongTerm = gyroLongTerm.getFirst();
					for(int i=0;i<gyroLongTerm.size();i++)
						if(gyroMaxLongTerm<gyroLongTerm.get(i)) gyroMaxLongTerm=gyroLongTerm.get(i);
				}
				
				//long term and short term full
				//we check for fall
				// 1. is the user stationary?
				if(accMax-accMin<thAccDiff && gyroMax-gyroMin<thGyroDiff)
					// 2. is the user lying?
					if((float)Math.acos(data.accY/9.81f)>thAngle)
						// 3. did the user had a fast movement before?
						if(accMaxLongTerm>thAccLongTerm && gyroMaxLongTerm>thGyroLongTerm)
							//emergency
							server.emergency();
			}
		}
	}
}
