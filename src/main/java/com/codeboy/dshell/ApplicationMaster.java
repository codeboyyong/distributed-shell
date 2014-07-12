package com.codeboy.dshell;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.client.api.AMRMClient;
import org.apache.hadoop.yarn.client.api.AMRMClient.ContainerRequest;
import org.apache.hadoop.yarn.client.api.NMClient;
import org.apache.hadoop.yarn.conf.YarnConfiguration;

import com.codeboy.dshell.webshell.WebShellServer;

public class ApplicationMaster {
 
	private static final ApplicationMaster INSTANCE = new ApplicationMaster(); 

	private ApplicationMaster(){
		
	}
	
	public static void main(String[] args) throws Exception {
		ApplicationMaster.INSTANCE.start(args);
	}

	private void start(String[] args) throws Exception {
		final String command = args[0];
		final int n = Integer.valueOf(args[1]);

		// Initialize clients to ResourceManager and NodeManagers
		Configuration conf = new YarnConfiguration();
		// talk to resource manager
		AMRMClient<ContainerRequest> rmClient = AMRMClient.createAMRMClient();
		rmClient.init(conf);
		rmClient.start();

		// talk to node manager
		NMClient nmClient = NMClient.createNMClient();
		nmClient.init(conf);
		nmClient.start();

		// Register with ResourceManager
		System.out.println("registerApplicationMaster 0");
		// registerApplicationMaster
		// resourceManager.registerApplicationMaster(appMasterHostname,
		// appMasterRpcPort, appMasterTrackingUrl);
		WebShellServer.INTANCE.start();

		rmClient.registerApplicationMaster(WebShellServer.AM_HOST, WebShellServer.AM_PORT, WebShellServer.URL);
		// System.out.println("registerApplicationMaster 1");
		// rmClient.registerApplicationMaster("", 1, "");

//		// Priority for worker containers - priorities are intra-application
//		Priority priority = Records.newRecord(Priority.class);
//		priority.setPriority(0);
//
//		// Resource requirements for worker containers
//		Resource capability = Records.newRecord(Resource.class);
//		capability.setMemory(128);
//		capability.setVirtualCores(1);
//
//		// Make container requests to ResourceManager
//		for (int i = 0; i < n; ++i) {
//			ContainerRequest containerAsk = new ContainerRequest(capability,
//					null, null, priority);
//			System.out.println("Making res-req " + i);
//			rmClient.addContainerRequest(containerAsk);
//		}
//
//
//		// Obtain allocated containers and launch
//		int allocatedContainers = 0;
//
//		// Un-register with ResourceManager
//		rmClient.unregisterApplicationMaster(FinalApplicationStatus.SUCCEEDED,
//				"", "");
//
//		while (allocatedContainers < n) {
//			AllocateResponse response = rmClient.allocate(0);
//			for (Container container : response.getAllocatedContainers()) {
//				++allocatedContainers;
//
//				// Launch container by create ContainerLaunchContext
//				ContainerLaunchContext ctx = Records
//						.newRecord(ContainerLaunchContext.class);
//				ctx.setCommands(Collections.singletonList(command + " 1>"
//						+ ApplicationConstants.LOG_DIR_EXPANSION_VAR
//						+ "/stdout" + " 2>"
//						+ ApplicationConstants.LOG_DIR_EXPANSION_VAR
//						+ "/stderr"));
//				System.out
//						.println("Launching container " + allocatedContainers);
//				nmClient.startContainer(container, ctx);
//			}
//			Thread.sleep(100);
//		}
//
//		// Now wait for containers to complete
//		int completedContainers = 0;
//		while (completedContainers < n) {
//			AllocateResponse response = rmClient.allocate(completedContainers
//					/ n);
//			for (ContainerStatus status : response
//					.getCompletedContainersStatuses()) {
//				completedContainers++;
//				System.out
//						.println("Completed container " + completedContainers);
//			}
//			Thread.sleep(100);
//		}

		// System.exit(0);
		
	}
	
}

