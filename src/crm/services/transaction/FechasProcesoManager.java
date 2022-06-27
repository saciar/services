package crm.services.transaction;

import java.rmi.RemoteException;
import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import crm.libraries.abm.entities.FechasProceso;
import crm.services.sei.FechasProcesoManagerSEI;
import crm.services.util.DateConverter;
import crm.services.util.HibernateUtil;

public class FechasProcesoManager implements FechasProcesoManagerSEI,ManagerService {
	
	public void update(FechasProceso fecha) throws RemoteException {
		FechasProceso entity = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			if (StringUtils.isBlank(fecha.getCodigo())) {
				entity = new FechasProceso();
				HibernateUtil.assignID(session,entity);
			
			} else {
				entity = (FechasProceso) session.get(FechasProceso.class, fecha.getCodigo());
			}

			entity.setFechaProceso(fecha.getFechaProceso());
		    
			session.saveOrUpdate(entity);

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
	
	public String getFechaProcesoById(String cod) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		
		Integer i = Integer.valueOf(cod);
		
		Object o = session.createQuery
		("select fechaProceso from FechasProceso where codigo = "+i)
		.uniqueResult();
		
		HibernateUtil.cerrarSession(session);
		
		return o.toString();

	}
	
	public String getMaxCodigo() throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		
		String query = "select max(codigo) from FechasProceso";
		
		Object result = session.createQuery(query)
						.uniqueResult();
		
		HibernateUtil.cerrarSession(session);
		
		return String.valueOf(result);
	}
}
