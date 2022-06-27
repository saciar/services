package crm.services.wsdl2.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.VariacionMes;

public interface VariacionMesManagerSEI extends Remote{
	public VariacionMes getById(String codigo) throws RemoteException;
	
	public VariacionMes[] getAll() throws RemoteException;
	
	public VariacionMes[] findByField(String field,String value) throws RemoteException;
	
	public String getVariacionById(String codigo) throws RemoteException;
	
	public int getVariacionByMes(int mes) throws RemoteException;
	
}
