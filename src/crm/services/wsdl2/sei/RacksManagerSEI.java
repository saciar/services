package crm.services.wsdl2.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.Rack;

public interface RacksManagerSEI extends Remote{
	public Rack getClienteContactoById(String codigo) throws RemoteException;
	public Rack[] findByField(String field,String value) throws RemoteException;	
	public void remove(String codigo) throws RemoteException;
	public String update(Rack rack) throws RemoteException;
	public void removeRack(int codigo) throws RemoteException;
}
