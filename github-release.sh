# Copyright (c) 2014 Terry Burton
#
# https://github.com/terryburton/travis-github-release
#
# Permission is hereby granted, free of charge, to any
# person obtaining a copy of this software and associated
# documentation files (the "Software"), to deal in the
# Software without restriction, including without
# limitation the rights to use, copy, modify, merge,
# publish, distribute, sublicense, and/or sell copies of
# the Software, and to permit persons to whom the Software
# is furnished to do so, subject to the following
# conditions:
#
# The above copyright notice and this permission notice
# shall be included in all copies or substantial portions
# of the Software.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
# KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
# THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
# PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
# THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
# DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
# CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
# CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
# IN THE SOFTWARE.

# This script provides a simple continuous deployment
# solution that allows Travis CI to publish a new GitHub 
# release and upload assets to it whenever a tag is pushed:
# git tag; git push --tags
#
# It was created as a temporary solution whilst waiting for
# Travis DPL to support GitHub, which it now does:
#
# http://docs.travis-ci.com/user/deployment/releases/
#
# Place this script somewhere in your project repository (perhaps by forking
# the github-travis-release repo and adding your fork as a git submodule) then
# put something like this to your .travis.yml:
#
# after_success: .travis/github-release.sh "$TRAVIS_REPO_SLUG" "`head -1 src/VERSION`" build/release/*
#
# The first argument is your repository in the format
# "username/repository", which Travis provides in the
# TRAVIS_REPO_SLUG environment variable.
#
# The second argument is the release version which as a
# sanity check should match the tag that you are releasing.
# You could pass "`git describe`" to satisfy this check.
#
# The remaining arguments are a list of asset files that you
# want to publish along with the release.
#
# The script requires that you create a GitHub OAuth access
# token to facilitate the upload:
#
# https://help.github.com/articles/creating-an-access-token-for-command-line-use
#
# You must pass this securely in the GITHUBTOKEN environment
# variable:
#
# http://docs.travis-ci.com/user/encryption-keys/
#
# For testing purposes you can create a local convenience
# file in the script directory called GITHUBTOKEN that sets
# the GITHUBTOKEN environment variable. If you do so you MUST
# ensure that this doesn't get pushed to your repository,
# perhaps by adding it to a .gitignore file.
#
# Should you get stuck then look at a working example. This
# code is being used by Barcode Writer in Pure PostScript
# for automated deployment:
#
# https://github.com/bwipp/postscriptbarcode

set -e
COMMIT=$@

REPO="gama-platform/gama"
RELEASE="latest"
thePATH="/home/travis/.m2/repository/msi/gama/msi.gama.application.product/1.7.0-SNAPSHOT/msi.gama.application.product-1.7.0-SNAPSHOT"














declare -a RELEASEFILES=( "$thePATH-linux.gtk.x86.zip" "$thePATH-linux.gtk.x86_64.zip" "$thePATH-macosx.cocoa.x86_64.zip" "$thePATH-win32.win32.x86.zip" "$thePATH-win32.win32.x86_64.zip" )


COMMIT="${COMMIT:0:7}"

timestamp=$(date '+_%D')

SUFFIX="$timestamp.$COMMIT.zip"
echo $SUFFIX


declare -a NEWFILES=( "GAMA1.7-Linux.x86$SUFFIX" "GAMA1.7-Linux.x64$SUFFIX" "GAMA1.7-Mac.x64$SUFFIX" "GAMA1.7-Win.x86$SUFFIX" "GAMA1.7-Win.x64$SUFFIX" )



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
