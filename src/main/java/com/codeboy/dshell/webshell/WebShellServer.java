package com.codeboy.dshell.webshell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.codeboy.dshell.common.DistributedNode;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class WebShellServer {

	public static final int AM_PORT = 8898;
	public static final String AM_HOST = "localhost";
	public static final String AM_URL = "/distributedshell";
	public static final String AM_URL_EXEX_CMD = "/rest/execmd";
	public static final String PARAM_CMD =  "cmd";
	
	private static Boolean started = false;
	
	public static final WebShellServer INTANCE = new WebShellServer();
	public static final String URL = AM_HOST + ":" + WebShellServer.AM_PORT + WebShellServer.AM_URL;

	/***
	 * TODO:move to standalone web shell project
	 */
	private WebShellServer() {
  
	}

	public void start() throws IOException {  
		synchronized (started) {
			if (started == false) {

				HttpServer server = HttpServer.create(new InetSocketAddress(AM_PORT), 0);
				List<DistributedNode> nodes = new ArrayList<DistributedNode>();
				DistributedNode masterNode = new DistributedNode(AM_HOST, AM_PORT+"",AM_URL_EXEX_CMD, System.getProperty("user.name").toString());
				nodes.add(masterNode) ;
				server.createContext(AM_URL, new AMMainPageHandler(masterNode,nodes));
				server.createContext(AM_URL_EXEX_CMD, new AMRESTAPIHandler( nodes));
				
				server.createContext("/hello", new HttpHandler(){

					@Override
					public void handle(HttpExchange t) throws IOException {
						String response = "Hello world!" ;
						t.sendResponseHeaders(200, response.length());
						OutputStream os = t.getResponseBody();
						os.write(response.getBytes());
						os.close();
						
					}});

				server.setExecutor(null); // creates a default executor
				server.start();
				started = true;
			}
		}

	}
	
	public static void main(String args[]) throws IOException{
		WebShellServer.INTANCE.start();
	}
	
}


/***
 * Light weight web server 1) sever the app master tracking url 2) server cmd
 * execution REST API
 * 
 * @author zhaoyong
 * 
 */
//http://localhot:8898/distributedshell
class AMMainPageHandler implements HttpHandler {
 	private List<DistributedNode> nodes;
	private String URL_TEMPLATE ="$distributed-shell-url";
	private DistributedNode masterNode;
	
	public AMMainPageHandler(DistributedNode masterNode,List<DistributedNode> nodes) {
		this.nodes = nodes;
		this.masterNode=masterNode;
	}

	@Override
	public void handle(HttpExchange t) throws IOException {
		String htmlTemplate = readTemplateHTML("/ApplicationMaster.html");
		String response = htmlTemplate.replace(URL_TEMPLATE, masterNode.getHttpBaseURL() + WebShellServer.AM_URL_EXEX_CMD);
		t.sendResponseHeaders(200, response.length());
		OutputStream os = t.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}

	private String readTemplateHTML(String htmlTemplateResource) throws IOException {
		InputStream stream = WebShellServer.class.getResourceAsStream(htmlTemplateResource);
 		InputStreamReader is = new InputStreamReader(stream);
		StringBuilder sb=new StringBuilder();
		BufferedReader br = new BufferedReader(is);
		String read = br.readLine();

		while(read != null) {
		    //System.out.println(read);
		    sb.append(read).append("\n");
		    read =br.readLine();
		}

		return sb.toString();
	}
}

//http://localhot:8898/rest/execmd?cmd=ls
class AMRESTAPIHandler implements HttpHandler {
 	private List<DistributedNode> nodes;

	public AMRESTAPIHandler(List<DistributedNode> nodes) {
		this.nodes = nodes;
	}

	@Override
	public void handle(HttpExchange t) throws IOException {
		//  =>  /rest/execmd?cmd=123
		String uri = t.getRequestURI().toString();
		HashMap paramMap = readParamMap(uri);
		String cmd = paramMap.get(WebShellServer.PARAM_CMD).toString();
 		String response = "You want to execute this command:" + cmd;
 		response = response+"\n-----------------------------------------------------------------\n" ;
 		response = response+CommandExecutor.executeCommand(cmd, true) ;
		t.sendResponseHeaders(200, response.length());
		OutputStream os = t.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}
//  /rest/execmd?cmd=ls 
	private HashMap readParamMap(String uri) {
		HashMap paramMap = new HashMap<String,String> () ;
		uri=uri.substring(uri.lastIndexOf("?")+1);
		String[] pairs = uri.split("&");
		if(pairs!=null){
			for(String pair : pairs){
				 String[] p = pair.split("=") ;	
				 paramMap.put(p[0], p[1]);
			}
		}
		return paramMap;
	}
}