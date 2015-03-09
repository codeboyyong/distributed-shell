#!/bin/sh
HADOOP_HOST=localhost
HADOOP_VERSION=2.6.0
HADOOP_URL=http://www.gtlib.gatech.edu/pub/apache/hadoop/common/hadoop-2.6.0/hadoop-2.6.0.tar.gz
HADOOP_HOME=~/hadoop-install
HADOOP_CONF_TEMPLATE_DIR=../


HADOOP_DIR=$HADOOP_HOME/hadoop-$HADOOP_VERSION

if [ "$1" != "" ]
then
    HADOOP_DIR="$1"
fi
echo "HADOOP_DIR=$HADOOP_DIR"

if [ ! -d $HADOOP_HOME/hadoop-$HADOOP_VERSION ]
then
    ./install-hadoop.sh $HADOOP_HOST $HADOOP_VERSION $HADOOP_URL $HADOOP_HOME $HADOOP_CONF_TEMPLATE_DIR
fi


$HADOOP_DIR/sbin/start-dfs.sh
$HADOOP_DIR/sbin/start-yarn.sh
open http://$HADOOP_HOST:8088
