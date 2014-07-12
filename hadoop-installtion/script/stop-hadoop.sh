#!/bin/sh
 HADOOP_VERSION=2.3.0-cdh5.0.0
 HADOOP_HOME=~/hadoop-install


HADOOP_DIR=$HADOOP_HOME/hadoop-$HADOOP_VERSION

if [ "$1" != "" ]
then
HADOOP_DIR="$1"
fi
echo "HADOOP_DIR=$HADOOP_DIR"


$HADOOP_DIR/sbin/stop-dfs.sh
$HADOOP_DIR/sbin/stop-yarn.sh