#!/bin/bash

usr=svcEC2
svr=10.10.30.143

usage()
{
cat  <<- _EOF_

  DESCRIPTION
  
  - copy longbow_data from repo server to
  feeds development and db servers in parallel
  for faster delivery.  

  USAGE: $SCRIPT_NAME <SNAPSHOT_DATE>
   ex.  sh script.sh 20210407

  Options:
    -h|--help     Displays detailed usage information.

_EOF_
}

if [ -z "$1" ]
then usage ; exit 1
fi

SNAPSHOT=$1
if [[ "${SNAPSHOT}" == "" ]]
then
  usage
  exit 1
fi

log ()
{
  info="${1}"
  msg="${2}"
  time=$(TZ=Asia/Manila date +%FT%T)
  echo -e "[${info}]: ${time} ${msg}"
}

copy_longbow_data ()
{

  SNAPDB=/data/ENDECA_DATA_REPO_6.5/FULL/MERGE/"$1"/snapdb
  log INFO "Starting hotcopy"
  scp -rp "$usr"@"$svr":"$SNAPDB"/LONGBOW_* "$2" || exit 1
  log INFO "Files copied"

  cd "$2" || exit
  pwd

  md5sum LONGBOW_*.gz |  sed 's/[!@#\$%^&*()]//g' | cut -d\  -f1 > "$1".txt
  scp -rp "$usr"@"$svr":"$SNAPDB"/md5.txt .

  file1="$1.txt"
  file2="md5.txt"
  
  if ! cmp --silent "$file1" "$file2"; then 
    log ERROR "md5sum mismatch!!! ABORT!!!" 
    pr -m -t "$file1" "$file2" 
    exit 1
  fi

  echo "======================================================"
  echo ""
  pr -m -t "$file1" "$file2"
  echo ""
  echo "======================================================"
  
  log SUCCESS "validated files. data copied successfully"
  
  rm -f "$1".txt ||:
  rm -f md5.tx ||:

}

copyTo ()

{
  if [[ $1 == "csntxfeedsmydb01" ]]; then
  DEST=/mysql/mysqldata
  elif [[ $1 == "10.10.75.100" ]]; then
  DEST=/data
  elif [[ $1 == "csfeedsbe01" ]]; then
  DEST=/mysql/mysqldata
  fi
}

getHost ()
{
host="$(hostname)"

hostname

if [[ "$host" == "csvmfeedsdev.perfectfitgroup.local" ]]; then
  HOSTNAME=10.10.75.100
  #scp -rp "$usr"@"$svr":"$SNAPDB"/LONGBOW_* /data
elif [[ "$host" == "CSFEEDSBE01" ]]; then
  HOSTNAME=csfeedsbe01
  #scp -rp s"$usr"@"$svr":"$SNAPDB"/LONGBOW_* /mysql/mysqldata
elif [[ "$host" == "CSENDREPO01.perfectfitgroup.local" ]]; then
  HOSTNAME=csendrepo01
fi
}

#
# Start Process
#

getHost

  echo "SNAPSHOT : $SNAPSHOT"
  echo "HOSTNAME : $HOSTNAME"

copyTo "$HOSTNAME"

  if [[ $HOSTNAME == "csendrepo01" ]]; then
    sh md5sum.sh "$SNAPSHOT"
    exit
  fi
  
copy_longbow_data "$SNAPSHOT" $DEST

