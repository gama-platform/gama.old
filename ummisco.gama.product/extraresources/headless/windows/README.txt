*******************************************************************
* GAMA version 1.8.1     	                                          *
*http://gama-platform.org			                              *
* (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners                  *
*******************************************************************

How to start GAMA in headless mode with script file

   command:
	gama-headless.bat [opt] xmlInputFile outputDirectory

   option:
	-m to define the memory allocated by the simulation"

How to start GAMA in headless mode with java command line
    java -cp GAMA_JARS  -Xms512m -Xmx2048 -Djava.awt.headless=true  org.eclipse.core.launcher.Main  -application msi.gama.headless.id4 XMLInputFile OutputDirectory
