#!/bin/bash

#
#	Generate list of jars containings .so\|.dylib\|.jnilib to sign for MacOS release
#	Can automatically parse 4 releases at once
#

haveLib=false

function addJarInFile(){
	if [[ $(tail -n 1 needToSign.txt) != "$1" ]]; then
		echo $1 >> needToSign.txt
	fi

	if (( $# != 1 )); then
		echo "[$1] $2" >> needToSign.txt
	fi
}

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
			addJarInFile $f
		else
			if [ $(jar tf "$f" | grep '\.jar' | wc -l) -gt 0 ]; then
				jar tf "$f" | grep '\.jar' > nestedJar.txt
				while read j
				do
					echo "[$(echo $f | rev | cut -d "/" -f 1 | rev)] Check in $j"
					jar xf "$f" "$j"
					if haveSomethingToSign "$j"; then
						echo "==> Need to sign $j <=="
						addJarInFile $f $j
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
	find . -maxdepth 1 -type d -exec rm -fr {} \;
	echo "xxx"
}


touch alreadySawJar.txt needToSign.txt currentAppJar.txt nestedJar.txt
for gama in ./Gama**zip; do
	unzipAndParse $gama
done

# Remove duplicated lines
awk '!a[$0]++' needToSign.txt > tmp.txt
cat tmp.txt > needToSign.txt