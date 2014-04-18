package paxos;

public class Proposal {
	private int proposalID;
	private  final String proposer;

	public Proposal(int proposalID, String proposer){
		this.proposalID = proposalID;
		this.proposer = proposer;
	}

	public int getProposalID() {
		return proposalID;
	}

	public void setProposalID(int proposalID) {
		this.proposalID = proposalID;
	}

	public String getProposer() {
		return proposer;
	}

	public void incrementProposalID(){
		this.proposalID ++;
	}

	public boolean isGreaterThan(Proposal proposal){
		if(this.proposalID >proposal.proposalID)
			return true;
		else if(this.proposer.compareTo(proposal.proposer)>0)
			return true;
		return false;
	}
}
