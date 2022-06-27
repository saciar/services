package crm.services.wsdl2.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface SmsManagerSEI extends Remote{
	public Object[] buscarSmsParaLiqOperador(String fechaInicial, String fechaFinal, long codOp) throws RemoteException;
	public Object[] buscarSmsDetalleTodos(String fechaInicial, String fechaFinal) throws RemoteException;
	public Object[] buscarSmsDetalleOperador(String fechaInicial, String fechaFinal, long codOp) throws RemoteException;
	public Object[] buscarSmsParaLiqTodos(String fechaInicial, String fechaFinal) throws RemoteException;
	public Object[] buscarSmsPorNroPptoDetalleOperador(long nroppto, long codOp) throws RemoteException;
	public Object[] buscarSmsPorNroPptoDetalleTodos(long nroppto) throws RemoteException;
	public Object[] buscarSmsPorNroPptoLiqTodos(long nroppto) throws RemoteException;
	public Object[] buscarSmsPorNroPptoLiqOperador(long nroppto, long codOp) throws RemoteException;
}
