package crm.services.wsdl2.manager;

import java.rmi.RemoteException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import crm.libraries.abm.entities.PrtPptoPeriodo;
import crm.services.util.HibernateUtil;
import crm.services.wsdl2.sei.PrtPptoPeriodoManagerSEI;

public class PrtPptoPeriodoManager implements PrtPptoPeriodoManagerSEI,WSDL2Service {

	public PrtPptoPeriodo getById(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(PrtPptoPeriodo.class);
		c.add(Expression.eq("codigo", codigo));
		c.add(Expression.eq("activo","S"));
		PrtPptoPeriodo result = (PrtPptoPeriodo) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return result;
	}
	

	public PrtPptoPeriodo[] getAll() throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(PrtPptoPeriodo.class);
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);
		return (PrtPptoPeriodo[])list.toArray(new PrtPptoPeriodo[0]);
	}

	public PrtPptoPeriodo[] findByField(String field,String value){		
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(PrtPptoPeriodo.class);
		c.add(Expression.like(field,"%" + value + "%"));
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);
		return (PrtPptoPeriodo[])list.toArray(new PrtPptoPeriodo[0]);
	}

	public void remove(String codigo) throws RemoteException {
		PrtPptoPeriodo entity = null;
		Session session = null;
		Transaction tx = null;
		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();
			entity = (PrtPptoPeriodo) session.get(PrtPptoPeriodo.class, codigo);						
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

	public void update(PrtPptoPeriodo pps) throws RemoteException {
		PrtPptoPeriodo model = null;
		Session session = null;
		Transaction tx = null;
		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			if (StringUtils.isBlank(pps.getCodigo())) {
				model = new PrtPptoPeriodo();
				HibernateUtil.assignID(session,model);
			} else {
				model = (PrtPptoPeriodo) session.get(PrtPptoPeriodo.class, pps.getCodigo());
			}
			model.setTitulo(pps.getTitulo());
			model.setDescripcion(pps.getDescripcion());
			model.setActivo(pps.getActivo());
		    
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
