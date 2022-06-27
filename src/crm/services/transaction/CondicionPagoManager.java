package crm.services.transaction;

import java.rmi.RemoteException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import crm.libraries.abm.entities.CondicionPago;
import crm.services.sei.CondicionPagoManagerSEI;
import crm.services.util.CollectionUtil;
import crm.services.util.HibernateUtil;

public class CondicionPagoManager implements CondicionPagoManagerSEI,ManagerService {

	public Object[] getCondicionPagosReport() throws RemoteException {

		Session session = HibernateUtil.abrirSession();
		// Criteria c = session.createCriteria(Cliente.class);
		// List list = c.list();

		List list = session.createQuery("select codigo,descripcion from CondicionPago order by descripcion").list();

		HibernateUtil.cerrarSession(session);

		return CollectionUtil.listToObjectArray(list);
	}
	
	
	

	public CondicionPago getCondicionPagoById(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		// Servicio s = (Servicio)session.get(Servicio.class,codigo);
		Criteria c = session.createCriteria(CondicionPago.class);
		c.add(Expression.eq("codigo", codigo));
		c.add(Expression.eq("activo","S"));
		CondicionPago a = (CondicionPago) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return a;
	}

	public CondicionPago getCondicionPagoByDescripcion(String descripcion) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(CondicionPago.class);
		c.add(Expression.eq("descripcion", descripcion));
		CondicionPago a = (CondicionPago) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return a;
	}
	
	public CondicionPago[] getAllCondicionPagos() throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(CondicionPago.class);
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (CondicionPago[])list.toArray(new CondicionPago[0]);
	}

	public CondicionPago[] findByField(String field,String value){
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(CondicionPago.class);
		c.add(Expression.like(field,"%" + value + "%"));
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (CondicionPago[])list.toArray(new CondicionPago[0]);
	}
	
	public CondicionPago[] getAllCondicionPagosTranslated(String lang) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void remove(String codigo) throws RemoteException {
		CondicionPago entity = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			
			entity = (CondicionPago) session.get(CondicionPago.class, codigo);						
			entity.setActivo("N");
		    
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

	public void update(CondicionPago condicionPago) throws RemoteException {
		CondicionPago entity = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			if (StringUtils.isBlank(condicionPago.getCodigo())) {
				entity = new CondicionPago();
				HibernateUtil.assignID(session,entity);
			
			} else {
				entity = (CondicionPago) session.get(CondicionPago.class, condicionPago.getCodigo());
			}

			entity.setDescripcion(condicionPago.getDescripcion());
			entity.setDescripcionDetallada(condicionPago.getDescripcionDetallada());
			entity.setActivo(condicionPago.getActivo());
		    
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
	
}
