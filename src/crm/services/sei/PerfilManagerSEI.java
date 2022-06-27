package crm.services.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.Perfil;

public interface PerfilManagerSEI extends Remote {
	public static final String PERFIL_ADMIN = "1";
	public static final String PERFIL_VENDEDOR = "2";
	public static final String PERFIL_SUPERVISOR = "3";
	public static final String PERFIL_GERENCIA_COMERCIAL = "4";
	public static final String PERFIL_COLD = "5";	
	public static final String PERFIL_FACTURACION = "6";
	public static final String PERFIL_COBRANZAS = "7";
	public static final String PERFIL_DEPOSITOS = "8";
	public static final String PERFIL_GERENCIA_ADMINISTRATIVA = "9";
	public static final String PERFIL_GASTOS = "10";
	
	public Perfil getPerfilById(String codigo) throws RemoteException;
	public Perfil getPerfilByDescripcion(String descripcion) throws RemoteException;
	public Perfil[] getAllPerfiles() throws RemoteException;
	//public Perfil[] getAllPerfilesTranslated(String lang) throws RemoteException;
	public Perfil[] findByField(String field,String value) throws RemoteException;
	public void remove(String codigo) throws RemoteException;
	public void update(Perfil acceso) throws RemoteException;
}
