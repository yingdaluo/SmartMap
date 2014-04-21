package paxos;

public interface Messenger {
	//Proposers
	public void sendPrepare(ProposalID proposal);
	public void sendAcceptRequest(ProposalID proposal, Object proposalValue);
	public void sendCommit(Object value);
	
	//Acceptors
	public void sendPrepareOK(String toProposer, ProposalID fromProposal, ProposalID acceptedProposal, Object acceptedValue);
	public void sendAcceptOK(String toProposer, ProposalID fromProposal);
	public void sendReject(String toProposer, ProposalID fromProposal, ProposalID receivedMaxProposal);

}
