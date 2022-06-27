package crm.services.transaction;

import java.rmi.RemoteException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import crm.libraries.abm.entities.CategReferencia;
import crm.services.sei.CategReferenciaManagerSEI;
import crm.services.util.HibernateUtil;

public class CategReferenciaManager implements CategReferenciaManagerSEI,ManagerService {

	public CategReferencia getCategReferenciaById(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		// Servicio s = (Servicio)session.get(Servicio.class,codigo);
		Criteria c = session.createCriteria(CategReferencia.class);
		c.add(Expression.eq("codigo", codigo));
		c.add(Expression.eq("activo","S"));
		CategReferencia a = (CategReferencia) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return a;
	}

	public CategReferencia[] getAllCategReferencias() throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(CategReferencia.class);
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (CategReferencia[])list.toArray(new CategReferencia[0]);
	}

	public CategReferencia[] findByField(String field,String value){
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(CategReferencia.class);
		c.add(Expression.like(field,"%" + value + "%"));
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (CategReferencia[])list.toArray(new CategReferencia[0]);
	}
	
	public CategReferencia[] getAllCategReferenciasTranslated(String lang) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public void remove(String codigo) throws RemoteException {
		CategReferencia entity = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			
			entity = (CategReferencia) session.get(CategReferencia.class, codigo);						
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

	public void update(CategReferencia categReferencia) throws RemoteException {
		CategReferencia cr = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			if (StringUtils.isBlank(categReferencia.getCodigo())) {
				cr = new CategReferencia();
				HibernateUtil.assignID(session,cr);
			} else {
				cr = (CategReferencia) session.get(CategReferencia.class, categReferencia.getCodigo());
			}

			cr.setDescripcion(categReferencia.getDescripcion());
			cr.setActivo(categReferencia.getActivo());
		    

			session.saveOrUpdate(cr);

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

	

}
