package crm.services.wsdl2.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;

import crm.libraries.abm.entities.GastoSubcontratacion;
import crm.libraries.abm.entities.Ppto_GastoSC;
import crm.libraries.abm.entities.Subcontratado;

public interface GastosManagerSEI extends Remote {

	public Object[] buscarPorNumero(long nro) throws RemoteException;
	public int haveSubcontratados(long nropto) throws RemoteException;
	public Object[] buscarPorFecha(String startDate, String endDate) throws RemoteException;
	public Object[] getServiciosSucontratados(long nro, long nropto) throws RemoteException;
	public boolean grabarGastoSubcontratacion(long codGasto, double costo, long codProv, String estado) throws RemoteException;
	public void guardarServicioSubcontratado(Subcontratado subc, long codSalaServicio) throws RemoteException;
	public Object[] getCostosXServicio(long codServicio) throws RemoteException;
	
	public Object[] getSubcontratadoByServ(long cosServ) throws RemoteException;
}
