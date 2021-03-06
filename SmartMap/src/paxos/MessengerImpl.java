package paxos;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Random;

public class MessengerImpl implements Messenger{
	private final HashMap<String, String> remoteAddressMap;
	private HashMap<String, PaxosNode> nodeMap =  new HashMap<String, PaxosNode>();
	private String nodeID;
	private HashMap<String, String> deadNodes =  new HashMap<String, String>();
	private int maxDelay = 0;
	
	public MessengerImpl(HashMap<String, String> remoteAddressMap, String nodeID) {
		this.remoteAddressMap = remoteAddressMap;
		this.nodeID = nodeID;
	}
	
	@Override
	public void sendPrepare(int instanceID ,ProposalID proposal) {
		Message message =  new Message(nodeID, instanceID, Message.Type.Prepare, null, proposal, null);
		sendMessageToAllAcceptors(message);
	}

	@Override
	public void sendAcceptRequest(int instanceID ,ProposalID proposal, Object proposalValue) {
		Message message =  new Message(nodeID, instanceID, Message.Type.AcceptRequest, proposalValue, proposal, null);
		sendMessageToAllAcceptors(message);
	}

	@Override
	public void sendCommit(int instanceID ,Object value) {
		Message message =  new Message(null, instanceID, Message.Type.Commit, value, null, null);
		sendMessageToAllAcceptors(message);
	}

	@Override
	public void sendPrepareOK(int instanceID ,String toProposer, ProposalID fromProposal,
			ProposalID acceptedProposal, Object acceptedValue) {
		Message message =  new Message(nodeID, instanceID, Message.Type.PrepareOK, acceptedValue, fromProposal, acceptedProposal);
		PaxosNode remoteNode = getRemoteNode(toProposer);
		if(remoteNode!=null){
			try {
				enjoyDelay();
				remoteNode.putproposerQueue(message);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		
	}

	@Override
	public void sendAcceptOK(int instanceID ,String toProposer, ProposalID fromProposal) {
		Message message =  new Message(nodeID,instanceID, Message.Type.AcceptOK, null, fromProposal, null);
		PaxosNode remoteNode = getRemoteNode(toProposer);
		if(remoteNode!=null){
			try {
				enjoyDelay();
				remoteNode.putproposerQueue(message);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		
	}

	@Override
	public void sendReject(int instanceID ,String toProposer, ProposalID fromProposal,
			ProposalID receivedMaxProposal) {
		Message message =  new Message(nodeID,instanceID, Message.Type.Reject, null, fromProposal, receivedMaxProposal);
		PaxosNode remoteNode = getRemoteNode(toProposer);
		if(remoteNode!=null){
			try {
				remoteNode.putproposerQueue(message);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	@Override
	public void sendCommitToSingleNode(int instanceID, Object value, String toNodeID) {
		Message message = new Message(nodeID, instanceID, Message.Type.Commit, value, null, null);
		PaxosNode remoteNode = getRemoteNode(toNodeID);
		if(remoteNode!=null){
			try {
				enjoyDelay();
				remoteNode.putacceptorQueue(message);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void sendCommitRequest(int instanceID) {
		Message message = new Message(nodeID, instanceID, Message.Type.CommitRequest, null, null, null);
		sendMessageToAllAcceptors(message);
	}
	

	private void sendMessageToAllAcceptors(Message message){
		for(String remoteNodeID : remoteAddressMap.keySet()){
			PaxosNode remoteNode = getRemoteNode(remoteNodeID);
			if(remoteNode!=null){
				try {
					enjoyDelay();
					remoteNode.putacceptorQueue(message);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	private PaxosNode getRemoteNode(String nodeID){
		if(nodeMap.containsKey(nodeID))
			return nodeMap.get(nodeID);
		if(deadNodes.containsKey(nodeID))
			return null;
		register(nodeID);
		return nodeMap.get(nodeID);
	}
	
	private void register(String nodeID){
		try{
			String remoteIPAddress = "//"+remoteAddressMap.get(nodeID)+"/RMI";
			PaxosNode remoteNode =  (PaxosNode)Naming.lookup(remoteIPAddress);
			nodeMap.put(nodeID, remoteNode);
		} catch (NotBoundException e){
			
		} catch (MalformedURLException e){
			e.printStackTrace();
		} catch (RemoteException e){
			deadNodes.put(nodeID, nodeID);
			//System.out.println("Cannot connect to node:"+ nodeID);
		}
			
	}

	@Override
	public void setMaxDelay(int maxDelay) {
		this.maxDelay = maxDelay;
	}
	
	private void enjoyDelay(){
		if(maxDelay == 0)
			return;
		try {
			Random random = new Random();
			int delay = random.nextInt(maxDelay);
			Thread.sleep(delay);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
