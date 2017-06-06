#!/bin/bash
commit_website_files() {
	git config --global user.email "travis@travis-ci.com"
	git config --global user.name "Travis CI"
	git config --global push.default simple		
	git clone --depth=50 --branch=master https://github.com/dphilippon/dphilippon.github.io.git dphilippon/dphilippon.github.io
	cd dphilippon/dphilippon.github.io
	git remote rm origin
	git remote add origin https://hqnghi88:$HQN_KEY@github.com/dphilippon/dphilippon.github.io.git
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
compile (){
	echo "Compile GAMA project"		
	sh ./compile.sh	
}
build(){	
	echo "Build GAMA project"	
	sh ./build.sh			
}

deploy(){	
	echo "Deploy to p2 update site"	
	sh ./deploy.sh
}

release(){
	echo "Upload continuous release to github"		
	bash ./github-release.sh "$TRAVIS_COMMIT" 
}

MESSAGE=$(git log -1 HEAD --pretty=format:%s)
echo $MESSAGE
echo $MSG
if [[ "$TRAVIS_EVENT_TYPE" == "cron" ]]; then
	build 	
	clean
	deploy  
	release  
	commit_website_files
else
	if  [[ ${MESSAGE} == *"ci deploy"* ]]; then		
		if  [[ ${MESSAGE} == *"ci clean"* ]] || [[ $MSG == *"ci clean"* ]]; then
			clean
		fi 
		build  
		deploy 
	fi
	if  [[ ${MESSAGE} == *"ci docs"* ]] || [[ $MSG == *"ci docs"* ]]; then	
		commit_website_files
	fi	
	if  [[ ${MESSAGE} == *"ci release"* ]]; then	
		release 
	fi	
fi
