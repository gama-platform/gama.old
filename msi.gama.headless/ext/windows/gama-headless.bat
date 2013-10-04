echo off
cls
setLocal EnableDelayedExpansion
set inputFile=%1
set outputFile=%2
set memory=2048m


echo ******************************************************************
echo * GAMA version 1.6                                               *
echo * http://gama-platform.googlecode.com                            *
echo * (c) 2007-2013 UMI 209 UMMISCO IRD/UPMC and Partners            *
echo ******************************************************************


if o%inputFile%k EQU ok  (goto help)
if o%outputFile%k EQU ok (goto help)
if %inputFile% EQU -?  (goto help)
if %inputFile% EQU --help (goto help)

if %inputFile% EQU -m (goto memory) else ( set inputFile=%~f1 )

:continue
if not exist "%inputFile%" ( goto notExistInputFile  )
if  exist %outputFile% ( goto existOutputDirectory)

 set CLASSPATH=
 for /R ..\plugins %%a in (*.jar) do (
   set CLASSPATH=%%a;!CLASSPATH!
 )
 set CLASSPATH=!CLASSPATH!"
 call java  -Xms512m -Xmx%memory%  org.eclipse.core.launcher.Main  -application msi.gama.headless.id4 "%inputFile%" "%outputFile%"
 goto end
 
:help
	echo Help:
	echo   command: sh gama-headless.bat [opt] xmlInputFile outputDirectory 
	echo     option:
	echo       -m to define the memory allocated by the simulation
	goto end
	
:memory
	set inputFile=%~f3
	set outputFile=%4
	set memory=%2
	goto continue
 
:notExistInputFile
	echo The input file does not exist. Please check the path of your input file
	goto end
 
:existOutputDirectory
	echo The output directory already exists. Please check the path of your output directory
	goto end
	
:end
 