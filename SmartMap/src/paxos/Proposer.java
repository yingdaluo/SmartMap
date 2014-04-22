package paxos;

public interface Proposer {
	public void setNewProposalInstance(int instanceID, Object value) ;
	public void prepare();
	public void receivePrepareOK(ProposalID proposal, ProposalID prevAcceptedProposal, String acceptorID, Object value);
	public void receiveReject(ProposalID proposal, ProposalID prevAcceptedProposal);
	public void receiveNackAccept(ProposalID proposal, Object value);
	public void receiveAcceptOK(ProposalID proposal, String acceptorID);
	public boolean isWorking();
}
