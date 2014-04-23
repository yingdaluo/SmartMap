package paxos;

public class PaxosInstance {
	private int instanceID;
	private Object value;
	private String nodeID;
	public int getInstanceID() {
		return instanceID;
	}
	public PaxosInstance(int instanceID, String nodeID, Object value) {
		this.instanceID = instanceID;
		this.nodeID = nodeID;
		this.value = value;
	}
	
	public void setInstanceID(int instanceID) {
		this.instanceID = instanceID;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	public String getNodeID() {
		return nodeID;
	}
	public void setNodeID(String nodeID) {
		this.nodeID = nodeID;
	}
	
	
}
