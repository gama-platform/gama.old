@echo off
>result.txt (
@echo "gama-headless.bat -help"
gama-headless.bat -help

@echo "gama-headless.bat -version"
gama-headless.bat -version 

@echo "gama-headless.bat -validate"
gama-headless.bat -validate 

@echo "gama-headless.bat -test"
gama-headless.bat -test 

@echo "gama-headless.bat samples/predatorPrey.xml outputFolder"
gama-headless.bat samples/predatorPrey.xml outputFolder 
 
@echo "gama-headless.bat -m 12000m samples/predatorPrey.xml outputFolder"
gama-headless.bat -m 12000m samples/predatorPrey.xml outputFolder 

@echo "gama-headless.bat -hpc 3 samples/predatorPrey.xml outputFolder"
gama-headless.bat -hpc 3 samples/predatorPrey.xml outputFolder 

@echo "gama-headless.bat samples/predatorPrey.xml outputFolder"
gama-headless.bat samples/predatorPrey.xml outputFolder 

@echo "gama-headless.bat -xml prey_predatorExp samples/predatorPrey/pr\edatorPrey.gaml t.xml"
gama-headless.bat -xml prey_predatorExp samples/predatorPrey/predatorPrey.gaml t.xml 

@echo "gama-headless.bat t.xml out"
gama-headless.bat t.xml out 
)