package test;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

import paxos.PaxosServer;

public class PaxosTest {

	private void BasicTest1(){
		/**
		 * Basic test 1. Three proposers will propose 3 proposals to all nodes. Test if all proposals are committed in same sequence for each of them.
		 * Node Number: 3
		 * Active Proposers: 3
		 * Proposals for each proposer: 1.
		 * */
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
			server1.getMyNode().putClientMessageQueue("value1");
			server2.getMyNode().putClientMessageQueue("value2");
			server3.getMyNode().putClientMessageQueue("value3");
			server1.getMyNode().putClientMessageQueue("value4");
			server2.getMyNode().putClientMessageQueue("value5");
			server3.getMyNode().putClientMessageQueue("value6");
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			ArrayList<Object> list1 = server1.getMyNode().deliver();
			ArrayList<Object> list2 = server2.getMyNode().deliver();
			ArrayList<Object> list3 = server3.getMyNode().deliver();
			
			ArrayList<ArrayList<Object>> list = new ArrayList<ArrayList<Object>>();
			list.add(list1);
			list.add(list2);
			list.add(list3);
			if(isIdentity(list))
				System.out.println("Basic Test 1 passed");
			else 
				System.out.println("Basic Test 1 failed");
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		
		server1.close();
		server2.close();
		server3.close();
	}
	
	
	
	
	
	private boolean isIdentity(ArrayList<ArrayList<Object>> list){
		ArrayList<Object> sampleList = list.get(0);
		for(ArrayList<Object> correspondingList : list){
			if(correspondingList.size()!=sampleList.size())return false;
			for(int i=0;  i<correspondingList.size();i++){
				if(!correspondingList.get(i).equals(sampleList.get(i)))
					return false;
			}
		}
		return true;
	}
	
	
	public static void main(String args[]){
		PaxosTest test = new PaxosTest();
		test.BasicTest1();
		System.exit(0);
	}
}
