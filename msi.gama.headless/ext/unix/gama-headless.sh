#! /bin/sh
memory=2048m
declare -i i
declare -i j
for ((i=0;i<=$#;i++))
do

if test ${!i} = "-m"
then
j=$i+1
memory=-m${!j}
fi
if test ${!i} = "-c"
then
export PARAM=$PARAM\ -c
fi
if test ${!i} = "-t"
then
j=$i+1
export PARAM=$PARAM\ -t\ ${!j}
i=$i+1

fi
if [ ${!i} != '-m' ] && [ ${!i} != '-c' ] && [ ${!i} != '-t' ]
then
inputFile=${!i}
j=$i+1
i=$i+1
outputFile=${!j}
fi
done

echo "******************************************************************"
echo "* GAMA version 1.7.0                                             *"
echo "* http://gama-platform.googlecode.com                            *"
echo "* (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners              *"
echo "******************************************************************"

if test ! -f "$inputFile"; then
echo "The input file does not exist. Please check the path of your input file" >&2
exit 1
fi

if test -d "$outputFile"; then
echo "The output directory already exist. Please check the path of your output directory" >&2
exit 1
fi

# assuming this file is within the gama deployment
GAMAHOME=$(cd $(dirname $0)/.. && pwd -P)

gamaDirectory=$(cd $GAMAHOME/plugins && pwd)
DUMPLIST=$(ls  $gamaDirectory/*.jar )

for fic in $DUMPLIST; do
GAMA=$GAMA:$fic
done

mP=$( cd $(dirname $inputFile) && pwd -P )
mF=$(basename $inputFile)
mfull=$mP/$mF
echo "GAMA is starting..."


echo "GAMA is starting..."
exec java -cp $GAMA -Xms512m -Xmx$memory  -Djava.awt.headless=true org.eclipse.core.launcher.Main  -application msi.gama.headless.id4 -data $outputFile/.work $mfull $outputFile
