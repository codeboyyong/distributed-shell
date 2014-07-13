package com.codeboy.dshell;

import com.codeboy.dshell.common.MasterNode;
import com.codeboy.dshell.webshell.WebShellServer;


public class ApplicationMasterTrackingUI {

	public static final String AM_HOST = "localhost";

	public static final int AM_TRACKING_PORT = 8898;
	
	public static final String AM_TRACKING_PATH = "/distributedshell";

	public static final String AM_TRACKING_URL = AM_HOST + ":" + AM_TRACKING_PORT + AM_TRACKING_PATH;

	//one jvm , one instance
	public static void startWebShellServer() throws Exception { 
		MasterNode masterNode= new MasterNode(AM_TRACKING_PATH,AM_HOST,AM_TRACKING_PORT+"",System.getProperty("user.name"));
		new WebShellServer(masterNode).start();     
	}
	
 
}
