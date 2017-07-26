cd ummisco.gama.annotations &&
mvn -X clean install &&
cd - &&
cd msi.gama.processor &&
mvn -X clean install &&
cd - &&
cd msi.gama.parent &&
mvn -X clean compile && 
cd -
