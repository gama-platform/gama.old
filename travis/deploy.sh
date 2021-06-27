#!/bin/bash

echo "DEPLOY"
echo $SSH_USER_ID

mkdir -m 0700 -p ~/.ssh && ssh-keyscan $SSH_HOST | tee -a ~/.ssh/known_hosts
echo -e "StrictHostKeyChecking no\n" >> ~/.ssh/config 
ssh-keyscan -H $SSH_HOST >> ~/.ssh/known_hosts
cd msi.gama.parent &&
mvn clean install -P p2Repo --settings ../travis/settings.xml && 
cd - 

