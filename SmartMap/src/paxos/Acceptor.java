package paxos;


public interface Acceptor {
	public void receivePrepare(String fromProposer, Proposal incomingProposal);
	
	public void receiveAcceptRequest(String fromProposer, Proposal incomingProposal, Object value);
}
