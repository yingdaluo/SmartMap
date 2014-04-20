package paxos;

import java.util.HashMap;

public class PaxosRunner {

	public static void main(String args[]){

		HashMap<String, String> remoteAddressSet = new HashMap<String, String>();
		remoteAddressSet.put("node1", "localhost:9901");
		remoteAddressSet.put("node2", "localhost:9902");

		PaxosServer server1 = new PaxosServer(remoteAddressSet, "node1", "localhost:9901");
		PaxosServer server2 = new PaxosServer(remoteAddressSet, "node2", "localhost:9902");

		server1.start();
		server2.start();


		server1.register();
		server2.register();
	}
}
