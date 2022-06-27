package crm.services.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.Comision;

public interface ComisionManagerSEI extends Remote {
	public Comision getComisionById(String codigo) throws RemoteException;
	public Comision getComisionByVendedor(String vendedor) throws RemoteException;
	public Comision[] getAllComisiones() throws RemoteException;
	public Comision[] findByField(String field,String value) throws RemoteException;
	//public Comision[] getAllComisionesTranslated(String lang) throws RemoteException;
	public void remove(String codigo) throws RemoteException;
	public void removeByVendedor(String codigoVendedor)throws RemoteException ;
	public void update(Comision comision) throws RemoteException;
	public String getMarcoLiquidacionByCodVendedor(String codVendedor) throws RemoteException;
	public String getPorcentajeByCodVendedor(String codVendedor) throws RemoteException;
	public boolean isLugarComisionable(String codLugar) throws RemoteException;
}
