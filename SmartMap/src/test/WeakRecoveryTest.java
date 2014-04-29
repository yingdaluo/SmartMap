package test;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

import paxos.PaxosServer;

public class WeakRecoveryTest {
	public static void main(String[] args){
		System.out.println("Weak recovery start");
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
		
		try {
			server1.getMyNode().recover();
			server2.getMyNode().recover();
			server3.getMyNode().recover();
			System.out.println("System recovered");
		} catch (RemoteException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		try {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			server1.getMyNode().putClientMessageQueue("value6");
			server2.getMyNode().putClientMessageQueue("value7");
			server3.getMyNode().putClientMessageQueue("value8");
			
			System.out.println("Value 6 -- 8 posted");

			try {
				Thread.sleep(10000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			ArrayList<Object> result = server3.getMyNode().deliver();
			System.out.println("Get delivered values from node 3:");

			for(Object obj : result){
				String str = (String) obj;
				System.out.println(str);
			}

		} catch (RemoteException e) {
			e.printStackTrace();
		}

		server1.close();
		server2.close();
		server3.close();
		System.exit(0);
	}
}
