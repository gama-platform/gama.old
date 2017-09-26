cd ummisco.gama.annotations
mvn -q clean install
cd - 
cd msi.gama.processor 
mvn -q clean install 
cd - 
change=$(git log --pretty=format: --name-only --since="1 hour ago")
if [[ ${change} == *"msi.gama.ext"* ]]; then		
	cd msi.gama.ext 
	mvn -q clean install 
	cd -
fi
cd msi.gama.parent 
mvn -q clean install 
cd -
