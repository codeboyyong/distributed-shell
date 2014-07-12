#!/bin/sh
#This command need 2 input
#HADOOP_DIR = ~/hadoop-install/hadoop-2.3.0-cdh5
#HADOOP_ISYARN=true

if [ "$1" != "" ]
then
    HADOOP_DIR="$1"
fi
echo "HADOOP_DIR=$HADOOP_DIR"

if [ "$2" != "" ]
then
    HADOOP_ISYARN="$2"
fi
echo "HADOOP_ISYARN=$HADOOP_ISYARN"


if [ "$HADOOP_ISYARN" = "true" ]
then
   $HADOOP_DIR/sbin/stop-dfs.sh
   $HADOOP_DIR/sbin/stop-yarn.sh
else
   $HADOOP_DIR/sbin/stop-dfs.sh
   $HADOOP_DIR/bin/stop-mapred.sh
fi

