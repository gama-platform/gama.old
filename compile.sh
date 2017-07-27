#!/bin/bash

compile (){
	echo "Compile GAMA project"		
	if  [[ $MSG == *"ci debug"* ]]; then		
		cd ummisco.gama.annotations &&
		mvn -X clean install -Dmaven.test.skip=true &&
		cd - &&
		cd msi.gama.processor &&
		mvn -X clean install -Dmaven.test.skip=true &&
		cd - &&
		cd msi.gama.parent &&
		mvn -X clean compile -Dmaven.test.skip=true && 
		cd -
	else
		cd ummisco.gama.annotations &&
		mvn -q clean install -Dmaven.test.skip=true &&
		cd - &&
		cd msi.gama.processor &&
		mvn -q clean install -Dmaven.test.skip=true &&
		cd - &&
		cd msi.gama.parent &&
		mvn -q clean compile -Dmaven.test.skip=true && 
		cd -
	fi
}

clean(){
	echo "Clean p2 update site"		
	sshpass -e ssh gamaws@51.255.46.42 /var/www/gama_updates/clean.sh
}

deploy(){	
	echo "Deploy to p2 update site"	
	sh ./deploy.sh
}


MESSAGE=$(git log -1 HEAD --pretty=format:%s)
echo $MESSAGE
echo $MSG
if [[ "$TRAVIS_EVENT_TYPE" == "cron" ]]; then 	
	clean
	deploy  
else
	if  [[ ${MESSAGE} == *"ci deploy"* ]]; then		
		if  [[ ${MESSAGE} == *"ci clean"* ]] || [[ $MSG == *"ci clean"* ]]; then
			clean
		fi 
		deploy 
	else	
		compile
	fi
fi
