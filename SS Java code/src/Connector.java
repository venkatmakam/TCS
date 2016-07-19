


import com.etas.vrta.comms.VECUConnection;
import com.etas.vrta.comms.VECUFinder;

public class Connector {
	static final String host="127.0.0.1";
	static final String ecu_path = "C:\\ETASData\\ISOLAR-EVE3.0\\workspace\\VECU_FinalTEST\\VECUs\\MinGW\\Bin\\VECU_FinalTEST.exe";

	private String DLWheelSpeedDevice = "DLWheelSpeed.Value";
	private String DRWheelSpeedDevice = "DRWheelSpeed.Value";
	private String ULWheelSpeedDevice = "ULWheelSpeed.Value";
	private String URWheelSpeedDevice = "URWheelSpeed.Value";
	private String CarSpeedDevice = "CarSpeed.Value";
	private String SteeringAngleDevice = "SteeringAngle.Value";
	private String GyroDevice = "Gyro.Value";
	private String ThrottleActuator = "ThrottleActuator.Value";
	private String DLBrakeActuator = "DLBrakeActuator.Value";
	private String DRBrakeActuator = "DRBrakeActuator.Value";
	private String ULBrakeActuator = "ULBrakeActuator.Value";
	private String URBrakeActuator = "URBrakeActuator.Value";
	protected VECUConnection m_connection;

	public void setUp() throws Exception {
		m_connection=VECUFinder.attach(host, ecu_path);
		m_connection.start();
	}

	public void sendDLWheelSpeed(Object DLWheelSpeed)
	{
		m_connection.action(DLWheelSpeedDevice).send(DLWheelSpeed);
	}
	public void sendDRWheelSpeed(Object DRWheelSpeed)
	{
		m_connection.action(DRWheelSpeedDevice).send(DRWheelSpeed);
	}
	public void sendULWheelSpeed(Object ULWheelSpeed)
	{
		m_connection.action(ULWheelSpeedDevice).send(ULWheelSpeed);
	}
	public void sendURWheelSpeed(Object URWheelSpeed)
	{
		m_connection.action(URWheelSpeedDevice).send(URWheelSpeed);
	}
	public void sendCarSpeed(Object CarSpeed)
	{
		m_connection.action(CarSpeedDevice).send(CarSpeed);
	}
	public void sendSteeringAngle(Object SteeringAngle)
	{
		m_connection.action(SteeringAngleDevice).send(SteeringAngle);
	}
	public void sendGyro(Object Gyro)
	{
		m_connection.action(GyroDevice).send(Gyro);
	}
	
	
	public Object getThrottleActuator()
	{
		return m_connection.event(ThrottleActuator).state();
	}
	public Object getDLBrakeActuator()
	{
		return m_connection.event(DLBrakeActuator).state();
	}
	public Object getDRBrakeActuator()
	{
		return m_connection.event(DRBrakeActuator).state();
	}
	public Object getULBrakeActuator()
	{
		return m_connection.event(ULBrakeActuator).state();
	}
	public Object getURBrakeActuator()
	{
		return m_connection.event(URBrakeActuator).state();
	}
	

}

