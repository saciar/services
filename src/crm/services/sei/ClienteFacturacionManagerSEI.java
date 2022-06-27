package crm.services.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.ClienteFacturacion;

public interface ClienteFacturacionManagerSEI extends Remote {
	public ClienteFacturacion getClienteFacturacionById(String codigo) throws RemoteException;
	public ClienteFacturacion[] getAllClienteFacturaciones() throws RemoteException;
	public ClienteFacturacion[] findByField(String field,String value) throws RemoteException;
	//public ClienteFacturacion[] getAllClienteFacturacionesTranslated(String lang) throws RemoteException;
	public void remove(String codigo) throws RemoteException;
	public void update(ClienteFacturacion clienteFacturacion) throws RemoteException;
}
