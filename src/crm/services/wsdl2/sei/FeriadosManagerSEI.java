package crm.services.wsdl2.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface FeriadosManagerSEI extends Remote{

	public String getIdPorFecha(String f) throws RemoteException;
} 
