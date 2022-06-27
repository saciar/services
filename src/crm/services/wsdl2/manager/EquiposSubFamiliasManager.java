package crm.services.wsdl2.manager;

import java.rmi.RemoteException;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;

import crm.libraries.abm.entities.EquiposSubFamilias;
import crm.services.util.HibernateUtil;
import crm.services.wsdl2.sei.EquiposSubFamiliasManagerSEI;

public class EquiposSubFamiliasManager implements EquiposSubFamiliasManagerSEI, WSDL2Service{
	public EquiposSubFamilias getById(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(EquiposSubFamilias.class);
		c.add(Expression.eq("codigo", codigo));
		c.add(Expression.eq("eqSubfamActivo",new Boolean(true)));
		EquiposSubFamilias result = (EquiposSubFamilias) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return result;
	}


	public EquiposSubFamilias[] getAll() throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(EquiposSubFamilias.class);
		c.add(Expression.eq("eqSubfamActivo",new Boolean(true)));
		c.addOrder(Order.asc("eqSubfamDescripcion"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);
		return (EquiposSubFamilias[])list.toArray(new EquiposSubFamilias[0]);
	}

	public EquiposSubFamilias[] findByField(String field,String value){
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(EquiposSubFamilias.class);
		c.add(Expression.eq(field, value));
		c.add(Expression.eq("eqSubfamActivo",new Boolean(true)));
		c.addOrder(Order.asc("eqSubfamDescripcion"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);
		return (EquiposSubFamilias[])list.toArray(new EquiposSubFamilias[0]);
	}

	public void remove(String codigo) throws RemoteException {
		EquiposSubFamilias entity = null;
		Session session = null;
		Transaction tx = null;
		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();
			entity = (EquiposSubFamilias) session.get(EquiposSubFamilias.class, codigo);
			entity.setEqSubfamActivo(false);
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

	public void update(EquiposSubFamilias familia) throws RemoteException {
		EquiposSubFamilias model = null;
		Session session = null;
		Transaction tx = null;
		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			if (familia.getCodigo().equals(null)) {
				model = new EquiposSubFamilias();
				HibernateUtil.assignID(session,model);
			} else {
				model = (EquiposSubFamilias) session.get(EquiposSubFamilias.class, familia.getCodigo());
			}
			model.setEqSubfamActivo(familia.isEqSubfamActivo());
			model.setEqSubfamDescripcion(familia.getEqSubfamDescripcion());
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
