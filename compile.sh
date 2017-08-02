#!/bin/bash

compile (){
	echo "Compile GAMA project"			
	cd ummisco.gama.annotations &&
	mvn -q clean install -Dmaven.test.skip=true &&
	cd - &&
	cd msi.gama.processor &&
	mvn -q clean install -Dmaven.test.skip=true &&
	cd - &&
	cd msi.gama.parent &&
	
	if  [[ $MSG == *"ci debug"* ]]; then		
		mvn -X clean compile -Dmaven.test.skip=true
	else
		mvn -q clean compile -Dmaven.test.skip=true
	fi
		
	cd -
}


install (){
	echo "Install GAMA project"			
	cd ummisco.gama.annotations &&
	mvn -q clean install -Dmaven.test.skip=true &&
	cd - &&
	cd msi.gama.processor &&
	mvn -q clean install -Dmaven.test.skip=true &&
	cd - &&
	cd msi.gama.parent &&
	
	if  [[ $MSG == *"ci debug"* ]]; then		
		mvn -X clean install -Dmaven.test.skip=true
	else
		mvn -q clean install -Dmaven.test.skip=true
	fi
		
	cd -
}



MESSAGE=$(git log -1 HEAD --pretty=format:%s)
echo $MESSAGE
echo $MSG
if [[ "$TRAVIS_EVENT_TYPE" == "cron" ]] || [[ $MSG == *"ci cron"* ]]; then 	
	install
else		
	compile
fi
