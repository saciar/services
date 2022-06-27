package crm.services.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.FamiliaServ;

public interface FamiliaServManagerSEI extends Remote {
	public static final String FAMILIA_SERVICIO_SUBCONTRATADO = "1";
	public static final String FAMILIA_SERVICIO_ARMADO = "72";
	public static final String FAMILIA_ASISTENTES = "108";
	
	public FamiliaServ getFamiliaServById(String codigo) throws RemoteException;
	public FamiliaServ getFamiliaServByDescripcion(String descripcion) throws RemoteException;
	public FamiliaServ[] getAllFamiliaServs() throws RemoteException;
	public FamiliaServ[] findByField(String field,String value) throws RemoteException;
	//public FamiliaServ[] getAllFamiliaServsTranslated(String lang) throws RemoteException;
	public void remove(String codigo) throws RemoteException;
	public void update(FamiliaServ familiaServ) throws RemoteException;
	public Object[] getFamiliaReport() throws RemoteException;
	public String getDescripcionByServicio(String codFamServ) throws RemoteException ;
}
