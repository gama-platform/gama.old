#! /bin/bash
cd /home/travis/build/gama-platform/gama/ummisco.gama.product/target/products/ummisco.gama.application.product/linux/gtk/x86_64/headless
memory=2048m
declare -i i

i=0

for ((i=1;i<=$#;i=$i+1))
do
if test ${!i} = "-m"
then
    i=$i+1
    memory=${!i}
else
    PARAM=$PARAM\ ${!i}
    i=$i+1
    PARAM=$PARAM\ ${!i}
fi
done

passWork=.work$RANDOM

java  -cp ../plugins/org.eclipse.equinox.launcher*.jar -Xms512m -Xmx2048m  -Djava.awt.headless=true org.eclipse.core.launcher.Main  -application msi.gama.headless.id4 -data $passWork -validate 

java  -cp ../plugins/org.eclipse.equinox.launcher*.jar -Xms512m -Xmx2048m  -Djava.awt.headless=true org.eclipse.core.launcher.Main  -application msi.gama.headless.id4 -data $passWork -validate 
res=$?
		
if [[ $res -gt 0 ]]; then	
	rm -rf $passWork
	exit $res
fi



java  -cp ../plugins/org.eclipse.equinox.launcher*.jar -Xms512m -Xmx2048m  -Djava.awt.headless=true org.eclipse.core.launcher.Main  -application msi.gama.headless.id4 -data $passWork -test -failed   
res=$?			
rm -rf $passWork
if [[ $res -gt 0 ]]; then
	exit $res
fi
