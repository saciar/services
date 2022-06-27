package crm.services.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.DiasAVencer;

public interface DiasAVencerManagerSEI extends Remote {
	public DiasAVencer getDiasAVenver() throws RemoteException;
	public void update(DiasAVencer diasAVencer) throws RemoteException;
}
