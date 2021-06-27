#!/bin/sh

# Decrypt the file
mkdir $GITHUB_WORKSPACE/secrets
# --batch to prevent interactive command
# --yes to assume "yes" for questions
gpg --quiet --batch --yes --decrypt --passphrase="$LARGE_SECRET_PASSPHRASE" \
--output $GITHUB_WORKSPACE/secrets/my_secret.json $GITHUB_WORKSPACE/travis/my_secret.json.gpg