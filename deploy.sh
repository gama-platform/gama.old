#!/bin/bash
cd ummisco.gama.annotations && 
mvn -q deploy -P p2Repo --settings ../settings.xml && 
cd -
cd msi.gama.processor &&
mvn -q deploy -P p2Repo --settings ../settings.xml && 
cd -
cd msi.gama.parent &&
mvn deploy -P p2Repo --settings ../settings.xml && 
cd -


