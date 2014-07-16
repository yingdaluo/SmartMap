package paxos;

public class AcceptorImpl implements Acceptor {
	private ProposalID promisedProposal = null;
	private ProposalID acceptedProposal = null;
	private Object acceptedValue = null;
	private Messenger router;
	private int instanceID;
	public AcceptorImpl(int instanceID, Messenger router) {
		this.router = router;
		this.instanceID = instanceID;
	}
	@Override
	public void receivePrepare(String proposerID, ProposalID incomingProposal) {
		if (promisedProposal != null && promisedProposal.equals(incomingProposal)) { 
			router.sendPrepareOK(instanceID, proposerID, incomingProposal, acceptedProposal, acceptedValue);
		}
		else if(promisedProposal == null || incomingProposal.isGreaterThan(promisedProposal)){
			promisedProposal = incomingProposal;
			router.sendPrepareOK(instanceID, proposerID, incomingProposal, acceptedProposal, acceptedValue);
		}else{
			router.sendReject(instanceID, proposerID, incomingProposal, promisedProposal);
		}
	}

	@Override
	public void receiveAcceptRequest(String proposerID, ProposalID incomingProposal, Object value) {
		if (promisedProposal == null || incomingProposal.isGreaterThan(promisedProposal) || incomingProposal.equals(promisedProposal)) {
			promisedProposal    = incomingProposal;
			acceptedProposal    = incomingProposal;
			acceptedValue = value;
			router.sendAcceptOK(instanceID, proposerID, incomingProposal);
		}else{
			router.sendReject(instanceID, proposerID, incomingProposal, promisedProposal);
		}
	}

}
