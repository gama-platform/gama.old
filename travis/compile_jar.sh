#!/bin/bash

MESSAGE=$(git log -1 HEAD --pretty=format:%s)
echo $MESSAGE

cd msi.gama.headless 
mvn clean install -f pom2.xml -T1 
cd -