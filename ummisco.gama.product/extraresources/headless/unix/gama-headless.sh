#!/bin/bash
memory=4096m

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

echo "******************************************************************"
echo "* GAMA version 1.8.1                                             *"
echo "* http://gama-platform.org                                       *"
echo "* (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners                *"
echo "******************************************************************"
# create output folder if not existing
if [ ! -d "${@: -1}" ]; then
    mkdir ${@: -1}
fi
# create workspace in output folder
passWork=${@: -1}/.workspace$(find ${@: -1} -name ".workspace*" | wc -l)
mkdir -p $passWork

if ! java -cp "$( dirname "${BASH_SOURCE[0]}" )"/../plugins/org.eclipse.equinox.launcher*.jar -Xms512m -Xmx$memory -Djava.awt.headless=true org.eclipse.core.launcher.Main -application msi.gama.headless.id4 -data $passWork "$@"; then
    echo "Error in you command, here's the log :"
    cat /$passWork/.metadata/.log
    exit 1
fi
