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
	

}
 
commit_io_website_files() {
	echo "Trigger to githubio"
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

clean(){
	echo "Clean p2 update site"		
	sshpass -e ssh gamaws@51.255.46.42 /var/www/gama_updates/clean.sh
}

deploy(){	
	echo "Deploy to p2 update site"	
	bash ./travis/deploy.sh
}
release_official(){	
	echo "Upload release to github"	
	bash ./travis/github_release_official.sh "$TRAVIS_COMMIT"  
}
release_181(){	
	echo "Upload continuous/on-demand release to github"	
	bash ./travis/github_release_181_withjdk.sh "$TRAVIS_COMMIT" 
}
release_continuous(){	
	echo "Upload continuous/on-demand release to github"	
	bash ./travis/github_release_withjdk.sh "$TRAVIS_COMMIT" 
}
release_monthly(){	
	echo "Upload monthly release to github"	
	bash ./travis/github_release_monthly_withjdk.sh "$TRAVIS_COMMIT" 
}


MESSAGE=$(git log -1 HEAD --pretty=format:%s)
echo $MESSAGE
 
  
  
if  [[ ${MESSAGE} == *"ci ext"* ]]; then			
	MSG+=" ci ext " 
fi	
 
if [[ "$TRAVIS_EVENT_TYPE" == "cron" ]] || [[ $MSG == *"ci cron"* ]]; then 	
	
	change=$(git log --pretty=format: --name-only --since="1 day ago")
	if [[ ${change} == *"msi.gama.ext"* ]]; then
			MSG+=" ci ext "
	fi
	deploy
	release_continuous
	if [[ $(date +%d) =~ 0[1-1] ]]; then
		release_monthly 
	fi

	commit_wiki_files
	commit_io_website_files
else
	if  [[ ${MESSAGE} == *"ci deploy"* ]] || [[ $MSG == *"ci deploy"* ]]; then		
		if  [[ ${MESSAGE} == *"ci clean"* ]] || [[ $MSG == *"ci clean"* ]]; then
			clean
			MSG+=" ci ext "
			echo $MSG
		fi 
		deploy 
	fi
	if  [[ ${MESSAGE} == *"ci docs"* ]] || [[ $MSG == *"ci docs"* ]]; then	
		commit_wiki_files
		commit_io_website_files
	fi	
	if  [[ ${MESSAGE} == *"ci 181"* ]] || [[ $MSG == *"ci 181"* ]]; then
		release_181
	fi	
	if  [[ ${MESSAGE} == *"ci release"* ]] || [[ $MSG == *"ci release"* ]]; then
		release_continuous
	fi	
fi

