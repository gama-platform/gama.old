#!/bin/bash

function signInJar(){
    local f

    echo "$1"

    # TODO : Prevent gathering META-INF folder
    jar tf "$1" | grep '\.so\|\.dylib\|\.jnilib\|\.jar'  > filelist.txt

    sed -i -e '/META-INF/d' filelist.txt

    if [[ -s "filelist.txt" ]]; then
        echo "$1"

        # Reverse list to prevent concurrency signature submition per architecture
        if [[ $isWithJDK ]]; then
            tail -r filelist.txt > reverse-filelist.txt
            rm filelist.txt
            mv reverse-filelist.txt filelist.txt
        fi

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

    fi
}

find ./ -name "*jar" > jarlist.txt

# Reverse list to prevent concurrency signature submition per architecture
if [[ $isWithJDK ]]; then
    tail -r filelist.txt > reverse-filelist.txt
    rm filelist.txt
    mv reverse-filelist.txt filelist.txt
fi

# Sign .jar files
while read j
do
    signInJar "$j"
    find . -not -wholename "*Gama.app*" -delete
done < jarlist.txt

# Sign single lib files
find ./ \( -name "*dylib" -o -name "*.so" -o -name "*.jnilib" \) -exec codesign --timestamp --force -s "$MACOS_DEV_ID" -v {} \;