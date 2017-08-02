#!/bin/bash
cd ummisco.gama.annotations && 
mvn -q clean deploy -P p2Repo --settings ../settings.xml -Dmaven.test.skip=true && 
cd -
cd msi.gama.processor &&
mvn -q clean deploy -P p2Repo --settings ../settings.xml -Dmaven.test.skip=true && 
cd -
cd msi.gama.parent &&
mvn -q clean deploy -P p2Repo --settings ../settings.xml -Dmaven.test.skip=true && 
cd -


