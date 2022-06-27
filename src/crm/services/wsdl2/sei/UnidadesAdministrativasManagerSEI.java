package crm.services.wsdl2.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.UnidadAdministrativa;

public interface UnidadesAdministrativasManagerSEI extends Remote {
	public UnidadAdministrativa getUnidadComercialById(String codigo) throws RemoteException;
	public void remove(String codigo) throws RemoteException;
	public String update(UnidadAdministrativa unidadAdministrativa) throws RemoteException;
	public UnidadAdministrativa[] findByField(String field,String value)throws RemoteException;
	public UnidadAdministrativa[] getAll()throws RemoteException;
}
