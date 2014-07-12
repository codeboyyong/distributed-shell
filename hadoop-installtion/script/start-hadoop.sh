#!/bin/sh
#$1 :HADOOP_HOST=localhost
#$2 :HADOOP_VERSION=2.3.0-cdh5.0.0
#$3 :HADOOP_URL=http://archive.cloudera.com/cdh5/cdh/5/hadoop-2.3.0-cdh5.0.0.tar.gz
#$4 :HADOOP_HOME=~/hadoop-installation

if [ "$3" != "" ]
then
    ./bin/install-hadoop.sh 
fi

./bin/start-hadoop.sh  ~/hadoop-install/hadoop-2.3.0-cdh5 true
