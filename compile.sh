cd ummisco.gama.annotations &&
mvn -q clean compile &&
cd - &&
cd msi.gama.processor &&
mvn -q clean compile &&
cd - &&
cd msi.gama.parent &&
mvn -q clean compile && 
cd -
