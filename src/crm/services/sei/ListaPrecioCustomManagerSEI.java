package crm.services.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.ListaPrecioCustom;

public interface ListaPrecioCustomManagerSEI extends Remote {
	public ListaPrecioCustom getListaPrecioCustomById(String codigo) throws RemoteException;
	public ListaPrecioCustom getListaPrecioCustomByCodLugar(String codLugar) throws RemoteException;
	public ListaPrecioCustom[] getAllListaPrecioCustoms() throws RemoteException;
	public ListaPrecioCustom[] findByField(String field,String value) throws RemoteException;
	//public ListaPrecioCustom[] getAllListaPrecioCustomsTranslated(String lang) throws RemoteException;
	public void remove(String codigo) throws RemoteException;
	public void update(ListaPrecioCustom listaPrecioCustom) throws RemoteException;
}
