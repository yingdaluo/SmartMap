package paxos;

public interface Messenger {
	//Proposers
	public void sendProposal(Proposal proposal);
	public void sendAcceptRequest(Proposal proposal, Object proposalValue);
	public void sendCommit(Object value);
	
	//Acceptors
	public void sendPrepareOK(String toProposer, Proposal fromProposal, Proposal acceptedProposal, Object acceptedValue);
	public void sendAcceptOK(String toProposer, Proposal fromProposal);
	public void sendReject(String toProposer, Proposal fromProposal, Proposal receivedMaxProposal);

}
