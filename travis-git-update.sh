#!/bin/sh

git config --global user.email "my.gama.bot@gmail.com"
git config --global user.name "gama-bot"
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
