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
import crm.libraries.abm.entities.Idioma;
import crm.services.sei.IdiomaManagerSEI;
import crm.services.util.HibernateUtil;

public class IdiomaManager implements IdiomaManagerSEI,ManagerService {

	public Idioma getIdiomaById(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		// Servicio s = (Servicio)session.get(Servicio.class,codigo);
		Criteria c = session.createCriteria(Idioma.class);
		c.add(Expression.eq("codigo", codigo));
		c.add(Expression.eq("activo","S"));
		Idioma a = (Idioma) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return a;
	}

	public Idioma getIdiomaByDescripcion(String descripcion) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(Idioma.class);
		c.add(Expression.eq("descripcion", descripcion));
		Idioma a = (Idioma) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return a;
	}
	
	public Idioma[] getAllIdiomas() throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(Idioma.class);
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (Idioma[])list.toArray(new Idioma[0]);
	}

	public Idioma[] findByField(String field,String value){
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(Idioma.class);
		c.add(Expression.like(field,"%" + value + "%"));
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (Idioma[])list.toArray(new Idioma[0]);
	}
	
	public Idioma[] getAllIdiomasTranslated(String lang) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void remove(String codigo) throws RemoteException {
		Idioma entity = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			
			entity = (Idioma) session.get(Idioma.class, codigo);						
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

	public void update(Idioma idioma) throws RemoteException {
		Idioma a = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			if (StringUtils.isBlank(idioma.getCodigo())) {
				a = new Idioma();
				// TODO: asignar ID
				assignID(session,a);
				
				//a.setCodigo(null);
			} else {
				a = (Idioma) session.get(Idioma.class, idioma.getCodigo());
			}

			a.setDescripcion(idioma.getDescripcion());
			a.setActivo(idioma.getActivo());
		    
		    
			// if (groupId == 0){
			// session.save(group);
			// }
			session.saveOrUpdate(a);

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
