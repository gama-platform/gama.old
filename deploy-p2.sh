#!/bin/sh
cd ummisco.gama.annotations &&
mvn clean deploy --settings ../settings.xml -DskipTests=true -B && 
cd - &&
cd msi.gama.processor &&
mvn clean deploy --settings ../settings.xml -DskipTests=true -B && 
cd - &&
cd msi.gama.parent &&
mvn clean deploy --settings ../settings.xml -DskipTests=true -B && 
cd -

