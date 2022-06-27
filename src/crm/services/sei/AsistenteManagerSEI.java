package crm.services.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.Asistente;

public interface AsistenteManagerSEI extends Remote {
	public Asistente getAsistenteById(String codigo) throws RemoteException;
	public Asistente getAsistenteByApYNom(String apYNom) throws RemoteException;
	public Asistente[] getAllAsistentes() throws RemoteException;
	public Asistente[] findByField(String field,String value) throws RemoteException;
	//public Asistente[] getAllAsistentesTranslated(String lang) throws RemoteException;
	public void remove(String codigo) throws RemoteException;
	public void update(Asistente asistente) throws RemoteException;
	public String getDescrpcion(String codigo)throws RemoteException;
}
