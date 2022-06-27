package crm.services.wsdl2.manager;

import java.rmi.RemoteException;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;

import crm.libraries.abm.entities.Transporte;
import crm.services.util.HibernateUtil;
import crm.services.wsdl2.sei.TransporteManagerSEI;

public class TransporteManager implements TransporteManagerSEI, WSDL2Service{
    public Transporte getById(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(Transporte.class);
		c.add(Expression.eq("codigo", codigo));
		c.add(Expression.eq("activo","S"));
		Transporte result = (Transporte) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return result;
	}


	public Transporte[] getAll() throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(Transporte.class);
		c.add(Expression.eq("activo","S"));
		c.addOrder(Order.asc("descripcion"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);
		return (Transporte[])list.toArray(new Transporte[0]);
	}

	public Transporte[] findByField(String field,String value){
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(Transporte.class);
		c.add(Expression.like(field,"%" + value + "%"));
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);
		return (Transporte[])list.toArray(new Transporte[0]);
	}

	public void remove(String codigo) throws RemoteException {
		Transporte entity = null;
		Session session = null;
		Transaction tx = null;
		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();
			entity = (Transporte) session.get(Transporte.class, codigo);
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

	public void update(Transporte marca) throws RemoteException {
		Transporte model = null;
		Session session = null;
		Transaction tx = null;
		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			if (marca.getCodigo().equals(null)) {
				model = new Transporte();
				HibernateUtil.assignID(session,model);
			} else {
				model = (Transporte) session.get(Transporte.class, marca.getCodigo());
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
