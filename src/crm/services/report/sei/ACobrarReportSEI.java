package crm.services.report.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.report.ACobrar;

public interface ACobrarReportSEI extends Remote{
	public ACobrar[] findByDate(int day1, int month1, int year1) throws RemoteException;

}
