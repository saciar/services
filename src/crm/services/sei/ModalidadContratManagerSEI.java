package crm.services.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.ModalidadContrat;

public interface ModalidadContratManagerSEI extends Remote {
	public static final String MODALIDAD_INTERNO = "1";
	public static final String MODALIDAD_EXTERNO = "2";
	public static final String MODALIDAD_INTERNO_OPCIONAL = "3";
	public static final String MODALIDAD_EXTERNO_OPCIONAL = "4";
	
	public ModalidadContrat getModalidadContratById(String codigo) throws RemoteException;
	public ModalidadContrat getModalidadContratByDescripcion(String descripcion) throws RemoteException;
	public ModalidadContrat[] getAllModalidadContrats() throws RemoteException;
	public ModalidadContrat[] findByField(String field,String value) throws RemoteException;
	//public ModalidadContrat[] getAllModalidadContratsTranslated(String lang) throws RemoteException;
	public void remove(String codigo) throws RemoteException;
	public void update(ModalidadContrat modalidadContrat) throws RemoteException;
}
