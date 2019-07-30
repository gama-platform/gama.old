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
echo * GAMA version 1.7.0                                             *
echo * http://gama-platform.googlecode.com                            *
echo * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC and Partners            *
echo ******************************************************************
rem @echo off
set FILENAME=..\plugins\org.eclipse.equinox.launcher_*.jar

set FILEPATH=

FOR /F %%f in ('dir /S /B %FILENAME%') do set FILEPATH=%%f

rem @echo off
call java  -cp %FILEPATH% -Xms512m -Xmx%memory%  -Djava.awt.headless=true org.eclipse.core.launcher.Main  -application msi.gama.headless.id4 -data "%workDir%" !param! 