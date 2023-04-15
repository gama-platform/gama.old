#!/bin/bash

javaVersion=$(java -version 2>&1 | head -n 1 | cut -d "\"" -f 2)
# Check if good Java version before everything
if [[ ${javaVersion:2} == 17 ]]; then
  echo "You should use Java 17 to run GAMA"
  echo "Found you using version : $javaVersion"
  exit 1
fi

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

if [[ $memory == "0" ]]; then
  memory=$(grep Xmx "$( dirname "${BASH_SOURCE[0]}" )"/../Eclipse/Gama.ini || echo "-Xmx4096m")
else
  memory=-Xmx$memory
fi

workspaceCreate=0
case "$@" in 
  *-help*|*-version*|*-validate*|*-test*|*-xml*|*-batch*|*-write-xmi*|*-socket*)
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
  # `expr` use is to remove whitespace from MacOS's result
  passWork=${@: -1}/.workspace$(find ${@: -1} -name ".workspace*" | expr $(wc -l))
  mkdir -p $passWork

# w/o output folder
else
  # create workspace in current folder
  passWork=${@: -1}/.workspace$(find ${@: -1} -name ".workspace*" | expr $(wc -l))
fi

if ! java -cp "$( dirname "${BASH_SOURCE[0]}" )"/../Eclipse/plugins/org.eclipse.equinox.launcher*.jar -Xms512m $memory -Djava.awt.headless=true org.eclipse.core.launcher.Main -configuration "$( dirname "${BASH_SOURCE[0]}" )"/configuration -application msi.gama.headless.product -data $passWork "$@"; then
    echo "Error in you command, here's the log :"
    cat $passWork/.metadata/.log
    exit 1
fi
