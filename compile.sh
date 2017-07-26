cd ummisco.gama.annotations &&
mvn -X clean install &&
cd - &&
cd msi.gama.processor &&
mvn -q clean install &&
cd - &&
cd msi.gama.parent &&
mvn -q clean compile && 
cd -
