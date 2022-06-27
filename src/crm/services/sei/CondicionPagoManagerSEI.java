package crm.services.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.CondicionPago;

public interface CondicionPagoManagerSEI extends Remote {
	public Object[] getCondicionPagosReport() throws RemoteException;
	
	public CondicionPago getCondicionPagoById(String codigo) throws RemoteException;
	public CondicionPago getCondicionPagoByDescripcion(String descripcion) throws RemoteException;
	public CondicionPago[] getAllCondicionPagos() throws RemoteException;
	public CondicionPago[] findByField(String field,String value) throws RemoteException;
	//public CondicionPago[] getAllCondicionPagosTranslated(String lang) throws RemoteException;
	public void remove(String codigo) throws RemoteException;
	public void update(CondicionPago condicionPago) throws RemoteException;
}
