#!/bin/bash

commit_website_files() {
	git config --global user.email "travis@travis-ci.org"
	git config --global user.name "Travis CI"
	git config --global push.default simple		
	git clone --depth=50 --branch=master https://github.com/gama-platform/gama.wiki.git ../gama-platform/gama.wiki
	ls /home/travis/build/gama-platform/gama/
	java -classpath "/home/travis/build/gama-platform/gama/msi.gama.documentation/libs/jdom-2.0.1.jar;/home/travis/build/gama-platform/gama/msi.gama.documentation/target/classes;/home/travis/build/gama-platform/gama/ummisco.gama.annotations/target/classes" msi.gama.doc.MainGenerateWiki	
}

commit_website_files_tmp() {


	cd gama-platform/gama.wiki
	git remote rm origin
	git remote add origin https://hqnghi88:$HQN_KEY@github.com/gama-platform/gama.wiki.git
	
	
	cd ../gama.wiki
	echo "Travis build trigger from gama core at $(date)" > log.txt
	git status
	git add -A		
	git commit -m "Regenerate docs - $(date)"
	git push origin HEAD:master


	git config --global user.email "travis@travis-ci.org"
	git config --global user.name "Travis CI"
	git config --global push.default simple		
	git clone --depth=50 --branch=master https://github.com/gama-platform/gama-platform.github.io.git gama-platform/gama-platform.github.io
	cd gama-platform/gama-platform.github.io
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
	clean
	deploy  
	release  
	commit_website_files
else
	if  [[ ${MESSAGE} == *"ci deploy"* ]]; then		
		if  [[ ${MESSAGE} == *"ci clean"* ]] || [[ $MSG == *"ci clean"* ]]; then
			clean
		fi 
		deploy 
	fi
	if  [[ ${MESSAGE} == *"ci docs"* ]] || [[ $MSG == *"ci docs"* ]]; then	
		commit_website_files
	fi	
	if  [[ ${MESSAGE} == *"ci release"* ]]; then	
		release 
	fi	
fi
