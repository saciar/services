package crm.services.wsdl2.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.Depositos;

public interface DepositosManagerSEI extends Remote{
	public Depositos getById(String codigo) throws RemoteException;
	public Depositos[] getAll() throws RemoteException;
	public Depositos[] findByField(String field,String value) throws RemoteException;
	public void remove(String codigo) throws RemoteException;
	public void update(Depositos model) throws RemoteException;
}
