package crm.services.wsdl2.manager;

import java.rmi.RemoteException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import crm.libraries.abm.entities.MonedaExtranjera;
import crm.services.util.HibernateUtil;
import crm.services.wsdl2.sei.MonedaExtranjeraManagerSEI;

public class MonedaExtranjeraManager implements MonedaExtranjeraManagerSEI, WSDL2Service{
	public MonedaExtranjera getById(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(MonedaExtranjera.class);
		c.add(Expression.eq("codigo", codigo));
		c.add(Expression.eq("activo","S"));
		MonedaExtranjera result = (MonedaExtranjera) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return result;
	}
	

	public MonedaExtranjera[] getAll() throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(MonedaExtranjera.class);
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);
		return (MonedaExtranjera[])list.toArray(new MonedaExtranjera[0]);
	}

	public MonedaExtranjera[] findByField(String field,String value){		
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(MonedaExtranjera.class);
		c.add(Expression.like(field,"%" + value + "%"));
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);
		return (MonedaExtranjera[])list.toArray(new MonedaExtranjera[0]);
	}

	public void remove(String codigo) throws RemoteException {
		MonedaExtranjera entity = null;
		Session session = null;
		Transaction tx = null;
		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();
			entity = (MonedaExtranjera) session.get(MonedaExtranjera.class, codigo);						
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

	public void update(MonedaExtranjera moneda) throws RemoteException {
		MonedaExtranjera model = null;
		Session session = null;
		Transaction tx = null;
		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			if (StringUtils.isBlank(moneda.getCodigo())) {
				model = new MonedaExtranjera();
				HibernateUtil.assignID(session,model);
			} else {
				model = (MonedaExtranjera) session.get(MonedaExtranjera.class, moneda.getCodigo());
			}
			model.setDescripcion(moneda.getDescripcion());
			model.setValor(moneda.getValor());
			model.setActivo(moneda.getActivo());
		    
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
