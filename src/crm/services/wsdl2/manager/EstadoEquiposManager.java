package crm.services.wsdl2.manager;

import java.rmi.RemoteException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import crm.libraries.abm.entities.EstadoEquipos;
import crm.services.util.HibernateUtil;
import crm.services.wsdl2.sei.EstadoEquiposManagerSEI;

public class EstadoEquiposManager implements EstadoEquiposManagerSEI, WSDL2Service{
    public EstadoEquipos getById(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(EstadoEquipos.class);
		c.add(Expression.eq("codigo", codigo));
		c.add(Expression.eq("activo","S"));
		EstadoEquipos result = (EstadoEquipos) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return result;
	}


	public EstadoEquipos[] getAll() throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(EstadoEquipos.class);
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);
		return (EstadoEquipos[])list.toArray(new EstadoEquipos[0]);
	}

	public EstadoEquipos[] findByField(String field,String value) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(EstadoEquipos.class);
		c.add(Expression.like(field,"%" + value + "%"));
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);
		return (EstadoEquipos[])list.toArray(new EstadoEquipos[0]);
	}
	
	public EstadoEquipos[] findByFields(Object[] field,Object[] value) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(EstadoEquipos.class);
		for(int i=0; i<field.length;i++){
			c.add(Expression.like((String)field[i],"%" + (String)value[i] + "%"));
		}
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);
		return (EstadoEquipos[])list.toArray(new EstadoEquipos[0]);
	}

	public void remove(String codigo) throws RemoteException {
		EstadoEquipos entity = null;
		Session session = null;
		Transaction tx = null;
		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();
			entity = (EstadoEquipos) session.get(EstadoEquipos.class, codigo);
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

	public void update(EstadoEquipos equipo) throws RemoteException {
		EstadoEquipos model = null;
		Session session = null;
		Transaction tx = null;
		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			if (StringUtils.isBlank(equipo.getCodigo())) {
				model = new EstadoEquipos();
				HibernateUtil.assignID(session,model);
			} else {
				model = (EstadoEquipos) session.get(EstadoEquipos.class, equipo.getCodigo());
			}
			model.setDescripcion(equipo.getDescripcion());
			model.setActivo(equipo.getActivo());
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
