package crm.services.wsdl2.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.PrtPptoCondReserva;;

public interface PrtPptoCondReservaManagerSEI extends Remote{
	public PrtPptoCondReserva getById(String codigo) throws RemoteException;
	public PrtPptoCondReserva[] getAll() throws RemoteException;
	public PrtPptoCondReserva[] findByField(String field,String value) throws RemoteException;
	public void remove(String codigo) throws RemoteException;
	public void update(PrtPptoCondReserva model) throws RemoteException;
}
