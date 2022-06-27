package crm.services.wsdl2.sei;

import java.rmi.RemoteException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import crm.libraries.abm.entities.PrtPptoInstalacion;
import crm.services.util.HibernateUtil;
import crm.services.wsdl2.manager.WSDL2Service;

public class PrtPptoInstalacionManager implements PrtPptoInstalacionManagerSEI,WSDL2Service {

	public PrtPptoInstalacion getById(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(PrtPptoInstalacion.class);
		c.add(Expression.eq("codigo", codigo));
		c.add(Expression.eq("activo","S"));
		PrtPptoInstalacion result = (PrtPptoInstalacion) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return result;
	}
	

	public PrtPptoInstalacion[] getAll() throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(PrtPptoInstalacion.class);
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);
		return (PrtPptoInstalacion[])list.toArray(new PrtPptoInstalacion[0]);
	}

	public PrtPptoInstalacion[] findByField(String field,String value){		
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(PrtPptoInstalacion.class);
		c.add(Expression.like(field,"%" + value + "%"));
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);
		return (PrtPptoInstalacion[])list.toArray(new PrtPptoInstalacion[0]);
	}

	public void remove(String codigo) throws RemoteException {
		PrtPptoInstalacion entity = null;
		Session session = null;
		Transaction tx = null;
		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();
			entity = (PrtPptoInstalacion) session.get(PrtPptoInstalacion.class, codigo);						
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

	public void update(PrtPptoInstalacion ppi) throws RemoteException {
		PrtPptoInstalacion model = null;
		Session session = null;
		Transaction tx = null;
		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			if (StringUtils.isBlank(ppi.getCodigo())) {
				model = new PrtPptoInstalacion();
				HibernateUtil.assignID(session,model);
			} else {
				model = (PrtPptoInstalacion) session.get(PrtPptoInstalacion.class, ppi.getCodigo());
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
