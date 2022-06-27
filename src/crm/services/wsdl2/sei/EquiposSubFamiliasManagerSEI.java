package crm.services.wsdl2.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.EquiposSubFamilias;

public interface EquiposSubFamiliasManagerSEI extends Remote{
    public EquiposSubFamilias getById(String codigo) throws RemoteException;
	public EquiposSubFamilias[] getAll() throws RemoteException;
	public EquiposSubFamilias[] findByField(String field,String value) throws RemoteException;
	public void remove(String codigo) throws RemoteException;
	public void update(EquiposSubFamilias model) throws RemoteException;

}
