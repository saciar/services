package crm.services.wsdl2.manager;

import java.rmi.RemoteException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import crm.libraries.abm.entities.SeguridadEquipos;
import crm.services.util.HibernateUtil;
import crm.services.wsdl2.sei.SeguridadEquiposManagerSEI;

public class SeguridadEquiposManager implements SeguridadEquiposManagerSEI,WSDL2Service {

	public SeguridadEquipos getById(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(SeguridadEquipos.class);
		c.add(Expression.eq("codigo", codigo));
		c.add(Expression.eq("activo","S"));
		SeguridadEquipos result = (SeguridadEquipos) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return result;
	}
	

	public SeguridadEquipos[] getAll() throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(SeguridadEquipos.class);
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);
		return (SeguridadEquipos[])list.toArray(new SeguridadEquipos[0]);
	}

	public SeguridadEquipos[] findByField(String field,String value){		
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(SeguridadEquipos.class);
		c.add(Expression.like(field,"%" + value + "%"));
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);
		return (SeguridadEquipos[])list.toArray(new SeguridadEquipos[0]);
	}

	public void remove(String codigo) throws RemoteException {
		SeguridadEquipos entity = null;
		Session session = null;
		Transaction tx = null;
		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();
			entity = (SeguridadEquipos) session.get(SeguridadEquipos.class, codigo);						
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

	public void update(SeguridadEquipos titulo) throws RemoteException {
		SeguridadEquipos model = null;
		Session session = null;
		Transaction tx = null;
		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			if (StringUtils.isBlank(titulo.getCodigo())) {
				model = new SeguridadEquipos();
				HibernateUtil.assignID(session,model);
			} else {
				model = (SeguridadEquipos) session.get(SeguridadEquipos.class, titulo.getCodigo());
			}
			model.setDescripcion(titulo.getDescripcion());
			model.setActivo(titulo.getActivo());
		    
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
