package crm.services.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.TxSeguimiento;

public interface TxSeguimientoManagerSEI  extends Remote {

	public TxSeguimiento[] getAllTxSeguimientos() throws RemoteException;
	
	public void update(TxSeguimiento txSeguimiento) throws RemoteException;
	
	public TxSeguimiento[] findByField(String field,String value) throws RemoteException;
	
	public Object[] getSeguimientosByNroPpto(long nroPpto) throws RemoteException;
}