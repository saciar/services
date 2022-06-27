package crm.services.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.DescPrecioDias;

public interface DescPrecioDiasManagerSEI extends Remote {
	public DescPrecioDias getDescPrecioDiasById(String codigo) throws RemoteException;
	public DescPrecioDias[] getDescPrecioDiasByServicio(String codigoServicio) throws RemoteException;
	public DescPrecioDias[] getAllDescPrecioDias() throws RemoteException;
	public DescPrecioDias[] findByField(String field,String value) throws RemoteException;
	//public DescPrecioDias[] getAllDescPrecioDiasTranslated(String lang) throws RemoteException;
	public void remove(String codigo) throws RemoteException;
	public void update(DescPrecioDias descPrecioDias) throws RemoteException;
}
