package paxos;

import java.io.Serializable;

public class Proposal implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -9515933220181197L;
	private int proposalID;
	private  final String proposerID;

	public Proposal(int proposalID, String proposer){
		this.proposalID = proposalID;
		this.proposerID = proposer;
	}

	public int getProposalID() {
		return proposalID;
	}

	public void setProposalID(int proposalID) {
		this.proposalID = proposalID;
	}

	public String getProposer() {
		return proposerID;
	}

	public void incrementProposalID(){
		this.proposalID ++;
	}

	public boolean isGreaterThan(Proposal proposal){
		if(this.proposalID >proposal.proposalID)
			return true;
		else if(this.proposerID.compareTo(proposal.proposerID)>0)
			return true;
		return false;
	}
}
