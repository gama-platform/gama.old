cd ummisco.gama.annotations
mvn -q clean install
cd - 
cd msi.gama.processor 
mvn -q clean install 
cd - 
cd msi.gama.ext 
mvn -q clean install
cd -
cd ummisco.gama.feature.dependencies 
mvn -q clean install
cd -
cd msi.gama.parent 
mvn clean install 
cd -
