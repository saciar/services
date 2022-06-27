package crm.services.report.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.report.ReferenciaGerencia;

public interface ReferenciaGerenciaReportSEI extends Remote{
	public ReferenciaGerencia[] findByRangeDate(int day1, int month1, int year1, 
			int day2, int month2, int year2, long codVendedor, long codUC, long codCondPago) throws RemoteException;
	
	public int findTotalPptos(int day1, int month1, int year1, 
			int day2, int month2, int year2) throws RemoteException;
	

}
