package crm.services.report.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.report.PresupuestosCobradosGerencia;;

public interface PresupuestosCobradosGerenciaReportSEI extends Remote{
	public PresupuestosCobradosGerencia[] findByRangeDate(int day1, int month1, int year1, 
			int day2, int month2, int year2) throws RemoteException;

}
