package main;

import java.util.LinkedList;

public class DataProcessorTest {
	private LinkedList<Float> acc;
	private LinkedList<Float> gyro;
	public Float thAccDiff,thGyroDiff,thAngle, thAccLongTerm, thGyroLongTerm;
	
	protected DataProcessorTest(){
		acc = new LinkedList<>();
		gyro = new LinkedList<>();
		
		thAccDiff = 3.924f;
		thGyroDiff = 60f;
		thAngle = 35f;
		thAccLongTerm = 9.81f*3.0f;
		thGyroLongTerm = 100f;
		
	}
	
	protected int calc(MovementDataTest data){
		
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
					
					if(accMax>thAccLongTerm)
						if(gyroMax>thGyroLongTerm)
						//emergency
							return 0;
						else return 1;
					else return 2;
				}
				else return 3;
			}
			else return 4;
		}
		else return 5;
	}

	protected void resetData() {
		acc.clear();
		gyro.clear();
	}
}
