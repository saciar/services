package crm.services.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Vector;

import crm.libraries.abm.entities.Pais;

public interface ServerTransactionManagerSEI extends Remote {
	public byte [] executeTransaction(byte [] transaction) throws RemoteException;
	public String executeTransaction2(String transaction) throws RemoteException;
	public String[] getArray() throws RemoteException;
	//public Pais getObject() throws RemoteException;
	public String status() throws RemoteException;
}