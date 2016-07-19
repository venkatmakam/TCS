@echo off
copy Samples\includes\Std_Types.h /Y "C:\ETASData\ISOLAR-EVE3.0\workspace\VECU_FinalTEST\src\gen\MinGW\RTAOS\" >nul
copy Samples\includes\Platform_Types.h /Y "C:\ETASData\ISOLAR-EVE3.0\workspace\VECU_FinalTEST\src\gen\MinGW\RTAOS\" >nul
copy Samples\includes\Compiler.h /Y "C:\ETASData\ISOLAR-EVE3.0\workspace\VECU_FinalTEST\src\gen\MinGW\RTAOS\" >nul
copy Samples\includes\Os_Compiler_Cfg.h /Y "C:\ETASData\ISOLAR-EVE3.0\workspace\VECU_FinalTEST\src\gen\MinGW\RTAOS\" >nul
copy Samples\includes\Compiler_Cfg.h /Y "C:\ETASData\ISOLAR-EVE3.0\workspace\VECU_FinalTEST\src\gen\MinGW\RTAOS\" >nul
if exist Samples\includes\Os_MemMap.h copy Samples\includes\Os_MemMap.h /Y "C:\ETASData\ISOLAR-EVE3.0\workspace\VECU_FinalTEST\src\gen\MinGW\RTAOS\" >nul
