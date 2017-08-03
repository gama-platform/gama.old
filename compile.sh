#!/bin/bash

compile (){
	echo "Compile GAMA project"			
	cd ummisco.gama.annotations &&
	mvn clean install -DskipTests -T 8C &&
	cd - &&
	cd msi.gama.processor &&
	mvn clean install -DskipTests -T 8C &&
	cd - &&
	cd msi.gama.parent &&
	
	if  [[ $MSG == *"ci debug"* ]]; then		
		mvn -X clean compile -DskipTests -T 8C
	else
		mvn clean compile -DskipTests -T 8C
	fi
		
	cd -
}


install (){
	echo "Install GAMA project"			
	cd ummisco.gama.annotations &&
	mvn clean install -DskipTests -T 8C &&
	cd - &&
	cd msi.gama.processor &&
	mvn clean install -DskipTests -T 8C &&
	cd - &&
	cd msi.gama.parent &&
	
	if  [[ $MSG == *"ci debug"* ]]; then		
		mvn -X clean install -DskipTests -T 8C
	else
		mvn clean install -DskipTests -T 8C
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
