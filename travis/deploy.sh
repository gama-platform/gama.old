#!/bin/bash


function mvn_deploy() {
	echo "Deploying " $1
	cd $1
	mvn deploy -DskipTests -Dcheckstyle.skip -T 8C -P p2Repo --settings ../travis/settings.xml
	res=$?
	if [[ $res -gt 0 ]]; then
		exit $res
	fi
	cd -
}
 
cd msi.gama.parent &&
mvn clean install -P p2Repo --settings ../travis/settings.xml && 
cd - 

