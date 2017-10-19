cd ummisco.gama.annotations
mvn clean install
cd - 
cd msi.gama.processor 
mvn clean install 
cd - 
cd msi.gama.parent 
mvn clean install -T 20C
cd -