package crm.services.wsdl2.manager;

import java.rmi.RemoteException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import crm.libraries.abm.entities.PrtPptoSeguridad;
import crm.services.util.HibernateUtil;
import crm.services.wsdl2.sei.PrtPptoSeguridadManagerSEI;

public class PrtPptoSeguridadManager implements PrtPptoSeguridadManagerSEI,WSDL2Service  {
	public PrtPptoSeguridad getById(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(PrtPptoSeguridad.class);
		c.add(Expression.eq("codigo", codigo));
		c.add(Expression.eq("activo","S"));
		PrtPptoSeguridad result = (PrtPptoSeguridad) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return result;
	}
	

	public PrtPptoSeguridad[] getAll() throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(PrtPptoSeguridad.class);
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);
		return (PrtPptoSeguridad[])list.toArray(new PrtPptoSeguridad[0]);
	}

	public PrtPptoSeguridad[] findByField(String field,String value) throws RemoteException{		
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(PrtPptoSeguridad.class);
		c.add(Expression.like(field,"%" + value + "%"));
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);
		return (PrtPptoSeguridad[])list.toArray(new PrtPptoSeguridad[0]);
	}

	public void remove(String codigo) throws RemoteException {
		PrtPptoSeguridad entity = null;
		Session session = null;
		Transaction tx = null;
		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();
			entity = (PrtPptoSeguridad) session.get(PrtPptoSeguridad.class, codigo);						
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

	public void update(PrtPptoSeguridad ppi) throws RemoteException {
		PrtPptoSeguridad model = null;
		Session session = null;
		Transaction tx = null;
		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			if (StringUtils.isBlank(ppi.getCodigo())) {
				model = new PrtPptoSeguridad();
				HibernateUtil.assignID(session,model);
			} else {
				model = (PrtPptoSeguridad) session.get(PrtPptoSeguridad.class, ppi.getCodigo());
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
