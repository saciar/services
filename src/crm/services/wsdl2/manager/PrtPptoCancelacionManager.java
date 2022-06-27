package crm.services.wsdl2.manager;

import java.rmi.RemoteException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import crm.libraries.abm.entities.PrtPptoCancelacion;
import crm.services.util.HibernateUtil;
import crm.services.wsdl2.sei.PrtPptoCancelacionManagerSEI;

public class PrtPptoCancelacionManager implements PrtPptoCancelacionManagerSEI,WSDL2Service {

	public PrtPptoCancelacion getById(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(PrtPptoCancelacion.class);
		c.add(Expression.eq("codigo", codigo));
		c.add(Expression.eq("activo","S"));
		PrtPptoCancelacion result = (PrtPptoCancelacion) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return result;
	}
	

	public PrtPptoCancelacion[] getAll() throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(PrtPptoCancelacion.class);
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);
		return (PrtPptoCancelacion[])list.toArray(new PrtPptoCancelacion[0]);
	}

	public PrtPptoCancelacion[] findByField(String field,String value){		
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(PrtPptoCancelacion.class);
		c.add(Expression.like(field,"%" + value + "%"));
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);
		return (PrtPptoCancelacion[])list.toArray(new PrtPptoCancelacion[0]);
	}

	public void remove(String codigo) throws RemoteException {
		PrtPptoCancelacion entity = null;
		Session session = null;
		Transaction tx = null;
		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();
			entity = (PrtPptoCancelacion) session.get(PrtPptoCancelacion.class, codigo);						
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

	public void update(PrtPptoCancelacion ppc) throws RemoteException {
		PrtPptoCancelacion model = null;
		Session session = null;
		Transaction tx = null;
		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			if (StringUtils.isBlank(ppc.getCodigo())) {
				model = new PrtPptoCancelacion();
				HibernateUtil.assignID(session,model);
			} else {
				model = (PrtPptoCancelacion) session.get(PrtPptoCancelacion.class, ppc.getCodigo());
			}
			model.setTitulo(ppc.getTitulo());
			model.setDescripcion(ppc.getDescripcion());
			model.setActivo(ppc.getActivo());
		    
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
