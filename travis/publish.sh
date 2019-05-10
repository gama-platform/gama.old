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

function update_tag() {
	echo "update tag " $1 
	git config --global user.email "hqnghi88@gmail.com"
	git config --global user.name "Travis CI"
	git remote rm origin
	git remote add origin https://hqnghi88:$HQN_KEY@github.com/gama-platform/gama.git
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
embed_jdk(){
git clone --depth=50 --branch=master https://github.com/gama-platform/jdk.git  jdk	


sudo cp -R jdk/linux/64/1.8.171/jdk /home/travis/build/gama-platform/gama/ummisco.gama.product/target/products/ummisco.gama.application.product/linux/gtk/x86_64
sudo cp jdk/linux/64/Gama.ini /home/travis/build/gama-platform/gama/ummisco.gama.product/target/products/ummisco.gama.application.product/linux/gtk/x86_64

sudo cp -R jdk/win/64/1.8.171/jdk /home/travis/build/gama-platform/gama/ummisco.gama.product/target/products/ummisco.gama.application.product/win32/win32/x86_64
sudo cp jdk/win/64/Gama.ini /home/travis/build/gama-platform/gama/ummisco.gama.product/target/products/ummisco.gama.application.product/win32/win32/x86_64

sudo cp -R jdk/mac/64/1.8.171/jdk /home/travis/build/gama-platform/gama/ummisco.gama.product/target/products/ummisco.gama.application.product/macosx/cocoa/x86_64/Gama.app/Contents
sudo cp jdk/mac/64/Gama.ini /home/travis/build/gama-platform/gama/ummisco.gama.product/target/products/ummisco.gama.application.product/macosx/cocoa/x86_64/Gama.app/Contents/Eclipse
	
}
release(){
	echo "Upload continuous release to github"		
	bash ./travis/githubReleaseOxygen.sh "$TRAVIS_COMMIT" 
}
release_on_demand(){	
	update_tag continuous
	bash ./travis/github_release_withjdk.sh "$TRAVIS_COMMIT" 
}
release_daily(){	
	update_tag daily

	bash ./travis/github_release_daily_withjdk.sh "$TRAVIS_COMMIT" 
}
release_monthly(){	
	update_tag monthly

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
	embed_jdk
	release_daily 
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
		embed_jdk
		release_on_demand 
		release_daily 
		release_monthly
	fi	
fi

if [[ $(date +%d) =~ 0[1-1] ]]; then
	embed_jdk
	release_monthly 
fi

