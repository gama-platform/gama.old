#!/bin/bash

MESSAGE=$(git log -1 HEAD --pretty=format:%s)
echo $MESSAGE

function mvn_install() {
	echo "Building " $1
	cd $1
	if mvn clean install -e; then
	   echo ok
	else
	   echo Something went wrong.
	   exit 1
	fi
	res=${PIPESTATUS[0]} 
	echo "return code $res"
	if [[ $res -ne 0 ]]; then
		exit $res
	fi
	cd -
}

function mvn_install_with_sonar() {
	echo "Building " $1
	cd $1
	if mvn clean org.jacoco:jacoco-maven-plugin:prepare-agent install -e sonar:sonar -Dsonar.login=${SONAR_TOKEN} -Dsonar.projectKey=gama-platform_gama$1 -Dsonar.host.url=https://sonarcloud.io; then
	   echo ok
	else
	   echo Something went wrong.
	   exit 1
	fi
	res=${PIPESTATUS[0]} 
	echo "return code $res"
	if [[ $res -ne 0 ]]; then
		exit $res
	fi
	cd -
}

#
#	Main
#
echo "Install GAMA project"

if [[ $MSG == *"ci sonarcloud"* ]] || [[ $MESSAGE == *"ci sonarcloud"* ]]; then 	
	mvn_install_with_sonar ummisco.gama.annotations
	mvn_install_with_sonar msi.gama.processor
	mvn_install_with_sonar msi.gama.parent
else		
	mvn_install ummisco.gama.annotations
	mvn_install msi.gama.processor
	mvn_install msi.gama.parent
fi
