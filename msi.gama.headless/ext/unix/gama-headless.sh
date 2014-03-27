inputFile=$1
outputFile=$2
memory=2048m
clear

echo "\n\n\n\n\n"
echo "******************************************************************"
echo "* GAMA version 1.6.1                                             *"
echo "* http://gama-platform.googlecode.com                            *"
echo "* (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners              *"
echo "******************************************************************"


if [[  ( $1 == "-?" || $1 == "--help" || $1 == "" || $2 == "") ]]
then
echo "Help:"
echo " command: sh gama-headless.sh [opt] xmlInputFile outputDirectory "
echo "\n option:"
echo " -m to define the memory allocated by the simulation"
echo "\n\n\n\n\n"
exit
fi


if [[  ( $1 == "-m" ) ]]
    then
        memory=$2
        inputFile=$3
        outputFile=$4
fi

if [[ ! ( -f $inputFile ) ]]
then
    echo "The input file does not exist. Please check the path of your input file"
    echo "\n\n\n\n\n"
    exit
fi

if [[  ( -d $outputFile ) ]]
then
echo "The output directory already exist. Please check the path of your output directory"
    echo "\n\n\n\n\n"
exit
fi

echo "\n\n\n\n\n"

export  LIB_HOME=./lib
export gamaDirectory=../plugins
#echo $gamaDirectory
DUMPLIST=`ls  $gamaDirectory/*.jar `

nb=0
for fic in $DUMPLIST; do
#    echo $fic
export GAMA=$GAMA:$fic
#     nb='expr $nb + 1'

done
mP=$( cd $(dirname $inputFile) ; pwd -P )
mF=$(basename $inputFile)
mfull=$mP/$mF

echo "GAMA is starting..."

java -cp $GAMA  -Xms512m -Xmx$memory  -Djava.awt.headless=true org.eclipse.core.launcher.Main  -application msi.gama.headless.id4 "$mfull" "$outputFile"