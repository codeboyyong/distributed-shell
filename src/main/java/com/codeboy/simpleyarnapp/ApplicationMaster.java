package com.codeboy.simpleyarnapp;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.ApplicationConstants;
import org.apache.hadoop.yarn.api.protocolrecords.AllocateResponse;
import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.api.records.ContainerLaunchContext;
import org.apache.hadoop.yarn.api.records.ContainerStatus;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.client.api.AMRMClient;
import org.apache.hadoop.yarn.client.api.AMRMClient.ContainerRequest;
import org.apache.hadoop.yarn.client.api.NMClient;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.util.Records;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class ApplicationMaster {

  private static final int AM_PORT = 8898;
private static final String AM_URL = "/distrbutedshell";

public static void main(String[] args) throws Exception {

    final String command = args[0];
    final int n = Integer.valueOf(args[1]);
    
    // Initialize clients to ResourceManager and NodeManagers
    Configuration conf = new YarnConfiguration();
     //talk to resource manager
    AMRMClient<ContainerRequest> rmClient = AMRMClient.createAMRMClient();
    rmClient.init(conf);
    rmClient.start();

    //talk to node manager
    NMClient nmClient = NMClient.createNMClient();
    nmClient.init(conf);
    nmClient.start();

    // Register with ResourceManager
    System.out.println("registerApplicationMaster 0");
    //registerApplicationMaster
    //resourceManager.registerApplicationMaster(appMasterHostname, appMasterRpcPort, appMasterTrackingUrl);

    rmClient.registerApplicationMaster("localhost", AM_PORT, "localhost:"
    		+ AM_PORT+    AM_URL);
//    System.out.println("registerApplicationMaster 1");
//    rmClient.registerApplicationMaster("", 1, "");
 
    // Priority for worker containers - priorities are intra-application
    Priority priority = Records.newRecord(Priority.class);
    priority.setPriority(0);

    // Resource requirements for worker containers
    Resource capability = Records.newRecord(Resource.class);
    capability.setMemory(128);
    capability.setVirtualCores(1);

    // Make container requests to ResourceManager
    for (int i = 0; i < n; ++i) {
      ContainerRequest containerAsk = new ContainerRequest(capability, null, null, priority);
      System.out.println("Making res-req " + i);
      rmClient.addContainerRequest(containerAsk);
    }

    HttpServer server = HttpServer.create(new InetSocketAddress(AM_PORT), 0);
    List<DistributedNode> nodes = new ArrayList<DistributedNode>();
	server.createContext(AM_URL, new AMHandler(nodes));
    server.setExecutor(null); // creates a default executor
    server.start();
 
    // Obtain allocated containers and launch 
    int allocatedContainers = 0;
    
    // Un-register with ResourceManager
    rmClient.unregisterApplicationMaster(
        FinalApplicationStatus.SUCCEEDED, "", "");
      
    while (allocatedContainers < n) {
      AllocateResponse response = rmClient.allocate(0);
      for (Container container : response.getAllocatedContainers()) {
        ++allocatedContainers;

        // Launch container by create ContainerLaunchContext
        ContainerLaunchContext ctx = 
            Records.newRecord(ContainerLaunchContext.class);
        ctx.setCommands(
            Collections.singletonList(
                command + 
                " 1>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/stdout" + 
                " 2>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/stderr" 
                ));
        System.out.println("Launching container " + allocatedContainers);
        nmClient.startContainer(container, ctx);
      }
      Thread.sleep(100);
    }

    // Now wait for containers to complete
    int completedContainers = 0;
    while (completedContainers < n) {
      AllocateResponse response = rmClient.allocate(completedContainers/n);
      for (ContainerStatus status : response.getCompletedContainersStatuses()) {
        completedContainers++;
        System.out.println("Completed container " + completedContainers);
      }
      Thread.sleep(100);
    }


    
    //System.exit(0);
  }
}

 class AMHandler implements HttpHandler {
//http://www.srikanthtechnologies.com/blog/java/rest_service_client.aspx
	private List<DistributedNode> nodes;

	public AMHandler(List<DistributedNode> nodes) {
		this.nodes = nodes;
	}

	@Override
	public void handle(HttpExchange t) throws IOException {
            String response = "This is the distributed shell";
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }


}
