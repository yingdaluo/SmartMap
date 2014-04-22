package paxos;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface PaxosNode extends Remote{

	public void putproposerQueue(Message message) throws RemoteException;

	public void putacceptorQueue(Message message) throws RemoteException;

	public void putClientMessageQueue(Object value) throws RemoteException;
	public ArrayList<Object> deliver() throws RemoteException;
}
