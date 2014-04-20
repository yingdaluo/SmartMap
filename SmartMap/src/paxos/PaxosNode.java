package paxos;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface PaxosNode extends Remote{

	public void putproposerQueue(Message message) throws RemoteException;

	public void putacceptorQueue(Message message) throws RemoteException;

	 
}
