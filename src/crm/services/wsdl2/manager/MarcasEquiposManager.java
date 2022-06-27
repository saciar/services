package crm.services.wsdl2.manager;

import java.rmi.RemoteException;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;

import crm.libraries.abm.entities.MarcaEquipo;
import crm.services.util.HibernateUtil;
import crm.services.wsdl2.sei.MarcasEquiposManagerSEI;

public class MarcasEquiposManager implements MarcasEquiposManagerSEI, WSDL2Service{
    public MarcaEquipo getById(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(MarcaEquipo.class);
		c.add(Expression.eq("codigo", codigo));
		c.add(Expression.eq("activo","S"));
		MarcaEquipo result = (MarcaEquipo) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return result;
	}


	public MarcaEquipo[] getAll() throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(MarcaEquipo.class);
		c.add(Expression.eq("activo","S"));
		c.addOrder(Order.asc("descripcion"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);
		return (MarcaEquipo[])list.toArray(new MarcaEquipo[0]);
	}

	public MarcaEquipo[] findByField(String field,String value){
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(MarcaEquipo.class);
		c.add(Expression.like(field,"%" + value + "%"));
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);
		return (MarcaEquipo[])list.toArray(new MarcaEquipo[0]);
	}

	public void remove(String codigo) throws RemoteException {
		MarcaEquipo entity = null;
		Session session = null;
		Transaction tx = null;
		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();
			entity = (MarcaEquipo) session.get(MarcaEquipo.class, codigo);
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

	public void update(MarcaEquipo marca) throws RemoteException {
		MarcaEquipo model = null;
		Session session = null;
		Transaction tx = null;
		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			if (marca.getCodigo().equals(null)) {
				model = new MarcaEquipo();
				HibernateUtil.assignID(session,model);
			} else {
				model = (MarcaEquipo) session.get(MarcaEquipo.class, marca.getCodigo());
			}
			model.setActivo(marca.getActivo());
			model.setDescripcion(marca.getDescripcion());
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
