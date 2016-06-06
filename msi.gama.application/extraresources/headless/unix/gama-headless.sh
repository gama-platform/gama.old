#! /bin/sh
memory=2048m
declare -i i

i=0
echo ${!i}

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

echo "******************************************************************"
echo "* GAMA version 1.7.0 V7                                          *"
echo "* http://gama-platform.org                                       *"
echo "* (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners              *"
echo "******************************************************************"
passWork=.work$RANDOM

java -cp ../eclipse/plugins/org.eclipse.equinox.launcher_1.3.100.v20150511-1540.jar -Xms512m -Xmx$memory  -Djava.awt.headless=true org.eclipse.core.launcher.Main  -application msi.gama.headless.id4 -data $passWork $PARAM $mfull $outputFile
rm -rf $passWork
