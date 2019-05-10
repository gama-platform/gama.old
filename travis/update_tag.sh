#!/bin/bash
 
update_continuous_tag() {
	echo "update_continuous_tag"		
	git config --global user.email "travis@travis-ci.org"
	git config --global user.name "Travis CI"
	git remote rm origin
	git remote add origin https://hqnghi88:$HQN_KEY@github.com/gama-platform/gama.git
	git config remote.origin.fetch "+refs/heads/*:refs/remotes/origin/*"
	git fetch
	git checkout master
	git pull origin master
	git push origin :refs/tags/continuous
	git tag -d continuous
	git tag -fa continuous -m "continuous"
	git push --tags -f
	git ls-remote --tags origin
	git show-ref --tags
}
update_monthly_tag() {
	echo "update_monthly_tag"		
	git config --global user.email "travis@travis-ci.org"
	git config --global user.name "Travis CI"
	git remote rm origin
	git remote add origin https://hqnghi88:$HQN_KEY@github.com/gama-platform/gama.git
	git config remote.origin.fetch "+refs/heads/*:refs/remotes/origin/*"
	git fetch
	git checkout master
	git pull
	git push origin :refs/tags/monthly
	git tag -d monthly
	git tag -fa monthly -m "monthly"
	git push --tags -f
	git ls-remote --tags origin
	git show-ref --tags
}
update_continuous_tag
update_daily_tag
if [[ $(date +%d) =~ 0[1-1] ]]; then
    update_monthly_tag
fi
