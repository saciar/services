package crm.services.wsdl2.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.Equipamientos;;

public interface EquipamientosManagerSEI extends Remote{
    public Equipamientos getById(String codigo) throws RemoteException;
	public Equipamientos[] getAll() throws RemoteException;
	public Equipamientos[] findByField(String field,String value) throws RemoteException;
	public void remove(String codigo) throws RemoteException;
	public Equipamientos update(Equipamientos model) throws RemoteException;
	public Equipamientos[] findByFields(Object[] field,Object[] value) throws RemoteException;
	public Equipamientos[] findByFieldExactly(String field,String value) throws RemoteException;
	public Object[] buscarEquipamientoxCodigoBarras(int codigo,String valor) throws RemoteException;

}
