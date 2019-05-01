#!/bin/bash
 
update_latest_tag() {
	echo "Upload continuous release to github"		
	git config --global user.email "travis@travis-ci.org"
	git config --global user.name "Travis CI"
	git config remote.origin.fetch "+refs/heads/*:refs/remotes/origin/*"
	git fetch
	git checkout master
	git pull
	git push origin :refs/tags/latest
	git tag -d latest
	git tag -fa latest -m "latest"
	git push --tags -f
	git ls-remote --tags origin
	git show-ref --tags
}
update_alpha_tag() {
	echo "Upload continuous release to github"		
	git config --global user.email "travis@travis-ci.org"
	git config --global user.name "Travis CI"
	git config remote.origin.fetch "+refs/heads/*:refs/remotes/origin/*"
	git fetch
	git checkout master
	git pull
	git push origin :refs/tags/latest
	git tag -d latest
	git tag -fa latest -m "alpha"
	git push --tags -f
	git ls-remote --tags origin
	git show-ref --tags
}
update_latest_tag
update_alpha_tag
if [[ $(date +%d) =~ 0[1-1] ]]; then
    update_alpha_tag
fi
