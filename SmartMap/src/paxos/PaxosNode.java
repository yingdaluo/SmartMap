package paxos;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface PaxosNode extends Remote{

	public void putproposerQueue(Message message) throws RemoteException;

	public void putacceptorQueue(Message message) throws RemoteException;

	public void putClientMessageQueue(Object value) throws RemoteException;

	public ArrayList<Object> deliver() throws RemoteException;

	public boolean close() throws RemoteException;

	public void setLostPossibility(double possibility) throws RemoteException;

	public void setDelay(int delay) throws RemoteException;
	
	public void recover()throws RemoteException;
	
}
