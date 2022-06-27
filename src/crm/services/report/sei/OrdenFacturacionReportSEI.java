package crm.services.report.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;

import crm.libraries.report.OrdenFacturacion;

public interface OrdenFacturacionReportSEI extends Remote {
	public OrdenFacturacion[] findByNroPpto(long nroPpto) throws RemoteException;
	
	public boolean sendOFByEmail (long nroPpto, String vendedorName) throws RemoteException;
	
	public void savePdfFile(long nroPpto) throws RemoteException;
	
	public boolean sendOFByEmail2(long nroPpto, String usuarioId, String destinatario) throws RemoteException;
}
