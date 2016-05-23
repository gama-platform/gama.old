#!/bin/bash
#
# (c) SourceClear, 2016
#
# Install script
#
# This script may be invoked as a result of a curl download "curl -SSL https://srcclr | bash"
# or invoked directly by the use follwing downloading the tarball.
#


command_exist() {
  type "$@" > /dev/null 2>&1
}

has_git() {
  if ! command_exist git; then
    echo "srcclr depends on git. Please install git and try again." >&2
    if [[ -r /etc/os-release ]]; then
      . /etc/os-release
      if [[ $ID = ubuntu || "$ID" = debian ]]; then
        read _ UBUNTU_VERSION_NAME <<< "$VERSION"
        cat << END_INSTALL_GIT
One way to install git on $ID is to:
sudo apt-get update
sudo apt-get install git
END_INSTALL_GIT
      fi
   fi
   if [[ "$ID" = centos ]]; then
     echo "sudo yum install git"
   fi
   if [[ "$ID" = fedora ]]; then
     echo "sudo dnf install git"
   fi
   if [[ "$ID" = macosx ]]; then
     if [[ $HOMEBREW = true ]] ; then
       echo "brew install git"
     else
       echo "See: https://git-scm.com/download/mac"
      fi
    fi
    exit 1
  else
    # check version
    GIT_MAJOR_VERSION=$(git --version | awk '{print $3}' | awk -F'.' '{print $1}')
    GIT_MINOR_VERSION=$(git --version | awk '{print $3}' | awk -F'.' '{print $2}')
    if [ $GIT_MAJOR_VERSION -lt 1 ] ; then
      if [ $GIT_MINOR_VERSION -lt 9 ] ; then
        echo "SourceClear requires git version 1.9 or higher. Please upgrade git."
      fi
    fi
  fi
}

#
# Gather OS information
#
if [ -r /etc/os-release ]; then
  . /etc/os-release
  if ! [[ "$ID" = ubuntu || "$ID" = debian || "$ID" = centos || "$ID" = fedora ]] ; then
    echo "SourceClear has not validated support of $ID version $VERSION"
    exit 1
  fi
else
  # test for centos version 6 that does not have /etc/os-release.
  if [ -r /etc/system-release ] ; then
    ID=$(awk '{print $1;}' /etc/system-release | tr [A-Z] [a-z])
    VERSION_ID=$(awk '{print $3;}' /etc/system-release)
    if ! [[ "$ID" = centos && (( "$VERSION_ID" > 6 )) ]] ; then
      echo "SourceClear has not validated support of $ID version $VERSION_ID"
      exit 1
    fi
  else
    if command_exist sw_vers; then
      # might be a mac
      ID=$(sw_vers | grep ProductName | awk -F':' '{print tolower($2)}' | tr -d '[:space:]')
      VERSION_ID=$(sw_vers | grep ProductVersion | awk -F':' '{print $2}' | tr -d '[:space:]')
    else
      cat << END_UNKNOWN_OS
SourceClear does not support this operating system.  exiting.
END_UNKNOWN_OS
      exit 1
    fi
  fi
fi

HOMEBREW=false
if [[ "$ID" = macosx ]] ; then
  if [[ -x /usr/local/bin/brew ]] ; then
    HOMEBREW=true
  fi
fi


#
# Installation mode.  The first argument to this script may be "system" or
# "local", in which case we skip the following steps and proceed with the
# script based install.
#
MODE=${1:-'none'}
if [[ $MODE = none ]] ; then
  #
  # Test for better install options
  #
  if [[ "$ID" = macosx ]]; then
    if [[ $HOMEBREW = true ]] ; then
      has_git
      cat << END_BREW_INSTALL
Looks like you have homebrew installed! Great, you can install the SourceClear CLI with:

brew tap srcclr/srcclr
brew update
brew install srcclr

END_BREW_INSTALL
      printf 'Would you like to continue the install (Y/n)? '
      read answer
      answer=${answer:-"y"}
      if echo "$answer" | grep -iq "^n"; then
        exit 0
      fi
    fi
  fi

  if command_exist apt-get; then
    cat << END_UBUNTU_INSTALL
Looks like your system supports apt-get. You can install the SourceClear CLI
by doing the following:

# Retrieve and install our GPG signing key
sudo apt-key adv --keyserver keyserver.ubuntu.com --recv-keys DF7DD7A50B746DD4

# Add srcclr to your apt repo list
sudo add-apt-repository "deb https://download.srcclr.com/ubuntu stable/"

# update and install
sudo apt-get update
sudo apt-get install srcclr
END_UBUNTU_INSTALL
    printf 'Would you like to continue the install (Y/n)? '
    read answer
    answer=${answer:-"y"}
    if echo "$answer" | grep -iq "^n"; then
      exit 0
    fi
  fi
fi

#
# Install using tarball, if another method was not already selected
#

has_git

#
# Install system wide configuration
#
BASEDIR=$(dirname $0)
SRCCLR_VERSION=$(head -n 1 ${BASEDIR}/VERSION)
TARGET_PREFIX=${TARGET_PREFIX:-"/opt"}
TARGET_DEST=${TARGET_DEST:-"${TARGET_PREFIX}/srcclr/${SRCCLR_VERSION}"}

# Find an appropriate bin
if [ -d /opt/local/bin ] ; then
  TARGET_BIN_DEFAULT='/opt/local/bin'
else
  if [ -d /usr/local/bin ] ; then
    TARGET_BIN_DEFAULT='/usr/local/bin'
  fi
fi
TARGET_BIN=${TARGET_BIN:-"${TARGET_BIN_DEFAULT}"}

# Find an appropriate share
if [ -d /opt/local/share ] ; then
  TARGET_SHARE_DEFAULT='/opt/local/share'
else
  if [ -d /usr/local/share ] ; then
    TARGET_SHARE_DEFAULT='/usr/local/share'
  fi
fi
TARGET_SHARE=${TARGET_SHARE:-"${TARGET_SHARE_DEFAULT}"}

#
# Install locally configuration
#
LOCAL_DEST=$PWD/srcclr
LOCAL_BIN=$PWD/srcclr/bin
LOCAL_SHARE=$PWD/srcclr
if [[ -e "$PWD/bin/srcclr" ]] ; then
  # this install script is running inside download directory
  # and we don't want to install in the distribution tgz
  LOCAL_DEST=$HOME/srcclr
  LOCAL_BIN=$HOME/srcclr/bin
  LOCAL_SHARE=$HOME/srcclr/share
fi

if [[ "$MODE" = none || "$MODE" = ask ]] ; then
  cat << END_SCRIPT_INSTALL
Running install script.

System-wide installation will place files in:
$TARGET_DEST
$TARGET_BIN
$TARGET_SHARE
and will require root access to install.

Local installation will will place files in:
$LOCAL_DEST

and you will need to put $LOCAL_BIN in your PATH.
However, root access is not required for installation.

END_SCRIPT_INSTALL
  while true; do
    read -p "Would you like to install system-wide (s) or locally (l)? " sl
    case $sl in
      [Ss]* ) echo "Installing with root privileges"; exec sudo "$0" "system" ; break;;
      [Ll]* ) MODE='local'; break;;
          * ) echo "Please answer system-wide (s) yes or locally (l):";;
    esac
  done
fi

if [[ $MODE = local ]] ; then
  if [[ -e "$LOCAL_DEST" ]] ; then
    echo "$LOCAL_DEST already exists.  exiting";
    exit 1
  fi
  TARGET_DEST="$LOCAL_DEST"
  mkdir -p ${TARGET_DEST}
  TARGET_BIN="$LOCAL_BIN"
  mkdir -p ${TARGET_BIN}
  TARGET_SHARE="$LOCAL_SHARE"
  mkdir -p ${TARGET_SHARE}
fi

bash_me() {
  echo "==> $@"
  bash -c "$@"
}

log_me() {
  echo "### $@" >&2
}

echo "==> copying files into ${TARGET_DEST}"
mkdir -m 0755 -p ${TARGET_DEST}
(cd ${BASEDIR} ; tar -cf - *) | (cd ${TARGET_DEST} ; tar -xf -)


BIN_PATH=""
if ! [[ $MODE = local ]] ; then
  bash_me "ln -f -s ${TARGET_DEST}/bin/srcclr ${TARGET_BIN}/srcclr-${SRCCLR_VERSION}"
  bash_me "ln -f -s ${TARGET_BIN}/srcclr-${SRCCLR_VERSION} ${TARGET_BIN}/srcclr"
else
  BIN_PATH="${TARGET_DEST}/bin/"
fi


cat << WELCOME_MESSAGE

=============================== SUCCESS ========================================

The SourceClear cli is now installed.  The next step is to activate it with
a token from the agent configuration page on srcclr.com using:

${BIN_PATH}srcclr activate

WELCOME_MESSAGE
exit 0
