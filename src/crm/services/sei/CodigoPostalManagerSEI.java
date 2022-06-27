package crm.services.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.CodigoPostal;

public interface CodigoPostalManagerSEI extends Remote {
	public CodigoPostal getCodigoPostalById(String codigo) throws RemoteException;
	public CodigoPostal getCodigoPostalByCP(String cp) throws RemoteException;
	public CodigoPostal[] getAllCodigoPostales() throws RemoteException;
	public CodigoPostal[] findByField(String field,String value) throws RemoteException;
	public CodigoPostal[] findByLocalidadId(String value) throws RemoteException;
	//public CodigoPostal[] getAllCodigoPostalesTranslated(String lang) throws RemoteException;
	public void remove(String codigo) throws RemoteException;
	public void update(CodigoPostal codigoPostal) throws RemoteException;
	public Object[] findNamesByLocalidadId(String value) throws RemoteException;
}
