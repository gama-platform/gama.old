#!/bin/bash

function noTimeOut(){
    while true
    do
        sleep 25
        echo "No timeout please"
    done
}

function signInJar(){
    local f

    echo "$1"

    # TODO : Prevent gathering META-INF folder
    jar tf "$1" | grep '\.so\|\.dylib\|\.jnilib\|\.jar'  > filelist.txt

    sed -i -e '/META-INF/d' filelist.txt

    while read f
    do
        jar xf "$1" "$f"

        if [[ "$f" =~ .*".jar" ]]; then
            mkdir "_sub" && cd "_sub"

            signInJar "../$f"
            cd ".." && rm -fr "_sub"
        else
            codesign --timestamp --force -s "$MACOS_DEV_ID" -v "$f"
            echo "---"
        fi

        jar uf "$1" "$f"
    done < filelist.txt
}

noTimeOut &

find ./ -name "*jar" > jarlist.txt

# Sign .jar files
while read j
do
    signInJar "$j"
    
    find . -not -wholename "*Gama.app*" -delete

    echo "xxx"
done < jarlist.txt

# Sign single lib files
find ./ \( -name "*dylib" -o -name "*.so" -o -name "*.jnilib" \) -exec codesign --timestamp --force -s "$MACOS_DEV_ID" -v {} \;

# Kill noTimeOut()
kill $(ps -aux | grep mac-sign.sh | grep bash | cut -d " " -f 2)