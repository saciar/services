package crm.services.wsdl2.manager;

import java.rmi.RemoteException;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;

import crm.libraries.abm.entities.EquiposFamilias;
import crm.services.util.HibernateUtil;
import crm.services.wsdl2.sei.EquiposFamiliasManagerSEI;

public class EquiposFamiliasManager implements EquiposFamiliasManagerSEI, WSDL2Service{
    public EquiposFamilias getById(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(EquiposFamilias.class);
		c.add(Expression.eq("codigo", codigo));
		c.add(Expression.eq("eqfamActivo",new Boolean(true)));
		EquiposFamilias result = (EquiposFamilias) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return result;
	}


	public EquiposFamilias[] getAll() throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(EquiposFamilias.class);
		c.add(Expression.eq("eqfamActivo",new Boolean(true)));
		c.addOrder(Order.asc("eqfamDescripcion"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);
		return (EquiposFamilias[])list.toArray(new EquiposFamilias[0]);
	}

	public EquiposFamilias[] findByField(String field,String value){
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(EquiposFamilias.class);
		c.add(Expression.like(field,"%" + value + "%"));
		c.add(Expression.eq("eqfamActivo",new Boolean(true)));
		List list = c.list();
		HibernateUtil.cerrarSession(session);
		return (EquiposFamilias[])list.toArray(new EquiposFamilias[0]);
	}

	public void remove(String codigo) throws RemoteException {
		EquiposFamilias entity = null;
		Session session = null;
		Transaction tx = null;
		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();
			entity = (EquiposFamilias) session.get(EquiposFamilias.class, codigo);
			entity.setEqfamActivo(false);
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

	public void update(EquiposFamilias familia) throws RemoteException {
		EquiposFamilias model = null;
		Session session = null;
		Transaction tx = null;
		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			if (familia.getCodigo().equals(null)) {
				model = new EquiposFamilias();
				HibernateUtil.assignID(session,model);
			} else {
				model = (EquiposFamilias) session.get(EquiposFamilias.class, familia.getCodigo());
			}
			model.setEqfamActivo(familia.isEqfamActivo());
			model.setEqfamDescripcion(familia.getEqfamDescripcion());
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
