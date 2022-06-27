package crm.services.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.CostoOperativo;
;

public interface CostoOperativoManagerSEI extends Remote {
	public CostoOperativo getCostoOperativo() throws RemoteException;
	public void update(CostoOperativo costoOperativo) throws RemoteException;
}
