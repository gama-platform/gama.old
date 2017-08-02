#!/bin/bash
cd ummisco.gama.annotations && 
mvn -q clean deploy -P p2Repo --settings ../settings.xml && 
cd -
cd msi.gama.processor &&
mvn -q clean deploy -P p2Repo --settings ../settings.xml && 
cd -
cd msi.gama.parent &&
mvn deploy -P p2Repo --settings ../settings.xml && 
cd -


