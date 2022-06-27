package crm.services.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.ClienteContacto;

public interface ClienteContactoManagerSEI extends Remote{
	public ClienteContacto getClienteContactoById(String codigo) throws RemoteException;
	public ClienteContacto[] getAllClienteContactos() throws RemoteException;
	public ClienteContacto[] findByField(String field,String value) throws RemoteException;
	public ClienteContacto[] findByClientAndField(String client,String field,String value) throws RemoteException;
	public void remove(String codigo) throws RemoteException;
	public String update(ClienteContacto clienteContacto) throws RemoteException;
	//public ClienteContacto[] getAllClienteContactosTranslated(String lang) throws RemoteException;
	
	public int getCantidadClienteContactos() throws RemoteException;
	public Object[] getClienteContactosReport() throws RemoteException;
	public Object[] getClienteContactosReportLimited(int firstResult, int maxResults) throws RemoteException;	
	//public Object[] getClienteContactosModificadosReport(long time) throws RemoteException;
	
	//public ClienteContacto getClienteContactoInfo(String codigo) throws RemoteException;
	public Object[] getClienteContactoByClienteCodeReport(String codCliente) throws RemoteException;
}
