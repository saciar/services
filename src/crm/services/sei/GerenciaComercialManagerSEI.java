package crm.services.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.GerenciaComercial;
import crm.libraries.abm.entities.Usuario;

public interface GerenciaComercialManagerSEI extends Remote {
	public GerenciaComercial getGerenciaComercialById(String codigo) throws RemoteException;
	public GerenciaComercial[] findByDescripcionOrGerente(String descripcion, String gerente)throws RemoteException;
	public GerenciaComercial[] getAllGerenciaComercials() throws RemoteException;
	public GerenciaComercial[] findByField(String field,String value) throws RemoteException;
	//public GerenciaComercial[] getAllGerenciaComercialsTranslated(String lang) throws RemoteException;
	public void remove(String codigo) throws RemoteException;
	public void update(GerenciaComercial gerenciaComercial) throws RemoteException;
}
