package crm.services.wsdl2.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.FuenteEvento;;

public interface FuenteEventoManagerSEI extends Remote {
	public FuenteEvento getFuenteEventoById(String codigo) throws RemoteException;
	public FuenteEvento getFuenteEventoByDescripcion(String descripcion) throws RemoteException;
	public FuenteEvento[] getAllFuenteEventos() throws RemoteException;
	public FuenteEvento[] findByField(String field,String value) throws RemoteException;
	//public TipoEvento[] getAllTipoEventosTranslated(String lang) throws RemoteException;
	public void remove(String codigo) throws RemoteException;
	public void update(FuenteEvento tipoEvento) throws RemoteException;
	public Object[] getFuenteEventosReport() throws RemoteException;

}
