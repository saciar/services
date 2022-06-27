package crm.services.wsdl2.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.TipoVenta;

public interface TipoVentaManagerSEI extends Remote {
	public TipoVenta getTipoVentaById(String codigo) throws RemoteException;
	public TipoVenta getTipoVentaByDescripcion(String descripcion) throws RemoteException;
	public TipoVenta[] getAllTipoVentas() throws RemoteException;
	public TipoVenta[] findByField(String field,String value) throws RemoteException;
	//public TipoEvento[] getAllTipoEventosTranslated(String lang) throws RemoteException;
	public void remove(String codigo) throws RemoteException;
	public void update(TipoVenta tipoEvento) throws RemoteException;
	public Object[] getTipoVentasReport() throws RemoteException;

}
