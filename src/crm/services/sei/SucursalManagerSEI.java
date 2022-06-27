package crm.services.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.Sucursal;

public interface SucursalManagerSEI  extends Remote {

	public String getSucursalNameByCodigo(String codigo) throws RemoteException;
	public Sucursal getSucursalById(String codigo) throws RemoteException;
	public Sucursal getSucursalByDescripcion(String descripcion) throws RemoteException;
	public Sucursal[] getAllSucursales() throws RemoteException;
	public Sucursal[] findByField(String field,String value) throws RemoteException;
	//public Sucursal[] getAllSucursalesTranslated(String lang) throws RemoteException;
	public void remove(String codigo) throws RemoteException;
	public void update(Sucursal sucursal) throws RemoteException;
}