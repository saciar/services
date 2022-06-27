package crm.services.wsdl2.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.UnidadAdministrador;

public interface UnidadAdministradorManagerSEI extends Remote{
	public String getCodigoUnidad(String codigoVendedor) throws RemoteException;
	public UnidadAdministrador[] getUnidadByCodigoUnidad(String codigoUnidad)throws RemoteException ;
	public void removeByCodigoUnidad(String codigoUnidad) throws RemoteException;
	public String update(UnidadAdministrador unidadVendedor) throws RemoteException;	
	public Object[] getAdministradoresByUnidadAdministrativa(String codUnidad) throws RemoteException;
}
