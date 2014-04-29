package test;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

import paxos.PaxosServer;

public class PaxosTest {

	private void BasicTest1(){
		/**
		 * Basic test 1. Three proposers will propose 3 proposals to all nodes concurrently. Test if all proposals are committed in same sequence for each of them.
		 * Node Number: 3
		 * Active Proposers: 3
		 * Proposals for each proposer: 1.
		 * */
		System.out.println("BasicTest1 start");
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
			Thread.sleep(2000);
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

			if(isValidLength(list,6)&& isIdentical(list))
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

	private void BasicTest2() {
		/**
		 * Basic test 2. One proposers will propose 3 proposals to all 5 nodes. Two nodes are not started.
		 * Test if all proposals are committed in correct sequence.
		 * Node Number: 5
		 * Active Proposers: 3
		 * Proposals for each proposer: 1.
		 * */
		System.out.println("BasicTest2 start");
		HashMap<String, String> remoteAddressSet = new HashMap<String, String>();
		remoteAddressSet.put("node1", "localhost:9901");
		remoteAddressSet.put("node2", "localhost:9902");
		remoteAddressSet.put("node3", "localhost:9903");
		remoteAddressSet.put("node4", "localhost:9904");
		remoteAddressSet.put("node5", "localhost:9905");
		PaxosServer server1 = new PaxosServer(remoteAddressSet, "node1", "localhost:9901",3);
		PaxosServer server2 = new PaxosServer(remoteAddressSet, "node2", "localhost:9902",3);
		PaxosServer server3 = new PaxosServer(remoteAddressSet, "node3", "localhost:9903",3);
		server1.start();
		server2.start();
		server3.start();
		try {
			server1.getMyNode().putClientMessageQueue("value1");
			server1.getMyNode().putClientMessageQueue("value2");
			server1.getMyNode().putClientMessageQueue("value3");
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ArrayList<Object> sampleList = new ArrayList<Object>();
		sampleList.add("value1");
		sampleList.add("value2");
		sampleList.add("value3");

		try {
			ArrayList<Object> list1 = server1.getMyNode().deliver();
			ArrayList<Object> list2 = server2.getMyNode().deliver();
			ArrayList<Object> list3 = server3.getMyNode().deliver();
			ArrayList<ArrayList<Object>> list = new ArrayList<ArrayList<Object>>();
			list.add(list1);
			list.add(list2);
			list.add(list3);

			if(isValidLength(list,3)&& isIdenticalWith(list, sampleList))
				System.out.println("Basic Test 2 passed");
			else 
				System.out.println("Basic Test 2 failed");
		} catch (RemoteException e) {
			e.printStackTrace();
		}


		server1.close();
		server2.close();
		server3.close();

	}

	private void BasicTest3(){
		/**
		 * Basic test 3. Two proposers will propose 2 proposals to all 5 nodes. 
		 * Three nodes are of 50% chance to lose message (both in proposers and in acceptors)
		 * Test if all proposals are committed in same sequence.
		 * Node Number: 5
		 * Active Proposers: 2
		 * Proposals for each proposer: 2.
		 * */
		System.out.println("BasicTest3 start");
		HashMap<String, String> remoteAddressSet = new HashMap<String, String>();
		remoteAddressSet.put("node1", "localhost:9901");
		remoteAddressSet.put("node2", "localhost:9902");
		remoteAddressSet.put("node3", "localhost:9903");
		remoteAddressSet.put("node4", "localhost:9904");
		remoteAddressSet.put("node5", "localhost:9905");
		PaxosServer server1 = new PaxosServer(remoteAddressSet, "node1", "localhost:9901",3);
		PaxosServer server2 = new PaxosServer(remoteAddressSet, "node2", "localhost:9902",3);
		PaxosServer server3 = new PaxosServer(remoteAddressSet, "node3", "localhost:9903",3);
		PaxosServer server4 = new PaxosServer(remoteAddressSet, "node4", "localhost:9904",3);
		PaxosServer server5 = new PaxosServer(remoteAddressSet, "node5", "localhost:9905",3);



		server1.start();
		server2.start();
		server3.start();
		server4.start();
		server5.start();


		try {
			server1.getMyNode().setLostPossibility(0.3);
			server2.getMyNode().setLostPossibility(0.3);
			server3.getMyNode().setLostPossibility(0.3);
			server4.getMyNode().setLostPossibility(0.3);
			server5.getMyNode().setLostPossibility(0.3);
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			server1.getMyNode().putClientMessageQueue("value1");
			server1.getMyNode().putClientMessageQueue("value2");
			server2.getMyNode().putClientMessageQueue("value3");
			server2.getMyNode().putClientMessageQueue("value4");
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		try {
			Thread.sleep(30000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			ArrayList<Object> list1 = server1.getMyNode().deliver();
			ArrayList<Object> list2 = server2.getMyNode().deliver();
			ArrayList<Object> list3 = server3.getMyNode().deliver();
			ArrayList<Object> list4 = server4.getMyNode().deliver();
			ArrayList<Object> list5 = server5.getMyNode().deliver();
			ArrayList<ArrayList<Object>> list = new ArrayList<ArrayList<Object>>();
			list.add(list1);
			list.add(list2);
			list.add(list3);
			list.add(list4);
			list.add(list5);

			if(isValidLength(list,4)&& isIdentical(list))
				System.out.println("Basic Test 3 passed");
			else 
				System.out.println("Basic Test 3 failed");
		} catch (RemoteException e) {
			e.printStackTrace();
		}


		server1.close();
		server2.close();
		server3.close();
		server4.close();
		server5.close();
	}

	private void BasicTest4(){
		/**
		 * Basic test 4. Two proposers will propose 2 proposals to all 5 nodes. 
		 * Messages from all nodes can be delayed from 0 to 500 milliseconds
		 * Test if all proposals are committed in same sequence after 60 seconds.
		 * Node Number: 5
		 * Active Proposers: 2
		 * Proposals for each proposer: 2.
		 * */
		System.out.println("BasicTest4 start");
		HashMap<String, String> remoteAddressSet = new HashMap<String, String>();
		remoteAddressSet.put("node1", "localhost:9901");
		remoteAddressSet.put("node2", "localhost:9902");
		remoteAddressSet.put("node3", "localhost:9903");
		remoteAddressSet.put("node4", "localhost:9904");
		remoteAddressSet.put("node5", "localhost:9905");
		PaxosServer server1 = new PaxosServer(remoteAddressSet, "node1", "localhost:9901",3);
		PaxosServer server2 = new PaxosServer(remoteAddressSet, "node2", "localhost:9902",3);
		PaxosServer server3 = new PaxosServer(remoteAddressSet, "node3", "localhost:9903",3);
		PaxosServer server4 = new PaxosServer(remoteAddressSet, "node4", "localhost:9904",3);
		PaxosServer server5 = new PaxosServer(remoteAddressSet, "node5", "localhost:9905",3);



		server1.start();
		server2.start();
		server3.start();
		server4.start();
		server5.start();


		try {
			server1.getMyNode().setDelay(500);
			server2.getMyNode().setDelay(500);
			server3.getMyNode().setDelay(500);
			server4.getMyNode().setDelay(500);
			server5.getMyNode().setDelay(500);
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			server1.getMyNode().putClientMessageQueue("value1");
			server1.getMyNode().putClientMessageQueue("value2");
			server2.getMyNode().putClientMessageQueue("value3");
			server2.getMyNode().putClientMessageQueue("value4");
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		try {
			Thread.sleep(60000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			ArrayList<Object> list1 = server1.getMyNode().deliver();
			ArrayList<Object> list2 = server2.getMyNode().deliver();
			ArrayList<Object> list3 = server3.getMyNode().deliver();
			ArrayList<Object> list4 = server4.getMyNode().deliver();
			ArrayList<Object> list5 = server5.getMyNode().deliver();
			ArrayList<ArrayList<Object>> list = new ArrayList<ArrayList<Object>>();
			list.add(list1);
			list.add(list2);
			list.add(list3);
			list.add(list4);
			list.add(list5);

			if(isValidLength(list,4)&& isIdentical(list))
				System.out.println("Basic Test 4 passed");
			else 
				System.out.println("Basic Test 4 failed");
		} catch (RemoteException e) {
			e.printStackTrace();
		}


		server1.close();
		server2.close();
		server3.close();
		server4.close();
		server5.close();
	}

	private void BasicTest5(){
		/**
		 * Basic test 5. Two proposers will propose 2 proposals to all 5 nodes. 
		 * Messages from all nodes can be delayed from 0 to 100 milliseconds, and all of them can be lost at 50% possibility.
		 * Test if all proposals are committed in same sequence after 20 seconds.
		 * Node Number: 5
		 * Active Proposers: 2
		 * Proposals for each proposer: 2.
		 * */
		System.out.println("BasicTest5 start");
		HashMap<String, String> remoteAddressSet = new HashMap<String, String>();
		remoteAddressSet.put("node1", "localhost:9901");
		remoteAddressSet.put("node2", "localhost:9902");
		remoteAddressSet.put("node3", "localhost:9903");
		remoteAddressSet.put("node4", "localhost:9904");
		remoteAddressSet.put("node5", "localhost:9905");
		PaxosServer server1 = new PaxosServer(remoteAddressSet, "node1", "localhost:9901",3);
		PaxosServer server2 = new PaxosServer(remoteAddressSet, "node2", "localhost:9902",3);
		PaxosServer server3 = new PaxosServer(remoteAddressSet, "node3", "localhost:9903",3);
		PaxosServer server4 = new PaxosServer(remoteAddressSet, "node4", "localhost:9904",3);
		PaxosServer server5 = new PaxosServer(remoteAddressSet, "node5", "localhost:9905",3);



		server1.start();
		server2.start();
		server3.start();
		server4.start();
		server5.start();


		try {
			server1.getMyNode().setDelay(200);
			server1.getMyNode().setLostPossibility(0.3);
			server2.getMyNode().setDelay(200);
			server2.getMyNode().setLostPossibility(0.3);
			server3.getMyNode().setDelay(200);
			server3.getMyNode().setLostPossibility(0.3);
			server4.getMyNode().setDelay(200);
			server4.getMyNode().setLostPossibility(0.3);
			server5.getMyNode().setDelay(200);
			server5.getMyNode().setLostPossibility(0.3);
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			server1.getMyNode().putClientMessageQueue("value1");
			server1.getMyNode().putClientMessageQueue("value2");
			server2.getMyNode().putClientMessageQueue("value3");
			server2.getMyNode().putClientMessageQueue("value4");
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		try {
			Thread.sleep(30000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			ArrayList<Object> list1 = server1.getMyNode().deliver();
			ArrayList<Object> list2 = server2.getMyNode().deliver();
			ArrayList<Object> list3 = server3.getMyNode().deliver();
			ArrayList<Object> list4 = server4.getMyNode().deliver();
			ArrayList<Object> list5 = server5.getMyNode().deliver();
			ArrayList<ArrayList<Object>> list = new ArrayList<ArrayList<Object>>();
			list.add(list1);
			list.add(list2);
			list.add(list3);
			list.add(list4);
			list.add(list5);

			if(isValidLength(list,4)&& isIdentical(list))
				System.out.println("Basic Test 5 passed");
			else 
				System.out.println("Basic Test 5 failed");
		} catch (RemoteException e) {
			e.printStackTrace();
		}


		server1.close();
		server2.close();
		server3.close();
		server4.close();
		server5.close();
	}

	private void BasicTest6(){
		/**
		 * Basic test 6. 
		 * Testing network partition. Five nodes, only one node has whole network graph.
		 * Two proposers on other two nodes, which are located in different partition,
		 * will propose 2 proposals to all nodes within their partition.
		 * Test if all proposals are committed in correct sequence.
		 * Node Number: 5
		 * Active Proposers: 2
		 * Proposals for each proposer: 2.
		 * No delay, no lost message.
		 * */
		System.out.println("BasicTest6 start");
		HashMap<String, String> remoteAddressSetAll = new HashMap<String, String>();
		remoteAddressSetAll.put("node1", "localhost:9901");
		remoteAddressSetAll.put("node2", "localhost:9902");
		remoteAddressSetAll.put("node3", "localhost:9903");
		remoteAddressSetAll.put("node4", "localhost:9904");
		remoteAddressSetAll.put("node5", "localhost:9905");

		HashMap<String, String> remoteAddressSet1 = new HashMap<String, String>();
		remoteAddressSet1.put("node1", "localhost:9901");
		remoteAddressSet1.put("node2", "localhost:9902");
		remoteAddressSet1.put("node3", "localhost:9903");

		HashMap<String, String> remoteAddressSet2 = new HashMap<String, String>();
		remoteAddressSet2.put("node3", "localhost:9903");
		remoteAddressSet2.put("node4", "localhost:9904");
		remoteAddressSet2.put("node5", "localhost:9905");


		PaxosServer server1 = new PaxosServer(remoteAddressSet1, "node1", "localhost:9901",3);
		PaxosServer server2 = new PaxosServer(remoteAddressSet1, "node2", "localhost:9902",3);
		PaxosServer server3 = new PaxosServer(remoteAddressSetAll, "node3", "localhost:9903",3);
		PaxosServer server4 = new PaxosServer(remoteAddressSet2, "node4", "localhost:9904",3);
		PaxosServer server5 = new PaxosServer(remoteAddressSet2, "node5", "localhost:9905",3);

		server1.start();
		server2.start();
		server3.start();
		server4.start();
		server5.start();
		try {
			server1.getMyNode().putClientMessageQueue("value1");
			server1.getMyNode().putClientMessageQueue("value2");
			server4.getMyNode().putClientMessageQueue("value3");
			server4.getMyNode().putClientMessageQueue("value4");
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		try {
			Thread.sleep(10000);
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

			if(isValidLength(list,4)&& isIdentical(list))
				System.out.println("Basic Test 6 passed");
			else 
				System.out.println("Basic Test 6 failed");
		} catch (RemoteException e) {
			e.printStackTrace();
		}


		server1.close();
		server2.close();
		server3.close();
		server4.close();
		server5.close();
	}


	private void BasicTest7(){
		/**
		 * Basic test 7. Stress test.
		 * Node Number: 9
		 * Active Proposers: 5
		 * Proposals for each proposer: 10.
		 * No delay, no message lost.
		 * No network partition.
		 * */
		System.out.println("BasicTest7 start");
		HashMap<String, String> remoteAddressSet = new HashMap<String, String>();
		ArrayList<PaxosServer> serverList =  new ArrayList<PaxosServer>(9);
		for(int i=1; i<10; i++){
			remoteAddressSet.put("node"+i, "localhost:990"+i);
		}

		for(int i=1; i<10; i++){
			PaxosServer server = new PaxosServer(remoteAddressSet, "node"+i, "localhost:990"+i,5);
			serverList.add(server);
		}
		for(int i=0; i<9; i++){
			serverList.get(i).start();
		}

		try {
			PaxosServer server = serverList.get(0);
			for(int j=0; j<1000; j++){
				server.getMyNode().putClientMessageQueue("value"+String.valueOf(0)+String.valueOf(j));
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		try {
			Thread.sleep(30000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			ArrayList<ArrayList<Object>> list = new ArrayList<ArrayList<Object>>();
			for(int i=0; i<9; i++){
				ArrayList<Object> resultlist = serverList.get(i).getMyNode().deliver();
				list.add(resultlist);
			}

			if(isIdentical(list))
				System.out.println("Basic Test 7 passed");
			else 
				System.out.println("Basic Test 7 failed");
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		for(int i=0; i<9; i++){
			serverList.get(i).close();
		}
		
	}

	private boolean isValidLength(ArrayList<ArrayList<Object>> list, int number) {

		for(ArrayList<Object> correspondingList : list){
			if(correspondingList.size()!=number)return false;
		}
		return true;
	}


	private boolean isIdentical(ArrayList<ArrayList<Object>> list){
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

	private boolean isIdenticalWith(ArrayList<ArrayList<Object>> list, ArrayList<Object> sampleList){
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

//		test.BasicTest1();
//		test.BasicTest2();
//		test.BasicTest3();
//		test.BasicTest4();
//		test.BasicTest5();
//		test.BasicTest6();
//		test.BasicTest7();
		System.exit(0);
	}
}
