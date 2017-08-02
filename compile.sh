#!/bin/bash

compile (){
	echo "Compile GAMA project"			
	cd ummisco.gama.annotations &&
	mvn -q clean install &&
	cd - &&
	cd msi.gama.processor &&
	mvn -q clean install &&
	cd - &&
	cd msi.gama.parent &&
	
	if  [[ $MSG == *"ci debug"* ]]; then		
		mvn -X clean compile 
	else
		mvn clean compile 
	fi
		
	cd -
}


install (){
	echo "Install GAMA project"			
	cd ummisco.gama.annotations &&
	mvn -q clean install &&
	cd - &&
	cd msi.gama.processor &&
	mvn -q clean install &&
	cd - &&
	cd msi.gama.parent &&
	
	if  [[ $MSG == *"ci debug"* ]]; then		
		mvn -X clean install 
	else
		mvn -q clean install 
	fi
		
	cd -
}



MESSAGE=$(git log -1 HEAD --pretty=format:%s)
echo $MESSAGE
if [[ "$TRAVIS_EVENT_TYPE" == "cron" ]] || [[ $MSG == *"ci cron"* ]]; then 	
	install
else		
	compile
fi
