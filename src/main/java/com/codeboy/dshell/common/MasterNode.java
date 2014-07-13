package com.codeboy.dshell.common;

public class MasterNode extends DistributedNode {

	private String mainPageURLPath;

	public MasterNode(String mainPageURLPath, String host, String port ,String userName) {
		super(host, port,  userName);
		this.mainPageURLPath = mainPageURLPath;
	}

	// like /distributedpath
	public String getMainPageURLPath() {
		return mainPageURLPath;
	}

	// like localhost:8898/distributedpath
	public String getMainPageURL() {
		return getHttpBaseURL()+ mainPageURLPath;
	}
}
