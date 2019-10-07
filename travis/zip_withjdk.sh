#!/bin/bash
set -e
echo "zip_withjdk"		
COMMIT=$@

REPO="gama-platform/gama"
RELEASE="daily"
thePATH="/home/travis/build/gama-platform/gama/ummisco.gama.product/target/products/Gama1.7"















COMMIT="${COMMIT:0:7}"

timestamp=$(date '+_%D')

SUFFIX=$timestamp'_'$COMMIT'.zip'
echo $SUFFIX



n=0
RELEASEFILES[$n]="$thePATH-linux.gtk.x86_64.zip"
NEWFILES[$n]='GAMA1.8_Linux_64bits'$SUFFIX 
n=1
RELEASEFILES[$n]="$thePATH-macosx.cocoa.x86_64.zip"
NEWFILES[$n]='GAMA1.8_Mac_64bits'$SUFFIX
n=2
RELEASEFILES[$n]="$thePATH-win32.win32.x86_64.zip" 
NEWFILES[$n]='GAMA1.8_Win_64bits'$SUFFIX
n=3
RELEASEFILES[$n]="$thePATH-linux.gtk.x86_64_withJDK.zip"
NEWFILES[$n]='GAMA1.8_EmbeddedJDK_Linux_64bits'$SUFFIX
n=4
RELEASEFILES[$n]="$thePATH-win32.win32.x86_64_withJDK.zip" 
NEWFILES[$n]='GAMA1.8_EmbeddedJDK_Win_64bits'$SUFFIX
n=5
RELEASEFILES[$n]="$thePATH-macosx.cocoa.x86_64_withJDK.zip"
NEWFILES[$n]='GAMA1.8_EmbeddedJDK_Mac'$SUFFIX


git clone --depth=50 --branch=master https://github.com/gama-platform/jdk.git  jdk	

rem111(){
sudo rm "${RELEASEFILES[0]}"
sudo rm "${RELEASEFILES[1]}"
sudo rm "${RELEASEFILES[2]}"


	
cd /home/travis/build/gama-platform/gama/ummisco.gama.product/target/products/ummisco.gama.application.product/linux/gtk/x86_64
sudo zip -9 -qyr "${RELEASEFILES[0]}" . && echo "compressed ${RELEASEFILES[0]}" || echo "compress fail ${RELEASEFILES[0]}"
cd ../../../../../../../




cd /home/travis/build/gama-platform/gama/ummisco.gama.product/target/products/ummisco.gama.application.product/win32/win32/x86_64
sudo zip -9 -qr "${RELEASEFILES[2]}" . && echo "compressed ${RELEASEFILES[2]}" || echo "compress fail ${RELEASEFILES[2]}"
cd ../../../../../../../





cd /home/travis/build/gama-platform/gama/ummisco.gama.product/target/products/ummisco.gama.application.product/macosx/cocoa/x86_64
sudo zip -9 -qyr "${RELEASEFILES[1]}" . && echo "compressed ${RELEASEFILES[1]}" || echo "compress fail ${RELEASEFILES[1]}"
cd ../../../../../../../

}



























sudo cp -R jdk/linux/64/1.8.171/jdk /home/travis/build/gama-platform/gama/ummisco.gama.product/target/products/ummisco.gama.application.product/linux/gtk/x86_64
sudo cp jdk/linux/64/Gama.ini /home/travis/build/gama-platform/gama/ummisco.gama.product/target/products/ummisco.gama.application.product/linux/gtk/x86_64
sudo cp jdk/linux/gama-headless.sh /home/travis/build/gama-platform/gama/ummisco.gama.product/target/products/ummisco.gama.application.product/linux/gtk/x86_64/headless






sudo cp -R jdk/win/64/1.8.171/jdk /home/travis/build/gama-platform/gama/ummisco.gama.product/target/products/ummisco.gama.application.product/win32/win32/x86_64
sudo cp jdk/win/64/Gama.ini /home/travis/build/gama-platform/gama/ummisco.gama.product/target/products/ummisco.gama.application.product/win32/win32/x86_64
sudo cp jdk/win/gama-headless.bat /home/travis/build/gama-platform/gama/ummisco.gama.product/target/products/ummisco.gama.application.product/win32/win32/x86_64/headless







sudo cp -R jdk/mac/64/1.8.171/jdk /home/travis/build/gama-platform/gama/ummisco.gama.product/target/products/ummisco.gama.application.product/macosx/cocoa/x86_64/Gama.app/Contents
sudo cp jdk/mac/64/Gama.ini /home/travis/build/gama-platform/gama/ummisco.gama.product/target/products/ummisco.gama.application.product/macosx/cocoa/x86_64/Gama.app/Contents/Eclipse
	

	
cd /home/travis/build/gama-platform/gama/ummisco.gama.product/target/products/ummisco.gama.application.product/linux/gtk/x86_64

sudo zip -9 -qr "${RELEASEFILES[3]}" . && echo "compressed ${RELEASEFILES[3]}" || echo "compress fail ${RELEASEFILES[3]}"

cd ../../../../../../../




cd /home/travis/build/gama-platform/gama/ummisco.gama.product/target/products/ummisco.gama.application.product/win32/win32/x86_64

sudo zip -9 -qr "${RELEASEFILES[4]}" . && echo "compressed ${RELEASEFILES[4]}" || echo "compress fail ${RELEASEFILES[4]}"

cd ../../../../../../../





cd /home/travis/build/gama-platform/gama/ummisco.gama.product/target/products/ummisco.gama.application.product/macosx/cocoa/x86_64

sudo zip -9 -qyr "${RELEASEFILES[5]}" . && echo "compressed ${RELEASEFILES[5]}" || echo "compress fail ${RELEASEFILES[5]}"

cd ../../../../../../../



echo DONE
