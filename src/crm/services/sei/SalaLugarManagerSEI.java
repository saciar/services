package crm.services.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.SalaLugar;

public interface SalaLugarManagerSEI extends Remote {
	public SalaLugar getSalaLugarById(String codigo) throws RemoteException;
	public SalaLugar getSalaLugarByCodSala(String codigoSala) throws RemoteException;
	public SalaLugar[] getAllSalaLugares() throws RemoteException;
	public SalaLugar[] findByField(String field,String value) throws RemoteException;
	//public SalaLugar[] getAllSalaLugaresTranslated(String lang) throws RemoteException;
	public void remove(String codigo) throws RemoteException;
	public void update(SalaLugar salaLugar) throws RemoteException;
	public Object[] getSalaLugarReport() throws RemoteException;
	public Object[] getSalaLugarReportByLugar(String codigoLugar) throws RemoteException;
	public SalaLugar getSalaLugarByCodSalaAndLugar(String codigoSala, String codigoLugar) throws RemoteException ;
}
