package crm.services.wsdl2.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.EquiposFamilias;

public interface EquiposFamiliasManagerSEI extends Remote{
    public EquiposFamilias getById(String codigo) throws RemoteException;
	public EquiposFamilias[] getAll() throws RemoteException;
	public EquiposFamilias[] findByField(String field,String value) throws RemoteException;
	public void remove(String codigo) throws RemoteException;
	public void update(EquiposFamilias model) throws RemoteException;
}
