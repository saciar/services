package crm.services.wsdl2.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.Transporte;

public interface TransporteManagerSEI extends Remote{
    public Transporte getById(String codigo) throws RemoteException;
	public Transporte[] getAll() throws RemoteException;
	public Transporte[] findByField(String field,String value) throws RemoteException;
	public void remove(String codigo) throws RemoteException;
	public void update(Transporte model) throws RemoteException;


}
