package crm.services.wsdl2.manager;

import java.rmi.RemoteException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import crm.libraries.abm.entities.IngresoEquipo;
import crm.services.util.HibernateUtil;
import crm.services.wsdl2.sei.IngresoEquipoManagerSEI;

public class IngresoEquipoManager implements IngresoEquipoManagerSEI, WSDL2Service{
    public IngresoEquipo getById(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(IngresoEquipo.class);
		c.add(Expression.eq("codigo", codigo));
		IngresoEquipo result = (IngresoEquipo) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return result;
	}

	public IngresoEquipo[] getAll() throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(IngresoEquipo.class);
		List list = c.list();
		HibernateUtil.cerrarSession(session);
		return (IngresoEquipo[])list.toArray(new IngresoEquipo[0]);
	}

	public IngresoEquipo[] findByField(String field,String value) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(IngresoEquipo.class);
		c.add(Expression.like(field,"%" + value + "%"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);
		return (IngresoEquipo[])list.toArray(new IngresoEquipo[0]);
	}
	
	public IngresoEquipo[] findByFieldExactly(String field,String value) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(IngresoEquipo.class);
		c.add(Expression.eq(field,value));
		List list = c.list();
		HibernateUtil.cerrarSession(session);
		return (IngresoEquipo[])list.toArray(new IngresoEquipo[0]);
	}
	
	public IngresoEquipo[] findByFields(Object[] field,Object[] value) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(IngresoEquipo.class);
		for(int i=0; i<field.length;i++){
			c.add(Expression.like((String)field[i],"%" + (String)value[i] + "%"));
		}
		List list = c.list();
		HibernateUtil.cerrarSession(session);
		return (IngresoEquipo[])list.toArray(new IngresoEquipo[0]);
	}

	public void remove(String codigo) throws RemoteException {
		IngresoEquipo entity = null;
		Session session = null;
		Transaction tx = null;
		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();
			entity = (IngresoEquipo) session.get(IngresoEquipo.class, codigo);
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

	public void update(IngresoEquipo equipo) throws RemoteException {
		IngresoEquipo model = null;
		Session session = null;
		Transaction tx = null;
		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			if (StringUtils.isBlank(equipo.getCodigo())) {
				model = new IngresoEquipo();
				HibernateUtil.assignID(session,model);
			} else {
				model = (IngresoEquipo) session.get(IngresoEquipo.class, equipo.getCodigo());
			}
			model.setCodEquipo(equipo.getCodEquipo());
			model.setCodDeposito(equipo.getCodDeposito());
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
