
public class WheelSpeedSensor {
	
	private float ULWheelSpeed;
	private float URWheelSpeed;
	private float DLWheelSpeed;
	private float DRWheelSpeed;
	
	public WheelSpeedSensor()
	{
		super();
		ULWheelSpeed = 0;
		URWheelSpeed = 0;
		DLWheelSpeed = 0;
		DRWheelSpeed = 0;
	}

	public float getULWheelSpeed() {
		return ULWheelSpeed;
	}

	public void setULWheelSpeed(float uLWheelSpeed) {
		ULWheelSpeed = uLWheelSpeed;
	}

	public float getURWheelSpeed() {
		return URWheelSpeed;
	}

	public void setURWheelSpeed(float uRWheelSpeed) {
		URWheelSpeed = uRWheelSpeed;
	}

	public float getDLWheelSpeed() {
		return DLWheelSpeed;
	}

	public void setDLWheelSpeed(float dLWheelSpeed) {
		DLWheelSpeed = dLWheelSpeed;
	}

	public float getDRWheelSpeed() {
		return DRWheelSpeed;
	}

	public void setDRWheelSpeed(float dRWheelSpeed) {
		DRWheelSpeed = dRWheelSpeed;
	}

}
