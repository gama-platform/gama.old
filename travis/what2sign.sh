#!/bin/bash

#
#	Generate list of jars containings .so\|.dylib\|.jnilib to sign for MacOS release
#	Can automatically parse 4 releases at once
#

haveLib=false

function getJarToCheck(){
	find "$1" -name "*.jar" > currentAppJar.txt

	# Remove already checked lines
	grep -v -x -f alreadySawJar.txt currentAppJar.txt > tmp.txt
	cat tmp.txt > currentAppJar.txt && rm tmp.txt

	cat currentAppJar.txt >> alreadySawJar.txt
}

function haveSomethingToSign(){
	if [ $(jar tf "$1" | grep '\.so\|\.dylib\|\.jnilib' | wc -l) -gt 0 ]; then
    	# 0 = true
		return 0 
	else
		return 1
	fi
}

function parseApp(){
	getJarToCheck "$1"

    while read f
    do
		if haveSomethingToSign "$f"; then
			echo "==> Need to sign $f <=="
			echo $f >> needToSign.txt
		else
			if [ $(jar tf "$f" | grep '\.jar' | wc -l) -gt 0 ]; then
				jar tf "$f" | grep '\.jar' > nestedJar.txt
				while read j
				do
					echo "[$(echo $f | rev | cut -d "/" -f 1 | rev)]\tCheck in $j"
					jar xf "$f" "$j"
					if haveSomethingToSign "$j"; then
						echo "==> Need to sign $j <=="
						echo $f >> needToSign.txt
					fi
				done < nestedJar.txt
			fi
		fi 

    done < currentAppJar.txt
}

function unzipAndParse(){
	echo "Unzipping $1 ..."
	unzip -q "$1"
	parseApp "./Gama.app"
	find . -type d -delete
	echo "\n"
}


touch alreadySawJar.txt needToSign.txt currentAppJar.txt nestedJar.txt
for gama in ./Gama**zip; do
	unzipAndParse $gama
done

# Remove duplicated lines
awk '!a[$0]++' needToSign.txt > tmp.txt
cat tmp.txt > needToSign.txt