#!/bin/bash
#
# SourceClear Install script
set -e

BASE=$(dirname $0)

#
# Pre-requistes section.  Sets the $ID and $VERSION_ID of the OS
#
command_exist() {
  type "$@" > /dev/null 2>&1
}

if [ -r /etc/os-release ]; then
   . /etc/os-release
   if ! [[ "$ID" = ubuntu || "$ID" = centos || "$ID" = fedora ]] ; then
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
     cat << END_UNKNOWN_OS
SourceClear does not support this operating system.  exiting.
END_UNKNOWN_OS
     exit 1
  fi
fi


#
# Install
#

if [ "`id -u`" != "0" ]; then
  echo -n "Installation must be run as root. sudo to root now? [ enter | ^c to abort ]"
  read 
  exec sudo "$0" "$@"
fi

BASEDIR=$(dirname $0)

bash_me() {
   echo "==> $@"
   bash -c "$@"
}

log_me() {
   echo "### $@" >&2
}

if (command_exist useradd); then
    if ( grep srcclr /etc/passwd > /dev/null 2>&1 ) ; then
        echo "srcclr user already exists."
    else
        bash_me "useradd --system --shell /bin/bash --create-home --user-group srcclr"
    fi
else
    echo "No useradd command, cannot continue."
    exit 1
fi

if [ "$ID" = ubuntu ] ; then
   bash_me "cp ${BASE}/init.d/srcclr.ubuntu /etc/init.d/srcclr"
   bash_me "chmod +x /etc/init.d/srcclr"
   bash_me "update-rc.d srcclr defaults"
fi

if [[ "$ID" = centos && (( $VERSION_ID < 7 )) ]] ; then
   bash_me "cp ${BASE}/service/init.d/srcclr-agent.centos /etc/init.d/srcclr-agent"
   bash_me "chmod +x /etc/init.d/srcclr-agent"
fi
AGENT_START_INSTRUCTIONS="sudo service srcclr start"


cat << WELCOME_MESSAGE

=============================== SUCCESS ========================================

The srcclr service is now installed.  The next step is to confirm that the
srcclr user can build projects.  The service runs as the srcclr user so that
builds do not run as root.  The srcclr user should be configured to git clone
and then build projects.

# To become the srcclr user
sudo su - srcclr

# To test if java maven project will build.  See "srcclr help" for
# other test commmands.
srcclr test --maven

# Start srcclr in server mode using:
$AGENT_START_INSTRUCTIONS

You will find log output in /var/log/srcclr.log.  

WELCOME_MESSAGE

