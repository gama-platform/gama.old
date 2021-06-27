#!/bin/bash

bash $GITHUB_WORKSPACE/travis/decrypt_secret.sh
echo "DEPLOY" 
mkdir -m 0700 -p ~/.ssh 
chmod 750 ~
chmod 700 ~/.ssh 
echo -e "Host *\n" >> ~/.ssh/config 
echo -e "IdentitiesOnly yes\n" >> ~/.ssh/config 
echo -e "Port 22\n" >> ~/.ssh/config 
echo -e "StrictHostKeyChecking no\n" >> ~/.ssh/config 
echo -e "PubkeyAuthentication no\n" >> ~/.ssh/config 
cat ~/.ssh/config 
ssh-keyscan -H $SSH_HOST >> ~/.ssh/known_hosts
cd msi.gama.p2updatesite &&
mvn clean install -P p2Repo --settings ../travis/settings.xml && 
cd - 

