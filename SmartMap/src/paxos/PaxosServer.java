package paxos;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NoSuchObjectException;
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
			System.setProperty("java.security.policy", "policy.txt");
			System.setSecurityManager(new java.rmi.RMISecurityManager());
			myNode = (PaxosNode) new PaxosNodeImpl(remoteAddressMap, nodeID, quorum);
			reg= LocateRegistry.createRegistry(port);
			Naming.bind(address, myNode);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (AlreadyBoundException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}


	public void close() {
		try {
			Naming.unbind(address);
			UnicastRemoteObject.unexportObject(reg, true);
		} catch (NoSuchObjectException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		}

	}

	public PaxosNode getMyNode() {
		return myNode;
	}
}
