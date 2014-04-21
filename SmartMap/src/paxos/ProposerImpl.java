package paxos;

import java.util.HashSet;

public class ProposerImpl implements Proposer {
	private final int quorum;
	private Object proposedValue = null;
	private ProposalID myProposal;
	private ProposalID lastAcceptedProposal = null;
	private Messenger router;
	private HashSet<String> promiseSet   = new HashSet<String>();
	private HashSet<String> acceptedSet   = new HashSet<String>();
	private boolean isWorking = false;
	public ProposerImpl(String proposer, Messenger router, int quorum){
		myProposal = new ProposalID(0, proposer);
		this.router = router;
		this.quorum = quorum;
	}

	@Override
	public void setProposalValue(Object value) {
		if(!isWorking)
			proposedValue = value;
	}

	@Override
	public void prepare() {
		promiseSet.clear();
		acceptedSet.clear();
		lastAcceptedProposal = null;
		isWorking = true;
		myProposal.incrementProposalID();
		System.out.println("Proposer "+myProposal.getProposer()+": I am the proposer of "+ myProposal.getProposer() + ". I will send proposal:"+ myProposal.getProposalID()+","+myProposal.getProposer());
		router.sendPrepare(myProposal);
	}

	@Override
	public void receivePrepareOK(ProposalID proposal, ProposalID prevAcceptedProposal, String acceptorID, Object value) {
		if(prevAcceptedProposal!=null)
			System.out.println("Proposer "+myProposal.getProposer()+": Prepare ok accepted. Previous accepted proposal:"+prevAcceptedProposal.getProposalID()+","+ prevAcceptedProposal.getProposer());
		else 
			System.out.println("Proposer "+myProposal.getProposer()+": Prepare ok accepted. No previous proposals");
		if(!proposal.equals(myProposal)|| promiseSet.contains(acceptorID)){
			return;
		}
		promiseSet.add(acceptorID);
		if(prevAcceptedProposal == null){
			//do nothing
		}else if (lastAcceptedProposal == null || prevAcceptedProposal.isGreaterThan(lastAcceptedProposal)) {
			lastAcceptedProposal = prevAcceptedProposal;
			System.out.println("Proposer "+myProposal.getProposer()+": proposedValue need change. PreAcceptedValue is:"+value);
			if (value != null)
				proposedValue = value;
		}
		if(promiseSet.size()>=quorum){
			System.out.println("Proposer "+myProposal.getProposer()+": I want to send accept request of value:"+proposedValue);
			router.sendAcceptRequest(myProposal, proposedValue);
		}
	}

	@Override
	public void receiveReject(ProposalID proposal, ProposalID prevAcceptedProposal) {
		if(!proposal.equals(myProposal))
			return;
		System.out.println("Proposer "+myProposal.getProposer()+": Prepare reject received. PrevAcceptedProposal Number is:" +prevAcceptedProposal.getProposalID());
		if(prevAcceptedProposal.isGreaterThan(myProposal))
			myProposal.setProposalID(prevAcceptedProposal.getProposalID());
		prepare();
	}

	public void receiveAcceptOK(ProposalID proposal, String acceptorID){
		if(!proposal.equals(myProposal)|| acceptedSet.contains(acceptorID))
			return;
		acceptedSet.add(acceptorID);
		
		if(acceptedSet.size()>=quorum){
			commit();
		}

	}


	@Override
	public void receiveNackAccept(ProposalID proposal, Object value) {
		// TODO Auto-generated method stub
	}

	public boolean isWorking() {
		return isWorking;
	}


	private void commit(){
		router.sendCommit(proposedValue);
		isWorking = false;
	}


}
