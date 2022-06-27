package crm.services.report.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.report.InfoSemanal;

public interface InfoSemanalReportSEI extends Remote {
	public InfoSemanal findByWeek(int week, int year) throws RemoteException;
}
