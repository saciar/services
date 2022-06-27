package crm.services.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.NuevoCliente;

public interface NuevoClienteManagerSEI extends Remote{

	public abstract NuevoCliente getNuevoClienteById(String codigoCliente)
			throws RemoteException;

	public abstract Object[] getNuevoClienteByClienteCodeReport(
			String codCliente) throws RemoteException;

	public abstract String update(NuevoCliente nuevoCliente)
			throws RemoteException;

	public abstract boolean isNuevoContacto(String codCliente)
			throws RemoteException;

}