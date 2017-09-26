#!/bin/bash

compile (){
	echo "Compile GAMA project"			
	cd ummisco.gama.annotations
	mvn clean install -DskipTests
	cd -
	cd msi.gama.processor
	mvn clean install -DskipTests
	cd - 
	
	change=$(git log --pretty=format: --name-only --since="1 hour ago")
	
	if [[ ${change} == *"msi.gama.ext"* ]] || [[ $MSG == *"ci ext"* ]]; then
		cd msi.gama.ext 
		mvn deploy -DskipTests -T 8C -P p2Repo --settings ../settings.xml
		cd -
	fi
	
	
	cd msi.gama.parent
	
	if  [[ $MSG == *"ci debug"* ]]; then		
		mvn -e clean compile -DskipTests
	else
		mvn clean compile -offline -DskipTests
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
		mvn -e clean install -DskipTests -T 8C
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
