package crm.services.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.CategReferencia;


public interface CategReferenciaManagerSEI extends Remote {
	public CategReferencia getCategReferenciaById(String codigo) throws RemoteException;
	public CategReferencia[] getAllCategReferencias() throws RemoteException;
	public CategReferencia[] findByField(String field,String value) throws RemoteException;
	//public CategReferencia[] getAllCategReferenciasTranslated(String lang) throws RemoteException;
	public void remove(String codigo) throws RemoteException;
	public void update(CategReferencia titulo) throws RemoteException;
}
