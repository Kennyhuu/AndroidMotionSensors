package server;

public class MovementData{
	public float accX;
	public float accY;
	public float accZ;
	public float posX;
	public float posY;
	public float posZ;
	
	protected MovementData(){
		this(0f,0f,0f,0f,0f,0f);
	}

	//should be protected TODO
	public 
	MovementData(float ax, float ay, float az, float px, float py, float pz){
		accX = ax;
		accY = ay;
		accZ = az;
		posX = px;
		posY = py;
		posZ = pz;
	}
	
	protected MovementData(MovementData d){
		this(d.accX,d.accY,d.accZ,d.posX,d.posY,d.posZ);
	}
	
}
