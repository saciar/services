package crm.services.wsdl2.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.PrtPptoSignature;

public interface PrtPptoSignatureManagerSEI extends Remote {
	public PrtPptoSignature getById(String codigo) throws RemoteException;
	public PrtPptoSignature[] getAll() throws RemoteException;
	public PrtPptoSignature[] findByField(String field,String value) throws RemoteException;
	public void remove(String codigo) throws RemoteException;
	public void update(PrtPptoSignature model) throws RemoteException;
}
