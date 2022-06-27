package crm.services.transaction;

import java.rmi.RemoteException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import crm.libraries.abm.entities.ABMEntity;
import crm.libraries.abm.entities.EventoUniforme;
import crm.services.sei.EventoUniformeManagerSEI;
import crm.services.util.HibernateUtil;

public class EventoUniformeManager implements EventoUniformeManagerSEI,ManagerService {

	public EventoUniforme getEventoUniformeById(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		// Servicio s = (Servicio)session.get(Servicio.class,codigo);
		Criteria c = session.createCriteria(EventoUniforme.class);
		c.add(Expression.eq("codigo", codigo));
		c.add(Expression.eq("activo","S"));
		EventoUniforme a = (EventoUniforme) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return a;
	}
	
	public EventoUniforme getEventoUniformeByDescripcion(String descripcion) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(EventoUniforme.class);
		c.add(Expression.eq("descripcion", descripcion));
		EventoUniforme a = (EventoUniforme) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return a;
	}

	public EventoUniforme[] getAllEventoUniformes() throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(EventoUniforme.class);
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (EventoUniforme[])list.toArray(new EventoUniforme[0]);
	}

	public EventoUniforme[] findByField(String field,String value){		
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(EventoUniforme.class);
		c.add(Expression.like(field,"%" + value + "%"));
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (EventoUniforme[])list.toArray(new EventoUniforme[0]);
	}
	
	
	public EventoUniforme[] getAllEventoUniformesTranslated(String lang) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public void remove(String codigo) throws RemoteException {
		EventoUniforme entity = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			
			entity = (EventoUniforme) session.get(EventoUniforme.class, codigo);						
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

	public void update(EventoUniforme eventoUniforme) throws RemoteException {
		EventoUniforme t = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			if (StringUtils.isBlank(eventoUniforme.getCodigo())) {
				t = new EventoUniforme();
				// TODO: asignar ID
				assignID(session,t);
				
				//a.setCodigo(null);
			} else {
				t = (EventoUniforme) session.get(EventoUniforme.class, eventoUniforme.getCodigo());
			}

			t.setDescripcion(eventoUniforme.getDescripcion());
			t.setActivo(eventoUniforme.getActivo());
		    

			session.saveOrUpdate(t);

			tx.commit();
			session.flush();
		} catch (HibernateException he) {
			if (tx != null && tx.isActive())
				tx.rollback();

			he.printStackTrace(System.err);
			// throw new SystemException(he);
		} finally {
			HibernateUtil.cerrarSession(session);
		}
	}

	
	
	private void assignID(Session session,ABMEntity e) {

		String className = e.getClass().toString();
		String entityType = className.substring(className.lastIndexOf(".") + 1,
				className.length());
		String entityAlias = entityType.toLowerCase();

		// ID = MAX_ID_UNTIL_NOW + 1
		String query = "select max(" + entityAlias + ".codigo) + 1 from " 
		+ entityType + " " + entityAlias;
		
		Object result = session.createQuery(query)		
						.uniqueResult();
		
		String codigo = result != null ? result.toString():"1";

		e.setCodigo(codigo);

	}
}
