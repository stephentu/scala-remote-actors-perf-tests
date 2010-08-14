#!/bin/sh

THISFILE=`readlink -f $0`
BASEDIR=`dirname $THISFILE`
source $BASEDIR/nodes.sh

USER=sltu
CLUSTER=millennium.berkeley.edu

OLD_IFS=$IFS
IFS=","
for node in $NODES
do
cat <<EOF | ssh -o StrictHostKeyChecking=no $USER@$node.$CLUSTER "bash"
killall java
EOF
done
IFS=$OLD_IFS
