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
			//System.out.print("send prepare ok.");
			//System.out.print(" AcceptedProposal:"+acceptedValue);
			//System.out.println(". AcceptedValue:"+acceptedValue);
			router.sendPrepareOK(instanceID, proposerID, incomingProposal, acceptedProposal, acceptedValue);
		}else{
			router.sendReject(instanceID, proposerID, incomingProposal, promisedProposal);
		}
	}

	@Override
	public void receiveAcceptRequest(String proposerID, ProposalID incomingProposal, Object value) {
//		//System.out.println("Received accept request");
		//System.out.println("My instanceID is:"+instanceID+". My accepted value is:"+acceptedValue+", and its acceptedProposal is:"+acceptedProposal);
		if(promisedProposal == null){}
			//System.out.println("My instanceID is:"+instanceID+". The promised proposal of mine is: null, and the incoming proposal is:"+incomingProposal.getProposalID()+"|"+incomingProposal.getProposer());
		else{}
			//System.out.println("My instanceID is:"+instanceID+". The promised proposal of mine is:"+promisedProposal.getProposalID()+"|"+promisedProposal.getProposer()+", and the incoming proposal is:"+incomingProposal.getProposalID()+"|"+incomingProposal.getProposer() +" with value"+value);
		if (promisedProposal == null || incomingProposal.isGreaterThan(promisedProposal) || incomingProposal.equals(promisedProposal)) {
			promisedProposal    = incomingProposal;
			acceptedProposal    = incomingProposal;
			acceptedValue = value;
			//System.out.println("Send Accept OK message");
			router.sendAcceptOK(instanceID, proposerID, incomingProposal);
		}else{
			//System.out.println("Reject this accept request.");
			router.sendReject(instanceID, proposerID, incomingProposal, promisedProposal);
		}
	}

//	@Override
//	public void receiveCommit(Object value) {
//		//System.out.println("Commit:"+value);
//		promisedProposal = null;
//		acceptedProposal = null;
//		acceptedValue = null;
//	}

}
