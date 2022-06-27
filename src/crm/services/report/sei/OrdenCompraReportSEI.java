package crm.services.report.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.report.OrdenCompra;

public interface OrdenCompraReportSEI extends Remote {
public OrdenCompra[] findByNroPpto(long nroPpto) throws RemoteException;
	
	public void sendOFByEmail (long nroPpto, String vendedorName) throws RemoteException;
	
	public void savePdfFile(long nroPpto) throws RemoteException;
}
