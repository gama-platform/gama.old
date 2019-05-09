#! /bin/bash
cd /home/travis/build/gama-platform/gama/ummisco.gama.product/target/products/ummisco.gama.application.product/linux/gtk/x86_64/headless
memory=2048m
outputFile=""
inputFile=""
declare -i i
declare -i j
console="no"
tunneling="no"
hpc="no"
verbose="no"
help="no"

i=0
echo ${!i}

for ((i=1;i<=$#;i=$i+1))
do
if test ${!i} = "-m"
then
i=$i+1
memory=${!i}
i=$i+100
fi
done

for ((i=1;i<=$#;i=$i+1))
do
if test ${!i} = "-c"
then
console="yes"
PARAM=$PARAM\ -c
i=$i+100
fi
done

for ((i=1;i<=$#;i=$i+1))
do
if test ${!i} = "-help"
then
help="yes"
i=$i+1000
fi
done

for ((i=1;i<=$#;i=$i+1))
do
if test ${!i} = "-hpc"
then
i=$i+1
export PARAM=$PARAM\ -hpc\ ${!i}
hpc="yes"
i=$i+1000
fi
done

for ((i=1;i<=$#;i=$i+1))
do
if test ${!i} = "-p"
then
i=$i+1
export PARAM=$PARAM\ -p
tunneling="yes"
i=$i+1000
fi
done

for ((i=1;i<=$#;i=$i+1))
do
if test ${!i} = "-v"
then
i=$i+1
export PARAM=$PARAM\ -v
verbose="yes"
i=$i+100
fi
done

if [ $console = 'no' ] && [ $tunneling = 'no' ] ; then
i=$#
i=$i-1
inputFile=${!i}
fi

if [ $tunneling = 'no' ] ; then
i=$#
i=$i
outputFile=${!i}
fi



 
# assuming this file is within the gama deployment
GAMAHOME=$(cd $(dirname $0)/.. && pwd -P)

gamaDirectory=$(cd $GAMAHOME/plugins && pwd)
DUMPLIST=$(ls  $gamaDirectory/*.jar )

for fic in $DUMPLIST; do
GAMA=$GAMA:$fic
done

passWork=/tmp/.work
if [ $console = 'no' ] && [ $tunneling = 'no' ] ; then
mP=$( cd $(dirname $inputFile) && pwd -P )
mF=$(basename $inputFile)
mfull=$mP/$mF
passWork=$outputFile/.work
fi

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
rm -rf $passWork
if [[ $res -gt 0 ]]; then
	exit $res
fi
