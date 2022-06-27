package crm.services.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.LiquidacionComisiones;

public interface LiquidacionComisionesManagerSEI extends Remote{
	
	public static final String VENDEDOR = "1";
	
	public static final String SUPERVISOR = "2";
	
	public static final String LUGAR_EVENTO = "3";
	
	public static final String CONTACTO_COMISIONABLE = "4";
	
	public void update(LiquidacionComisiones l) throws RemoteException;

	public boolean isPptoLiquidado(String nroPpto) throws RemoteException;
	
	public String getTipoComisionByNroPpto(String nroPpto) throws RemoteException;

}