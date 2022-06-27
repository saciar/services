package crm.services.report.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.report.OrdenServicio;

public interface OrdenServicioReportSEI extends Remote{
	public OrdenServicio[] findByNroPpto(long nroPpto) throws RemoteException;
	public void savePdfFile(long nroPpto) throws RemoteException;	
	
	public boolean sendOSByEmail2(long nroPpto, String fechaInicial, String fechaFinal, String evento, String usuarioId, String codLugar, String emailDestino) throws RemoteException;
	public boolean sendOSByEmail(long nroPpto, String fechaInicial, String fechaFinal, String evento, String usuarioId, String codLugar) throws RemoteException;
}
