package crm.services.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.Localidad;
import crm.libraries.abm.entities.Provincia;

public interface LocalidadManagerSEI extends Remote {
	public Localidad getLocalidadById(String codigo) throws RemoteException;
	public Localidad getLocalidadByCodLocalidad(String codLocalidad) throws RemoteException;
	public String getNombreLocalidadById(String codigo) throws RemoteException;
	public Localidad[] getAllLocalidades() throws RemoteException;
	public Localidad[] findByPartidoId(String value) throws RemoteException;
	public Localidad[] findByField(String field,String value) throws RemoteException;
	//public Localidad[] getAllLocalidadesTranslated(String lang) throws RemoteException;
	public void remove(String codigo) throws RemoteException;
	public void update(Localidad localidad) throws RemoteException;
	public Localidad getLocalidadByDescripcion(String descripcion) throws RemoteException;
	public Object[] findNamesByPartidoId(String value) throws RemoteException;
}
