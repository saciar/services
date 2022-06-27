package crm.services.report.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;

import crm.libraries.report.LimitesYEstadoGerencia;

public interface LimitesYEstadosGerenciaReportSEI extends Remote{
	
	public LimitesYEstadoGerencia[] findByRangeDate(int day1, int month1, int year1, 
			int day2, int month2, int year2, long codVendedor, long codUC, 
			long montoMayorMenor, double montoLimite, long fechaMayorMenor, long codEstado) throws RemoteException;
	
	public int findTotalPptos(int day1, int month1, int year1, 
			int day2, int month2, int year2) throws RemoteException;

}
