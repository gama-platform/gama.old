*******************************************************************
*GAMA version 1.8.0                                               *
*http://gama-platform.googlecode.com                              *
*(c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners                *
*******************************************************************

How to start GAMA in headless mode with script file

   command:
	 sh gama-headless.sh [opt] xmlInputFile outputDirectory

   option:
	-m to define the memory allocated by the simulation"

How to start GAMA in headless mode with java command line
    java -cp GAMA_JARS  -Xms512m -Xmx2048 -Djava.awt.headless=true  org.eclipse.core.launcher.Main  -application msi.gama.headless.id4 XMLInputFile OutputDirectory
