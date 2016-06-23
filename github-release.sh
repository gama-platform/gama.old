set -e
COMMIT=$@

REPO="gama-platform/gama"
RELEASE="latest"
thePATH="/home/travis/.m2/repository/msi/gama/msi.gama.application.product/1.7.0-SNAPSHOT/msi.gama.application.product-1.7.0-SNAPSHOT"














RELEASEFILES['0']=$thePATH'-linux.gtk.x86.zip'
RELEASEFILES['1']=$thePATH'-linux.gtk.x86_64.zip'
RELEASEFILES['2']=$thePATH'-macosx.cocoa.x86_64.zip'
RELEASEFILES['3']=$thePATH'-win32.win32.x86.zip'
RELEASEFILES['4']=$thePATH'-win32.win32.x86_64.zip' 

COMMIT="${COMMIT:0:7}"

timestamp=$(date '+_%D')

SUFFIX="$timestamp.$COMMIT.zip"
echo $SUFFIX



NEWFILES['0']='GAMA1.7-Linux.x86'$SUFFIX
NEWFILES['1']='GAMA1.7-Linux.x64'$SUFFIX
NEWFILES['2']='GAMA1.7-Mac.x64'$SUFFIX
NEWFILES['3']='GAMA1.7-Win.x86'$SUFFIX
NEWFILES['4']='GAMA1.7-Win.x64'$SUFFIX


for (( i=0; i<5; i++ ))
do     
	FILE="${RELEASEFILES[$i]}"
	NFILE="${NEWFILES[$i]}"
	echo $FILE
	echo $NFILE
done






echo
echo "Getting info of $RELEASE tag..."
echo 
LK="https://api.github.com/repos/gama-platform/gama/releases/tags/$RELEASE"

  RESULT=` curl -s -X GET \
  -H "X-Parse-Application-Id: sensitive" \
  -H "X-Parse-REST-API-Key: sensitive" \
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
  -H "Content-Type: application/json" \
  -d '{"name":"value"}' \
    "$LK"`

check=${#RESULT}

if [ $check -ge 5 ]; then
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

for (( i=0; i<5; i++ ))
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
