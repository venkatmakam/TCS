#include "vrtaCore.h"
#include "vrtaSampleDevices.h"
#include "vrtaLoggerDevice.h"
#include "VirtualDevices.h"

vrtaSensor DLWheelSpeed("DLWheelSpeed");
vrtaSensor DRWheelSpeed("DRWheelSpeed");
vrtaSensor ULWheelSpeed("ULWheelSpeed");
vrtaSensor URWheelSpeed("URWheelSpeed");
vrtaSensor CarSpeed("CarSpeed");
vrtaSensor SteeringAngle("SteeringAngle");
vrtaSensor Gyro("Gyro");
vrtaActuator ThrottleActuator("ThrottleActuator");
vrtaActuator DLBrakeActuator("DLBrakeActuator");
vrtaActuator DRBrakeActuator("DRBrakeActuator");
vrtaActuator ULBrakeActuator("ULBrakeActuator");
vrtaActuator URBrakeActuator("URBrakeActuator");

int DLWheelSpeedValue(void){
	return DLWheelSpeed.Value();
}
int DRWheelSpeedValue(void) {
	return DRWheelSpeed.Value();
}
int ULWheelSpeedValue(void) {
	return ULWheelSpeed.Value();
}
int URWheelSpeedValue(void) {
	return URWheelSpeed.Value();
}
int CarSpeedValue(void) {
	return CarSpeed.Value();
}
int SteeringAngleValue(void) {
	return SteeringAngle.Value();
}
int GyroValue(void) {
	return Gyro.Value();
}



void  setActuatorLevel(int Throttle){
	ThrottleActuator.SetValue(Throttle);
}
void  setBrakeActuatorLevel(int DL, int DR, int UL, int UR){
	DLBrakeActuator.SetValue(DL);
	DRBrakeActuator.SetValue(DR);
	ULBrakeActuator.SetValue(UL);
	URBrakeActuator.SetValue(UR);
}

Logger Status("Status");


int status_printf(const vrtaTextPtr format, ...){
	va_list argptr;
	va_start(argptr, format);
	int ret = Status.printf(format, argptr);
	va_end(argptr);
	return ret;
}




void InitializeDevices(void){



}
