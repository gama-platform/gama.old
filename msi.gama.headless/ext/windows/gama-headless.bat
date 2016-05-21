echo off
cls
setLocal EnableDelayedExpansion
set inputFile=""
set outputFile="" 
set memory=2048m


:TOP

IF (%1) == () GOTO NEXT_CODE
	if %1 EQU -m ( 
		set  comm=%1
		set  next=%2
		set memory=!next!
		SHIFT
		GOTO DECALE
	)
	if %1 EQU -t ( 
		set commm=%1
		set next=%2
		set param=!commm! !next!
		SHIFT
		GOTO DECALE
	)
	if %1 EQU -c  ( 
		set param=!param! -c
		GOTO DECALE
	)
	if !inputFile! EQU ""  ( 
		set inputFile=%1
		GOTO DECALE
	)
	set outputFile=%1

:DECALE
SHIFT
GOTO TOP

:NEXT_CODE
echo ******************************************************************
echo * GAMA version 1.7.0                                             *
echo * http://gama-platform.org				                          *
echo * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC and Partners            *
echo ******************************************************************


if %inputFile% EQU ""  (goto help)
REM ~ if o%outputFile%k EQU ok (goto help)
REM ~ if %inputFile% EQU -?  (goto help)
REM ~ if %inputFile% EQU --help (goto help)

REM ~ if %inputFile% EQU -m (goto memory) else ( set inputFile=%~f1 )

:continue
 if not exist "%inputFile%" ( goto notExistInputFile  )
 if  exist %outputFile% ( goto existOutputDirectory)


 set CLASSPATH=
 for /R ..\plugins %%a in (*.jar) do (
   set CLASSPATH=%%a;!CLASSPATH!
 )
 set CLASSPATH=!CLASSPATH!"
 echo GAMA is starting...
call java  -Xms512m -Xmx%memory%  -Djava.awt.headless=true org.eclipse.core.launcher.Main  -application msi.gama.headless.id4 -data "%outputFile%/.work" !param! "%inputFile%" "%outputFile%"
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
 