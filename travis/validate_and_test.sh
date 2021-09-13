#! /bin/bash

MESSAGE=$(git log -1 HEAD --pretty=format:%s)
echo $MESSAGE 
if  [[ $MESSAGE == *"ci notest"* ]]; then	 
	exit 0 
else

	cd $GITHUB_WORKSPACE/ummisco.gama.product/target/products/ummisco.gama.application.product/linux/gtk/x86_64/headless
	memory=3048m 


	  
	DUMPLIST=$(ls  ../plugins/*.jar )

	for fic in $DUMPLIST; do
	GAMA=$GAMA:$fic
	done

	passWork=.work 

	echo "GAMA is starting..."
	#exec

	#GAMA=Gamaq
	# Run -validate or cat log file and close
	java -cp $GAMA -Xms512m -Xmx$memory  -Djava.awt.headless=true org.eclipse.core.launcher.Main  -application msi.gama.headless.id4 -data $passWork -validate || cat $GITHUB_WORKSPACE/ummisco.gama.product/target/products/ummisco.gama.application.product/linux/gtk/x86_64/headless/.work/.metadata/.log && exit $1
	res=$?		
	if [[ $res -gt 0 ]]; then	
		rm -rf $passWork
		exit $res
	fi

	echo "GAMA is starting..."
	java -cp $GAMA -Xms512m -Xmx$memory  -Djava.awt.headless=true org.eclipse.core.launcher.Main  -application msi.gama.headless.id4 -data $passWork -test -failed || cat $GITHUB_WORKSPACE/ummisco.gama.product/target/products/ummisco.gama.application.product/linux/gtk/x86_64/headless/.work/.metadata/.log && exit $1
	res=$?			
	if [[ $res -gt 0 ]]; then
		rm -rf $passWork
		exit $res
	fi

 
fi	