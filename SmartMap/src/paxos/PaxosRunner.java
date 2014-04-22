package paxos;

import java.rmi.RemoteException;
import java.util.HashMap;

public class PaxosRunner {

	public static void main(String args[]){

		HashMap<String, String> remoteAddressSet = new HashMap<String, String>();
		remoteAddressSet.put("node1", "localhost:9901");
		remoteAddressSet.put("node2", "localhost:9902");
		//remoteAddressSet.put("node3", "localhost:9903");
		PaxosServer server1 = new PaxosServer(remoteAddressSet, "node1", "localhost:9901",2);
		PaxosServer server2 = new PaxosServer(remoteAddressSet, "node2", "localhost:9902",2);
		//PaxosServer server3 = new PaxosServer(remoteAddressSet, "node3", "localhost:9903",2);
		server1.start();
		server2.start();
		//server3.start();
		try {
			server1.getMyNode().putClientMessageQueue("hahaha");
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		try {
			server2.getMyNode().putClientMessageQueue("nonono");
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		try {
			server1.getMyNode().putClientMessageQueue("let it go");
		} catch (RemoteException e) {
			e.printStackTrace();
		}
//		try {
//			Thread.sleep(5000);
//		} catch (InterruptedException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		try {
			server2.getMyNode().putClientMessageQueue("yesyesyes");
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		
	}
}
