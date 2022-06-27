package crm.services.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;

import crm.libraries.abm.entities.FechasProceso;

public interface FechasProcesoManagerSEI extends Remote{

	public void update(FechasProceso fecha) throws RemoteException;

	public String getFechaProcesoById(String cod) throws RemoteException;
	
	public String getMaxCodigo() throws RemoteException;
	
}