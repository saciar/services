package crm.services.wsdl2.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.PrtPptoCondPago;

public interface PrtPptoCondPagoManagerSEI extends Remote {
	public PrtPptoCondPago getById(String codigo) throws RemoteException;
	public PrtPptoCondPago[] getAll() throws RemoteException;
	public PrtPptoCondPago[] findByField(String field,String value) throws RemoteException;
	public void remove(String codigo) throws RemoteException;
	public void update(PrtPptoCondPago model) throws RemoteException;
}

