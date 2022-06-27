package crm.services.report.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.report.LimitesGerencia;;

public interface LimitesGerenciaReportSEI extends Remote{
	
	public LimitesGerencia[] findByRangeDate(int day1, int month1, int year1, 
			int day2, int month2, int year2, long codVendedor, long codUC, long mayorMenor, double montoLimite) throws RemoteException;

}
