package crm.services.report.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.report.CondicionPagoGerencia;

public interface CondicionPagoGerenciaReportSEI extends Remote{
	public CondicionPagoGerencia[] findByRangeDate(int day1, int month1, int year1, 
			int day2, int month2, int year2, long codVendedor, long codUC, long codCondPago) throws RemoteException;
	
	public int findTotalPptos(int day1, int month1, int year1, 
			int day2, int month2, int year2) throws RemoteException;

}
