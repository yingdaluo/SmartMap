package paxos;

import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public class PaxosNodeImpl {
	private Acceptor acceptor;
	private Proposer proposer;
	private Messenger router;

	private ConcurrentLinkedDeque<Message> proposerQueue = new ConcurrentLinkedDeque<Message>();
	private ConcurrentLinkedDeque<Message> acceptorQueue = new ConcurrentLinkedDeque<Message>();


	public PaxosNodeImpl() {
		// Do nothing
	}

	public PaxosNodeImpl(HashMap<String, PaxosNode> nodeMap,String nodeID, int quorum) {
		router = new MessengerImpl(nodeMap, nodeID);
		acceptor = new AcceptorImpl(router);
		proposer = new ProposerImpl(nodeID, router, quorum);

		ProposerHandler proposerHandler = new ProposerHandler();
		proposerHandler.run();

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
					Message message = proposerQueue.getFirst();
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
					Message message = acceptorQueue.getFirst();
					if(message.getMessageType() == Message.Type.Prepare){
						acceptor.receivePrepare(message.getToProposal().getProposer(), message.getToProposal());
					}else if(message.getMessageType()  == Message.Type.AcceptOK){
						acceptor.receiveAcceptRequest(message.getToProposal().getProposer(), message.getToProposal(), message.getValue());
					}else if (message.getMessageType()  == Message.Type.Commit){
						acceptor.receiveCommit(message.getValue());
					}
				}
			}
		}

	}
}
