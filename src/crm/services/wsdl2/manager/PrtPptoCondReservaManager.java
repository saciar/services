package crm.services.wsdl2.manager;

import java.rmi.RemoteException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import crm.libraries.abm.entities.PrtPptoCondReserva;
import crm.services.util.HibernateUtil;
import crm.services.wsdl2.sei.PrtPptoCondReservaManagerSEI;

public class PrtPptoCondReservaManager implements PrtPptoCondReservaManagerSEI,WSDL2Service {

	public PrtPptoCondReserva getById(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(PrtPptoCondReserva.class);
		c.add(Expression.eq("codigo", codigo));
		c.add(Expression.eq("activo","S"));
		PrtPptoCondReserva result = (PrtPptoCondReserva) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return result;
	}
	

	public PrtPptoCondReserva[] getAll() throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(PrtPptoCondReserva.class);
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);
		return (PrtPptoCondReserva[])list.toArray(new PrtPptoCondReserva[0]);
	}

	public PrtPptoCondReserva[] findByField(String field,String value){		
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(PrtPptoCondReserva.class);
		c.add(Expression.like(field,"%" + value + "%"));
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);
		return (PrtPptoCondReserva[])list.toArray(new PrtPptoCondReserva[0]);
	}

	public void remove(String codigo) throws RemoteException {
		PrtPptoCondReserva entity = null;
		Session session = null;
		Transaction tx = null;
		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();
			entity = (PrtPptoCondReserva) session.get(PrtPptoCondReserva.class, codigo);						
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

	public void update(PrtPptoCondReserva ppi) throws RemoteException {
		PrtPptoCondReserva model = null;
		Session session = null;
		Transaction tx = null;
		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			if (StringUtils.isBlank(ppi.getCodigo())) {
				model = new PrtPptoCondReserva();
				HibernateUtil.assignID(session,model);
			} else {
				model = (PrtPptoCondReserva) session.get(PrtPptoCondReserva.class, ppi.getCodigo());
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
