package crm.services.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.Usuario;


public interface UsuarioManagerSEI extends Remote {
	public Usuario getUsuarioById(String codigo) throws RemoteException;
	public Usuario[] findByField(String field,String value) throws RemoteException;
	public void remove(String codigo) throws RemoteException;
	public void update(Usuario usuario) throws RemoteException;
	public Usuario getUsuarioById2(String name, String pass) throws RemoteException;
	public String getUserCodeByUsername(String username, String password) throws RemoteException;
	public String getNameByCode(String code)throws RemoteException;
	//public String getEmailByCode(String code)throws RemoteException;
	public boolean getAccessTo(long codigoUsuario, int something)throws RemoteException;
	public boolean hasPerfil(String userId,String perfilId) throws RemoteException;
	public void sendPasswordByEmail (String codUsuario,String pass, String userName, String name) throws RemoteException;
}
