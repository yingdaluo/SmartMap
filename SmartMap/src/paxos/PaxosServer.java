package paxos;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
public class PaxosServer {

	private String address;
	private String nodeID;
	private int quorum;
	private int port;
	private PaxosNode myNode;
	private Registry reg;

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
			//System.out.println(address);
			System.setProperty("java.security.policy", "policy.txt");
			System.setSecurityManager(new java.rmi.RMISecurityManager());
			myNode = (PaxosNode) new PaxosNodeImpl(remoteAddressMap, nodeID, quorum);
			reg= LocateRegistry.createRegistry(port);
			Naming.bind(address, myNode);
			//System.out.println("RMI Server port Bind Success");


		} catch (RemoteException e) {
			//System.out.println("Errors on creating remote object");
			e.printStackTrace();
		} catch (AlreadyBoundException e) {
			//System.out.println("Already Bound");
			e.printStackTrace();
		} catch (MalformedURLException e) {
			//System.out.println("URL Error");
			e.printStackTrace();
		}
		//System.out.println("Server startup success: "+ nodeID);
	}


	public void close() {
		try {
			Naming.unbind(address);
			UnicastRemoteObject.unexportObject(reg, true);
			if(myNode.close()){
				//System.out.println("RMI Server unbind successfully");
			}
			else {
				//System.out.println("Node close failed");
			}
		} catch (RemoteException | MalformedURLException | NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public PaxosNode getMyNode() {
		return myNode;
	}
}
