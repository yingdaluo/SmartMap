package test;

import java.rmi.RemoteException;
import java.util.HashMap;

import paxos.PaxosServer;

public class SystemCrash {

	public static void main(String[] args) throws RemoteException{
		System.out.println("Recovery test start");
		HashMap<String, String> remoteAddressSet = new HashMap<String, String>();
		remoteAddressSet.put("node1", "localhost:9901");
		remoteAddressSet.put("node2", "localhost:9902");
		remoteAddressSet.put("node3", "localhost:9903");
		PaxosServer server1 = new PaxosServer(remoteAddressSet, "node1", "localhost:9901",2);
		PaxosServer server2 = new PaxosServer(remoteAddressSet, "node2", "localhost:9902",2);
		PaxosServer server3 = new PaxosServer(remoteAddressSet, "node3", "localhost:9903",2);
		server1.start();
		server2.start();
		server3.start();
		System.out.println("Three nodes start");
		try {
			server1.getMyNode().putClientMessageQueue("value1");
			server2.getMyNode().putClientMessageQueue("value2");

			server1.getMyNode().putClientMessageQueue("value3");
			server2.getMyNode().putClientMessageQueue("value4");
			server3.getMyNode().putClientMessageQueue("value5");

			System.out.println("value 1 -- 5 posted but not delivered");
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		server3.close();
		server1.close();
		server2.close();
		System.out.println("System crashed");
		System.exit(0);

	}
}
