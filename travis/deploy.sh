#!/bin/bash

echo "DEPLOY"
echo $SSH_USER_ID

echo -e "StrictHostKeyChecking no\n" >> ~/.ssh/config 
mkdir -m 0700 -p ~/.ssh && ssh-keyscan github.com | tee -a ~/.ssh/known_hosts
ssh-keyscan -H ${{ secrets.SSH_HOST }} >> ~/.ssh/known_hosts
cd msi.gama.parent &&
mvn clean install -P p2Repo --settings ../travis/settings.xml && 
cd - 

