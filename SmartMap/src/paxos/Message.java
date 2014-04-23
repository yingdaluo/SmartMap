package paxos;

import java.io.Serializable;

public class Message implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6377112373782950816L;

	public enum  Type{
		Prepare,
		PrepareOK,
		Reject, 
		AcceptRequest, 
		AcceptOK,
		Commit,
		CommitRequest
	}
	final String senderID;
	final int instanceID;
	final Type messageType;
	final Object value;
	final ProposalID toProposal;
	final ProposalID prevAcceptedProposal; 
	
	public Message(String senderID, int instanceID, Type type, Object value, ProposalID proposal, ProposalID prevAcceptedProposal) {
		this.senderID = senderID;
		this.instanceID = instanceID;
		this.messageType = type;
		this.value = value;
		this.toProposal = proposal;
		this.prevAcceptedProposal = prevAcceptedProposal;
	}

	public int getInstanceID() {
		return instanceID;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getSenderID() {
		return senderID;
	}

	public Type getMessageType() {
		return messageType;
	}

	public Object getValue() {
		return value;
	}

	public ProposalID getToProposal() {
		return toProposal;
	}

	public ProposalID getPrevAcceptedProposal() {
		return prevAcceptedProposal;
	}

}
