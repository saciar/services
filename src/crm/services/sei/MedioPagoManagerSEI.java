package crm.services.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.MedioPago;

public interface MedioPagoManagerSEI extends Remote {
	public Object[] getMedioPagosReport() throws RemoteException;
	public MedioPago getMedioPagoById(String codigo) throws RemoteException;
	public MedioPago getMedioPagoByDescripcion(String descripcion) throws RemoteException ;
	public MedioPago[] getAllMedioPagos() throws RemoteException ;
	public MedioPago[] findByField(String field,String value) throws RemoteException;
	//public MedioPago[] getAllMedioPagosTranslated(String lang) throws RemoteException ;
	public void remove(String codigo) throws RemoteException;
	public void update(MedioPago medioPago) throws RemoteException ;
}
