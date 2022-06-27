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
import crm.libraries.abm.entities.TipoRecibo;
import crm.services.util.CollectionUtil;
import crm.services.util.HibernateUtil;
import crm.services.wsdl2.sei.TipoReciboManagerSEI;

public class TipoReciboManager implements TipoReciboManagerSEI,WSDL2Service {

	public TipoRecibo getTipoReciboById(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();

		Criteria c = session.createCriteria(TipoRecibo.class);
		c.add(Expression.eq("codigo", codigo));
		c.add(Expression.eq("activo","S"));
		TipoRecibo a = (TipoRecibo) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return a;
	}
	
	public TipoRecibo getTipoReciboByDescripcion(String descripcion) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(TipoRecibo.class);
		c.add(Expression.eq("descripcion", descripcion));
		TipoRecibo a = (TipoRecibo) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return a;
	}

	public TipoRecibo[] getAllTipoRecibos() throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(TipoRecibo.class);
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (TipoRecibo[])list.toArray(new TipoRecibo[0]);
	}

	public TipoRecibo[] getAllTipoEventosTranslated(String lang) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public TipoRecibo[] findByField(String field,String value){
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(TipoRecibo.class);
		c.add(Expression.like(field,"%" + value + "%"));
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (TipoRecibo[])list.toArray(new TipoRecibo[0]);
	}
	
	public void remove(String codigo) throws RemoteException {
		TipoRecibo entity = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			
			entity = (TipoRecibo) session.get(TipoRecibo.class, codigo);						
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

	public void update(TipoRecibo cobrador) throws RemoteException {
		TipoRecibo te = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			if (StringUtils.isBlank(cobrador.getCodigo())) {
				te = new TipoRecibo();
				assignID(session,te);
			} else {
				te = (TipoRecibo) session.get(TipoRecibo.class, cobrador.getCodigo());
			}

			te.setDescripcion(cobrador.getDescripcion());
			te.setActivo(cobrador.getActivo());
		    
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

	public Object[] getTipoReciboReport() throws RemoteException {

		Session session = HibernateUtil.abrirSession();

		List list = session.createQuery("select codigo,descripcion from TipoRecibo order by descripcion").list();

		HibernateUtil.cerrarSession(session);

		return CollectionUtil.listToObjectArray(list);
	}

}
