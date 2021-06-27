#!/bin/bash

echo "DEPLOY"
echo ${ env.SSH_USER_ID }
cd msi.gama.parent &&
mvn clean install -P p2Repo --settings ../travis/settings.xml && 
cd - 

