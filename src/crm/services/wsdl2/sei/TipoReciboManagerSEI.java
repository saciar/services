package crm.services.wsdl2.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.TipoRecibo;

public interface TipoReciboManagerSEI extends Remote {
	public TipoRecibo getTipoReciboById(String codigo) throws RemoteException;
	public TipoRecibo getTipoReciboByDescripcion(String descripcion) throws RemoteException;
	public TipoRecibo[] getAllTipoRecibos() throws RemoteException;
	public TipoRecibo[] findByField(String field,String value) throws RemoteException;
	public void remove(String codigo) throws RemoteException;
	public void update(TipoRecibo tipoEvento) throws RemoteException;
	public Object[] getTipoReciboReport() throws RemoteException;

}
