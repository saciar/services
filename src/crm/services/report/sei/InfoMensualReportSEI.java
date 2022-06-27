package crm.services.report.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.report.InfoMensual;

public interface InfoMensualReportSEI extends Remote{
	public InfoMensual findByMonth(int month, int year) throws RemoteException;

}
