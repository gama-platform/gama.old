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
echo * GAMA version 1.9.3                                             *
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

set "ini_arguments="
set "skip_until_line=-server"
set "skipping=true"

for /f "usebackq delims=" %%a in (..\GAMA.ini) do (
	if !skipping!==true (
		if !skip_until_line!==%%a (
			set "skipping=false"
			set "line=%%a"
			set "ini_arguments=!ini_arguments!!line! "
		)
	) else (
		echo !skipping!
		set "line=%%a"
		set "ini_arguments=!ini_arguments!!line! "
	)
)

@echo Will run with these options:
@echo %ini_arguments%

if exist ..\jdk\ (
	echo "JDK"
	call ..\jdk\bin\java -cp !result! -Xms512m -Xmx%memory% !ini_arguments! -Djava.awt.headless=true org.eclipse.core.launcher.Main -configuration ./configuration -application msi.gama.headless.product -data "%workDir%" !param! 
) else (
	echo "JAVA_HOME"
  	call "%JAVA_HOME%\bin\java.exe" -cp !result! -Xms512m -Xmx%memory% !ini_arguments! -Djava.awt.headless=true org.eclipse.core.launcher.Main -configuration ./configuration -application msi.gama.headless.product -data "%workDir%" !param! 
)