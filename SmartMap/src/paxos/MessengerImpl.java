package paxos;

import java.awt.TrayIcon.MessageType;
import java.util.HashMap;

public class MessengerImpl implements Messenger{
	private final HashMap<String, PaxosNode> nodeMap;
	private String nodeID;
	public MessengerImpl(HashMap<String, PaxosNode> nodeMap, String nodeID) {
		this.nodeMap = nodeMap;
		this.nodeID = nodeID;
	}
	
	@Override
	public void sendProposal(Proposal proposal) {
		Message message =  new Message(nodeID, Message.Type.Prepare, null, proposal, null);
		sendMessageToAllAcceptors(message);
	}

	@Override
	public void sendAcceptRequest(Proposal proposal, Object proposalValue) {
		Message message =  new Message(nodeID, Message.Type.AcceptRequest, null, proposal, null);
		sendMessageToAllAcceptors(message);
	}

	@Override
	public void sendCommit(Object value) {
		Message message =  new Message(null, Message.Type.Commit, value, null, null);
		sendMessageToAllAcceptors(message);
	}

	@Override
	public void sendPrepareOK(String toProposer, Proposal fromProposal,
			Proposal acceptedProposal, Object acceptedValue) {
		Message message =  new Message(nodeID, Message.Type.PrepareOK, acceptedValue, fromProposal, acceptedProposal);
		PaxosNode remoteNode = nodeMap.get(toProposer);
		remoteNode.putproposerQueue(message);
	}

	@Override
	public void sendAcceptOK(String toProposer, Proposal fromProposal) {
		Message message =  new Message(nodeID, Message.Type.AcceptOK, null, fromProposal, null);
		PaxosNode remoteNode = nodeMap.get(toProposer);
		remoteNode.putproposerQueue(message);
	}

	@Override
	public void sendReject(String toProposer, Proposal fromProposal,
			Proposal receivedMaxProposal) {
		Message message =  new Message(nodeID, Message.Type.Reject, null, fromProposal, receivedMaxProposal);
		PaxosNode remoteNode = nodeMap.get(toProposer);
		remoteNode.putproposerQueue(message);
	}

	private void sendMessageToAllAcceptors(Message message){
		for(String remoteNodeID : nodeMap.keySet()){
			PaxosNode remoteNode = nodeMap.get(remoteNodeID);
			remoteNode.putacceptorQueue(message);
		}
	}
}
