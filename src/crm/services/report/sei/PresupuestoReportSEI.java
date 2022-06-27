package crm.services.report.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.report.Presupuesto;

public interface PresupuestoReportSEI extends Remote{
	
	public static int CODIGO_CASTELLANO = 1;
	
	public static int CODIGO_INGLES = 2;
	
	public Presupuesto[] findByNroPpto(long nroPpto, long idCancelacion, long idHeader, 
			long idFooter, long idInstalacion, long idValidez, long idFormaPago, long idCondPago, long idFirma,
			long idSeguridad, long idPersonal, long idCondReserva, long idTipoPresupuesto, long idPeriodo, long idMoneda, 
			double cotizacion) throws RemoteException;
	
	public void sendPresupByEmail(long nroPpto, String usuarioId, String codContactoCliente) throws RemoteException;
	
	public void savePdfFile(long nroPpto, long idCancelacion, long idHeader, long idFooter, long idInstalacion,
			long idValidez, long idFormaPago, long idCondPago, long idFirma, long idSeguridad, long idPersonal, long idCondReserva, long idTipoPresupuesto, 
			long idPeriodo, long idMoneda, double cotizacion) throws RemoteException;
	
	public void saveTxtFile(long nroPpto, long idCancelacion, long idHeader, long idFooter, long idInstalacion,
			long idValidez, long idFormaPago, long idCondPago, long idFirma, long idSeguridad, long idPersonal, long idCondReserva, long idTipoPresupuesto, 
			long idPeriodo, long idMoneda, double cotizacion) throws RemoteException;
	
	public void sendEmail(String usuarioId, String codContactoCliente) throws RemoteException;
}
