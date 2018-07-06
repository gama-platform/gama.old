#!/bin/bash

commit_wiki_files() {
	git config --global user.email "travis@travis-ci.org"
	git config --global user.name "Travis CI"
	git config --global push.default simple		
	cd ..
	git clone --depth=50 --branch=master https://github.com/gama-platform/gama.wiki.git  gama.wiki	
	cd /home/travis/build/gama-platform/gama/msi.gama.documentation/
	java -cp ".:libs/jdom-2.0.1.jar:target/classes:../ummisco.gama.annotations/target/classes"  msi.gama.doc.MainGenerateWiki -online	
	

	cd /home/travis/build/gama-platform/gama.wiki
	git remote rm origin
	git remote add origin https://hqnghi88:$HQN_KEY@github.com/gama-platform/gama.wiki.git
	git status
	git add -A		
	git commit -m "Regenerate operators artifacts on wiki  - $(date)"
	git push origin HEAD:master
	

}

commit_io_website_files() {

	git config --global user.email "travis@travis-ci.org"
	git config --global user.name "Travis CI"
	git config --global push.default simple		
	git clone --depth=50 --branch=master https://github.com/gama-platform/gama-platform.github.io.git /home/travis/build/gama-platform/gama-platform.github.io
	cd /home/travis/build/gama-platform/gama-platform.github.io
	git remote rm origin
	git remote add origin https://hqnghi88:$HQN_KEY@github.com/gama-platform/gama-platform.github.io.git
	echo "Travis build trigger from gama core at $(date)" > log.txt
	git status
	git add -A		
	git commit -m "Trigger to generate docs - $(date)"
	git push origin HEAD:master
}


clean(){
	echo "Clean p2 update site"		
	sshpass -e ssh gamaws@51.255.46.42 /var/www/gama_updates/clean.sh
}

deploy(){	
	echo "Deploy to p2 update site"	
	bash ./travis/deploy.sh
}

release(){
	echo "Upload continuous release to github"		
	bash ./travis/githubReleaseOxygen.sh "$TRAVIS_COMMIT" 
}
releaseJDK(){
	echo "Upload continuous release to github"		
	bash ./travis/github_release _withjdk.sh "$TRAVIS_COMMIT" 
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
	release 
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
	if  [[ ${MESSAGE} == *"ci release"* ]] || [[ $MSG == *"ci release"* ]]; then	
		release 
	fi	
	if  [[ ${MESSAGE} == *"ci releaseJDK"* ]] || [[ $MSG == *"ci releaseJDK"* ]]; then	
		releaseJDK 
	fi	
fi
