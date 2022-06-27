package crm.services.report.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.report.Adelanto;

public interface AdelantoReportSEI extends Remote {
	public Adelanto[] findByNroPpto(long nroPpto) throws RemoteException;
	
	public void sendOFByEmail (long nroPpto, String vendedorName) throws RemoteException;
	
	public void savePdfFile(long nroPpto) throws RemoteException;
	
	public boolean sendOFByEmail2(long nroPpto, String usuarioId, String destinatario) throws RemoteException;

}
