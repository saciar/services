package crm.services.wsdl2.manager;

import java.rmi.RemoteException;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import crm.libraries.abm.entities.Depositos;
import crm.services.util.HibernateUtil;
import crm.services.wsdl2.sei.DepositosManagerSEI;

public class DepositosManager implements DepositosManagerSEI, WSDL2Service{
    public Depositos getById(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(Depositos.class);
		c.add(Expression.eq("codigo", codigo));
		c.add(Expression.eq("depActivo",new Boolean(true)));
		Depositos result = (Depositos) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return result;
	}


	public Depositos[] getAll() throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(Depositos.class);
		c.add(Expression.eq("depActivo",new Boolean(true)));
		List list = c.list();
		HibernateUtil.cerrarSession(session);
		return (Depositos[])list.toArray(new Depositos[0]);
	}

	public Depositos[] findByField(String field,String value){
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(Depositos.class);
		c.add(Expression.like(field,"%" + value + "%"));
		c.add(Expression.eq("depActivo",new Boolean(true)));
		List list = c.list();
		HibernateUtil.cerrarSession(session);
		return (Depositos[])list.toArray(new Depositos[0]);
	}

	public void remove(String codigo) throws RemoteException {
		Depositos entity = null;
		Session session = null;
		Transaction tx = null;
		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();
			entity = (Depositos) session.get(Depositos.class, codigo);
			entity.setDepActivo(false);
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

	public void update(Depositos dep) throws RemoteException {
		Depositos model = null;
		Session session = null;
		Transaction tx = null;
		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			if (dep.getCodigo().equals(null)) {
				model = new Depositos();
				HibernateUtil.assignID(session,model);
			} else {
				model = (Depositos) session.get(Depositos.class, dep.getCodigo());
			}
			model.setDepActivo(dep.isDepActivo());
			model.setDepDescripcion(dep.getDepDescripcion());
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
