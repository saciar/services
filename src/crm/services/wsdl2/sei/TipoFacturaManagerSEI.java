package crm.services.wsdl2.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.TipoFactura;

public interface TipoFacturaManagerSEI extends Remote {
	public TipoFactura getTipoFacturaById(String codigo) throws RemoteException;
	public TipoFactura getTipoFacturaByDescripcion(String descripcion) throws RemoteException;
	public TipoFactura[] getAllTipoFacturas() throws RemoteException;
	public TipoFactura[] findByField(String field,String value) throws RemoteException;
	//public TipoFactura[] getAllTipoFacturasTranslated(String lang) throws RemoteException;
	public void remove(String codigo) throws RemoteException;
	public void update(TipoFactura tipoFactura) throws RemoteException;
	public Object[] getTipoFacturasReport() throws RemoteException;
}
