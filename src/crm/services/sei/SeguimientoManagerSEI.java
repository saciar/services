package crm.services.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.Seguimiento;

public interface SeguimientoManagerSEI extends Remote {
	public Seguimiento getSeguimientoById(String codigo) throws RemoteException;
	public Seguimiento getSeguimientoByDescripcion(String descripcion) throws RemoteException;
	public Seguimiento[] getAllSeguimientos() throws RemoteException;
	public Seguimiento[] findByField(String field,String value) throws RemoteException;
	//public Seguimiento[] getAllSeguimientosTranslated(String lang) throws RemoteException;
	public void remove(String codigo) throws RemoteException;
	public void update(Seguimiento seguimiento) throws RemoteException;
	public Object[] getAccionesReport() throws RemoteException;
}
