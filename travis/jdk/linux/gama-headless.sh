#!/bin/bash

memory="0"

for arg do
  shift
  case $arg in
    -m) 
    memory="${1}" 
    shift 
    ;;
    *) 
    set -- "$@" "$arg" 
    ;;
  esac
done

if [ $memory == "0" ]; then
  memory=$(grep Xmx "$( dirname $( realpath "${BASH_SOURCE[0]}" ) )"/../Gama.ini || echo "-Xmx4096m")
else
  memory=-Xmx$memory
fi

workspaceCreate=0
case "$@" in 
  *-help*|*-version*|*-validate*|*-test*|*-xml*|*-batch*|*-write-xmi*)
    workspaceCreate=1
    ;;
esac


echo "******************************************************************"
echo "* GAMA version 1.9.2                                             *"
echo "* http://gama-platform.org                                       *"
echo "* (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners                *"
echo "******************************************************************"
passWork=.workspace
# w/ output folder
if [ $workspaceCreate -eq 0 ]; then
  # create output folder if not existing
  if [ ! -d "${@: -1}" ]; then
      mkdir ${@: -1}
  fi
  # create workspace in output folder
  passWork=${@: -1}/.workspace$(find ${@: -1} -name ".workspace*" | wc -l)
  mkdir -p $passWork

# w/o output folder
else
  # create workspace in current folder
  passWork=.workspace$(find ./ -maxdepth 1 -name ".workspace*" | wc -l)
fi

if ! "$( dirname $( realpath "${BASH_SOURCE[0]}" ) )"/../jdk/bin/java -cp "$( dirname $( realpath "${BASH_SOURCE[0]}" ) )"/../plugins/org.eclipse.equinox.launcher*.jar -Xms512m $memory -Djava.awt.headless=true org.eclipse.core.launcher.Main -configuration "$( dirname $( realpath "${BASH_SOURCE[0]}" ) )"/configuration -application msi.gama.headless.product -data $passWork "$@"; then
    echo "Error in you command, here's the log :"
    cat $passWork/.metadata/.log
    exit 1
fi
