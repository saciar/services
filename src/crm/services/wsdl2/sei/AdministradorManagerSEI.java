package crm.services.wsdl2.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.Administrador;

public interface AdministradorManagerSEI extends Remote{
	public Administrador getAdministradorByUserId(String codigo) throws RemoteException;
	public void remove(String codigo) throws RemoteException;
	public String update(Administrador administrador) throws RemoteException;
	public Object[] getAdministradoresSinUnidadAdministrativa() throws RemoteException;
	public Administrador[] findByField(String field,String value) throws RemoteException;
	public Object[] getAdministradoresSinUnidadAdministrativaPorUnidad(String codUnidad) throws RemoteException;
	public String getCodAdministradorByCodUsuario(String codUsuario) throws RemoteException;
}
