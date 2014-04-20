package paxos;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.HashMap;
public class PaxosServer {

	private static HashMap<String, PaxosNode> nodeMap;

	private String address;
	private String nodeID;
	private int quorum;


	public void start(String IPAddress){

		try {
			address = "//"+IPAddress+"/RMI";
			System.setProperty("java.security.policy", "policy.txt");
			System.setSecurityManager(new java.rmi.RMISecurityManager());
			PaxosNode myNode = (PaxosNode) new PaxosNodeImpl();
			LocateRegistry.createRegistry(9999);
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
	}


	public void register(){
		HashMap<String, String> remoteAddressSet =  new HashMap<String, String>();
		//TODO: create remote server ip & port ArrayList;

		for(String nodeID: remoteAddressSet.keySet()){
			while(true)
				try{ 
					PaxosNode remoteNode =  (PaxosNode)Naming.lookup(remoteAddressSet.get(nodeID));
					nodeMap.put(nodeID, remoteNode);

					break;
				} catch (NotBoundException e){
					try {
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

		PaxosNode myNode = (PaxosNode) new PaxosNodeImpl(nodeMap, nodeID, quorum);
		try {
			Naming.rebind(address, myNode);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
