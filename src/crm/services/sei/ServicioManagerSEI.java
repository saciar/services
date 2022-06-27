package crm.services.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;

import crm.libraries.abm.entities.Servicio;

public interface ServicioManagerSEI extends Remote {
	public static final String SERVICIO_SUBCONTRATADO = "1";
	
	public Servicio getServicioById(String codigo) throws RemoteException;	
	public Servicio[] getAllServicios() throws RemoteException;
	//public Servicio[] getAllServiciosTranslated(String lang) throws RemoteException;
	public Servicio[] findByField(String field,String value) throws RemoteException;
	public Servicio[] findByFieldExactly(String field,String value) throws RemoteException;
	public void remove(String codigo) throws RemoteException;
	public String update(Servicio servicio, String descDetEspaniol, String descDetIngles) throws RemoteException;
	public Object[] getAllServiciosReport() throws RemoteException;
	public Object[] getServiciosByFamiliaAndPlaceReport(String familia) throws RemoteException;
	public double getPrecioVtaById(String cod) throws RemoteException;
	public double getDescuentoByServicioAndTechoDias(String codServ, int cantDias) throws RemoteException;
	public String admiteAccesorioSegunCodServicio(String codServicio)throws RemoteException ;
	public String buscarDescripcionEspaniol(String codigo)throws RemoteException;
	public String buscarDescripcionIngles(String codigo)throws RemoteException;
	public String buscarDescripcion(String codServicio, String idioma)throws RemoteException;
	
	public void setDescripcion(String servicioId,String idiomaId,String descripcion)throws RemoteException;
	public String getDescripcion(String servicioId,String idiomaId)throws RemoteException;
	public int getUnidadDeNegocio(String codigo) throws RemoteException;
	public String admiteDescuentoSegunCodServicio(String codServicio)throws RemoteException;
}
