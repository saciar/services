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
import crm.libraries.abm.entities.Seguimiento;
import crm.services.sei.SeguimientoManagerSEI;
import crm.services.util.CollectionUtil;
import crm.services.util.HibernateUtil;

public class SeguimientoManager implements SeguimientoManagerSEI,ManagerService {

	public Seguimiento getSeguimientoById(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();

		Criteria c = session.createCriteria(Seguimiento.class);
		c.add(Expression.eq("codigo", codigo));
		c.add(Expression.eq("activo","S"));
		Seguimiento a = (Seguimiento) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return a;
	}
	
	public Seguimiento getSeguimientoByDescripcion(String descripcion) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(Seguimiento.class);
		c.add(Expression.eq("descripcion", descripcion));
		Seguimiento a = (Seguimiento) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return a;
	}

	public Seguimiento[] getAllSeguimientos() throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(Seguimiento.class);
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (Seguimiento[])list.toArray(new Seguimiento[0]);
	}

	public Seguimiento[] getAllSeguimientosTranslated(String lang) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Seguimiento[] findByField(String field,String value){
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(Seguimiento.class);
		c.add(Expression.like(field,"%" + value + "%"));
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (Seguimiento[])list.toArray(new Seguimiento[0]);
	}

	public void remove(String codigo) throws RemoteException {
		Seguimiento entity = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			
			entity = (Seguimiento) session.get(Seguimiento.class, codigo);						
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

	public void update(Seguimiento seguimiento) throws RemoteException {
		Seguimiento p = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			if (StringUtils.isBlank(seguimiento.getCodigo())) {
				p = new Seguimiento();
				assignID(session,p);
			} else {
				p = (Seguimiento) session.get(Seguimiento.class, seguimiento.getCodigo());
			}

			p.setDescripcion(seguimiento.getDescripcion());
			p.setActivo(seguimiento.getActivo());
			    
			session.saveOrUpdate(p);

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
	
	public Object[] getAccionesReport() throws RemoteException {

		Session session = HibernateUtil.abrirSession();

		List list = session.createQuery(
				"select codigo,descripcion " +
				"from Seguimiento " +
				"where activo = 'S' " +
				"order by descripcion"
				)
				.list();

		HibernateUtil.cerrarSession(session);

		return CollectionUtil.listToObjectArray(list);
	}
}
