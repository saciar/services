package crm.services.wsdl2.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.PrtPptoTipoPresupuesto;

public interface PrtPptoTipoPresupuestoManagerSEI extends Remote{

	public PrtPptoTipoPresupuesto getById(String codigo) throws RemoteException;

	public PrtPptoTipoPresupuesto[] getAll() throws RemoteException;

	public PrtPptoTipoPresupuesto[] findByField(String field, String value) throws RemoteException;

	public void remove(String codigo) throws RemoteException;

	public void update(PrtPptoTipoPresupuesto ppi) throws RemoteException;

}
