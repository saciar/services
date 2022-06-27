package crm.services.report.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.report.PresupuestosGerencia;

public interface PresupuestosGerenciaReportSEI extends Remote{
	public PresupuestosGerencia[] findByRangeDate(int day1, int month1, int year1, 
			int day2, int month2, int year2, long codVendedor, long codUC, long codLugar, long codEstado, String orden, int simple) throws RemoteException;
	
	public PresupuestosGerencia[] findByRangeDateAndCreateDate(int day1, int month1, int year1, 
			int day2, int month2, int year2, long codVendedor, long codUC, long codLugar, long codEstado, int day3, int month3, int year3, String orden, int simple) throws RemoteException;
	
	public PresupuestosGerencia[] findByRangeDateCobranzas(int day1, int month1, int year1, int day2, 
			int month2, int year2, long codVendedor, long codUC, long codLugar, long codEstado, String orden) throws RemoteException ;

}
