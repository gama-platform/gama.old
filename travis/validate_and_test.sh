#! /bin/bash
cd /home/travis/build/gama-platform/gama/ummisco.gama.product/target/products/ummisco.gama.application.product/linux/gtk/x86_64/headless
memory=2048m 


  
DUMPLIST=$(ls  ../plugins/*.jar )

for fic in $DUMPLIST; do
GAMA=$GAMA:$fic
done

passWork=.work 

echo "GAMA is starting..."
#exec

#GAMA=Gamaq
exec java -cp $GAMA -Xms512m -Xmx$memory  -Djava.awt.headless=true org.eclipse.core.launcher.Main  -application msi.gama.headless.id4 -data $passWork -validate 
res=$?
		
if [[ $res -gt 0 ]]; then	
	rm -rf $passWork
	exit $res
fi



exec java -cp $GAMA -Xms512m -Xmx$memory  -Djava.awt.headless=true org.eclipse.core.launcher.Main  -application msi.gama.headless.id4 -data $passWork -test -failed   
res=$?			
if [[ $res -gt 0 ]]; then
	rm -rf $passWork
	exit $res
fi
