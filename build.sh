cd msi.gama.processor &&
mvn clean install &&
cd - &&
cd msi.gama.parent &&
mvn clean install &&
cd - &&
cd  msi.gama.application &&
mvn clean install -f pom-product.xml
cd -

