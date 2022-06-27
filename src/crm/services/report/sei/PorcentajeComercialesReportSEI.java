package crm.services.report.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.report.PorcentajeGerencia;

public interface PorcentajeComercialesReportSEI extends Remote{
	public PorcentajeGerencia[] findByRangeDate(int day1, int month1, int year1, 
			int day2, int month2, int year2, long codVendedor, long codUC, long codLugar, long codEstado) throws RemoteException;
	
	public int findTotalPptos(int day1, int month1, int year1, 
			int day2, int month2, int year2, long codVendedor, long codUc) throws RemoteException;

}
