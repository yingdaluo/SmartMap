package paxos;

import java.util.HashSet;

public class ProposerImpl implements Proposer {
	private final int quorum;
	private Object proposedValue = null;
	private Proposal myProposal;
	private Proposal lastAcceptedProposal = null;
	private Messenger router;
	private HashSet<String> promiseSet   = new HashSet<String>();
	private HashSet<String> acceptedSet   = new HashSet<String>();

	public ProposerImpl(String proposer, Messenger router, int quorum){
		myProposal = new Proposal(0, proposer);
		this.router = router;
		this.quorum = quorum;
	}

	@Override
	public void setProposalValue(Object value) {
		proposedValue = value;
	}

	@Override
	public void prepare() {
		promiseSet.clear();
		myProposal.incrementProposalID();
		router.sendProposal(myProposal);
	}

	@Override
	public void receivePrepareOK(Proposal proposal, Proposal prevAcceptedProposal, String acceptorID, Object value) {
		if(proposal.isGreaterThan(myProposal))
			myProposal.setProposalID(proposal.getProposalID());
		
		if(!proposal.equals(myProposal)|| promiseSet.contains(acceptorID))
			return;
		promiseSet.add(acceptorID);
		if(prevAcceptedProposal == null){
			//do nothing
		}else if (lastAcceptedProposal == null || prevAcceptedProposal.isGreaterThan(lastAcceptedProposal)) {
			lastAcceptedProposal = prevAcceptedProposal;

        	if (value != null)
        		proposedValue = value;
        }
		if(promiseSet.size()>=quorum){
			router.sendAcceptRequest(myProposal, proposedValue);
		}
	}

	@Override
	public void receiveReject(Proposal proposal, Proposal prevAcceptedProposal) {
		if(!proposal.equals(myProposal))
			return;
		if(prevAcceptedProposal.isGreaterThan(myProposal))
			myProposal.setProposalID(prevAcceptedProposal.getProposalID());
		prepare();
	}

	public void receiveAcceptOK(Proposal proposal, String acceptorID){
		if(proposal.isGreaterThan(myProposal))
			myProposal.setProposalID(proposal.getProposalID());
		
		if(!proposal.equals(myProposal)|| acceptedSet.contains(acceptorID))
			return;
		acceptedSet.add(acceptorID);
		
		if(promiseSet.size()>=quorum){
			router.sendCommit(proposedValue);
		}
		
	}
	
	
	@Override
	public void receiveNackAccept(Proposal proposal, Object value) {
		// TODO Auto-generated method stub
	}

}
