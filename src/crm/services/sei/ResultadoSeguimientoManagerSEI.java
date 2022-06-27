package crm.services.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.ResultadoSeguimiento;

public interface ResultadoSeguimientoManagerSEI extends Remote {
	public ResultadoSeguimiento getResultadoSeguimientoById(String codigo) throws RemoteException;
	public ResultadoSeguimiento getResultadoSeguimientoByDescripcion(String descripcion) throws RemoteException;
	public ResultadoSeguimiento[] getAllResultadoSeguimientos() throws RemoteException;
	public ResultadoSeguimiento[] findByField(String field,String value) throws RemoteException;
	//public ResultadoSeguimiento[] getAllResultadoSeguimientosTranslated(String lang) throws RemoteException;
	public void remove(String codigo) throws RemoteException;
	public void update(ResultadoSeguimiento resultadoSeguimiento) throws RemoteException;
	public Object[] getResultadosReportByAccion(String codAccion) throws RemoteException ;
}
