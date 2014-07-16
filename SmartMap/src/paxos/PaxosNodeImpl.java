package paxos;

import java.io.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class PaxosNodeImpl extends UnicastRemoteObject implements PaxosNode {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8138096021948421274L;
	private ConcurrentHashMap<Integer, Acceptor> acceptorMap = new ConcurrentHashMap<Integer, Acceptor>();
	private Proposer proposer;
	private Messenger messenger;

	private AtomicInteger instanceID = new AtomicInteger();
	private String nodeID;
	private double lostPossibility = 0;
	private int deliverIndex = 0;
	private Object value = null;
	private boolean finished = false;
	private String logFileName;
	private static final String logSplitBy = ",";

	private LinkedBlockingQueue<Message> proposerQueue = new LinkedBlockingQueue<Message>();
	private LinkedBlockingQueue<Message> acceptorQueue = new LinkedBlockingQueue<Message>();
	private LinkedBlockingQueue<Object> clientMsgQueue = new LinkedBlockingQueue<Object>();
	private Vector<Object> committedValues = new Vector<Object>();


	public PaxosNodeImpl(HashMap<String, String> addressMap,String nodeID, int quorum) throws RemoteException{
		this.nodeID = nodeID;
		logFileName = nodeID + ".log";
		messenger = new MessengerImpl(addressMap, nodeID);
		instanceID.set(0);
		proposer = new ProposerImpl(instanceID.get(), nodeID, messenger, quorum);

		execute();
	} 

	public void execute(){
		ProposerHandler proposerHandler = new ProposerHandler();
		proposerHandler.start();

		AcceptorHandler acceptorHandler = new AcceptorHandler();
		acceptorHandler.start();

		ClientHandler clientHandler = new ClientHandler();
		clientHandler.start();
		CommitRecovery commitRecovery = new CommitRecovery();
		commitRecovery.start();
	}

	@Override
	public void setLostPossibility(double possibility){
		this.lostPossibility = possibility;
	}


	@Override
	public void putproposerQueue(Message message){
		if(lostPossibility!=0){
			Random rand = new Random(System.currentTimeMillis());
			double chanceNow = rand.nextInt(100)/100.00;
			if(chanceNow>lostPossibility){
				//System.out.println("rand.nextDouble() is "+chanceNow+", lostPossibility is :"+lostPossibility+".Message passed");
				proposerQueue.add(message);
			}
		}else {
			proposerQueue.add(message);
		}
	}

	@Override
	public void putacceptorQueue(Message message){
		if(lostPossibility!=0){
			Random rand = new Random(System.currentTimeMillis());
			double chanceNow = rand.nextInt(100)/100.00;
			if(chanceNow>lostPossibility){
				acceptorQueue.add(message);
			}
		}else {
			acceptorQueue.add(message);
		}
	}

	@Override
	public void putClientMessageQueue(Object value) throws RemoteException {
		synchronized (clientMsgQueue) {
			clientMsgQueue.add(value);
			clientMsgQueue.notify();
		}
		//clientMsgQueue.add(value);
	}

	public ArrayList<Object> deliver(){
		ArrayList<Object> resultList =  new ArrayList<Object>();
		while(committedValues.size()>deliverIndex && committedValues.get(deliverIndex)!=null){
			resultList.add(committedValues.get(deliverIndex));
			deliverIndex++;
		}
		return resultList;
	}

	public void recover(){
		BufferedReader br = null;
		String line = "";
		int max = 0;
		// clear the committed values
		committedValues.clear();

		try {
			br = new BufferedReader(new FileReader(logFileName));
			while ((line = br.readLine()) != null) {
				// use comma as separator
				String[] data = line.split(logSplitBy);
				int id = Integer.parseInt(data[0]);
				if(id > max){
					max = id;
				}
				String value = data[1];
				// reconstruct the committedValues
				if(id>=committedValues.size()){
					for(int i= committedValues.size();i<=id; i++){
						committedValues.add(null);
					}	
				}	
				committedValues.setElementAt(value, id);
				Acceptor acceptor =  new AcceptorImpl(id, messenger);
				acceptorMap.put(id, acceptor);
				acceptor.receiveAcceptRequest(nodeID, new ProposalID(100, nodeID), value);
			}
			instanceID.set(max+1);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}	 
	}

	class ProposerHandler extends Thread{
		public void run() {
			while(!finished){
				try {
					Message message = proposerQueue.take();
					if(message.getMessageType() == Message.Type.PrepareOK){
						proposer.receivePrepareOK(message.getInstanceID(), message.getToProposal(), message.getPrevAcceptedProposal(), message.getSenderID(), message.getValue());
					}else if(message.getMessageType()  == Message.Type.AcceptOK){
						proposer.receiveAcceptOK(message.getInstanceID(), message.getToProposal(), message.getSenderID());
					}else if(message.getMessageType()== Message.Type.Reject){
						proposer.receiveReject(message.getInstanceID(), message.getToProposal(), message.getPrevAcceptedProposal());
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	class AcceptorHandler extends Thread{
		public void run() {
			while(!finished){
				Message message;
				try {
					message = acceptorQueue.take();

					Acceptor acceptor = acceptorMap.get(message.getInstanceID());
					if(acceptor == null){
						acceptor =  new AcceptorImpl(message.getInstanceID(), messenger);
						acceptorMap.put(message.getInstanceID(), acceptor);
					}					
					if(message.getMessageType() == Message.Type.Prepare){
						acceptor.receivePrepare(message.getToProposal().getProposer(), message.getToProposal());
					}else if(message.getMessageType()  == Message.Type.AcceptRequest){
						acceptor.receiveAcceptRequest(message.getToProposal().getProposer(), message.getToProposal(), message.getValue());
					}else if (message.getMessageType()  == Message.Type.Commit){
						commitValue(message);
					}else if (message.getMessageType()  == Message.Type.CommitRequest){
						commitRequest(message);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	private void commitRequest(Message message){
		// send back the requested commit value if it is available
		if (committedValues.size() > message.getInstanceID()){
			Object value = committedValues.get(message.getInstanceID());
			if (value != null) {
				messenger.sendCommitToSingleNode(message.getInstanceID(), value, message.getSenderID());
			}
		}else if(!committedValues.isEmpty() && message.getInstanceID() == Integer.MAX_VALUE){
			Object value = committedValues.lastElement();
			if (value != null) {
				messenger.sendCommitToSingleNode(committedValues.size()-1, value, message.getSenderID());
			}
		}
	}

	private void commitValue(Message message){
		if(message.getInstanceID()>=committedValues.size()){
			for(int i= committedValues.size();i<=message.getInstanceID(); i++){
				committedValues.add(null);
			}	
		}
		if(committedValues.get(message.getInstanceID()) == null|| !committedValues.get(message.getInstanceID()).equals(message.getValue())){
			committedValues.setElementAt(message.getValue(), message.getInstanceID());	
			// Durable commit: write the new committed value to the log file
			try{
				PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(logFileName, true)));
				out.println(Integer.toString(message.getInstanceID())+logSplitBy+message.getValue().toString());
				out.close();
			}catch (IOException e) {
				e.printStackTrace();	
			}

		}
	}

	class ClientHandler extends Thread{
		public void run() {
			while(!finished){
				if(acceptorMap.size() == 0 && !proposer.isWorking()){//initial state
					try {
						value = clientMsgQueue.take();
						proposer.setNewProposalInstance(instanceID.get(), value);
						instanceID.set(instanceID.get()+1);
						proposer.prepare();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				}else if(!proposer.isWorking()){
					if(!proposer.isCommitSuccess()){
						proposer.setNewProposalInstance(instanceID.get(), value);
						instanceID.set(instanceID.get()+1);
						proposer.prepare();
					}else{
						try {
							value = clientMsgQueue.take();
							proposer.setNewProposalInstance(instanceID.get(), value);
							instanceID.set(instanceID.get()+1);
							proposer.prepare();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

					}
				}
			}

		}
	}

	@Override
	public boolean close() throws RemoteException {
		finished = true;
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return true;
	}

	class CommitRecovery extends Thread{
		public void run() {
			while(!finished){
				// scan the committedValue and request committed values if it is necessary
				for (int i=0; i < committedValues.size(); i++){
					if(committedValues.get(i) == null){
						// send request to all accepters for committed value to a particular instance ID
						messenger.sendCommitRequest(i);
					}
				}
				messenger.sendCommitRequest(Integer.MAX_VALUE);
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void setDelay(int maxDelay){
		messenger.setMaxDelay(maxDelay);
	}


}
