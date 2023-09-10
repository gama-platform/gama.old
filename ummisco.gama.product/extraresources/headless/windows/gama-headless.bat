echo off
cls
setLocal EnableDelayedExpansion
set inputFile=""
set outputFile="" 
set memory=2048m
set workDir=.work%RANDOM%
SETLOCAL enabledelayedexpansion


:TOP

IF (%1) == () GOTO NEXT_CODE
	if %1 EQU -m ( 
		set  comm=%1
		set  next=%2
		set memory=!next!
		SHIFT
		GOTO DECALE
	)

	set param=%param% %1
	GOTO DECALE
:DECALE
SHIFT
GOTO TOP

:NEXT_CODE
echo ******************************************************************
echo * GAMA version 1.9.0                                             *
echo * http://gama-platform.org                                       *
echo * (c) 2007-2023 UMI 209 UMMISCO IRD/SU and Partners              *
echo ******************************************************************

set FILENAME="..\plugins\"
FOR /F %%e in ('dir /b %FILENAME%') do ( 
 	SET result=%%e
	if "!result:~0,29!" == "org.eclipse.equinox.launcher_" (  
		goto END
	)
)
:END
@echo !result!
@echo workDir = %workDir% 
@echo memory = %memory% 

set "result=..\plugins\%result%"

echo %result%
echo %JAVA_HOME%

if exist ..\jdk\ (
	echo "JDK"
	call ..\jdk\bin\java -cp !result! -Xms512m -Xmx%memory% --add-exports=java.base/java.lang=ALL-UNNAMED --add-exports=java.desktop/sun.awt=ALL-UNNAMED --add-exports=java.desktop/sun.java2d=ALL-UNNAMED --add-exports=java.desktop/sun.awt.image=ALL-UNNAMED --add-exports=java.base/java.math=ALL-UNNAMED --add-exports=java.base/java.lang=ALL-UNNAMED  --add-exports=java.base/sun.nio.ch=ALL-UNNAMED  --add-opens=java.base/java.lang=ALL-UNNAMED  --add-opens=java.base/jdk.internal.loader=ALL-UNNAMED  --add-opens=java.base/java.math=ALL-UNNAMED   --add-opens=java.base/java.util=ALL-UNNAMED  --add-opens=java.base/java.util.concurrent=ALL-UNNAMED  --add-opens=java.base/java.util.concurrent.atomic=ALL-UNNAMED  --add-opens=java.base/java.net=ALL-UNNAMED  --add-opens=java.base/java.security=ALL-UNNAMED  --add-opens=java.desktop/java.awt=ALL-UNNAMED  --add-opens=java.base/java.io=ALL-UNNAMED  --add-opens=java.base/java.time=ALL-UNNAMED  --add-opens=java.base/java.util.concurrent.locks=ALL-UNNAMED --add-opens=java.base/java.text=ALL-UNNAMED  --add-opens=java.base/java.lang.ref=ALL-UNNAMED  --add-opens=java.sql/java.sql=ALL-UNNAMED -Djava.awt.headless=true org.eclipse.core.launcher.Main -configuration ./configuration -application msi.gama.headless.product -data "%workDir%" !param! 
) else (
	echo "JAVA_HOME"
  	call "%JAVA_HOME%\bin\java.exe" -cp !result! -Xms512m -Xmx%memory% --add-exports=java.base/java.lang=ALL-UNNAMED --add-exports=java.desktop/sun.awt=ALL-UNNAMED --add-exports=java.desktop/sun.java2d=ALL-UNNAMED --add-exports=java.desktop/sun.awt.image=ALL-UNNAMED --add-exports=java.base/java.math=ALL-UNNAMED --add-exports=java.base/java.lang=ALL-UNNAMED  --add-exports=java.base/sun.nio.ch=ALL-UNNAMED  --add-opens=java.base/java.lang=ALL-UNNAMED  --add-opens=java.base/jdk.internal.loader=ALL-UNNAMED  --add-opens=java.base/java.math=ALL-UNNAMED   --add-opens=java.base/java.util=ALL-UNNAMED  --add-opens=java.base/java.util.concurrent=ALL-UNNAMED  --add-opens=java.base/java.util.concurrent.atomic=ALL-UNNAMED  --add-opens=java.base/java.net=ALL-UNNAMED  --add-opens=java.base/java.security=ALL-UNNAMED  --add-opens=java.desktop/java.awt=ALL-UNNAMED  --add-opens=java.base/java.io=ALL-UNNAMED  --add-opens=java.base/java.time=ALL-UNNAMED  --add-opens=java.base/java.util.concurrent.locks=ALL-UNNAMED --add-opens=java.base/java.text=ALL-UNNAMED  --add-opens=java.base/java.lang.ref=ALL-UNNAMED  --add-opens=java.sql/java.sql=ALL-UNNAMED -Djava.awt.headless=true org.eclipse.core.launcher.Main -configuration ./configuration -application msi.gama.headless.product -data "%workDir%" !param! 
)