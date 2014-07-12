distrbuted-shell
================

A real distributed shell for yarn 
The target is to create a real distrbuted shell in yarn

simple usgae:
1) mvn package
2) export HADOOP_YARN_HOME=/apache2.2.0/hadoop-2.2.0
  export HADOOP_VERSION=2.2.0
3) cp ./target/distributed-shell-1.0-SNAPSHOT.jar $HADOOP_YARN_HOME
4) cd $HADOOP_YARN_HOME

### Unmanaged mode

$ bin/hadoop jar $HADOOP_YARN_HOME/share/hadoop/yarn/hadoop-yarn-applications-unmanaged-am-launcher-$HADOOP_VERSION.jar \
Client -classpath distributed-shell-1.0-SNAPSHOT.jar  \
-cmd "java com.codeboy.simpleyarnapp.ApplicationMaster /bin/date 2"

You will the date out put is in the out of the yarn job.
In my case, it is $HADOOP_YARN_HOME/logs/userlogs/application_1398635014957_0001/container_1398635014957_0001_01_000002

### Managed mode

$ bin/hadoop fs -copyFromLocal distributed-shell-1.0-SNAPSHOT.jar /apps/simple/distributed-shell-1.0-SNAPSHOT.jar

$ bin/hadoop jar distributed-shell-1.0-SNAPSHOT.jar com.hortonworks.simpleyarnapp.Client /bin/date 2 /apps/simple/distributed-shell-1.0-SNAPSHOT.jar


Use mvn eclipse:eclipse to generate eclipse project
Currently this is only developed and tested in Mac os X10.9


