cd ummisco.gama.annotations
mvn -ntp clean install $1
cd - 
cd msi.gama.processor 
mvn -ntp clean install $1
cd - 
cd msi.gama.parent 
mvn -ntp clean install $1
cd -
