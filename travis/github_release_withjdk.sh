#!/bin/bash




function update_tag() {
	echo "update tag " $1 
	git config --global user.email "my.gama.bot@gmail.com"
	git config --global user.name "GAMA Bot"
	git remote rm origin
	git remote add origin https://gama-bot:$BOT_TOKEN@github.com/gama-platform/gama.git
	git config remote.origin.fetch "+refs/heads/*:refs/remotes/origin/*"
	git fetch origin
	git checkout --track origin/master
	git pull
	git status
	git push origin :refs/tags/$1
	git tag -d $1
	git tag -fa $1 -m "$1"
	git push --tags -f
	git ls-remote --tags origin
	git show-ref --tags
}


set -e
echo "github_release_withjdk"		
COMMIT=$@

REPO="gama-platform/gama"
RELEASE="1.9.2"















COMMIT="${COMMIT:0:7}"
BRANCH_NAME=$(echo $GITHUB_REF | cut -d'/' -f 3)
COMMIT=$(echo $GITHUB_SHA | cut -c1-8)

timestamp=$(date '+_%D')

SUFFIX=$timestamp'_'$COMMIT'.zip'
SUFFIX_MAC=$timestamp'_'$COMMIT'.dmg'
SUFFIX_DEB=$timestamp'_'$COMMIT'.deb'
SUFFIX_AUR=$timestamp'_'$COMMIT'.pkg.tar.zst'
SUFFIX_EXE=$timestamp'_'$COMMIT'.exe'
echo $SUFFIX



n=0
# Linux .zip & .deb
suff=($SUFFIX $SUFFIX_DEB) 
for s in ${suff[@]}; 
do
    RELEASEFILES[$n]=$GITHUB_WORKSPACE"/gama-platform_1.9.2-1_amd64."$(echo $s | rev | cut -d "." -f 1 | rev)
		NEWFILES[$n]='GAMA_1.9.2_Linux'$s 
		n=$n+1
		RELEASEFILES[$n]=$GITHUB_WORKSPACE"/gama-platform-jdk_1.9.2-1_amd64."$(echo $s | rev | cut -d "." -f 1 | rev)
		NEWFILES[$n]='GAMA_1.9.2_Linux_with_JDK'$s
		n=$n+1
done


# macOS Intel & M1
archi=("OS" "OS_M1") 
for a in ${archi[@]}; 
do
		zipArchi="x86_64"
		echo $archi[2]
		if [ $a == ${archi[1]} ]; then
		  zipArchi="aarch64"
		fi

		RELEASEFILES[$n]=$thePATH"-macosx.cocoa."$zipArchi".dmg"
		NEWFILES[$n]='GAMA_1.9.2_Mac'$a''$SUFFIX_MAC
		n=$n+1
		RELEASEFILES[$n]=$thePATH"-macosx.cocoa."$zipArchi"_withJDK.dmg"
		NEWFILES[$n]='GAMA_1.9.2_Mac'$a'_with_JDK'$SUFFIX_MAC
		n=$n+1
done

# Windows
RELEASEFILES[$n]="$thePATH-win32.win32.x86_64.zip" 
NEWFILES[$n]='GAMA_1.9.2_Windows'$SUFFIX
n=$n+1
RELEASEFILES[$n]="$thePATH-win32.win32.x86_64_withJDK.zip" 
NEWFILES[$n]='GAMA_1.9.2_Windows_with_JDK'$SUFFIX
n=$n+1
RELEASEFILES[$n]=$GITHUB_WORKSPACE"/Gama_installer_x86_64.exe" 
NEWFILES[$n]='GAMA_1.9.2_Windows'$SUFFIX_EXE
n=$n+1
RELEASEFILES[$n]=$GITHUB_WORKSPACE"/Gama_installer_x86_64_withJDK.exe" 
NEWFILES[$n]='GAMA_1.9.2_Windows_with_JDK'$SUFFIX_EXE
n=$n+1
 

i=0
for (( i=0; i<${#NEWFILES[@]} ; i++ ))
do
	FILE="${RELEASEFILES[$i]}"
	NFILE="${NEWFILES[$i]}"
	ls -lh -- "$FILE"
	echo "$NFILE"
done





LK1="https://api.github.com/repos/gama-platform/gama/releases/tags/$RELEASE"

echo   "Getting info of release Continuous...  "
RESULT1=`curl  -s -X GET \
-H "Authorization: token $BOT_TOKEN"   \
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
	-H "Authorization: token $BOT_TOKEN"   \
	"$LK1"`	
	echo $RESULT1
	break
		fi
	done 


	#update_tag $RELEASE

	echo   "Creating release Continuous...  "
LK="https://api.github.com/repos/gama-platform/gama/releases"

  RESULT=` curl -s -X POST \
  -H "X-Parse-Application-Id: sensitive" \
  -H "X-Parse-REST-API-Key: sensitive" \
  -H "Authorization: token $BOT_TOKEN"   \
  -H "Content-Type: application/json" \
  -d '{"tag_name": "'$RELEASE'", "name":"Alpha Version 1.9.2 ('$COMMIT')","body":"Alpha release for GAMA 1.9.2, which adds compatibility with JDK 17 . Please test and report issues","draft": false,"prerelease": true}' \
    "$LK"`
echo $RESULT	

















echo
echo "Getting info of $RELEASE tag..."
echo 
LK="https://api.github.com/repos/gama-platform/gama/releases/tags/$RELEASE"

  RESULT=` curl -s -X GET \
  -H "X-Parse-Application-Id: sensitive" \
  -H "X-Parse-REST-API-Key: sensitive" \
  -H "Authorization: token $BOT_TOKEN"   \
  -H "Content-Type: application/json" \
  -d '{"name":"value"}' \
    "$LK"`
echo $RESULT	
RELEASEID=`echo "$RESULT" | sed -ne 's/^  "id": \(.*\),$/\1/p'`
echo $RELEASEID


echo 
echo "Upload new files..."
echo

for (( i=0; i<${#NEWFILES[@]}; i++ ))
do     
	FILE="${RELEASEFILES[$i]}"
	NFILE="${NEWFILES[$i]}"

  FILENAME=`basename $FILE`
  echo   "Uploading $NFILE...  "
  LK="https://uploads.github.com/repos/gama-platform/gama/releases/$RELEASEID/assets?name=$NFILE"
  
  RESULT=`curl -s -w  "\n%{http_code}\n"                   \
    -H "Authorization: token $BOT_TOKEN"                \
    -H "Accept: application/vnd.github.manifold-preview"  \
    -H "Content-Type: application/zip"                    \
    --data-binary "@$FILE"                                \
    "$LK"`
	echo $RESULT
done 
 
echo DONE
