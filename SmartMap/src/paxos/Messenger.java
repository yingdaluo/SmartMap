package paxos;

public interface Messenger {
	//Proposers
	public void sendPrepare(int instanceID ,ProposalID proposal);
	public void sendAcceptRequest(int instanceID ,ProposalID proposal, Object proposalValue);
	public void sendCommit(int instanceID ,Object value);
	
	//Acceptors
	public void sendPrepareOK(int instanceID ,String toProposer, ProposalID fromProposal, ProposalID acceptedProposal, Object acceptedValue);
	public void sendAcceptOK(int instanceID ,String toProposer, ProposalID fromProposal);
	public void sendReject(int instanceID ,String toProposer, ProposalID fromProposal, ProposalID receivedMaxProposal);

}
