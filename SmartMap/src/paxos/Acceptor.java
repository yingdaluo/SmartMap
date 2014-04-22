package paxos;


public interface Acceptor {
	public void receivePrepare(String fromProposer, ProposalID incomingProposal);
	
	public void receiveAcceptRequest(String fromProposer, ProposalID incomingProposal, Object value);
	
//	public void receiveCommit(Object value);
}
