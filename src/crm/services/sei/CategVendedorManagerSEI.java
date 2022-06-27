package crm.services.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.CategVendedor;

public interface CategVendedorManagerSEI extends Remote {
	public static final String CATEGORY_VENDEDOR = "1";
	public static final String CATEGORY_SUPERVISOR = "2";
	public static final String CATEGORY_GERENTE = "3";	
	public static final String CATEGORY_REFERENCIA = "4";
	public static final String CATEGORY_LUGAR_EVENTO = "5";	
	public static final String CATEGORY_GERENTE_ADMINISTRATIVO = "6";	
	public static final String CATEGORY_COBRANZA = "7";
	public static final String CATEGORY_FACTURACION = "8";
	
	public CategVendedor getCategVendedorById(String codigo) throws RemoteException;
	public CategVendedor getCategVendedorByDescripcion(String descripcion) throws RemoteException;
	public CategVendedor[] getAllCategVendedores() throws RemoteException;
	public CategVendedor[] findByField(String field,String value) throws RemoteException;
	//public CategVendedor[] getAllCategVendedoresTranslated(String lang) throws RemoteException;
	public void remove(String codigo) throws RemoteException;
	public void update(CategVendedor categVendedor) throws RemoteException;
}
