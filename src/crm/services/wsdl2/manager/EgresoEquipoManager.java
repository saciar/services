package crm.services.wsdl2.manager;

import java.rmi.RemoteException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import crm.libraries.abm.entities.EgresoEquipo;
import crm.services.util.HibernateUtil;
import crm.services.wsdl2.sei.EgresoEquipoManagerSEI;

public class EgresoEquipoManager implements EgresoEquipoManagerSEI, WSDL2Service{
    public EgresoEquipo getById(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(EgresoEquipo.class);
		c.add(Expression.eq("codigo", codigo));
		EgresoEquipo result = (EgresoEquipo) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return result;
	}

	public EgresoEquipo[] getAll() throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(EgresoEquipo.class);
		List list = c.list();
		HibernateUtil.cerrarSession(session);
		return (EgresoEquipo[])list.toArray(new EgresoEquipo[0]);
	}

	public EgresoEquipo[] findByField(String field,String value) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(EgresoEquipo.class);
		c.add(Expression.like(field,"%" + value + "%"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);
		return (EgresoEquipo[])list.toArray(new EgresoEquipo[0]);
	}
	
	public EgresoEquipo[] findByFieldExactly(String field,String value) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(EgresoEquipo.class);
		c.add(Expression.eq(field,value));
		List list = c.list();
		HibernateUtil.cerrarSession(session);
		return (EgresoEquipo[])list.toArray(new EgresoEquipo[0]);
	}
	
	public EgresoEquipo[] findByFields(Object[] field,Object[] value) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(EgresoEquipo.class);
		for(int i=0; i<field.length;i++){
			c.add(Expression.like((String)field[i],"%" + (String)value[i] + "%"));
		}
		List list = c.list();
		HibernateUtil.cerrarSession(session);
		return (EgresoEquipo[])list.toArray(new EgresoEquipo[0]);
	}

	public void remove(String codigo) throws RemoteException {
		EgresoEquipo entity = null;
		Session session = null;
		Transaction tx = null;
		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();
			entity = (EgresoEquipo) session.get(EgresoEquipo.class, codigo);
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

	public void update(EgresoEquipo equipo) throws RemoteException {
		EgresoEquipo model = null;
		Session session = null;
		Transaction tx = null;
		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			if (StringUtils.isBlank(equipo.getCodigo())) {
				model = new EgresoEquipo();
				HibernateUtil.assignID(session,model);
			} else {
				model = (EgresoEquipo) session.get(EgresoEquipo.class, equipo.getCodigo());
			}
			model.setCodEquipo(equipo.getCodEquipo());
			model.setCodEgreso(equipo.getCodEgreso());

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
