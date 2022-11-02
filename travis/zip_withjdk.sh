#!/bin/bash

#
#	Prepare utils variables
#

set -e
thePATH="$GITHUB_WORKSPACE/ummisco.gama.product/target/products/Gama1.7"


echo "zip_withjdk"
cd $GITHUB_WORKSPACE/ummisco.gama.product/target/products
echo "$(git log -1 HEAD --pretty=format:%s)"

#
#	Download latest JDK
#

wget -q $(curl https://api.github.com/repos/adoptium/temurin17-binaries/releases/tags/jdk-17.0.5+8 | grep "/OpenJDK17U-jdk_x64_linux.*.gz\"" | cut -d ':' -f 2,3 | tr -d \") -O "jdk_linux_17.tar.gz"
wget -q $(curl https://api.github.com/repos/adoptium/temurin17-binaries/releases/tags/jdk-17.0.5+8 | grep "/OpenJDK17U-jdk_x64_window.*.zip\"" | cut -d ':' -f 2,3 | tr -d \") -O "jdk_win32_17.zip"
wget -q $(curl https://api.github.com/repos/adoptium/temurin17-binaries/releases/tags/jdk-17.0.5+8 | grep "/OpenJDK17U-jdk_x64_mac.*.gz\"" | cut -d ':' -f 2,3 | tr -d \") -O "jdk_macosx_17.tar.gz"
wget -q $(curl https://api.github.com/repos/adoptium/temurin17-binaries/releases/tags/jdk-17.0.5+8 | grep "/OpenJDK17U-jdk_aarch64_mac.*.gz\"" | cut -d ':' -f 2,3 | tr -d \") -O "jdk_macosx_aarch_17.tar.gz"

#
#	Prepare downloaded JDK
#

for os in "linux" "macosx" "macosx_aarch" "win32"; do
	mkdir  jdk_$os

	echo "unzip jdk $os"	

	if [[ -f "jdk_$os\_17.tar.gz" ]]; then
		tar -zxf jdk_$os\_17.tar.gz -C jdk_$os/
	else
		unzip -q jdk_$os\_17.zip -d jdk_$os
	fi
	mv jdk_$os/jdk-17* jdk_$os/jdk
done


#
# Modify .ini file to use custom JDK
#

for folder in "linux/gtk/x86_64" "win32/win32/x86_64" "macosx/cocoa/x86_64"; do

	os=$(echo $folder | cut -d "/" -f 1)

	sudo cp -R jdk_$os/jdk $GITHUB_WORKSPACE/ummisco.gama.product/target/products/ummisco.gama.application.product/$folder

	echo "-vm" > Gama.ini
	echo "./jdk/bin/java" >> Gama.ini
	cat $GITHUB_WORKSPACE/ummisco.gama.product/target/products/ummisco.gama.application.product/$folder/Gama.ini >> Gama.ini
	rm $GITHUB_WORKSPACE/ummisco.gama.product/target/products/ummisco.gama.application.product/$folder/Gama.ini
	mv Gama.ini $GITHUB_WORKSPACE/ummisco.gama.product/target/products/ummisco.gama.application.product/$folder/Gama.ini
	sudo cp $GITHUB_WORKSPACE/travis/jdk/$os/gama-headless.sh $GITHUB_WORKSPACE/ummisco.gama.product/target/products/ummisco.gama.application.product/$folder/headless
done

# Too complicated to add it in loop
sudo cp -R jdk_osx_aarch/jdk $GITHUB_WORKSPACE/ummisco.gama.product/target/products/ummisco.gama.application.product/macosx/cocoa/aarch64/Gama.app/Contents
#sudo cp $GITHUB_WORKSPACE/travis/jdk/mac/64/Gama.ini $GITHUB_WORKSPACE/ummisco.gama.product/target/products/ummisco.gama.application.product/macosx/cocoa/aarch64/Gama.app/Contents/Eclipse
echo "-vm" > Gama.ini
echo "../jdk/Contents/Home/bin/java" >> Gama.ini
cat $GITHUB_WORKSPACE/ummisco.gama.product/target/products/ummisco.gama.application.product/macosx/cocoa/aarch64/Gama.app/Contents/Eclipse/Gama.ini >> Gama.ini
rm $GITHUB_WORKSPACE/ummisco.gama.product/target/products/ummisco.gama.application.product/macosx/cocoa/aarch64/Gama.app/Contents/Eclipse/Gama.ini
mv Gama.ini $GITHUB_WORKSPACE/ummisco.gama.product/target/products/ummisco.gama.application.product/macosx/cocoa/aarch64/Gama.app/Contents/Eclipse/Gama.ini

sudo cp $GITHUB_WORKSPACE/travis/jdk/mac/gama-headless.sh $GITHUB_WORKSPACE/ummisco.gama.product/target/products/ummisco.gama.application.product/macosx/cocoa/aarch64/Gama.app/Contents/headless


#
# Add custom jar signing certificate in custom JDK
#

if [[ -f "~/sign.maven" ]]; then 
	keytool -export -alias gama-platform -file ~/GamaPlatform.cer -keystore ~/gama.keystore -storepass $
	find $GITHUB_WORKSPACE/ummisco.gama.product/target/products/ummisco.gama.application.product -name "cacerts" -exec keytool -importcert -noprompt -file ~/GamaPlatform.cer -keystore {} -alias gama-platform -storepass "changeit" \;
fi


#
# Create final zip archives
#
n=0
RELEASEFILES[$n]="$thePATH-linux.gtk.x86_64_withJDK.zip" 
n=$((n+1))
RELEASEFILES[$n]="$thePATH-win32.win32.x86_64_withJDK.zip"  
n=$((n+1))
RELEASEFILES[$n]="$thePATH-macosx.cocoa.x86_64_withJDK.zip" 
n=$((n+1))
RELEASEFILES[$n]="$thePATH-macosx.cocoa.aarch64_withJDK.zip" 

folderIndex=0
for folder in "linux/gtk/x86_64" "win32/win32/x86_64" "macosx/cocoa/x86_64" "macosx/cocoa/aarch64"; do
	cd $GITHUB_WORKSPACE/ummisco.gama.product/target/products/ummisco.gama.application.product/$folder

	sudo zip -9 -qyr "${RELEASEFILES[$folderIndex]}" . && echo "compressed ${RELEASEFILES[$folderIndex]}" || echo "compress fail ${RELEASEFILES[$folderIndex]}"

	folderIndex=$((folderIndex+1))
done

echo DONE
