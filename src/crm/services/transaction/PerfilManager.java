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
import crm.libraries.abm.entities.Perfil;
import crm.services.sei.PerfilManagerSEI;
import crm.services.util.HibernateUtil;

public class PerfilManager implements PerfilManagerSEI,ManagerService {

	public Perfil getPerfilById(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();

		Criteria c = session.createCriteria(Perfil.class);
		c.add(Expression.eq("codigo", codigo));
		c.add(Expression.eq("activo","S"));
		Perfil a = (Perfil) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return a;
	}

	public Perfil getPerfilByDescripcion(String descripcion) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(Perfil.class);
		c.add(Expression.eq("descripcion", descripcion));
		Perfil a = (Perfil) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return a;
	}
	
	public Perfil[] getAllPerfiles() throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(Perfil.class);
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (Perfil[])list.toArray(new Perfil[0]);
	}

	public Perfil[] findByField(String field,String value){
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(Perfil.class);
		c.add(Expression.like(field,"%" + value + "%"));
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (Perfil[])list.toArray(new Perfil[0]);
	}
	
	public Perfil[] getAllPerfilesTranslated(String lang) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public void remove(String codigo) throws RemoteException {
		Perfil entity = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			
			entity = (Perfil) session.get(Perfil.class, codigo);						
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

	public void update(Perfil perfil) throws RemoteException {
		Perfil p = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			if (StringUtils.isBlank(perfil.getCodigo())) {
				p = new Perfil();
				// TODO: asignar ID
				assignID(session,p);
				
				//a.setCodigo(null);
			} else {
				p = (Perfil) session.get(Perfil.class, perfil.getCodigo());
			}

			p.setDescripcion(perfil.getDescripcion());
			p.setActivo(perfil.getActivo());
		    
		    
			// if (groupId == 0){
			// session.save(group);
			// }
			session.saveOrUpdate(p);

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
