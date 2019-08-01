@echo off
>result.txt (
@echo "%1 -help"
%1 -help

@echo "%1 -version"
%1 -version 

@echo "%1 -validate"
%1 -validate 

@echo "%1 -test"
%1 -test 

@echo "%1 samples/predatorPrey.xml outputFolder"
%1 samples/predatorPrey.xml outputFolder 
 
@echo "%1 -m 12000m samples/predatorPrey.xml outputFolder"
%1 -m 12000m samples/predatorPrey.xml outputFolder 

@echo "%1 -hpc 3 samples/predatorPrey.xml outputFolder"
%1 -hpc 3 samples/predatorPrey.xml outputFolder 

@echo "%1 samples/predatorPrey.xml outputFolder"
%1 samples/predatorPrey.xml outputFolder 

@echo "%1 -xml prey_predatorExp samples/predatorPrey/pr\edatorPrey.gaml t.xml"
%1 -xml prey_predatorExp samples/predatorPrey/predatorPrey.gaml t.xml 

@echo "%1 t.xml out"
%1 t.xml out 
)