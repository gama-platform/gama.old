cd ummisco.gama.annotations &&
mvn clean deploy -B -DcreateChecksum=true --settings ../settings.xml  && 
cd - &&
cd msi.gama.processor &&
mvn clean deploy -B -DcreateChecksum=true --settings ../settings.xml  &&
cd - &&
cd msi.gama.parent &&
mvn -q clean deploy -B -DcreateChecksum=true --settings ../settings.xml &&
cd -