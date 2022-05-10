package linda.server;

import java.rmi.*;
import java.util.Collection;

import linda.AsynchronousCallback;
import linda.Callback;
import linda.Tuple;
import linda.Linda.eventMode;
import linda.Linda.eventTiming;


public interface LindaServerInterface extends Remote {
	

    public void write(Tuple t) throws RemoteException;

    public Tuple take(Tuple template) throws RemoteException;

    public Tuple read(Tuple template) throws RemoteException;

    public Tuple tryTake(Tuple template) throws RemoteException;

    public Tuple tryRead(Tuple template) throws RemoteException;

    public Collection<Tuple> takeAll(Tuple template) throws RemoteException;

    public Collection<Tuple> readAll(Tuple template) throws RemoteException;

    public void eventRegister(eventMode mode, eventTiming timing, Tuple template, RemoteCallback callback) throws RemoteException;
    
    public void debug(String prefix) throws RemoteException;
	
}
