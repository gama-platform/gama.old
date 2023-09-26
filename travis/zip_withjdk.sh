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

wget -q $(curl https://api.github.com/repos/adoptium/temurin17-binaries/releases/tags/jdk-17.0.8.1+1 | grep "/OpenJDK17U-jdk_x64_linux.*.gz\"" | cut -d ':' -f 2,3 | tr -d \") -O "jdk_linux-17.tar.gz"
wget -q $(curl https://api.github.com/repos/adoptium/temurin17-binaries/releases/tags/jdk-17.0.8.1+1 | grep "/OpenJDK17U-jdk_x64_window.*.zip\"" | cut -d ':' -f 2,3 | tr -d \") -O "jdk_win32-17.zip"
wget -q $(curl https://api.github.com/repos/adoptium/temurin17-binaries/releases/tags/jdk-17.0.8.1+1 | grep "/OpenJDK17U-jdk_x64_mac.*.gz\"" | cut -d ':' -f 2,3 | tr -d \") -O "jdk_macosx-17.tar.gz"
wget -q $(curl https://api.github.com/repos/adoptium/temurin17-binaries/releases/tags/jdk-17.0.8.1+1 | grep "/OpenJDK17U-jdk_aarch64_mac.*.gz\"" | cut -d ':' -f 2,3 | tr -d \") -O "jdk_macosx_aarch-17.tar.gz"

#
#	Prepare downloaded JDK
#

for os in "linux" "macosx" "macosx_aarch" "win32"; do
	mkdir jdk_$os

	echo "unzip jdk $os"	

    if [[ -f "jdk_$os-17.tar.gz" ]]; then
		tar -zxf jdk_$os-17.tar.gz -C jdk_$os/
	else
		unzip -q jdk_$os-17.zip -d jdk_$os
	fi
	mv jdk_$os/jdk-17* jdk_$os/jdk
done


#
# Modify .ini file to use custom JDK
#

for folder in "linux/gtk/x86_64" "win32/win32/x86_64" "macosx/cocoa/x86_64/Gama.app/Contents" "macosx/cocoa/aarch64/Gama.app/Contents"; do

	#
	# Get OS (first attribute in the path)
	# + Add suffix if build for ARM64 system
	os="$(echo $folder | cut -d '/' -f 1)$(if [[ "$folder" == *'aarch64'* ]]; then echo '_aarch'; fi )"

	echo "Add custom JDK for $os"

	#
	# Specific sub-path for Eclipse in MacOS
	folderEclipse=$folder
	if [[ "$os" == "macosx"* ]]; then
		folderEclipse="$folder/Eclipse"
	fi

	sudo cp -R jdk_$os/jdk $GITHUB_WORKSPACE/ummisco.gama.product/target/products/ummisco.gama.application.product/$folder

	echo "-vm" > Gama.ini
	if [[ "$os" == "macosx"* ]]; then
		echo "../jdk/Contents/Home/bin/java" >> Gama.ini
	elif [[ "$os" == "win32"* ]]; then
		echo "./jdk/bin/javaw" >> Gama.ini
	else
		echo "./jdk/bin/java" >> Gama.ini
	fi
	
	cat $GITHUB_WORKSPACE/ummisco.gama.product/target/products/ummisco.gama.application.product/$folderEclipse/Gama.ini >> Gama.ini
	rm $GITHUB_WORKSPACE/ummisco.gama.product/target/products/ummisco.gama.application.product/$folderEclipse/Gama.ini
	mv Gama.ini $GITHUB_WORKSPACE/ummisco.gama.product/target/products/ummisco.gama.application.product/$folderEclipse/Gama.ini

done


#
# Add custom jar signing certificate in custom JDK
#

if [[ -f "$GITHUB_WORKSPACE/sign.maven" ]]; then 
	keytool -export -alias gama-platform -file ~/GamaPlatform.cer -keystore ~/gama.keystore -storepass "$GAMA_STORE"
	sudo find $GITHUB_WORKSPACE/ummisco.gama.product/target/products/ummisco.gama.application.product -name "cacerts" -exec keytool -importcert -noprompt -file ~/GamaPlatform.cer -keystore {} -alias gama-platform -storepass "changeit" \;
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
