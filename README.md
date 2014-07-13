distributed-shell
================

The target of this project is to create a real distrbuted web shell in yarn cluster.

###simple usgae:

	1) mvn package
	2)  export HADOOP_YARN_HOME=/apache2.2.0/hadoop-2.2.0
  		export HADOOP_VERSION=2.2.0
	3) cp ./target/distributed-shell-0.1.jar $HADOOP_YARN_HOME
	4) cd $HADOOP_YARN_HOME, then run the following

#### Unmanaged mode

	$ bin/hadoop jar ./share/hadoop/yarn/hadoop-yarn-applications-unmanaged-am-launcher-$HADOOP_VERSION.jar \
	Client -classpath distributed-shell-0.1.jar  \
	-cmd "java com.codeboy.dshell.ApplicationMaster"

`The check this url in your browser: http://localhost:8088, click the "TrackingUrl" of your job, you will see the distributed web shell`

#### Managed mode

	$ bin/hadoop fs -copyFromLocal distributed-shell-0.1.jar /apps/simple/distributed-shell.jar

	$ bin/hadoop jar distributed-shell-0.1.jar com.codeboy.dshell.ApplicationMasterClient /apps/simple/distributed-shell.jar


Use mvn eclipse:eclipse to generate eclipse project
Currently this is only developed and tested in Mac os X10.9 with Eclipse 4.2

### how to test webshell with out the yarn

`mvn exec:java -Dexec.mainClass=com.codeboy.dshell.webshell.WebShellServer`

`open this url in your browser : http://localhost:8898/distributedshell`
