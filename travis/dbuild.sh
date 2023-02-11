cd ummisco.gama.annotations
mvnd clean install $1
cd - 
cd msi.gama.processor 
mvnd clean install $1
cd - 
cd msi.gama.parent 
mv pom.xml pom1.xml
cp pom_m1.xml pom.xml 
mvn clean install -Dmaven.test.skip $1
rm pom.xml
mv pom1.xml pom.xml
cd - 