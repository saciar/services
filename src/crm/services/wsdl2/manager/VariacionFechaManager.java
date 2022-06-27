package crm.services.wsdl2.manager;

import java.rmi.RemoteException;
import java.sql.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;

import crm.libraries.abm.entities.VariacionFecha;
import crm.services.util.HibernateUtil;
import crm.services.wsdl2.sei.VariacionFechaManagerSEI;

public class VariacionFechaManager implements WSDL2Service, VariacionFechaManagerSEI{
	public int getVariacionFecha(String fecha) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		int result=0;
		List l  = session.createSQLQuery(
				"select vf_porcentaje as var from mst_variacion_fecha " +
				"where vf_fecha < '"+ fecha+"'")
				.addScalar("var", Hibernate.INTEGER)
				.list();
		
		if ( l != null ) {
			
			Iterator it = l.iterator();
			
			while ( it.hasNext() ) {
				
				int codigoAcceso =(Integer)it.next();
				result += codigoAcceso;
				
			}
			
		}
		
		HibernateUtil.cerrarSession(session);
		
		/*if(st != null){
			result=Integer.getInteger(st);
		}*/
		return result;
	}
	
	public VariacionFecha[] getVariacionesFecha(String fecha) throws RemoteException{

		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(VariacionFecha.class);
		c.add(Expression.lt("fecha", fecha));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (VariacionFecha[])list.toArray(new VariacionFecha[0]);
	}
	
	private static VariacionFechaManager instance; 
	
	public static VariacionFechaManager instance() {
		try {
			if (instance == null) {
				instance = new VariacionFechaManager();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return instance;
	}

}
