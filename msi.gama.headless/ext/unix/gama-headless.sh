#! /bin/sh
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



echo "******************************************************************"
echo "* GAMA version 1.7.0 V7                                          *"
echo "* http://gama-platform.org                                       *"
echo "* (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners              *"
echo "******************************************************************"
if [ $help = "yes" ]  ;  then
echo ""
echo " sh ./gama-headless.sh [Options] [XML Input] [output directory]"
echo ""
echo ""
echo "List of available options:"
echo "      -help     -- get the help of the command line"
echo "      -m mem    -- allocate memory (ex 2048m)"
echo "      -c        -- start the console to write xml parameter file"
echo "      -hpc core -- set the number of core available for experimentation"
echo "      -p        -- start piplines to interact with another framework"
echo ""
echo ""
exit 1
fi




if [ ! -f "$inputFile" ] && [ $console = "no" ] && [ $tunneling = "no" ] ;  then
echo "The input or output file are not specied. Please check the path of your files and output file."
echo "Use the help for more information (./gama-headless -help)"
exit 1
fi

if   [ -d "$inputFile" ]  && [ $console = "no" ] && [ $tunneling = "no" ] ; then
    echo "The defined input is not an XML parameter file" 
    echo "Use the help for more information (./gama-headless -help)"
    exit 1
fi

if [ $tunneling = "no" ] && [ -d "$outputFile" ]   ; then
echo "The output directory already exist. Please check the path of your output directory" 
echo "Use the help for more information (./gama-headless -help)"
exit 1
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
exec java -cp $GAMA -Xms512m -Xmx$memory  -Djava.awt.headless=true org.eclipse.core.launcher.Main  -application msi.gama.headless.id4 -data $passWork $PARAM $mfull $outputFile
