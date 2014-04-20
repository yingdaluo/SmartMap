package paxos;
import java.rmi.Remote;

public interface PaxosNode extends Remote{

	public void putproposerQueue(Message message);

	public void putacceptorQueue(Message message);

	 
}
