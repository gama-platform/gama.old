#!/bin/bash
cd msi.gama.parent &&
mvn clean deploy --settings ../settings.xml && 
cd -