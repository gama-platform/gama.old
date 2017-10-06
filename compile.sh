#!/bin/bash

compile (){
	echo "Compile GAMA project"			
	cd ummisco.gama.annotations
	mvn clean install
	cd -
	cd msi.gama.processor
	mvn clean install
	cd - 
	
	change=$(git log --pretty=format: --name-only --since="1 hour ago")
	
	if [[ ${change} == *"msi.gama.ext"* ]] || [[ $MSG == *"ci ext"* ]]; then
		cd msi.gama.ext 
		mvn clean install
		cd -			
		cd ummisco.gama.feature.dependencies 
		mvn clean install
		cd -
	fi
	
	
	cd msi.gama.parent
	
	if  [[ $MSG == *"ci debug"* ]]; then		
		mvn -e clean compile
	else
		mvn clean compile
	fi
		
	cd -
}


install (){
	echo "Install GAMA project"			
	cd ummisco.gama.annotations
	mvn clean install -T 8C
	cd -
	cd msi.gama.processor
	mvn clean install -T 8C
	cd -
	
	
	
	change=$(git log --pretty=format: --name-only --since="1 hour ago")
	
	if [[ ${change} == *"msi.gama.ext"* ]] || [[ $MSG == *"ci ext"* ]]; then
		cd msi.gama.ext 
		mvn clean install -T 8C
		cd -
		cd ummisco.gama.feature.dependencies 
		mvn clean install -T 8C
		cd -
	fi
	
	
	
	cd msi.gama.parent
	
	if  [[ $MSG == *"ci debug"* ]]; then		
		mvn -e clean install -DskipTests -T 8C
	else
		mvn clean install -DskipTests -T 8C
	fi
		
	cd -
}



MESSAGE=$(git log -1 HEAD --pretty=format:%s)
echo $MESSAGE
if  [[ ${MESSAGE} == *"ci clean"* ]] || [[ $MSG == *"ci clean"* ]]; then
	MSG+=" ci ext "
fi 
if [[ "$TRAVIS_EVENT_TYPE" == "cron" ]] || [[ $MSG == *"ci cron"* ]]; then 	
	install
else		
	install
fi
