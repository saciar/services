package crm.services.report.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.report.Evento;

public interface EventReportSEI extends Remote {
	public Evento[] findByWeek(int week,int year) throws RemoteException;
	public Evento[] findByWeekAndVendedor(int week,int year, long codVendedor) throws RemoteException;
	public Evento[] findByWeekAndUC(int week,int year, long codUC) throws RemoteException;
	public Evento[] findByDay(int day,int month, int year) throws RemoteException;
	public Evento[] findByDayAndVendedor(int day, int month, int year, long codVend) throws RemoteException;
	public Evento[] findByDayAndUC(int day, int month, int year, long codUC) throws RemoteException;
	
}
