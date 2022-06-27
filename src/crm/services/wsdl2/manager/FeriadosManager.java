package crm.services.wsdl2.manager;

import java.rmi.RemoteException;

import org.hibernate.Query;
import org.hibernate.Session;

import crm.services.util.HibernateUtil;
import crm.services.wsdl2.sei.FeriadosManagerSEI;

public class FeriadosManager implements WSDL2Service,FeriadosManagerSEI{

	public String getIdPorFecha(String f) throws RemoteException {
		Session session = HibernateUtil.abrirSession();

		Query query = 
			session.createQuery("select codigo from Feriados where fecha = :fec");
		
		query.setString("fec", f);
		
		String desc = (String)query.uniqueResult();
		
		HibernateUtil.cerrarSession(session);
		
    	return desc;
    }  
	
}
