#!/bin/bash

#
#	SCRIPT watchdog
#

# check if 2 param
oldVersion="1.9.0"
newVersion="1.9.1"

# Set path
path="$( dirname $( realpath "${BASH_SOURCE[0]}" ) )/.."

#
#	Should I clean maven ?
#
cd ummisco.gama.annotations && mvn clean && cd -
cd msi.gama.processor && mvn clean && cd -
cd msi.gama.parent && mvn clean && cd -

#
#	UPDATING MAVEN
#

echo "Update .qualifier"
find $path -name "*.xml" -exec sed -i "s/$oldVersion.qualifier/$newVersion.qualifier/g" {} \;
find $path -name "*.product" -exec sed -i "s/$oldVersion.qualifier/$newVersion.qualifier/g" {} \;
find $path -name "MANIFEST.MF" -exec sed -i "s/$oldVersion.qualifier/$newVersion.qualifier/g" {} \;
echo "Update -SNAPSHOT"
find $path -name "*.xml" -exec sed -i "s/$oldVersion-SNAPSHOT/$newVersion-SNAPSHOT/g" {} \;

echo "Finish updating meta-data from .product"
find $path -name "*.product" -exec sed -i "s/$oldVersion/$newVersion/g" {} \;

echo "Update sites url"
find $path -name "feature.xml" -exec sed -i "s/$oldVersion Update/$newVersion Update/g" {} \;
find $path -name "feature.xml" -exec sed -i "s/org\/$oldVersion/org\/$newVersion/g" {} \;
find $path -name "pom.xml" -exec sed -i "s/$oldVersion<\/url>/$newVersion<\/url>/g" {} \;

#
#	UPDATING JAVA HEADERS
#

echo "Update JAVA header version"
find $path -name "*.java" -exec sed -i "s/(v.$oldVersion)/(v.$newVersion)/g" {} \;
echo "Update JAVA header copyright"
find $path -name "*.java" -exec sed -i "s/(c) 2007-2022 UMI 209/(c) 2007-$( date "+%Y" ) UMI 209/g" {} \;

echo "Update everything in ummisco.gama.product/extraresources"
find $path/ummisco.gama.product/extraresources -not -wholename "*/samples/*" -type f -exec sed -i "s/$oldVersion/$newVersion/g" {} \;

#
#	UPDATING Travis folder
#

echo "Updating travis folder"
find $path/travis -type f -exec sed -i "s/$oldVersion/$newVersion/g" {} \;


#
#	EXTRA Forgotten
#
echo "Update extra individual files"

sed -i "# GAMA $newVersion" $path/README.md
sed -i "s/V$oldVersion http/V$newVersion http/g" $path/msi.gama.application/plugin.xml

sed -i "s/$oldVersion/$newVersion/g" $path/msi.gama.core/src/msi/gama/runtime/GAMA.java
sed -i "s/$oldVersion/$newVersion/g" $path/ummisco.gama.annotations/src/msi/gama/precompiler/doc/utils/Constants.java