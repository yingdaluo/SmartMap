package dbManager;

import java.rmi.RemoteException;
import java.util.ArrayList;

import paxos.PaxosServer;

public class DBRunner extends Thread{
	private final ArrayList<PaxosServer> serverList;
	private DBClass dbClass = new DBClass();
	public DBRunner(ArrayList<PaxosServer> serverList) {
		this.serverList = serverList;
	}

	public void run() {
		while (true) {
			for(int i=0; i<serverList.size(); i++){
				try {
					ArrayList<Object> list = serverList.get(i).getMyNode().deliver();
					for(Object object : list){
						String statement = (String) object;
						try {
							System.out.println(statement);
							dbClass.Update(statement, "smartmap_node"+i);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
