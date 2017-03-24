#!/bin/bash
cd msi.gama.p2updatesite &&
mvn clean install -P p2Repo --settings ../settings.xml && 
cd -