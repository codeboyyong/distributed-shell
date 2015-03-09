package com.codeboy.dshell.webshell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import com.codeboy.dshell.common.DistributedNode;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
//http://localhot:8898/distributedshell

public class AMMainPageHandler  implements HttpHandler {
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
			//${user.name}
			read = read.replace("${shell.promp}", getBashPrompt()) ;
			read = read.replace("${welcome.msg}",getWelComeMessage()) ; 
			sb.append(read).append("\n");
			read = br.readLine();
		}

		return sb.toString();
	}

	private CharSequence getWelComeMessage() { 
//		return "Welcome to yarn distributed shell:\n"+ 
//				"SHELL = "+System.getenv("SHELL")+"\n"+
//				"------------------------------------------------------------------\n" +
				return	getBashPrompt()   ;
	}

	public static String getBashPrompt() {
		return "["+WebShellServer.userName+"]$";
	}
}