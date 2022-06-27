package crm.services.wsdl2.manager;

import java.rmi.RemoteException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import crm.libraries.abm.entities.PrtPptoPersonal;
import crm.services.util.HibernateUtil;
import crm.services.wsdl2.sei.PrtPptoPersonalManagerSEI;

public class PrtPptoPersonalManager implements PrtPptoPersonalManagerSEI,WSDL2Service {

	public PrtPptoPersonal getById(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(PrtPptoPersonal.class);
		c.add(Expression.eq("codigo", codigo));
		c.add(Expression.eq("activo","S"));
		PrtPptoPersonal result = (PrtPptoPersonal) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return result;
	}
	

	public PrtPptoPersonal[] getAll() throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(PrtPptoPersonal.class);
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);
		return (PrtPptoPersonal[])list.toArray(new PrtPptoPersonal[0]);
	}

	public PrtPptoPersonal[] findByField(String field,String value) throws RemoteException{	
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(PrtPptoPersonal.class);
		c.add(Expression.like(field,"%" + value + "%"));
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);
		return (PrtPptoPersonal[])list.toArray(new PrtPptoPersonal[0]);
	}

	public void remove(String codigo) throws RemoteException {
		PrtPptoPersonal entity = null;
		Session session = null;
		Transaction tx = null;
		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();
			entity = (PrtPptoPersonal) session.get(PrtPptoPersonal.class, codigo);						
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

	public void update(PrtPptoPersonal ppi) throws RemoteException {
		PrtPptoPersonal model = null;
		Session session = null;
		Transaction tx = null;
		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			if (StringUtils.isBlank(ppi.getCodigo())) {
				model = new PrtPptoPersonal();
				HibernateUtil.assignID(session,model);
			} else {
				model = (PrtPptoPersonal) session.get(PrtPptoPersonal.class, ppi.getCodigo());
			}
			model.setTitulo(ppi.getTitulo());
			model.setDescripcion(ppi.getDescripcion());
			model.setActivo(ppi.getActivo());
		    
			session.saveOrUpdate(model);

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
