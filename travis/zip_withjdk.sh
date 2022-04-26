#!/bin/bash
set -e
echo "zip_withjdk"		
COMMIT=$@

REPO="gama-platform/gama"
RELEASE="1.8.2"
thePATH="$GITHUB_WORKSPACE/ummisco.gama.product/target/products/Gama1.7"


cd $GITHUB_WORKSPACE/ummisco.gama.product/target/products


MESSAGE=$(git log -1 HEAD --pretty=format:%s)
echo $MESSAGE










COMMIT="${COMMIT:0:7}"

BRANCH_NAME=$(echo $GITHUB_REF | cut -d'/' -f 3)
COMMIT=$(echo $GITHUB_SHA | cut -c1-8)
timestamp=$(date '+_%D')

SUFFIX=$timestamp'_'$COMMIT'.zip'
echo $SUFFIX



n=0
RELEASEFILES[$n]="$thePATH-linux.gtk.x86_64.zip" 
n=1
RELEASEFILES[$n]="$thePATH-macosx.cocoa.x86_64.zip" 
n=2
RELEASEFILES[$n]="$thePATH-macosx.cocoa.aarch64.zip" 
n=3
RELEASEFILES[$n]="$thePATH-win32.win32.x86_64.zip"  
n=4
RELEASEFILES[$n]="$thePATH-linux.gtk.x86_64_withJDK.zip" 
n=5
RELEASEFILES[$n]="$thePATH-win32.win32.x86_64_withJDK.zip"  
n=6
RELEASEFILES[$n]="$thePATH-macosx.cocoa.x86_64_withJDK.zip" 
n=7
RELEASEFILES[$n]="$thePATH-macosx.cocoa.aarch64_withJDK.zip" 



wget -q $(curl https://api.github.com/repos/adoptium/temurin17-binaries/releases/latest | grep "/OpenJDK17U-jdk_x64_linux.*.gz\"" | cut -d ':' -f 2,3 | tr -d \") -O "jdk_linux_17.tar.gz"
wget -q $(curl https://api.github.com/repos/adoptium/temurin17-binaries/releases/latest | grep "/OpenJDK17U-jdk_x64_window.*.zip\"" | cut -d ':' -f 2,3 | tr -d \") -O "jdk_win_17.zip"
wget -q $(curl https://api.github.com/repos/adoptium/temurin17-binaries/releases/latest | grep "/OpenJDK17U-jdk_x64_mac.*.gz\"" | cut -d ':' -f 2,3 | tr -d \") -O "jdk_osx_17.tar.gz"
#wget -q $(curl https://api.github.com/repos/adoptium/temurin17-binaries/releases/latest | grep "/OpenJDK17U-jdk_aarch64_mac.*.gz\"" | cut -d ':' -f 2,3 | tr -d \") -O "jdk_osx_aarch_17.tar.gz"
wget -q 'https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.2%2B8/OpenJDK17U-jdk_aarch64_mac_hotspot_17.0.2_8.tar.gz' -O "jdk_osx_aarch_17.tar.gz"

mkdir  jdk_linux
mkdir  jdk_win
mkdir  jdk_osx
mkdir  jdk_osx_aarch


echo "unzip jdk linux"	
tar -zxf jdk_linux_17.tar.gz -C jdk_linux/
mv jdk_linux/jdk-17* jdk_linux/jdk

echo "unzip jdk osx"	
tar -zxf jdk_osx_17.tar.gz -C jdk_osx/ 
mv jdk_osx/jdk-17* jdk_osx/jdk 

echo "unzip jdk osx_aarch"	
tar -zxf jdk_osx_aarch_17.tar.gz -C jdk_osx_aarch/ 
mv jdk_osx_aarch/jdk-17* jdk_osx_aarch/jdk 

echo "unzip jdk win"	
unzip -q jdk_win_17.zip -d jdk_win
mv jdk_win/jdk-17* jdk_win/jdk 











sudo cp -R jdk_linux/jdk $GITHUB_WORKSPACE/ummisco.gama.product/target/products/ummisco.gama.application.product/linux/gtk/x86_64
#sudo cp $GITHUB_WORKSPACE/travis/jdk/linux/64/Gama.ini $GITHUB_WORKSPACE/ummisco.gama.product/target/products/ummisco.gama.application.product/linux/gtk/x86_64
echo "-vm" > Gama.ini
echo "./jdk/bin/java" >> Gama.ini
cat $GITHUB_WORKSPACE/ummisco.gama.product/target/products/ummisco.gama.application.product/linux/gtk/x86_64/Gama.ini >> Gama.ini
rm $GITHUB_WORKSPACE/ummisco.gama.product/target/products/ummisco.gama.application.product/linux/gtk/x86_64/Gama.ini
mv Gama.ini $GITHUB_WORKSPACE/ummisco.gama.product/target/products/ummisco.gama.application.product/linux/gtk/x86_64/Gama.ini
sudo cp $GITHUB_WORKSPACE/travis/jdk/linux/gama-headless.sh $GITHUB_WORKSPACE/ummisco.gama.product/target/products/ummisco.gama.application.product/linux/gtk/x86_64/headless




sudo cp -R jdk_win/jdk $GITHUB_WORKSPACE/ummisco.gama.product/target/products/ummisco.gama.application.product/win32/win32/x86_64
#sudo cp $GITHUB_WORKSPACE/travis/jdk/win/64/Gama.ini $GITHUB_WORKSPACE/ummisco.gama.product/target/products/ummisco.gama.application.product/win32/win32/x86_64
echo "-vm" > Gama.ini
echo "./jdk/bin/" >> Gama.ini
cat $GITHUB_WORKSPACE/ummisco.gama.product/target/products/ummisco.gama.application.product/win32/win32/x86_64/Gama.ini >> Gama.ini
rm $GITHUB_WORKSPACE/ummisco.gama.product/target/products/ummisco.gama.application.product/win32/win32/x86_64/Gama.ini
mv Gama.ini $GITHUB_WORKSPACE/ummisco.gama.product/target/products/ummisco.gama.application.product/win32/win32/x86_64/Gama.ini

sudo cp $GITHUB_WORKSPACE/travis/jdk/win/gama-headless.bat $GITHUB_WORKSPACE/ummisco.gama.product/target/products/ummisco.gama.application.product/win32/win32/x86_64/headless







sudo cp -R jdk_osx/jdk $GITHUB_WORKSPACE/ummisco.gama.product/target/products/ummisco.gama.application.product/macosx/cocoa/x86_64/Gama.app/Contents
#sudo cp $GITHUB_WORKSPACE/travis/jdk/mac/64/Gama.ini $GITHUB_WORKSPACE/ummisco.gama.product/target/products/ummisco.gama.application.product/macosx/cocoa/x86_64/Gama.app/Contents/Eclipse
echo "-vm" > Gama.ini
echo "../jdk/Contents/Home/bin/java" >> Gama.ini
cat $GITHUB_WORKSPACE/ummisco.gama.product/target/products/ummisco.gama.application.product/macosx/cocoa/x86_64/Gama.app/Contents/Eclipse/Gama.ini >> Gama.ini
rm $GITHUB_WORKSPACE/ummisco.gama.product/target/products/ummisco.gama.application.product/macosx/cocoa/x86_64/Gama.app/Contents/Eclipse/Gama.ini
mv Gama.ini $GITHUB_WORKSPACE/ummisco.gama.product/target/products/ummisco.gama.application.product/macosx/cocoa/x86_64/Gama.app/Contents/Eclipse/Gama.ini

sudo cp $GITHUB_WORKSPACE/travis/jdk/mac/gama-headless.sh $GITHUB_WORKSPACE/ummisco.gama.product/target/products/ummisco.gama.application.product/macosx/cocoa/x86_64/Gama.app/Contents/headless



sudo cp -R jdk_osx_aarch/jdk $GITHUB_WORKSPACE/ummisco.gama.product/target/products/ummisco.gama.application.product/macosx/cocoa/aarch64/Gama.app/Contents
#sudo cp $GITHUB_WORKSPACE/travis/jdk/mac/64/Gama.ini $GITHUB_WORKSPACE/ummisco.gama.product/target/products/ummisco.gama.application.product/macosx/cocoa/aarch64/Gama.app/Contents/Eclipse
echo "-vm" > Gama.ini
echo "../jdk/Contents/Home/bin/java" >> Gama.ini
cat $GITHUB_WORKSPACE/ummisco.gama.product/target/products/ummisco.gama.application.product/macosx/cocoa/aarch64/Gama.app/Contents/Eclipse/Gama.ini >> Gama.ini
rm $GITHUB_WORKSPACE/ummisco.gama.product/target/products/ummisco.gama.application.product/macosx/cocoa/aarch64/Gama.app/Contents/Eclipse/Gama.ini
mv Gama.ini $GITHUB_WORKSPACE/ummisco.gama.product/target/products/ummisco.gama.application.product/macosx/cocoa/aarch64/Gama.app/Contents/Eclipse/Gama.ini

sudo cp $GITHUB_WORKSPACE/travis/jdk/mac/gama-headless.sh $GITHUB_WORKSPACE/ummisco.gama.product/target/products/ummisco.gama.application.product/macosx/cocoa/aarch64/Gama.app/Contents/headless




	
cd $GITHUB_WORKSPACE/ummisco.gama.product/target/products/ummisco.gama.application.product/linux/gtk/x86_64

sudo zip -9 -qyr "${RELEASEFILES[4]}" . && echo "compressed ${RELEASEFILES[4]}" || echo "compress fail ${RELEASEFILES[4]}"

cd ../../../../../../../
 



cd $GITHUB_WORKSPACE/ummisco.gama.product/target/products/ummisco.gama.application.product/win32/win32/x86_64

sudo zip -9 -qr "${RELEASEFILES[5]}" . && echo "compressed ${RELEASEFILES[5]}" || echo "compress fail ${RELEASEFILES[5]}"

cd ../../../../../../../





cd $GITHUB_WORKSPACE/ummisco.gama.product/target/products/ummisco.gama.application.product/macosx/cocoa/x86_64

sudo zip -9 -qyr "${RELEASEFILES[6]}" . && echo "compressed ${RELEASEFILES[6]}" || echo "compress fail ${RELEASEFILES[6]}"

cd ../../../../../../../


cd $GITHUB_WORKSPACE/ummisco.gama.product/target/products/ummisco.gama.application.product/macosx/cocoa/aarch64

sudo zip -9 -qyr "${RELEASEFILES[7]}" . && echo "compressed ${RELEASEFILES[7]}" || echo "compress fail ${RELEASEFILES[7]}"


echo DONE
