package crm.services.wsdl2.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.AgendaTareasCobranzas;

public interface AgendaTareasCobranzasManagerSEI extends Remote{
	public void remove(String codigo) throws RemoteException;
	public String update(AgendaTareasCobranzas administrador) throws RemoteException;	
	public AgendaTareasCobranzas[] findByField(String field,String value) throws RemoteException;
	public AgendaTareasCobranzas getAgendaById(String codigo) throws RemoteException;
	public AgendaTareasCobranzas[] findAlertaToday(String date) throws RemoteException;
	public Object[] getClienteEventoToAgenda(long nroppto) throws RemoteException;
}
