#!/bin/bash

function signInJar(){
    local f
    local nJ

    # TODO : Prevent gathering META-INF folder
    jar tf "$1" | grep '\.so\|\.dylib\|\.jnilib\'  > filelist.txt

    sed -i -e '/META-INF/d' filelist.txt

    if [[ -s "filelist.txt" ]]; then
        echo "$1"

        while read f
        do
            jar xf "$1" "$f"
            codesign --timestamp --force -s "$MACOS_DEV_ID" -v "$f"

            jar uf "$1" "$f"
        done < filelist.txt

    fi

    grep "^\[$f" needToSign.txt | cut -d " " -f 2 > nestedJar.txt
    if [[ -s "nestedJar.txt" ]]; then
        while read nJ
        do
            jar xf "$1" "$nJ"

            echo "Signing in nested $nJ"
            mkdir "_sub" && cd "_sub"
            signInJar "../$nJ"
            cd ".." && rm -fr "_sub"
            echo "---"
            
            jar uf "$1" "$nJ"
        done < nestedJar.txt
    fi
}

grep "^./" needToSign.txt > jar.txt

# Sign .jar files
while read j
do
    if [  -f "$j" ]; then
        echo "Signing in $j"
        signInJar "$j"
        find . -not -wholename "*Gama.app*" -not -name "needToSign.txt" -not -name "jar.txt" -delete
        echo "xxx"
    fi
done < jar.txt

# Sign single lib files
find ./ \( -name "*dylib" -o -name "*.so" -o -name "*.jnilib" \) -exec codesign --timestamp --force -s "$MACOS_DEV_ID" -v {} \;

# Clean-up apple mess
find ./Gama.app -name "jar*.tmp" -exec rm -fr {} \;