package crm.services.wsdl2.manager;

import java.rmi.RemoteException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import crm.libraries.abm.entities.PrtPptoFPago;
import crm.services.util.HibernateUtil;
import crm.services.wsdl2.sei.PrtPptoFPagoManagerSEI;

public class PrtPptoFPagoManager implements PrtPptoFPagoManagerSEI,WSDL2Service {

	public PrtPptoFPago getById(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(PrtPptoFPago.class);
		c.add(Expression.eq("codigo", codigo));
		c.add(Expression.eq("activo","S"));
		PrtPptoFPago result = (PrtPptoFPago) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return result;
	}
	

	public PrtPptoFPago[] getAll() throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(PrtPptoFPago.class);
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);
		return (PrtPptoFPago[])list.toArray(new PrtPptoFPago[0]);
	}

	public PrtPptoFPago[] findByField(String field,String value){		
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(PrtPptoFPago.class);
		c.add(Expression.like(field,"%" + value + "%"));
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);
		return (PrtPptoFPago[])list.toArray(new PrtPptoFPago[0]);
	}

	public void remove(String codigo) throws RemoteException {
		PrtPptoFPago entity = null;
		Session session = null;
		Transaction tx = null;
		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();
			entity = (PrtPptoFPago) session.get(PrtPptoFPago.class, codigo);						
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

	public void update(PrtPptoFPago ppfp) throws RemoteException {
		PrtPptoFPago model = null;
		Session session = null;
		Transaction tx = null;
		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			if (StringUtils.isBlank(ppfp.getCodigo())) {
				model = new PrtPptoFPago();
				HibernateUtil.assignID(session,model);
			} else {
				model = (PrtPptoFPago) session.get(PrtPptoFPago.class, ppfp.getCodigo());
			}
			model.setTitulo(ppfp.getTitulo());
			model.setDescripcion(ppfp.getDescripcion());
			model.setActivo(ppfp.getActivo());
		    
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
