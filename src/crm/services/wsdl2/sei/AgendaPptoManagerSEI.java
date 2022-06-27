package crm.services.wsdl2.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.AgendaPpto;

public interface AgendaPptoManagerSEI extends Remote{
	public String update(AgendaPpto cobrador) throws RemoteException;	
	public AgendaPpto[] findByField(String field,String value) throws RemoteException;
	public AgendaPpto getDataById(String codigo) throws RemoteException;

}
