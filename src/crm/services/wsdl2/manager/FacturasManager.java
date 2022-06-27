package crm.services.wsdl2.manager;

import java.rmi.RemoteException;
import java.util.Date;

import org.hibernate.Session;

import crm.services.util.DateConverter;
import crm.services.util.HibernateUtil;
import crm.services.wsdl2.sei.FacturasManagerSEI;

public class FacturasManager implements FacturasManagerSEI,WSDL2Service{

	public void setFactura(long id, String nroFactura) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		
		 session.createQuery(
				"update Ppto_Facturas set factura = :nrofact, fechaAppend = :fecha where id = :nro ")
				.setString("nrofact",nroFactura)
				.setString("fecha",DateConverter.convertDateToString(new Date(),
				"yyyy-MM-dd H:mm:ss"))				
				.setLong("nro", id)
				.executeUpdate();
		 
		 HibernateUtil.cerrarSession(session);
	}

	
	
}
