cd ummisco.gama.annotations
mvn clean install
cd - 
cd msi.gama.processor 
mvn clean install 
cd - 
cd msi.gama.ext 
mvn clean install
cd -
cd ummisco.gama.feature.dependencies 
mvn clean install
cd -
cd msi.gama.parent 
mvn clean install -f pom_for_eclipse.xml
cd -