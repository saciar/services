package crm.services.wsdl2.manager;

import java.rmi.RemoteException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import crm.libraries.abm.entities.ModoIngEquipos;
import crm.services.util.HibernateUtil;
import crm.services.wsdl2.sei.ModoIngEquiposManagerSEI;

public class ModoIngEquiposManager implements ModoIngEquiposManagerSEI,WSDL2Service {

	public ModoIngEquipos getById(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(ModoIngEquipos.class);
		c.add(Expression.eq("codigo", codigo));
		c.add(Expression.eq("activo","S"));
		ModoIngEquipos result = (ModoIngEquipos) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return result;
	}
	

	public ModoIngEquipos[] getAll() throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(ModoIngEquipos.class);
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);
		return (ModoIngEquipos[])list.toArray(new ModoIngEquipos[0]);
	}

	public ModoIngEquipos[] findByField(String field,String value){		
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(ModoIngEquipos.class);
		c.add(Expression.like(field,"%" + value + "%"));
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);
		return (ModoIngEquipos[])list.toArray(new ModoIngEquipos[0]);
	}

	public void remove(String codigo) throws RemoteException {
		ModoIngEquipos entity = null;
		Session session = null;
		Transaction tx = null;
		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();
			entity = (ModoIngEquipos) session.get(ModoIngEquipos.class, codigo);						
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

	public void update(ModoIngEquipos titulo) throws RemoteException {
		ModoIngEquipos model = null;
		Session session = null;
		Transaction tx = null;
		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			if (StringUtils.isBlank(titulo.getCodigo())) {
				model = new ModoIngEquipos();
				HibernateUtil.assignID(session,model);
			} else {
				model = (ModoIngEquipos) session.get(ModoIngEquipos.class, titulo.getCodigo());
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
