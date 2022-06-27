package crm.services.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.Vendedor;

public interface VendedorManagerSEI extends Remote {

	
	public Vendedor getVendedorById(String codigo) throws RemoteException;
	public Vendedor getVendedorByApYNom(String apYNom) throws RemoteException;
	public Vendedor[] getAllVendedores() throws RemoteException;
	public Vendedor[] findByField(String field,String value) throws RemoteException;
	public Vendedor[] findByCategoryIdAndField(String categoryId,String field,String value) throws RemoteException;
	//public Vendedor[] getAllVendedoresTranslated(String lang) throws RemoteException;
	public void remove(String codigo) throws RemoteException;
	public String update(Vendedor vendedor/*,String unidadComercial*/) throws RemoteException;
	public Object[] getAllVendedoresNotInUnidadesVendedores(String categoria) throws RemoteException;
	public Object[] getAllVendedoresNotInUnidadesVendedoresByVendedores(String categoria,String vendedoresArray) throws RemoteException ;
	public Object[] getAllVendedoresNotInUnidadesComerciales(String categoria) throws RemoteException ;
	public Object[] getAllVendedoresNotInUnidadesComercialesByVendedores(String categoria,String vendedor) throws RemoteException ;
	//public String[] getVendedorReportByCodigoReport(String codigo, String categoria) throws RemoteException;
	public Object[] getVendedorReportByCodigoReport(String codigo) throws RemoteException;
	public Object[] getVendedoresByComercialUnit(String codigoUC) throws RemoteException;
	public boolean isVendedorById(String codVendedor) throws RemoteException;
	public Object[] getVendedoresSinUnidadComercial(String categoria) throws RemoteException;
}
