/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package crm.services.wsdl2.sei;

import crm.libraries.abm.entities.Equipos;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author saciar
 */
public interface EquiposManagerSEI extends Remote{
    public Equipos getById(String codigo) throws RemoteException;
	public Equipos[] getAll() throws RemoteException;
	public Equipos[] findByField(String field,String value) throws RemoteException;
	public void remove(String codigo) throws RemoteException;
	public Equipos update(Equipos model) throws RemoteException;
	public Equipos[] findByFields(Object[] field,Object[] value) throws RemoteException;
	public Equipos[] findByFieldExactly(String field,String value) throws RemoteException;
}
