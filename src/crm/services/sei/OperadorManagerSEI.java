package crm.services.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.Operador;

public interface OperadorManagerSEI extends Remote {
	public Operador getOperadorById(String codigo) throws RemoteException;
	public Operador getOperadorByApYNom(String apYNom) throws RemoteException;
	public Operador[] getAllOperadores() throws RemoteException;
	public Operador[] findByField(String field,String value) throws RemoteException;
	//public Operador[] getAllOperadoresTranslated(String lang) throws RemoteException;
	public void remove(String codigo) throws RemoteException;
	public void update(Operador vendedor) throws RemoteException;
	public String getDescrpcion(String codigo)throws RemoteException ;
	public Operador[] getOperadorByModalidad(String modalidad) throws RemoteException;
}
