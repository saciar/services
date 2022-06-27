package crm.services.report.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.report.DiaryPlannerSala;;

public interface DiaryPlannerReportSEI extends Remote{
	public DiaryPlannerSala[] findByDay(int day,int month, int year, String tipo) throws RemoteException;
	public DiaryPlannerSala[] findByDateRange(int day1,int month1, int year1, int day2,int month2, int year2) throws RemoteException;
}
