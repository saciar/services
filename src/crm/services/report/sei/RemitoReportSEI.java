package crm.services.report.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.report.Remito;

public interface RemitoReportSEI extends Remote{
	public Remito[] findByNroPpto(long nroppto) throws RemoteException;
}
