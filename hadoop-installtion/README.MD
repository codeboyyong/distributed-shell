prerequest

1) ssh to localhost without password

	ssh-keygen -t dsa -P '' -f ~/.ssh/id_rsa 
	cat ~/.ssh/id_rsa.pub >> ~/.ssh/authorized_keys 

2) make sure JAVA_HOME exists in your shell