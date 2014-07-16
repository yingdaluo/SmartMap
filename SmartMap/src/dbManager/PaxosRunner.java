package dbManager;

import java.util.ArrayList;
import java.util.HashMap;

import paxos.PaxosServer;

public class PaxosRunner {

	public static void main(String args[]){
		ArrayList<PaxosServer> serverList;
		HashMap<String, String> remoteAddressSet = new HashMap<String, String>();
		serverList =  new ArrayList<PaxosServer>(5);
		ArrayList<String> dbList = new ArrayList<String>(5);
		for(int i=0; i<5; i++){
			remoteAddressSet.put("node"+i, "localhost:990"+i);
			dbList.add("smartmap_node"+i);
		}
		for(int i=0; i<5; i++){
			PaxosServer server = new PaxosServer(remoteAddressSet, "node"+i, "localhost:990"+i,5);
			serverList.add(server);
		}
		for(int i=0; i<5; i++){
			serverList.get(i).start();
		}
		DBRunner dbRunner = new DBRunner(serverList);
		dbRunner.start();
		
	}
}
