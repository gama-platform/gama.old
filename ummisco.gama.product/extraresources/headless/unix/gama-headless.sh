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
  memory=$(grep Xmx "$( dirname $( realpath "${BASH_SOURCE[0]}" ) )"/../Gama.ini || echo "-Xmx4096m")
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
echo "* GAMA version 1.9.3                                             *"
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

if ! java -cp "$( dirname $( realpath "${BASH_SOURCE[0]}" ) )"/../plugins/org.eclipse.equinox.launcher*.jar -Xms512m $memory --add-exports=java.base/java.lang=ALL-UNNAMED --add-exports=java.desktop/sun.awt=ALL-UNNAMED --add-exports=java.desktop/sun.java2d=ALL-UNNAMED --add-exports=java.desktop/sun.awt.image=ALL-UNNAMED --add-exports=java.base/java.math=ALL-UNNAMED --add-exports=java.base/java.lang=ALL-UNNAMED  --add-exports=java.base/sun.nio.ch=ALL-UNNAMED  --add-opens=java.base/java.lang=ALL-UNNAMED  --add-opens=java.base/jdk.internal.loader=ALL-UNNAMED  --add-opens=java.base/java.math=ALL-UNNAMED   --add-opens=java.base/java.util=ALL-UNNAMED  --add-opens=java.base/java.util.concurrent=ALL-UNNAMED  --add-opens=java.base/java.util.concurrent.atomic=ALL-UNNAMED  --add-opens=java.base/java.net=ALL-UNNAMED  --add-opens=java.base/java.security=ALL-UNNAMED  --add-opens=java.desktop/java.awt=ALL-UNNAMED  --add-opens=java.base/java.io=ALL-UNNAMED  --add-opens=java.base/java.time=ALL-UNNAMED  --add-opens=java.base/java.util.concurrent.locks=ALL-UNNAMED --add-opens=java.base/java.text=ALL-UNNAMED  --add-opens=java.base/java.lang.ref=ALL-UNNAMED  --add-opens=java.sql/java.sql=ALL-UNNAMED -Djava.awt.headless=true org.eclipse.core.launcher.Main -configuration "$( dirname $( realpath "${BASH_SOURCE[0]}" ) )"/configuration -application msi.gama.headless.product -data $passWork "$@"; then
    echo "Error in you command, here's the log :"
    cat $passWork/.metadata/.log
    exit 1
fi
