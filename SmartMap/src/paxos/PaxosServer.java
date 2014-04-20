package paxos;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.HashMap;
public class PaxosServer {

	private static HashMap<String, PaxosNode> nodeMap = new HashMap<String, PaxosNode>();

	private String address;
	private String nodeID;
	private int quorum;
	private int port;
	
	private HashMap<String, String> remoteAddressSet =  new HashMap<String, String>();
	
	public PaxosServer(HashMap<String, String> remoteAddressSet, String nodeID, String IPAddress){
		this.remoteAddressSet = remoteAddressSet;
		this.nodeID = nodeID;
		this.address = "//"+IPAddress+"/RMI";
		this.port = Integer.parseInt(IPAddress.split(":")[1]);
	}
	
	public void start(){

		try {
			System.out.println(address);
			System.setProperty("java.security.policy", "policy.txt");
			System.setSecurityManager(new java.rmi.RMISecurityManager());
			PaxosNode myNode = (PaxosNode) new PaxosNodeImpl();
			LocateRegistry.createRegistry(port);
			Naming.bind(address, myNode);
			System.out.println("RMI Server port Bind Success");
		} catch (RemoteException e) {
			System.out.println("Errors on creating remote object");
			e.printStackTrace();
		} catch (AlreadyBoundException e) {
			System.out.println("Already Bound");
			e.printStackTrace();
		} catch (MalformedURLException e) {
			System.out.println("URL Error");
			e.printStackTrace();
		}
		System.out.println("Startup success: "+ nodeID);
	}


	public void register(){
		for(String nodeID: remoteAddressSet.keySet()){
			while(true)
				try{
					String remoteIPAddress = "//"+remoteAddressSet.get(nodeID)+"/RMI";
					System.out.println(remoteIPAddress);
					PaxosNode remoteNode =  (PaxosNode)Naming.lookup(remoteIPAddress);
					nodeMap.put(nodeID, remoteNode);
					System.out.println("RMI Client Bind Success:" + nodeID);
					break;
				} catch (NotBoundException e){
					try {
						e.printStackTrace();
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				} catch (MalformedURLException e){
					e.printStackTrace();
				} catch (RemoteException e){
					e.printStackTrace();
				}		
		}

		
		try {
			PaxosNode myNode = (PaxosNode) new PaxosNodeImpl(nodeMap, nodeID, quorum);
			Naming.rebind(address, myNode);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Register success: "+ nodeID);
	}
}
