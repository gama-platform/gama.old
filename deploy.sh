#!/bin/bash
cd ummisco.gama.annotations 
mvn deploy -DskipTests -T 8C -P p2Repo --settings ../settings.xml
cd -
cd msi.gama.processor
mvn deploy -DskipTests -T 8C -P p2Repo --settings ../settings.xml
cd -

change=$(git log --pretty=format: --name-only --since="1 hour ago")
if [[ ${change} == *"msi.gama.ext"* ]]; then		
	cd msi.gama.ext 
	mvn deploy -DskipTests -T 8C -P p2Repo --settings ../settings.xml
	cd -
fi
cd msi.gama.parent
mvn deploy -DskipTests -T 8C -P p2Repo --settings ../settings.xml
cd -


