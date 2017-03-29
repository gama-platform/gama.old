#!/bin/bash
cd ummisco.gama.annotations &&
mvn clean install -P p2Repo --settings ../settings.xml && 
cd -

cd msi.gama.processor &&
mvn clean install -P p2Repo --settings ../settings.xml && 
cd -

cd msi.gama.parent &&
mvn clean install -P p2Repo --settings ../settings.xml && 
cd -


