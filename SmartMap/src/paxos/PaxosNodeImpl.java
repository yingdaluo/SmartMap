package paxos;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public class PaxosNodeImpl extends UnicastRemoteObject implements PaxosNode {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8138096021948421274L;
	private Acceptor acceptor;
	private Proposer proposer;
	private Messenger router;

	private ConcurrentLinkedDeque<Message> proposerQueue = new ConcurrentLinkedDeque<Message>();
	private ConcurrentLinkedDeque<Message> acceptorQueue = new ConcurrentLinkedDeque<Message>();
	private ConcurrentLinkedDeque<Object> clientMsgQueue = new ConcurrentLinkedDeque<Object>();


	public PaxosNodeImpl()throws RemoteException {
		// Do nothing
	}

	public PaxosNodeImpl(HashMap<String, String> addressMap,String nodeID, int quorum) throws RemoteException{
		router = new MessengerImpl(addressMap, nodeID);
		acceptor = new AcceptorImpl(router);
		proposer = new ProposerImpl(nodeID, router, quorum);

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

	class ProposerHandler extends Thread{
		public void run() {
			while(true){
				if(!proposerQueue.isEmpty()){
					Message message = proposerQueue.removeFirst();
					if(message.getMessageType() == Message.Type.PrepareOK){
						proposer.receivePrepareOK(message.getToProposal(), message.getPrevAcceptedProposal(), message.getSenderID(), message.getValue());
					}else if(message.getMessageType()  == Message.Type.AcceptOK){
						proposer.receiveAcceptOK(message.getToProposal(), message.getSenderID());
					}else if(message.getMessageType()== Message.Type.Reject){
						proposer.receiveReject(message.getToProposal(), message.getPrevAcceptedProposal());
					}
				}
			}
		}
	}


	class AcceptorHandler extends Thread{
		public void run() {
			while(true){
				if(!acceptorQueue.isEmpty()){
					Message message = acceptorQueue.removeFirst();
					if(message.getMessageType() == Message.Type.Prepare){
						acceptor.receivePrepare(message.getToProposal().getProposer(), message.getToProposal());
					}else if(message.getMessageType()  == Message.Type.AcceptRequest){
						acceptor.receiveAcceptRequest(message.getToProposal().getProposer(), message.getToProposal(), message.getValue());
					}else if (message.getMessageType()  == Message.Type.Commit){
						acceptor.receiveCommit(message.getValue());
					}
				}
			}
		}
	}

	class ClientHandler extends Thread{
		public void run() {
			while(true){
				if(!clientMsgQueue.isEmpty() && !proposer.isWorking()){
					Object value = clientMsgQueue.removeFirst();
					proposer.setProposalValue(value);
					proposer.prepare();
				}
			}
		}
	}

	@Override
	public void putClientMessageQueue(Object value) throws RemoteException {
		clientMsgQueue.add(value);
	}
}
