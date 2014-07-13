export HADOOP_VERSION=2.3.0-cdh5.0.0
export HADOOP_HOME=~/hadoop-install/hadoop-${HADOOP_VERSION}
$HADOOP_HOME/bin/hadoop jar $HADOOP_HOME/share/hadoop/mapreduce/hadoop-mapreduce-examples-*.jar  pi 2 4