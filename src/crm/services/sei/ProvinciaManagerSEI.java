package crm.services.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.Provincia;

public interface ProvinciaManagerSEI extends Remote {
	public Provincia getProvinciaById(String codigo) throws RemoteException;
	public Provincia getProvinciaByCodProvincia(String codProvincia) throws RemoteException;
	public Provincia[] getAllProvincias() throws RemoteException;
	public Provincia[] findByPaisId(String value) throws RemoteException;
	public Object[] findCodAndDescriptionByPaisId(String value) throws RemoteException;
	public Provincia[] findByField(String field,String value) throws RemoteException;
	//public Provincia[] getAllProvinciasTranslated(String lang) throws RemoteException;
	public void remove(String codigo) throws RemoteException;
	public void update(Provincia titulo) throws RemoteException;
	public String getNombreProvinciaById(String codigo) throws RemoteException;
	public Provincia getProvinciaByDescripcion(String desc) throws RemoteException;
}
