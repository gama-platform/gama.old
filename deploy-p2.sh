#!/bin/sh

cd msi.gama.p2updatesite &&
mvn clean install -X -P uploadRepo && 
cd -
