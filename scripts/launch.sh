#!/bin/sh

WORK_DIR=/work/sltu/scala-remote-actors-perf-tests

AKKA_EXEC=$WORK_DIR/bin/akka.sh
STOCK_EXEC=$WORK_DIR/bin/stock.sh
GSOC_BLOCKING_EXEC=$WORK_DIR/bin/gsoc-blocking.sh
GSOC_NONBLOCKING_EXEC=$WORK_DIR/bin/gsoc-nonblocking.sh

RESULTS_DIR=/scratch/sltu/perfresults

AKKA_RESULTS=$RESULTS_DIR/akka
STOCK_RESULTS=$RESULTS_DIR/stock
GSOC_BLOCKING_RESULTS=$RESULTS_DIR/gsoc-blocking
GSOC_NONBLOCKING_RESULTS=$RESULTS_DIR/gsoc-nonblocking

if [ $# -lt 2 ]
then
  echo "Usage: `basename $0` [mode] [testname] args"
  exit 1
fi

case $1 in 
"akka")
  EXEC_FILE=$AKKA_EXEC
  OUT_DIR=$AKKA_RESULTS
;;
"stock")
  EXEC_FILE=$STOCK_EXEC
  OUT_DIR=$STOCK_RESULTS
;;
"gsoc-blocking")
  EXEC_FILE=$GSOC_BLOCKING_EXEC
  OUT_DIR=$GSOC_BLOCKING_RESULTS
;;
"gsoc-nonblocking")
  EXEC_FILE=$GSOC_NONBLOCKING_EXEC
  OUT_DIR=$GSOC_NONBLOCKING_RESULTS
;;
*)
  echo "Mode must be akka, stock, gsoc-blocking, or gsoc-nonblocking"
  exit 1
;;
esac

TESTNAME=$2

USER=sltu
CLUSTER=millennium.berkeley.edu

THISFILE=`readlink -f $0`
BASEDIR=`dirname $THISFILE`
source $BASEDIR/nodes.sh

shift 2
ARGV=$@

OLD_IFS=$IFS
IFS=","
for node in $NODES
do
cat <<EOF | ssh -o StrictHostKeyChecking=no $USER@$node.$CLUSTER "bash"
mkdir -p $OUT_DIR
echo '$EXEC_FILE --nodes=$NODES $ARGV 1> $OUT_DIR/$node-$TESTNAME.stdout 2> $OUT_DIR/$node-$TESTNAME.stderr &'
$EXEC_FILE --nodes=$NODES $ARGV 1> $OUT_DIR/$node-$TESTNAME.stdout 2> $OUT_DIR/$node-$TESTNAME.stderr &
EOF
done
IFS=$OLD_IFS
