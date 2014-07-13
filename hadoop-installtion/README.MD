####This is a utility scripts to help you start hadoop cdh5 pseudo cluster

####prerequisite

1) ssh to localhost without password,you can achieve this by run the following command

`ssh-keygen -t dsa -P '' -f ~/.ssh/id_dsa`
 
`cat ~/.ssh/id_dsa.pub >> ~/.ssh/authorized_keys` 

2) make sure JAVA_HOME exists in your shell

3) make sure you have at least 8G memory, 16 is prefre, since yarn cluster and job need consume lots of memory

####script
	start-hadoop.sh
		Will start cdh5 in your local machine, first time will auto download and install into ~/hadoop-install, it might take several menites
	
	stop-hadoop.sh
		Stop the hadoop cdh5
		
	runHadoopPI.sh
		After started, you can run this script to check whether the cdh5 cluster is OK
