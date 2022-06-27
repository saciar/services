package crm.services.wsdl2.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.TipoCobrador;

public interface TipoCobradorManagerSEI extends Remote {
	public TipoCobrador getTipoCobradorById(String codigo) throws RemoteException;
	public TipoCobrador getTipoCobradorByDescripcion(String descripcion) throws RemoteException;
	public TipoCobrador[] getAllTipoCobradores() throws RemoteException;
	public TipoCobrador[] findByField(String field,String value) throws RemoteException;
	public void remove(String codigo) throws RemoteException;
	public void update(TipoCobrador tipoEvento) throws RemoteException;
	public Object[] getTipoCobradoresReport() throws RemoteException;

}
