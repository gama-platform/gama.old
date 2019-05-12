#!/bin/bash




function update_tag() {
	echo "update tag " $1 
	git config --global user.email "hqnghi88@gmail.com"
	git config --global user.name "Travis CI"
	git remote rm origin
	git remote add origin https://hqnghi88:$HQN_KEY@github.com/gama-platform/gama.git
	git config remote.origin.fetch "+refs/heads/*:refs/remotes/origin/*"
	git fetch
	git checkout master
	git pull origin master
	git push origin :refs/tags/$1
	git tag -d $1
	git tag -fa $1 -m "$1"
	git push --tags -f
	git ls-remote --tags origin
	git show-ref --tags
}


set -e
echo "github_release_continuous_withjdk"		
COMMIT=$@

REPO="gama-platform/gama"
RELEASE="continuous"
thePATH="/home/travis/build/gama-platform/gama/ummisco.gama.product/target/products/Gama1.7"















COMMIT="${COMMIT:0:7}"

timestamp=$(date '+_%D')

SUFFIX=$timestamp'_'$COMMIT'.zip'
echo $SUFFIX



n=0
RELEASEFILES[$n]="$thePATH-linux.gtk.x86_64.zip"
NEWFILES[$n]='GAMA1.8_Continuous_Linux_64bits'$SUFFIX 
n=1
RELEASEFILES[$n]="$thePATH-macosx.cocoa.x86_64.zip"
NEWFILES[$n]='GAMA1.8_Continuous_Mac_64bits'$SUFFIX
n=2
RELEASEFILES[$n]="$thePATH-win32.win32.x86_64.zip" 
NEWFILES[$n]='GAMA1.8_Continuous_Win_64bits'$SUFFIX
n=3
RELEASEFILES[$n]="$thePATH-linux.gtk.x86_64_withJDK.zip"
NEWFILES[$n]='GAMA1.8_Continuous__withJDK_Linux_64bits'$SUFFIX
n=4
RELEASEFILES[$n]="$thePATH-win32.win32.x86_64_withJDK.zip" 
NEWFILES[$n]='GAMA1.8_Continuous__withJDK_Win_64bits'$SUFFIX
n=5
RELEASEFILES[$n]="$thePATH-macosx.cocoa.x86_64_withJDK.zip"
NEWFILES[$n]='GAMA1.8_Continuous__withJDK_MacOS'$SUFFIX
 

i=0
for (( i=0; i<6; i++ ))
do
	FILE="${RELEASEFILES[$i]}"
	NFILE="${NEWFILES[$i]}"
	ls -sh $FILE
	echo $NFILE
done





LK1="https://api.github.com/repos/gama-platform/gama/releases/tags/$RELEASE"

echo   "Getting info of release Continuous...  "
RESULT1=`curl  -s -X GET \
-H "Authorization: token $HQN_TOKEN"   \
"$LK1"`	
echo $RESULT1

	json=$RESULT1
	prop='id'
	
    temp=`echo $json | sed 's/\\\\\//\//g' | sed 's/[{}]//g' | awk -v k="text" '{n=split($0,a,","); for (i=1; i<=n; i++) print a[i]}' | sed 's/\"\:\"/\|/g' | sed 's/[\,]/ /g' | sed 's/\"//g' | grep -w $prop`
    
	assets=`echo ${temp##*|}`

	for theid in $assets; do
		if [ "$theid" != "id:" ]; then
	LK1="https://api.github.com/repos/gama-platform/gama/releases/$theid"

	echo   "Deleting release Continuous...  "
	RESULT1=`curl  -s -X DELETE \
	-H "Authorization: token $HQN_TOKEN"   \
	"$LK1"`	
	echo $RESULT1
	break
		fi
	done 


	update_tag continuous

	echo   "Creating release Continuous...  "
LK="https://api.github.com/repos/gama-platform/gama/releases"

  RESULT=` curl -s -X POST \
  -H "X-Parse-Application-Id: sensitive" \
  -H "X-Parse-REST-API-Key: sensitive" \
  -H "Authorization: token $HQN_TOKEN"   \
  -H "Content-Type: application/json" \
  -d '{"tag_name": "'$RELEASE'", "name":"Continuous build","body":"Built once a day or with \"ci release\" \n # The release file names are composed with a number of segments separated by `_` : \n
_ the version of the release, (e.g., `GAMA1.8`)  \n
_ the name of the OS: either `Linux`, `Mac` or `Win`  \n
_ the number of bits: 64bits or 32 bits  \n
_ the day it has been produced (e.g., `07.03.18` for the 3rd of July, 2018)  \n
_ the hashcode of the corresponding commit (e.g., `bbe2b68` that can be retrieved at https://github.com/gama-platform/gama/commits/master)
","draft": false,"prerelease": true}' \
    "$LK"`
echo $RESULT	

















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
