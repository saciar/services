package crm.services.report.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.services.report.manager.ReportBuilder;

public interface InformeOSReportSEI extends Remote{
	public void createInformeOS(long nroPpto) throws RemoteException;
}
