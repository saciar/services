package crm.services.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.Proveedor;

public interface ProveedorManagerSEI extends Remote {
	public Proveedor getProveedorById(String codigo) throws RemoteException;
	public Proveedor getProveedorByNombre(String nombre) throws RemoteException;
	public Proveedor[] getAllProveedores() throws RemoteException;
	public Proveedor[] findByField(String field,String value) throws RemoteException;
	//public Proveedor[] getAllProveedoresTranslated(String lang) throws RemoteException;
	public void remove(String codigo) throws RemoteException;
	public void update(Proveedor proveedor) throws RemoteException;
	public String getDescrpcion(String codigo)throws RemoteException;
}
