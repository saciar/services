package crm.services.wsdl2.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.DestinatarioOF;;

public interface DestinatarioOFManagerSEI extends Remote{
	public String update(DestinatarioOF destinatario) throws RemoteException;	
	public DestinatarioOF getById(String id)throws RemoteException;
	public DestinatarioOF[] getAll()throws RemoteException;
}
