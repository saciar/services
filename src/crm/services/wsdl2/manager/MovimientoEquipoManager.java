package crm.services.wsdl2.manager;

import java.rmi.RemoteException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import crm.libraries.abm.entities.MovimientoEquipo;
import crm.services.util.HibernateUtil;

public class MovimientoEquipoManager {
	public MovimientoEquipo getById(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(MovimientoEquipo.class);
		c.add(Expression.eq("codigo", codigo));
		MovimientoEquipo result = (MovimientoEquipo) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return result;
	}

	public MovimientoEquipo[] getAll() throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(MovimientoEquipo.class);
		List list = c.list();
		HibernateUtil.cerrarSession(session);
		return (MovimientoEquipo[])list.toArray(new MovimientoEquipo[0]);
	}

	public MovimientoEquipo[] findByField(String field,String value) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(MovimientoEquipo.class);
		c.add(Expression.like(field,"%" + value + "%"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);
		return (MovimientoEquipo[])list.toArray(new MovimientoEquipo[0]);
	}
	
	public MovimientoEquipo[] findByFieldExactly(String field,String value) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(MovimientoEquipo.class);
		c.add(Expression.eq(field,value));
		List list = c.list();
		HibernateUtil.cerrarSession(session);
		return (MovimientoEquipo[])list.toArray(new MovimientoEquipo[0]);
	}
	
	public MovimientoEquipo[] findByFields(Object[] field,Object[] value) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(MovimientoEquipo.class);
		for(int i=0; i<field.length;i++){
			c.add(Expression.like((String)field[i],"%" + (String)value[i] + "%"));
		}
		List list = c.list();
		HibernateUtil.cerrarSession(session);
		return (MovimientoEquipo[])list.toArray(new MovimientoEquipo[0]);
	}

	public void remove(String codigo) throws RemoteException {
		MovimientoEquipo entity = null;
		Session session = null;
		Transaction tx = null;
		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();
			entity = (MovimientoEquipo) session.get(MovimientoEquipo.class, codigo);
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

	public void update(MovimientoEquipo equipo) throws RemoteException {
		MovimientoEquipo model = null;
		Session session = null;
		Transaction tx = null;
		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			if (StringUtils.isBlank(equipo.getCodigo())) {
				model = new MovimientoEquipo();
				HibernateUtil.assignID(session,model);
			} else {
				model = (MovimientoEquipo) session.get(MovimientoEquipo.class, equipo.getCodigo());
			}
			model.setCodEquipo(equipo.getCodEquipo());
			model.setNroOrden(equipo.getNroOrden());
			model.setDeposito(equipo.getDeposito());
			model.setFecha(equipo.getFecha());
			model.setOperador(equipo.getOperador());
			model.setNroOrden(equipo.getNroOrden());
			model.setTipo(equipo.getTipo());
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
