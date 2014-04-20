package paxos;

public interface Proposer {
	public void setProposalValue(Object value);
	public void prepare();
	public void receivePrepareOK(Proposal proposal, Proposal prevAcceptedProposal, String acceptorID, Object value);
	public void receiveReject(Proposal proposal, Proposal prevAcceptedProposal);
	public void receiveNackAccept(Proposal proposal, Object value);
	public void receiveAcceptOK(Proposal proposal, String acceptorID);
}
