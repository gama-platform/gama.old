#!/bin/bash
set -e
COMMIT=$@

REPO="gama-platform/gama"
RELEASE="latest"
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
NEWFILES[$n]='GAMA1.8_EmbeddedJDK_MacOS'$SUFFIX


git clone --depth=50 --branch=master https://github.com/gama-platform/jdk.git  jdk	

sudo cp -R jdk/linux/64/1.8.171/jdk /home/travis/build/gama-platform/gama/ummisco.gama.product/target/products/ummisco.gama.application.product/linux/gtk/x86_64
sudo cp jdk/linux/64/Gama.ini /home/travis/build/gama-platform/gama/ummisco.gama.product/target/products/ummisco.gama.application.product/linux/gtk/x86_64

sudo cp -R jdk/win/64/1.8.171/jdk /home/travis/build/gama-platform/gama/ummisco.gama.product/target/products/ummisco.gama.application.product/win32/win32/x86_64
sudo cp jdk/win/64/Gama.ini /home/travis/build/gama-platform/gama/ummisco.gama.product/target/products/ummisco.gama.application.product/win32/win32/x86_64

sudo cp -R jdk/mac/64/1.8.171/jdk /home/travis/build/gama-platform/gama/ummisco.gama.product/target/products/ummisco.gama.application.product/macosx/cocoa/x86_64/Gama.app/Contents
sudo cp jdk/mac/64/Gama.ini /home/travis/build/gama-platform/gama/ummisco.gama.product/target/products/ummisco.gama.application.product/macosx/cocoa/x86_64/Gama.app/Contents/Eclipse
	
	
cd /home/travis/build/gama-platform/gama/ummisco.gama.product/target/products/ummisco.gama.application.product/linux/gtk/x86_64

sudo zip -qr "${RELEASEFILES[3]}" . && echo "compressed ${RELEASEFILES[3]}" || echo "compress fail ${RELEASEFILES[3]}"

cd ../../../../../../../




cd /home/travis/build/gama-platform/gama/ummisco.gama.product/target/products/ummisco.gama.application.product/win32/win32/x86_64

sudo zip -qr "${RELEASEFILES[4]}" . && echo "compressed ${RELEASEFILES[4]}" || echo "compress fail ${RELEASEFILES[4]}"

cd ../../../../../../../





cd /home/travis/build/gama-platform/gama/ummisco.gama.product/target/products/ummisco.gama.application.product/macosx/cocoa/x86_64

sudo zip -qyr "${RELEASEFILES[5]}" . && echo "compressed ${RELEASEFILES[5]}" || echo "compress fail ${RELEASEFILES[5]}"

cd ../../../../../../../


i=0
for (( i=0; i<6; i++ ))
do
	FILE="${RELEASEFILES[$i]}"
	NFILE="${NEWFILES[$i]}"
	ls -sh $FILE
	echo $NFILE
done






echo
echo "Getting info of $RELEASE tag..."
echo 
LK="https://api.github.com/repos/gama-platform/gama/releases/tags/$RELEASE"

  RESULT=` curl -s -X GET \
  -H "X-Parse-Application-Id: sensitive" \
  -H "X-Parse-REST-API-Key: sensitive" \
  -H "Authorization: token $HQN_TOKEN"   \
  -H "Content-Type: application/json" \
  -d '{"name":"value"}' \
    "$LK"`
echo $RESULT	
RELEASEID=`echo "$RESULT" | sed -ne 's/^  "id": \(.*\),$/\1/p'`
echo $RELEASEID


  LK="https://api.github.com/repos/gama-platform/gama/releases/$RELEASEID/assets"
  
  RESULT=` curl -s -X GET \
  -H "X-Parse-Application-Id: sensitive" \
  -H "X-Parse-REST-API-Key: sensitive" \
  -H "Authorization: token $HQN_TOKEN"   \
  -H "Content-Type: application/json" \
  -d '{"name":"value"}' \
    "$LK"`

check=${#RESULT}

if [ $check -ge 3 ]; then
	echo 
	echo "Remove old files..."
	echo
	json=$RESULT
	prop='id'
	
    temp=`echo $json | sed 's/\\\\\//\//g' | sed 's/[{}]//g' | awk -v k="text" '{n=split($0,a,","); for (i=1; i<=n; i++) print a[i]}' | sed 's/\"\:\"/\|/g' | sed 's/[\,]/ /g' | sed 's/\"//g' | grep -w $prop`
    
	assets=`echo ${temp##*|}`

	for theid in $assets; do
		if [ "$theid" != "id:" ]; then
		  LK1="https://api.github.com/repos/gama-platform/gama/releases/assets/$theid"
		  
			echo   "Deleting $LK1...  "
		  RESULT1=`curl  -s -X  "DELETE"                \
			-H "Authorization: token $HQN_TOKEN"   \
			"$LK1"`	
			echo $RESULT1
		fi
	done 
fi


echo 
echo "Upload new files..."
echo

for (( i=0; i<6; i++ ))
do     
	FILE="${RELEASEFILES[$i]}"
	NFILE="${NEWFILES[$i]}"

  FILENAME=`basename $FILE`
  echo   "Uploading $NFILE...  "
  LK="https://uploads.github.com/repos/gama-platform/gama/releases/$RELEASEID/assets?name=$NFILE"
  
  RESULT=`curl -s -w  "\n%{http_code}\n"                   \
    -H "Authorization: token $HQN_TOKEN"                \
    -H "Accept: application/vnd.github.manifold-preview"  \
    -H "Content-Type: application/zip"                    \
    --data-binary "@$FILE"                                \
    "$LK"`
	echo $RESULT
done 

echo DONE
