*******************************************************************
*GAMA version 1.7.0                                               *
*http://gama-platform.org			                              *
*(c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners                *
*******************************************************************

How to start GAMA in headless mode with script file

   command:
	 sh gama-headless.sh [opt] xmlInputFile outputDirectory

   option:
	-m to define the memory allocated by the simulation"

How to start GAMA in headless mode with java command line
    java -cp GAMA_JARS  -Xms512m -Xmx2048 -Djava.awt.headless=true org.eclipse.core.launcher.Main  -application msi.gama.headless.id4 XMLInputFile OutputDirectory

=== Run GAMA headless experiment from Eclipse & GAMA Git Version ===

In the msi.gama.headless plugin, open the plugin.xml file.
Click on the "Launch an Eclipse application", on the right, under the Testing title.

The first launch will throw an Exception. Edit the application using the run simulations button, Run configurations...
In the Arguments pane, in program configuration add at the end the following instructions:

-v {absolute_path_to_workspace}/msi.gama.headless/ext/unix/samples/predatorPrey.xml {absolute_path_to_workspace}/msi.gama.headless/ext/unix/samples/test
  or 
-v {absolute_path_to_workspace}/msi.gama.headless/ext/unix/samples/roadTraffic.xml {absolute_path_to_workspace}/gama/msi.gama.headless/ext/unix/samples/test
