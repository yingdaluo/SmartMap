package paxos;

import java.io.Serializable;

public class ProposalID implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -9515933220181197L;
	private int proposalID;
	private  final String proposerID;

	public ProposalID(int proposalID, String proposer){
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

	public boolean isGreaterThan(ProposalID proposal){
		if(this.proposalID >proposal.proposalID)
			return true;
		else if(this.proposalID == proposal.proposalID && this.proposerID.compareTo(proposal.proposerID)>0)
			return true;
		return false;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProposalID other = (ProposalID) obj;
		if (proposalID != other.proposalID)
			return false;
		if (proposerID == null) {
			if (other.proposerID != null)
				return false;
		} else if (!proposerID.equals(other.proposerID))
			return false;
		return true;
	}

	public static void main(String args[]){
		ProposalID p1 = new ProposalID(2, "node1");
		ProposalID p2 = new ProposalID(2, "node1");
		
		System.out.println(p1.equals(p2));
	}
}
