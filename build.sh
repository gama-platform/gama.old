
cd msi.gama.processor &&
mvn clean install &&
cd - &&
cd msi.gama.ext &&
mvn clean install -f pom-dependencies.xml &&
cd - &&
cd msi.gama.parent &&
mvn clean install &&
cd - &&
cd  msi.gama.application &&
mvn clean install -f product-pom.xml &&
cd -

