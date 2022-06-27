package crm.services.transaction;

import java.rmi.RemoteException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import crm.libraries.abm.entities.ServicioDescripcion;
import crm.services.sei.ServicioDescripcionManagerSEI;
import crm.services.util.HibernateUtil;

public class ServicioDescripcionManager implements ServicioDescripcionManagerSEI,ManagerService {
	private static final Log log = LogFactory.getLog(ServicioDescripcionManager.class);
	
	public ServicioDescripcion[] findByServicio(String codServicio,String codIdioma){
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(ServicioDescripcion.class);
		c.add(Expression.eq("codServicio",codServicio));
		c.add(Expression.eq("codIdioma",codIdioma));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (ServicioDescripcion[])list.toArray(new ServicioDescripcion[0]);
	}
	
	public void removeByServicio(String codServicio,String codIdioma) throws RemoteException {				
		Session session = null;
		try {
			session = HibernateUtil.abrirSession();

			ServicioDescripcion[] descripciones = findByServicio(codServicio,codIdioma);
			for(int i = 0;i < descripciones.length;i ++){			
				session.delete(descripciones[i]);
			}
			session.flush();
		} catch (HibernateException he) {
			he.printStackTrace(System.err);
		} finally {
			HibernateUtil.cerrarSession(session);
		}
	}	
	
	public void saveDescripcion(String codServicio,String codIdioma,String descripcion) throws RemoteException {				
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();
			
			ServicioDescripcion sd = new ServicioDescripcion();
			//HibernateUtil.assignID(session,sd);
			
			sd.setCodServicio(codServicio);
			sd.setCodIdioma(codIdioma);
			sd.setDescripcion(descripcion);
			
			session.save(sd);
	
			tx.commit();
			session.flush();									
		} catch (HibernateException he) {
			if (tx != null && tx.isActive())
				tx.rollback();
	
			he.printStackTrace(System.err);			
		} finally {
			HibernateUtil.cerrarSession(session);
			
		}
	}
	
	

}
