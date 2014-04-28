#! /bin/sh
inputFile=$1
outputFile=$2
memory=2048m

echo "******************************************************************"
echo "* GAMA version 1.6.1                                             *"
echo "* http://gama-platform.googlecode.com                            *"
echo "* (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners              *"
echo "******************************************************************"


#if [[ ( "$1" == "-?" || $1 == "--help" || "x$1" == "" || "x$2" == "x" ) ]]; then
if test "$1" = "-?" -o "$1" = "--help" -o "x$1" = "x" -o "x$2" = "x" ; then
	echo "Help:" >&2
	echo " command: sh gama-headless.sh [opt] xmlInputFile outputDirectory " >&2
	echo " option:" >&2
	echo " -m to define the memory allocated by the simulation" >&2
	exit 1
fi


if test "$1" = "-m" ; then
        memory=$2
        inputFile=$3
        outputFile=$4
fi

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

exec java -cp $GAMA -Xms512m -Xmx$memory  -Djava.awt.headless=true org.eclipse.core.launcher.Main  -application msi.gama.headless.id4 $mfull $outputFile
