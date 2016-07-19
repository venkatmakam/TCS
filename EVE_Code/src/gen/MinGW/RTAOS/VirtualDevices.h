
#ifdef __cplusplus
extern "C" {
#endif

extern int status_printf(const vrtaTextPtr format, ...);
extern int DLWheelSpeedValue(void);
extern int DRWheelSpeedValue(void);
extern int ULWheelSpeedValue(void);
extern int URWheelSpeedValue(void);
extern int CarSpeedValue(void);
extern int SteeringAngleValue(void);
extern int GyroValue(void);
extern void  setActuatorLevel(int Throttle);
extern void  setBrakeActuatorLevel(int DL, int DR, int UL, int UR);

#ifdef __cplusplus
}
#endif
