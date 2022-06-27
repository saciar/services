package crm.services.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.Acceso;
import crm.libraries.abm.entities.MarcosLiquidacion;

public interface MarcosLiquidacionManagerSEI extends Remote {
	public MarcosLiquidacion getMarcosLiquidacionById(String codigo) throws RemoteException;
	public MarcosLiquidacion getMarcosLiquidacionByDescripcion(String descripcion) throws RemoteException;
	public MarcosLiquidacion[] getAllMarcosLiquidaciones() throws RemoteException;
	public MarcosLiquidacion[] findByField(String field,String value) throws RemoteException;
	//public MarcosLiquidacion[] getAllMarcosLiquidacionesTranslated(String lang) throws RemoteException;
	public void remove(String codigo) throws RemoteException;
	public void update(MarcosLiquidacion marcosLiquidacion) throws RemoteException;
}
