cd ummisco.gama.annotations &&
mvn -q clean install &&
cd - &&
cd msi.gama.processor &&
mvn -q clean install &&
cd - &&
cd msi.gama.parent &&
mvn -q clean install &&
cd -
