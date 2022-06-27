package crm.services.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.UnidadVendedor;

public interface UnidadVendedorManagerSEI  extends Remote {

	public String getCodigoUnidad(String codigoVendedor) throws RemoteException;
	public UnidadVendedor[] getUnidadByCodigoUnidad(String codigoUnidad)throws RemoteException ;
	public void removeByCodigoUnidad(String codigoUnidad) throws RemoteException;
	public void update(UnidadVendedor unidadVendedor) throws RemoteException ;	
}