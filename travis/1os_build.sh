

if [[ $(uname -m) == 'arm64' ]]; then
  
	cp msi.gama.parent/pom.xml msi.gama.parent/ori_pom.xml
	rm msi.gama.parent/pom.xml
	cp travis/os/aarch64/parent_pom.xml msi.gama.parent/pom.xml
	cp ummisco.gama.product/pom.xml ummisco.gama.product/ori_pom.xml
	rm ummisco.gama.product/pom.xml
	cp travis/os/aarch64/product_pom.xml ummisco.gama.product/pom.xml
	
	
	bash travis/build.sh
	
	rm msi.gama.parent/pom.xml
	mv msi.gama.parent/ori_pom.xml msi.gama.parent/pom.xml
	rm ummisco.gama.product/pom.xml
	mv ummisco.gama.product/ori_pom.xml ummisco.gama.product/pom.xml
	  
fi
