package crm.services.report.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.report.OrdenServicio;
import crm.libraries.report.OrdenServiciosDesconfirmada;

public interface OsDesconfirmadaReportSEI extends Remote{
		public OrdenServiciosDesconfirmada[] findByNroPpto(long nroPpto) throws RemoteException;
		public void savePdfFile(long nroPpto) throws RemoteException;	
		public boolean sendOSByEmail2(long nroPpto, String fechaInicial, String fechaFinal, String evento, String usuarioId, String codLugar, String emailDestino) throws RemoteException;
}
