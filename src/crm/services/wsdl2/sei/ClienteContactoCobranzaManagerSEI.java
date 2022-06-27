package crm.services.wsdl2.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.ClienteContactoCobranza;

public interface ClienteContactoCobranzaManagerSEI extends Remote{
	public ClienteContactoCobranza getClienteContactoById(String codigo) throws RemoteException;
	public ClienteContactoCobranza[] getAllClienteContactos() throws RemoteException;
	public ClienteContactoCobranza[] findByField(String field,String value) throws RemoteException;
	public ClienteContactoCobranza[] findByClientAndField(String client,String field,String value) throws RemoteException;
	public void remove(String codigo) throws RemoteException;
	public String update(ClienteContactoCobranza clienteContacto) throws RemoteException;
	//public ClienteContacto[] getAllClienteContactosTranslated(String lang) throws RemoteException;
	
	public int getCantidadClienteContactos() throws RemoteException;
	public Object[] getClienteContactosReport() throws RemoteException;
	public Object[] getClienteContactosReportLimited(int firstResult, int maxResults) throws RemoteException;	
	//public Object[] getClienteContactosModificadosReport(long time) throws RemoteException;
	
	//public ClienteContacto getClienteContactoInfo(String codigo) throws RemoteException;
	public Object[] getClienteContactoByClienteCodeReport(String codCliente) throws RemoteException;
}
