package crm.services.transaction;

import java.rmi.RemoteException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import crm.libraries.abm.entities.EstadoEvento;
import crm.services.report.manager.OrdenFacturacionReport;
import crm.services.sei.EstadoEventoManagerSEI;
import crm.services.util.HibernateUtil;

public class EstadoEventoManager implements EstadoEventoManagerSEI,ManagerService {

	public EstadoEvento getEstadoEventoById(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		// Servicio s = (Servicio)session.get(Servicio.class,codigo);
		Criteria c = session.createCriteria(EstadoEvento.class);
		c.add(Expression.eq("codigo", codigo));
		c.add(Expression.eq("activo","S"));
		EstadoEvento a = (EstadoEvento) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return a;
	}

	public EstadoEvento getEstadoEventoByDescripcion(String descripcion) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(EstadoEvento.class);
		c.add(Expression.eq("descripcion", descripcion));
		EstadoEvento a = (EstadoEvento) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return a;
	}
	
	public EstadoEvento[] getAllEstadoEventos() throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(EstadoEvento.class);
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (EstadoEvento[])list.toArray(new EstadoEvento[0]);
	}

	public EstadoEvento[] findByField(String field,String value){
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(EstadoEvento.class);
		c.add(Expression.like(field,"%" + value + "%"));
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (EstadoEvento[])list.toArray(new EstadoEvento[0]);
	}
	
	public EstadoEvento[] getAllEstadoEventosTranslated(String lang) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void remove(String codigo) throws RemoteException {
		EstadoEvento entity = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			
			entity = (EstadoEvento) session.get(EstadoEvento.class, codigo);						
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

	public void update(EstadoEvento estadoEvento) throws RemoteException {
		EstadoEvento entity = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			if (StringUtils.isBlank(estadoEvento.getCodigo())) {
				entity = new EstadoEvento();
				HibernateUtil.assignID(session,entity);
			
			} else {
				entity = (EstadoEvento) session.get(EstadoEvento.class, estadoEvento.getCodigo());
			}

			entity.setDescripcion(estadoEvento.getDescripcion());
			entity.setActivo(estadoEvento.getActivo());
		    
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
	
private static EstadoEventoManager instance;
	
	public static synchronized EstadoEventoManager instance() {

			if (instance == null) 
				instance = new EstadoEventoManager();

		return instance;
	}
}
