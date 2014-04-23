package paxos;

public interface Proposer {
	public void setNewProposalInstance(int instanceID, Object value) ;
	public void prepare();
	public void receivePrepareOK(int instanceID, ProposalID proposal, ProposalID prevAcceptedProposal, String acceptorID, Object value);
	public void receiveReject(int instanceID, ProposalID proposal, ProposalID prevAcceptedProposal);
	public void receiveNackAccept(int instanceID, ProposalID proposal, Object value);
	public void receiveAcceptOK(int instanceID, ProposalID proposal, String acceptorID);
	public boolean isWorking();
	public boolean isCommitSuccess();
}
