package crm.services.wsdl2.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.PrtPptoFPago;

public interface PrtPptoFPagoManagerSEI extends Remote {
	public PrtPptoFPago getById(String codigo) throws RemoteException;
	public PrtPptoFPago[] getAll() throws RemoteException;
	public PrtPptoFPago[] findByField(String field,String value) throws RemoteException;
	public void remove(String codigo) throws RemoteException;
	public void update(PrtPptoFPago model) throws RemoteException;
}
