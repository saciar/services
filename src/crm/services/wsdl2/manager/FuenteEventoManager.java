package crm.services.wsdl2.manager;

import java.rmi.RemoteException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import crm.libraries.abm.entities.ABMEntity;
import crm.libraries.abm.entities.FuenteEvento;
import crm.services.util.CollectionUtil;
import crm.services.util.HibernateUtil;
import crm.services.wsdl2.sei.FuenteEventoManagerSEI;

public class FuenteEventoManager implements FuenteEventoManagerSEI,WSDL2Service {

	public FuenteEvento getFuenteEventoById(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();

		Criteria c = session.createCriteria(FuenteEvento.class);
		c.add(Expression.eq("codigo", codigo));
		c.add(Expression.eq("activo","S"));
		FuenteEvento a = (FuenteEvento) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return a;
	}
	
	public FuenteEvento getFuenteEventoByDescripcion(String descripcion) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(FuenteEvento.class);
		c.add(Expression.eq("descripcion", descripcion));
		FuenteEvento a = (FuenteEvento) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return a;
	}

	public FuenteEvento[] getAllFuenteEventos() throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(FuenteEvento.class);
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (FuenteEvento[])list.toArray(new FuenteEvento[0]);
	}

	public FuenteEvento[] getAllFuenteEventosTranslated(String lang) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public FuenteEvento[] findByField(String field,String value){
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(FuenteEvento.class);
		c.add(Expression.like(field,"%" + value + "%"));
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (FuenteEvento[])list.toArray(new FuenteEvento[0]);
	}
	
	public void remove(String codigo) throws RemoteException {
		FuenteEvento entity = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			
			entity = (FuenteEvento) session.get(FuenteEvento.class, codigo);						
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

	public void update(FuenteEvento SubServicio) throws RemoteException {
		FuenteEvento te = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			if (StringUtils.isBlank(SubServicio.getCodigo())) {
				te = new FuenteEvento();
				assignID(session,te);
			} else {
				te = (FuenteEvento) session.get(FuenteEvento.class, SubServicio.getCodigo());
			}

			te.setDescripcion(SubServicio.getDescripcion());
			te.setActivo(SubServicio.getActivo());
		    
			session.saveOrUpdate(te);

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

	public Object[] getFuenteEventosReport() throws RemoteException {

		Session session = HibernateUtil.abrirSession();

		List list = session.createQuery("select codigo,descripcion from FuenteEvento where activo ='S' order by descripcion").list();

		HibernateUtil.cerrarSession(session);

		return CollectionUtil.listToObjectArray(list);
	}

}
