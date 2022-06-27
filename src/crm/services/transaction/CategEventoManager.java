package crm.services.transaction;

import java.rmi.RemoteException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import crm.libraries.abm.entities.CategEvento;
import crm.services.sei.CategEventoManagerSEI;
import crm.services.util.CollectionUtil;
import crm.services.util.HibernateUtil;

public class CategEventoManager implements CategEventoManagerSEI,ManagerService {

	public Object[] getCategEventosReport() throws RemoteException {

		Session session = HibernateUtil.abrirSession();
		// Criteria c = session.createCriteria(Cliente.class);
		// List list = c.list();

		List list = session.createQuery("select codigo,descripcion from CategEvento where activo = 'S' order by descripcion").list();

		HibernateUtil.cerrarSession(session);

		return CollectionUtil.listToObjectArray(list);
	}
	
	
	

	public CategEvento getCategEventoById(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		// Servicio s = (Servicio)session.get(Servicio.class,codigo);
		Criteria c = session.createCriteria(CategEvento.class);
		c.add(Expression.eq("codigo", codigo));
		c.add(Expression.eq("activo","S"));
		CategEvento a = (CategEvento) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return a;
	}

	public CategEvento getCategEventoByDescripcion(String descripcion) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(CategEvento.class);
		c.add(Expression.eq("descripcion", descripcion));
		CategEvento a = (CategEvento) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return a;
	}
	
	public CategEvento[] getAllCategEventos() throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(CategEvento.class);
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (CategEvento[])list.toArray(new CategEvento[0]);
	}

	public CategEvento[] findByField(String field,String value){
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(CategEvento.class);
		c.add(Expression.like(field,"%" + value + "%"));
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (CategEvento[])list.toArray(new CategEvento[0]);
	}
	
	public CategEvento[] getAllCategEventosTranslated(String lang) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void remove(String codigo) throws RemoteException {
		CategEvento entity = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			
			entity = (CategEvento) session.get(CategEvento.class, codigo);						
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

	public void update(CategEvento categEvento) throws RemoteException {
		CategEvento entity = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			if (StringUtils.isBlank(categEvento.getCodigo())) {
				entity = new CategEvento();
				HibernateUtil.assignID(session,entity);
			
			} else {
				entity = (CategEvento) session.get(CategEvento.class, categEvento.getCodigo());
			}

			entity.setDescripcion(categEvento.getDescripcion());
			entity.setActivo(categEvento.getActivo());
		    
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
