package test;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

import paxos.PaxosServer;

public class StrongRecoveryTest {
	public static void main(String[] args){
		System.out.println("Strong recovery start");
		HashMap<String, String> remoteAddressSet = new HashMap<String, String>();
		remoteAddressSet.put("node1", "localhost:9901");
		remoteAddressSet.put("node2", "localhost:9902");
		remoteAddressSet.put("node3", "localhost:9903");
		remoteAddressSet.put("node4", "localhost:9904");
		PaxosServer server1 = new PaxosServer(remoteAddressSet, "node1", "localhost:9901",3);
		PaxosServer server2 = new PaxosServer(remoteAddressSet, "node2", "localhost:9902",3);
		PaxosServer server3 = new PaxosServer(remoteAddressSet, "node3", "localhost:9903",3);
		PaxosServer server4 = new PaxosServer(remoteAddressSet, "node4", "localhost:9904",3);
		server1.start();
		server2.start();
		server3.start();
		server4.start();
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

			server1.getMyNode().putClientMessageQueue("value9");
			server2.getMyNode().putClientMessageQueue("value10");
			server3.getMyNode().putClientMessageQueue("value11");
			server4.getMyNode().putClientMessageQueue("value12");
			System.out.println("Value 9 -- 12 posted");

			try {
				Thread.sleep(10000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			ArrayList<Object> result1 = server1.getMyNode().deliver();
			ArrayList<Object> result2 = server2.getMyNode().deliver();
			ArrayList<Object> result3 = server3.getMyNode().deliver();
			ArrayList<Object> result4 = server4.getMyNode().deliver();
			System.out.println("Get delivered values from node 1:");
			System.out.println(result1);
			System.out.println("Get delivered values from node 2:");
			System.out.println(result2);
			System.out.println("Get delivered values from node 3:");
			System.out.println(result3);
			System.out.println("Get delivered values from node 4:");
			System.out.println(result4);

		} catch (RemoteException e) {
			e.printStackTrace();
		}

		server1.close();
		server2.close();
		server3.close();
		server4.close();
		System.exit(0);
	}
}
