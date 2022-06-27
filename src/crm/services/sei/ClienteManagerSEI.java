package crm.services.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;

import crm.libraries.abm.entities.Cliente;

public interface ClienteManagerSEI extends Remote {
	public Cliente getClienteById(String codigo) throws RemoteException;
	public Cliente getClienteInfo(String codigo) throws RemoteException;
	public Cliente[] getAllClientes() throws RemoteException;
	public Cliente[] findByField(String field,String value) throws RemoteException;
	//public Cliente[] getAllClientesTranslated(String lang) throws RemoteException;
	public void remove(String codigo) throws RemoteException;
	public String update(Cliente cliente) throws RemoteException;
	
	public int getCantidadClientes() throws RemoteException;
	public Object[] getClientesReport() throws RemoteException;
	public Object[] getClientesReportLimited(int firstResult, int maxResults) throws RemoteException;
	public Object[] getClientesModificadosReport(long fecha) throws RemoteException;
	
	public void testStringArrayParam(String[] test) throws RemoteException;
	public void testClientArrayParam(Cliente[] test) throws RemoteException;
	public void testClientParam(Cliente test) throws RemoteException;
	public Object[] obtenerCodigoYNombreFantasia(String nombre) throws RemoteException;
	public Object[] buscarPorNombreFantasiaOEmpresa(String nombre) throws RemoteException;
	public Object[] getClienteNoCobrado(String codigo) throws RemoteException;
	public Object[] getClienteNoUsados(String date) throws RemoteException;
}
