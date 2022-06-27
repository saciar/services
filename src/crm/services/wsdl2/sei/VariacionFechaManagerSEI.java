package crm.services.wsdl2.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.Date;

import crm.libraries.abm.entities.VariacionFecha;

public interface VariacionFechaManagerSEI extends Remote{
	public int getVariacionFecha(String fecha) throws RemoteException;
	public VariacionFecha[] getVariacionesFecha(String fecha) throws RemoteException;
}
