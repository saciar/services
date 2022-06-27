package crm.services.report.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.report.PresupuestosConfirmadosGerencia;

public interface PresupuestosConfirmadosGerenciaReportSEI extends Remote{
	public PresupuestosConfirmadosGerencia[] findByRangeDate(int day1, int month1, int year1, 
			int day2, int month2, int year2) throws RemoteException;


}
