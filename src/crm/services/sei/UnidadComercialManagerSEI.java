package crm.services.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.UnidadComercial;
import crm.libraries.abm.entities.Usuario;
import crm.libraries.abm.entities.Vendedor;

public interface UnidadComercialManagerSEI  extends Remote {

	public UnidadComercial getUCDataByCodigoUsuario(String codigoUsuario) throws RemoteException;
	public UnidadComercial getUCDataByCodigoUnidad(String codigoUnidad) throws RemoteException;
	
	public UnidadComercial getUnidadComercialById(String codigo) throws RemoteException;
	public UnidadComercial[] getUnidadComercialesBySupervisorOrDescripcion(String supervisor,String descripcion) throws RemoteException;
	public UnidadComercial[] getAllUnidadComerciales() throws RemoteException;
	public UnidadComercial[] findByField(String field,String value) throws RemoteException;
	//public UnidadComercial[] getAllUnidadComercialesTranslated(String lang) throws RemoteException;
	public void remove(String codigo) throws RemoteException;
	public String update(UnidadComercial unidadComercial) throws RemoteException;
	public UnidadComercial getUCDataByCodigoVendedor(String codigoVendedor) throws RemoteException;
	public Object[] getPptosOfAllUnidadComercial() throws RemoteException;
}