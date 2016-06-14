#!/bin/bash

# Copyright (c) 2014 Terry Burton
#
# https://github.com/terryburton/travis-github-release
#
# Permission is hereby granted, free of charge, to any
# person obtaining a copy of this software and associated
# documentation files (the "Software"), to deal in the
# Software without restriction, including without
# limitation the rights to use, copy, modify, merge,
# publish, distribute, sublicense, and/or sell copies of
# the Software, and to permit persons to whom the Software
# is furnished to do so, subject to the following
# conditions:
#
# The above copyright notice and this permission notice
# shall be included in all copies or substantial portions
# of the Software.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
# KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
# THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
# PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
# THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
# DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
# CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
# CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
# IN THE SOFTWARE.

# This script provides a simple continuous deployment
# solution that allows Travis CI to publish a new GitHub 
# release and upload assets to it whenever a tag is pushed:
# git tag; git push --tags
#
# It was created as a temporary solution whilst waiting for
# Travis DPL to support GitHub, which it now does:
#
# http://docs.travis-ci.com/user/deployment/releases/
#
# Place this script somewhere in your project repository (perhaps by forking
# the github-travis-release repo and adding your fork as a git submodule) then
# put something like this to your .travis.yml:
#
# after_success: .travis/github-release.sh "$TRAVIS_REPO_SLUG" "`head -1 src/VERSION`" build/release/*
#
# The first argument is your repository in the format
# "username/repository", which Travis provides in the
# TRAVIS_REPO_SLUG environment variable.
#
# The second argument is the release version which as a
# sanity check should match the tag that you are releasing.
# You could pass "`git describe`" to satisfy this check.
#
# The remaining arguments are a list of asset files that you
# want to publish along with the release.
#
# The script requires that you create a GitHub OAuth access
# token to facilitate the upload:
#
# https://help.github.com/articles/creating-an-access-token-for-command-line-use
#
# You must pass this securely in the GITHUBTOKEN environment
# variable:
#
# http://docs.travis-ci.com/user/encryption-keys/
#
# For testing purposes you can create a local convenience
# file in the script directory called GITHUBTOKEN that sets
# the GITHUBTOKEN environment variable. If you do so you MUST
# ensure that this doesn't get pushed to your repository,
# perhaps by adding it to a .gitignore file.
#
# Should you get stuck then look at a working example. This
# code is being used by Barcode Writer in Pure PostScript
# for automated deployment:
#
# https://github.com/bwipp/postscriptbarcode

set -e

REPO=$1 && shift
RELEASE=$1 && shift
RELEASEFILES=$@

echo REPO
echo RELEASE
echo RELEASEFILES