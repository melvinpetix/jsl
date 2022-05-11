#!/bin/bash

usr=svcEC2
svr=10.10.30.143
snapshot=SNAPSHOT

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

  log INFO "Verifying Files"
  
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

  echo "================================================================="
  pr -m -t "$file1" "$file2"
  echo "================================================================="
  
  log SUCCESS "validated files. data copied successfully"
  
  rm -f "$1".txt ||:
  rm -f md5.tx ||:
}

getHost ()
{
host="$(hostname)"
hostname
if [[ "$host" == "csvmfeedsdev.perfectfitgroup.local" ]]; then
  HOSTNAME=10.10.75.100
  DEST=/data
  #scp -rp "$usr"@"$svr":"$SNAPDB"/LONGBOW_* /data
elif [[ "$host" == "CSFEEDSBE01" ]]; then
  HOSTNAME=csfeedsbe01
  DEST=/mysql/mysqldata
  #scp -rp s"$usr"@"$svr":"$SNAPDB"/LONGBOW_* /mysql/mysqldata
elif [[ "$host" == "CSENDREPO01.perfectfitgroup.local" ]]; then
  HOSTNAME=csendrepo01
  DEST=/data/ENDECA_DATA_REPO_6.5/FULL/MERGE/"$snapshot"/snapdb
fi
}

#
# Start Process
#

getHost
  echo "$snapshot"
  echo "$HOSTNAME"

  if [[ $HOSTNAME == "csendrepo01" ]]; then
    sh md5sum.sh "$snapshot"
    exit
  fi
  
copy_longbow_data "$snapshot" $DEST
