package crm.services.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.VendedorUsuario;

public interface VendedorUsuarioManagerSEI  extends Remote {

	public String getCodigoVendedor(String codigoUsuario) throws RemoteException;
	
	public String getCodigoUsuario(String codigoVendedor) throws RemoteException;
	
	public boolean isVendedor(String codigoUsuario)throws RemoteException;
}