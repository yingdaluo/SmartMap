package paxos;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.HashMap;
public class PaxosServer {

	private String address;
	private String nodeID;
	private int quorum;
	private int port;

	private PaxosNode myNode;

	private HashMap<String, String> remoteAddressMap =  new HashMap<String, String>();

	public PaxosServer(HashMap<String, String> remoteAddressSet, String nodeID, String IPAddress, int quorum){
		this.remoteAddressMap = remoteAddressSet;
		this.nodeID = nodeID;
		this.address = "//"+IPAddress+"/RMI";
		this.quorum = quorum;
		this.port = Integer.parseInt(IPAddress.split(":")[1]);
	}

	public void start(){
		try {
			System.out.println(address);
			System.setProperty("java.security.policy", "policy.txt");
			System.setSecurityManager(new java.rmi.RMISecurityManager());
			myNode = (PaxosNode) new PaxosNodeImpl(remoteAddressMap, nodeID, quorum);
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
		System.out.println("Server startup success: "+ nodeID);
	}



	public PaxosNode getMyNode() {
		return myNode;
	}
}
