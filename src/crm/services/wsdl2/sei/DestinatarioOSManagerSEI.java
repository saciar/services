package crm.services.wsdl2.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.DestinatarioOS;


public interface DestinatarioOSManagerSEI extends Remote{
	public String update(DestinatarioOS destinatario) throws RemoteException;	
	public DestinatarioOS getById(String id)throws RemoteException;
	public DestinatarioOS[] getAll()throws RemoteException;

}
