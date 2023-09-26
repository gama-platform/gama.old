#!/bin/bash

commit_wiki_files() {
	git config --global user.email "my.gama.bot@gmail.com"
	git config --global user.name "GAMA Bot"
	git config --global push.default simple			
	cd $GITHUB_WORKSPACE
	git clone https://github.com/gama-platform/gama.wiki.git  ../gama.wiki	
	cd $GITHUB_WORKSPACE/msi.gama.documentation/
	java -cp ".:libs/jdom-2.0.1.jar:target/classes:../ummisco.gama.annotations/target/classes"  msi.gama.doc.MainGenerateWiki -online	
	cd $GITHUB_WORKSPACE/../gama.wiki
	git remote set-url origin https://gama-bot:$BOT_TOKEN@github.com/gama-platform/gama.wiki.git
	git status
	git add -A		
	git commit -m "Regenerate operators artifacts on wiki  - $(date)"
	git push
	
	echo "Trigger documentation website rebuild"
	curl -H "Accept: application/vnd.github+json" -H "Authorization: token $BOT_TOKEN" --request POST --data '{"event_type": "automated-generation"}' https://api.github.com/repos/gama-platform/gama-platform.github.io/dispatches 
}

function update_tag() {
	echo "update tag " $1 
	git config --global user.email "my.gama.bot@gmail.com"
	git config --global user.name "GAMA Bot"
	git remote rm origin
	git remote add origin https://gama-bot:$BOT_TOKEN@github.com/gama-platform/gama.git
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

deploy(){	
	echo "Deploy to p2 update site"	
	bash ./travis/deploy.sh
}

release_continuous(){	
	#echo "Update release tag"
	#update_tag 1.9.3

	echo "Upload continuous/on-demand release to github"
	bash ./travis/github_release_withjdk.sh "$TRAVIS_COMMIT" 

}


MESSAGE=$(git log -1 HEAD --pretty=format:%s)
echo $MESSAGE

if  [[ ${MESSAGE} == *"ci docs"* ]] || [[ $MSG == *"ci docs"* ]]; then	
	commit_wiki_files
fi	
if  [[ ${MESSAGE} == *"ci release"* ]] || [[ $MSG == *"ci release"* ]]; then
	release_continuous
fi	
