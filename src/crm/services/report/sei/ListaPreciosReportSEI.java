package crm.services.report.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.report.ListaPrecios;

public interface ListaPreciosReportSEI extends Remote{
	public ListaPrecios findByMes(int mes) throws RemoteException;	
	public ListaPrecios findByFecha(String fecha) throws RemoteException;
}
