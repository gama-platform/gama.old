#! /bin/bash

MESSAGE=$(git log -1 HEAD --pretty=format:%s)
echo $MESSAGE 
if  [[ $MESSAGE == *"ci notest"* ]]; then	 
	exit 0 
else

	echo "GAMA validate is starting..."
	bash $GITHUB_WORKSPACE/ummisco.gama.product/target/products/ummisco.gama.application.product/linux/gtk/x86_64/headless/gama-headless.sh -m 3048m -validate
	res=$?		
	if [[ $res -gt 0 ]]; then	
		rm -rf $passWork
		exit $res
	fi

	echo "GAMA test is starting..."
	bash $GITHUB_WORKSPACE/ummisco.gama.product/target/products/ummisco.gama.application.product/linux/gtk/x86_64/headless/gama-headless.sh -m 3048m -test -failed
	res=$?			
	if [[ $res -gt 0 ]]; then
		rm -rf $passWork
		exit $res
	fi

 
fi	