package crm.services.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.UnidadBonus;
import crm.libraries.abm.entities.Vendedor;

public interface UnidadBonusManagerSEI  extends Remote {

	public UnidadBonus getUnidadBonusById(String codigo,String nivel) throws RemoteException;
	public UnidadBonus[] getAllUnidadBonus() throws RemoteException;
	public UnidadBonus[] findByUnidadComercialId(String unidadComercial) throws RemoteException ;
	public UnidadBonus[] findByField(String field,String value) throws RemoteException;
	public void remove(String codigo,String nivel) throws RemoteException;
	public void update(UnidadBonus unidadBonus) throws RemoteException;

}