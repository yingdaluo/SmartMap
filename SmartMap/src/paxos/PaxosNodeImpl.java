package paxos;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public class PaxosNodeImpl extends UnicastRemoteObject implements PaxosNode {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8138096021948421274L;
	private ConcurrentHashMap<Integer, Acceptor> acceptorMap = new ConcurrentHashMap<Integer, Acceptor>();
	private Proposer proposer;
	private Messenger router;
	private int instanceID;
	private String nodeID;
	private int deliverIndex = 0;
	private boolean finished = false;

	private ConcurrentLinkedDeque<Message> proposerQueue = new ConcurrentLinkedDeque<Message>();
	private ConcurrentLinkedDeque<Message> acceptorQueue = new ConcurrentLinkedDeque<Message>();
	private ConcurrentLinkedDeque<Object> clientMsgQueue = new ConcurrentLinkedDeque<Object>();
	private Vector<Object> committedValues = new Vector<Object>();


	public PaxosNodeImpl(HashMap<String, String> addressMap,String nodeID, int quorum) throws RemoteException{
		this.nodeID = nodeID;
		router = new MessengerImpl(addressMap, nodeID);
		instanceID = 0;
		proposer = new ProposerImpl(instanceID, nodeID, router, quorum);

		execute();
	} 

	public void execute(){
		ProposerHandler proposerHandler = new ProposerHandler();
		proposerHandler.start();

		AcceptorHandler acceptorHandler = new AcceptorHandler();
		acceptorHandler.start();

		ClientHandler clientHandler = new ClientHandler();
		clientHandler.start();

	}

	public void putproposerQueue(Message message){
		proposerQueue.add(message);
	}

	public void putacceptorQueue(Message message){
		acceptorQueue.add(message);
	}

	@Override
	public void putClientMessageQueue(Object value) throws RemoteException {
		clientMsgQueue.add(value);
	}

	public ArrayList<Object> deliver(){
		ArrayList<Object> resultList =  new ArrayList<Object>();
		while(committedValues.get(deliverIndex)!=null){
			resultList.add(committedValues.get(deliverIndex));
			deliverIndex++;
		}
		return resultList;
	}

	class ProposerHandler extends Thread{
		public void run() {
			while(!finished){
				if(!proposerQueue.isEmpty()){
					Message message = proposerQueue.removeFirst();
					if(message.getMessageType() == Message.Type.PrepareOK){
						proposer.receivePrepareOK(message.getInstanceID(), message.getToProposal(), message.getPrevAcceptedProposal(), message.getSenderID(), message.getValue());
					}else if(message.getMessageType()  == Message.Type.AcceptOK){
						proposer.receiveAcceptOK(message.getInstanceID(), message.getToProposal(), message.getSenderID());
					}else if(message.getMessageType()== Message.Type.Reject){
						proposer.receiveReject(message.getInstanceID(), message.getToProposal(), message.getPrevAcceptedProposal());
					}
				}
			}
		}
	}

	class AcceptorHandler extends Thread{
		public void run() {
			while(!finished){
				if(!acceptorQueue.isEmpty()){
					Message message = acceptorQueue.removeFirst();
					Acceptor acceptor = acceptorMap.get(message.getInstanceID());
					if(acceptor == null){
						acceptor =  new AcceptorImpl(message.getInstanceID(), router);
						acceptorMap.put(message.getInstanceID(), acceptor);
					}					
					if(message.getMessageType() == Message.Type.Prepare){
						acceptor.receivePrepare(message.getToProposal().getProposer(), message.getToProposal());
					}else if(message.getMessageType()  == Message.Type.AcceptRequest){
						acceptor.receiveAcceptRequest(message.getToProposal().getProposer(), message.getToProposal(), message.getValue());
					}else if (message.getMessageType()  == Message.Type.Commit){
						if(message.getInstanceID()>=committedValues.size()){
							for(int i= committedValues.size();i<=message.getInstanceID(); i++){
								committedValues.add(null);
							}	
						}
						if(committedValues.get(message.getInstanceID()) == null|| !committedValues.get(message.getInstanceID()).equals(message.getValue())){
							System.out.println("Insert value:"+message.getValue()+" at committed list index:"+message.getInstanceID());
							committedValues.insertElementAt(message.getValue(), message.getInstanceID());	
							//TODO implement durable commit	
						}
					}
				}
			}
		}
	}

	class ClientHandler extends Thread{
		public void run() {
			while(!finished){
				if(!clientMsgQueue.isEmpty()){
					Object value = clientMsgQueue.getFirst();
					if(acceptorMap.size() == 0){//initial state
						if(!clientMsgQueue.isEmpty()){
							value = clientMsgQueue.getFirst();
							proposer.setNewProposalInstance(instanceID, value);
							instanceID++;
							proposer.prepare();
						}
					}else if(!proposer.isWorking()){
						if(!proposer.isCommitSuccess()){
							System.out.println("Let's prepare again for value :"+ value);
							value = clientMsgQueue.getFirst();
							proposer.setNewProposalInstance(instanceID, value);
							instanceID++;
							proposer.prepare();
						}else{
							clientMsgQueue.removeFirst();
							if(!clientMsgQueue.isEmpty()){
								value = clientMsgQueue.getFirst();
								proposer.setNewProposalInstance(instanceID, value);
								instanceID++;
								proposer.prepare();
							}
						}
					}
				}


//				if(acceptorMap.size() == 0){
//
//				}else if((!proposer.isWorking())){
//					if(){
//						Object value = clientMsgQueue.getFirst();
//						if(!proposer.isCommitSuccess()){
//							System.out.println("Let's prepare again for value :"+ value);
//							value = clientMsgQueue.getFirst();
//							proposer.setNewProposalInstance(instanceID, value);
//							instanceID++;
//							proposer.prepare();
//						}else{
//							clientMsgQueue.removeFirst();
//							if(!clientMsgQueue.isEmpty()){
//								value = clientMsgQueue.getFirst();
//								proposer.setNewProposalInstance(instanceID, value);
//								instanceID++;
//								proposer.prepare();
//							}
//						}
//					}
//				}
			}
			System.out.println("ClientHandler closed for node:"+nodeID);
		}
	}

	@Override
	public boolean close() throws RemoteException {
		finished = true;
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}


}
