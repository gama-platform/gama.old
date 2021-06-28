#!/bin/sh

# Decrypt the file
# --batch to prevent interactive command
# --yes to assume "yes" for questions
gpg --quiet --batch --yes --decrypt --passphrase="$SSH_USER_PWD" --output travis/settings_auth.xml travis/settings_auth.xml.gpg