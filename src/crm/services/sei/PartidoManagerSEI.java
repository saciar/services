package crm.services.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.Partido;
import crm.libraries.abm.entities.Provincia;

public interface PartidoManagerSEI extends Remote {
	public Partido getPartidoById(String codigo) throws RemoteException;
	public Partido getPartidoByDescripcion(String desc) throws RemoteException;
	public Partido getPartidoByCodPartido(String codPartido) throws RemoteException;
	public Partido[] getAllPartidos() throws RemoteException;
	public Partido[] findByProvinciaId(String value) throws RemoteException;
	public Partido[] findByField(String field,String value) throws RemoteException;
	//public Partido[] getAllPartidosTranslated(String lang) throws RemoteException;
	public void remove(String codigo) throws RemoteException;
	public void update(Partido partido) throws RemoteException;
	public String getNombrePartidoById(String codigo) throws RemoteException;
	public Object[] findNamesByProvinciaId(String value) throws RemoteException;
}
