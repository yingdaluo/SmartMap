package paxos;

public class AcceptorImpl implements Acceptor{
	private Proposal promisedProposal = null;
	private Proposal acceptedProposal = null;
	private Object acceptedValue = null;
	private Router router;
	
	public AcceptorImpl(Router router) {
		this.router = router;
	}
	@Override
	public void receivePrepare(String proposerID, Proposal incomingProposal) {
		if (promisedProposal != null && promisedProposal.equals(incomingProposal)) { 
			router.sendPrepareOK(proposerID, incomingProposal, acceptedProposal, acceptedValue);
		}
		else if(promisedProposal == null || incomingProposal.isGreaterThan(promisedProposal)){
			promisedProposal = incomingProposal;
			router.sendPrepareOK(proposerID, incomingProposal, acceptedProposal, acceptedValue);
		}else{
			router.sendReject(proposerID, incomingProposal, promisedProposal);
		}
	}

	@Override
	public void receiveAcceptRequest(String proposerID, Proposal incomingProposal, Object value) {
		if (promisedProposal == null || incomingProposal.isGreaterThan(promisedProposal) || incomingProposal.equals(promisedProposal)) {
			promisedProposal    = incomingProposal;
			acceptedProposal    = incomingProposal;
			acceptedValue = value;
			
			router.sendAcceptOK(proposerID, incomingProposal);
		}else{
			router.sendReject(proposerID, incomingProposal, promisedProposal);
		}
	}

}
