#!/bin/bash
cd ummisco.gama.annotations &&
mvn -q deploy -B -DuniqueVersion=false -DcreateChecksum=true --settings ../settings.xml  && 
cd - &&
cd msi.gama.processor &&
mvn -q deploy -B -DuniqueVersion=false -DcreateChecksum=true --settings ../settings.xml  &&
cd - &&
cd msi.gama.parent &&
mvn -q deploy -B -DuniqueVersion=false -DcreateChecksum=true --settings ../settings.xml &&
cd -

