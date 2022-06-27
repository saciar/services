package crm.services.report.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.report.Presupuesto;

public interface PresupuestoExcelReportSEI extends Remote{
	
	public static int CODIGO_CASTELLANO = 1;
	
	public static int CODIGO_INGLES = 2;
	
	public Presupuesto[] findByNroPpto(long nroPpto) throws RemoteException;


}
