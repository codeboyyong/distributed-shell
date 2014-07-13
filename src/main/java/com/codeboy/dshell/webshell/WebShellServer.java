package com.codeboy.dshell.webshell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;

import com.codeboy.dshell.common.DistributedNode;
import com.codeboy.dshell.common.MasterNode;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/***
 * Rest API + main page
 * 
 * @author zhaoyong
 * 
 */
public class WebShellServer {

	// REST API:execute command
	public static final String AM_REST_URL_EXEX_CMD = "/rest/execmd";

	// REST API:say hello
	private static final String REST_URL_HELLO = "/hello";

	public static final String PARAM_CMD = "cmd";

	private static final String REST_URL_KILLME = "/killme";

	private Boolean started = false;

	List<DistributedNode> nodes = null;

	private MasterNode masterNode;

	/***
	 * First version:local web shell TODO:move to stand-alone web shell project
	 */
	public WebShellServer(MasterNode masterNode, List<DistributedNode> nodes) {
		this.masterNode = masterNode;
		this.nodes = nodes;
	}

	public WebShellServer(MasterNode masterNode) {
		this(masterNode, null);
	}

	public void start() throws IOException {
		synchronized (started) {
			if (started == false) {

				HttpServer server = HttpServer.create(new InetSocketAddress(
						Integer.parseInt(masterNode.getPort())), 0);
				// List<DistributedNode> nodes = new
				// ArrayList<DistributedNode>();
				// DistributedNode masterNode = new DistributedNode(AM_HOST,
				// AM_PORT+"",AM_REST_URL_EXEX_CMD,
				// System.getProperty("user.name").toString());
				// nodes.add(masterNode) ;
				addMainPageContext(server);

				server.createContext(AM_REST_URL_EXEX_CMD,
						new AMRESTAPIHandler(nodes));

				server.createContext(REST_URL_HELLO, new HttpHandler() {
					@Override
					public void handle(HttpExchange t) throws IOException {
						String response = "Hello world!";
						sendResponse(t, response);
					}
				});
				server.createContext(REST_URL_KILLME, new HttpHandler() {
					@Override
					public void handle(HttpExchange t) throws IOException {
						System.exit(0);
					}
				});

				server.setExecutor(null); // creates a default executor
				server.start();
				started = true;
			}
		}

	}

	private void addMainPageContext(HttpServer server) {
		server.createContext(masterNode.getMainPageURLPath(),
				new AMMainPageHandler(masterNode, nodes));
	}

	public static void sendResponse(HttpExchange t, String response)
			throws IOException {
		t.sendResponseHeaders(200, response.length());
		OutputStream os = t.getResponseBody();
		os.write(response.getBytes());
		os.close();
	};

	// test use ...
	public static void main(String args[]) throws IOException {
		String mainPageURLPath = System.getProperty("mainPageURLPath");
		String host = System.getProperty("host");
		String port = System.getProperty("port");
		String userName = System.getProperty("user.name");
		if (mainPageURLPath == null || mainPageURLPath.isEmpty()) {
			mainPageURLPath = "/distributedshell";
		}
		if (host == null || host.isEmpty()) {
			host = "localhost";
		}
		if (port == null || port.isEmpty()) {
			port = "8898";
		}
		new WebShellServer(
				new MasterNode(mainPageURLPath, host, port, userName)).start();
	}

}

/***
 * Light weight web server 1) sever the app master tracking url 2) server cmd
 * execution REST API
 * 
 * @author zhaoyong
 * 
 */
// http://localhot:8898/distributedshell
class AMMainPageHandler implements HttpHandler {
	private List<DistributedNode> nodes;
	private String URL_TEMPLATE = "$distributed-shell-url";
	private DistributedNode masterNode;

	public AMMainPageHandler(DistributedNode masterNode,
			List<DistributedNode> nodes) {
		this.nodes = nodes;
		this.masterNode = masterNode;
	}

	/**
	 * First version is hardcoded, it is fine now
	 */
	@Override
	public void handle(HttpExchange t) throws IOException {
		String htmlTemplate = readTemplateHTML("/webshell.html");
		String response = htmlTemplate.replace(URL_TEMPLATE,
				masterNode.getHttpBaseURL()
						+ WebShellServer.AM_REST_URL_EXEX_CMD);
		WebShellServer.sendResponse(t, response);
	}

	private String readTemplateHTML(String htmlTemplateResource)
			throws IOException {
		InputStream stream = WebShellServer.class
				.getResourceAsStream(htmlTemplateResource);
		InputStreamReader is = new InputStreamReader(stream);
		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(is);
		String read = br.readLine();

		while (read != null) {
			// System.out.println(read);
			sb.append(read).append("\n");
			read = br.readLine();
		}

		return sb.toString();
	}
}

// http://localhot:8898/rest/execmd?cmd=ls
class AMRESTAPIHandler implements HttpHandler {
	private List<DistributedNode> nodes;

	public AMRESTAPIHandler(List<DistributedNode> nodes) {
		this.nodes = nodes;
	}

	@Override
	public void handle(HttpExchange t) throws IOException {
		// => /rest/execmd?cmd=123
		String uri = t.getRequestURI().toString();
		HashMap paramMap = readParamMap(uri);
		String cmd = paramMap.get(WebShellServer.PARAM_CMD).toString();
		String response = CommandExecutor.executeCommand(cmd, true);

		WebShellServer.sendResponse(t, response);
	}

	// /rest/execmd?cmd=ls
	private HashMap readParamMap(String uri) {
		HashMap paramMap = new HashMap<String, String>();
		uri = uri.substring(uri.lastIndexOf("?") + 1);
		String[] pairs = uri.split("&");
		if (pairs != null) {
			for (String pair : pairs) {
				String[] p = pair.split("=");
				paramMap.put(p[0], p[1]);
			}
		}
		return paramMap;
	}
}