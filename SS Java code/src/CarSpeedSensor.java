
public class CarSpeedSensor {

	private float CarSpeed;
	private float ActualCarSpeed;
	
	public CarSpeedSensor()
	{
		super();
		CarSpeed = 0;
	}

	public float getCarSpeed() {
		return CarSpeed;
	}

	public void setCarSpeed(float carSpeed) {
		CarSpeed = carSpeed;
	}
	
	public float getActualCarSpeed(){
		return ActualCarSpeed;
	}
	
	public void setActualCarSpeed(float actutalCarSpeed) {
		ActualCarSpeed = actutalCarSpeed;
	}
	
	
}
