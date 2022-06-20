cd ummisco.gama.annotations
mvnd clean install $1
cd - 
cd msi.gama.processor 
mvnd clean install $1
cd - 
cd msi.gama.parent 
mv pom.xml pom1.xml
mv pom_m1.xml pom.xml
mvnd clean install $1
mv pom.xml pom_m1.xml
mv pom1.xml pom.xml
cd -