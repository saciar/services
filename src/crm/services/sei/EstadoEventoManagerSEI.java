package crm.services.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.EstadoEvento;

public interface EstadoEventoManagerSEI extends Remote {
	public static final String CODIGO_ESTADO_NUEVO = "1";
	public static final String CODIGO_ESTADO_ACTUALIZADO = "2";
	public static final String CODIGO_ESTADO_CONFIRMADO = "3";
	public static final String CODIGO_ESTADO_CANCELADO = "4";
	public static final String CODIGO_ESTADO_RECHAZADO = "5";
	public static final String CODIGO_ESTADO_ORDEN_SERVICIO = "6";
	public static final String CODIGO_ESTADO_ORDEN_FACTURACION = "7";
	public static final String CODIGO_ESTADO_ORDEN_COMPRA = "8";
	public static final String CODIGO_ESTADO_FACTURADO = "9";
	public static final String CODIGO_ESTADO_COBRADO = "10";
	public static final String CODIGO_ESTADO_ADELANTO = "11";
	public static final String CODIGO_ESTADO_ADELANTADO = "12";
	public static final String CODIGO_ESTADO_A_COBRAR = "13";
	public static final String CODIGO_ESTADO_ADELANTO_A_COBRAR = "14";
	public static final String CODIGO_ESTADO_ADELANTO_COBRADO = "15";
	public static final String CODIGO_ESTADO_ADICIONALES = "16";
	public static final String CODIGO_ESTADO_ADICIONALES_FACTURADOS = "17";
	public static final String CODIGO_ESTADO_DESCONFIRMADO= "18";
	
	public EstadoEvento getEstadoEventoById(String codigo) throws RemoteException;
	public EstadoEvento getEstadoEventoByDescripcion(String descripcion) throws RemoteException;
	public EstadoEvento[] getAllEstadoEventos() throws RemoteException;
	public EstadoEvento[] findByField(String field,String value) throws RemoteException;
	//public EstadoEvento[] getAllEstadoEventosTranslated(String lang) throws RemoteException;
	public void remove(String codigo) throws RemoteException;
	public void update(EstadoEvento estadoEvento) throws RemoteException;
}
