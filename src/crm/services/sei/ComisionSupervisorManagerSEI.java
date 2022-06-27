package crm.services.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.ComisionSupervisor;;

public interface ComisionSupervisorManagerSEI extends Remote {
	public ComisionSupervisor getComisionSupervisorById(String codigo) throws RemoteException;
	public ComisionSupervisor getComisionSupervisorByVendedor(String vendedor) throws RemoteException;
	public ComisionSupervisor[] getAllComisionesSupervisores() throws RemoteException;
	public ComisionSupervisor[] findByField(String field,String value) throws RemoteException;
	//public ComisionSupervisor[] getAllComisionesSupervisoresTranslated(String lang) throws RemoteException;
	public void remove(String codigo) throws RemoteException;
	public void removeByVendedor(String codigoVendedor)throws RemoteException ;
	public void update(ComisionSupervisor comisionSupervisor) throws RemoteException;
}
