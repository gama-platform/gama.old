#!/bin/bash
cd ummisco.gama.annotations && 
mvn deploy -DskipTests -T 8C -P p2Repo --settings ../settings.xml && 
cd -
cd msi.gama.processor &&
mvn deploy -DskipTests -T 8C -P p2Repo --settings ../settings.xml && 
cd -
cd msi.gama.parent &&
mvn deploy -DskipTests -T 8C -P p2Repo --settings ../settings.xml && 
cd -


