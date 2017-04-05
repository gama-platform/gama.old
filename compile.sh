cd ummisco.gama.annotations &&
mvn -T 2C clean compile &&
cd - &&
cd msi.gama.processor &&
mvn -T 2C clean compile &&
cd - &&
cd msi.gama.parent &&
mvn -T 2C clean compile &&
cd -
