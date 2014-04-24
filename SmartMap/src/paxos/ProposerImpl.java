package paxos;

import java.util.HashSet;


public class ProposerImpl implements Proposer {
	private final int quorum;
	private int instanceID;
	private Object proposedValue = null;
	private Object initialValue;
	private ProposalID myProposal;
	private ProposalID lastAcceptedProposal = null;
	private Messenger router;
	private HashSet<String> promiseSet   = new HashSet<String>();
	private HashSet<String> acceptedSet   = new HashSet<String>();
	private boolean isWorking = false;

	public ProposerImpl(int instanceID, String proposer, Messenger router, int quorum){
		this.instanceID = instanceID;
		myProposal = new ProposalID(0, proposer);
		this.router = router;
		this.quorum = quorum;
	}

	@Override
	public void setNewProposalInstance(int instanceID, Object value) {
		if(!isWorking){
			this.instanceID = instanceID;
			proposedValue = value;
			initialValue = value;
		}
	}

	@Override
	public void prepare() {
		promiseSet.clear();
		acceptedSet.clear();
		lastAcceptedProposal = null;
		isWorking = true;
		myProposal.incrementProposalID();
		//System.out.println("Proposer "+myProposal.getProposer()+": I am the proposer of "+ myProposal.getProposer() + ". I will send proposal:"+ myProposal.getProposalID()+","+myProposal.getProposer()+"at instanceID:"+ instanceID);
		router.sendPrepare(instanceID, myProposal);
		TimeoutMonitor tmMonitor = new TimeoutMonitor();
		tmMonitor.start();
	}

	@Override
	public void receivePrepareOK(int instanceID, ProposalID proposal, ProposalID prevAcceptedProposal, String acceptorID, Object value) {
		if(!isWorking)
			return;
		if(prevAcceptedProposal!=null){
			//System.out.println("Proposer "+myProposal.getProposer()+": Prepare ok accepted. Previous accepted proposal:"+prevAcceptedProposal.getProposalID()+","+ prevAcceptedProposal.getProposer());
		}
		else {
			//System.out.println("Proposer "+myProposal.getProposer()+": Prepare ok accepted. No previous proposals");
		}
		if(!proposal.equals(myProposal)|| promiseSet.contains(acceptorID)){
			return;
		}
		promiseSet.add(acceptorID);
		if(prevAcceptedProposal == null){
			//do nothing
		}else if (lastAcceptedProposal == null || prevAcceptedProposal.isGreaterThan(lastAcceptedProposal)) {
			lastAcceptedProposal = prevAcceptedProposal;
			//System.out.println("Proposer "+myProposal.getProposer()+": proposedValue need change. PreAcceptedValue is:"+value);
			if (value != null){
				proposedValue = value;
			}

		}
		if(promiseSet.size()>=quorum){
			//System.out.println("Proposer "+myProposal.getProposer()+": I want to send accept request of value:"+proposedValue);
			router.sendAcceptRequest(instanceID, myProposal, proposedValue);
		}
	}

	@Override
	public void receiveReject(int instanceID, ProposalID proposal, ProposalID prevAcceptedProposal) {
		if(!isWorking)
			return;
		if(!proposal.equals(myProposal))
			return;
		//System.out.println("Proposer "+myProposal.getProposer()+": Prepare reject received. PrevAcceptedProposal Number is:" +prevAcceptedProposal.getProposalID());
		if(prevAcceptedProposal.isGreaterThan(myProposal)||prevAcceptedProposal.equals(myProposal)){
			myProposal.setProposalID(prevAcceptedProposal.getProposalID());
			prepare();
		}
		//		Random random = new Random();
		//		int  n = random.nextInt(100) + 100;
		//		try {
		//			Thread.sleep(n);
		//		} catch (InterruptedException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}

	}

	public void receiveAcceptOK(int instanceID, ProposalID proposal, String acceptorID){
		if(!isWorking)
			return;
		if(!proposal.equals(myProposal)|| acceptedSet.contains(acceptorID))
			return;
		acceptedSet.add(acceptorID);
		if(acceptedSet.size()>=quorum){
			commit();
		}

	}


	
	@Override
	public void receiveNackAccept(int instanceID, ProposalID proposal, Object value) {
		// TODO Auto-generated method stub
	}

	public boolean isWorking() {
		return isWorking;
	}


	private void commit(){
		router.sendCommit(instanceID, proposedValue);
		isWorking = false;
	}

	public boolean isCommitSuccess() {
		if(initialValue == null)
			return false;
		return initialValue.equals(proposedValue);
	}

	class TimeoutMonitor extends Thread{
		public void run(){
			try {
				Thread.sleep(3000);
				if(promiseSet.size()<quorum){
					//System.out.println("Too long to wait. Prepare again");
					router.sendPrepare(instanceID, myProposal);
					TimeoutMonitor tmMonitor = new TimeoutMonitor();
					tmMonitor.start();
				}else if(acceptedSet.size()<quorum){
					//System.out.println("Too long to wait. Resend accept request again");
					router.sendAcceptRequest(instanceID, myProposal, proposedValue);
					TimeoutMonitor tmMonitor = new TimeoutMonitor();
					tmMonitor.start();
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


}
