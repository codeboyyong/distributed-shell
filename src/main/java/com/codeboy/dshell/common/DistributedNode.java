package com.codeboy.dshell.common;

public class DistributedNode {

	String host;
	String port;
	String userName;

	public DistributedNode(String host, String port,  String userName) {
		super();
		this.host = host;
		this.port = port;
		this.userName = userName;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getHttpBaseURL() {
		return "http://" + host + ":" + port;
	}

}
