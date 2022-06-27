package crm.services.transaction;

import java.rmi.RemoteException;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import crm.libraries.abm.entities.UnidadVendedor;
import crm.services.sei.UnidadVendedorManagerSEI;
import crm.services.util.CollectionUtil;
import crm.services.util.HibernateUtil;

public class UnidadVendedorManager implements UnidadVendedorManagerSEI,ManagerService {
   	public String getCodigoUnidad(String codigoVendedor){
		Session session = HibernateUtil.abrirSession();

		Query query = 
			session.createQuery("select codigoUnidad from UnidadVendedor where codigoVendedor = :codigoVendedor and activo = 'S'");
		
		query.setString("codigoVendedor", codigoVendedor);
		
		String desc = (String)query.uniqueResult();
		
		HibernateUtil.cerrarSession(session);
		
    	return desc;
    }  	
   	
	public UnidadVendedor[] getUnidadByCodigoUnidad(String codigoUnidad)throws RemoteException {
	
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(UnidadVendedor.class);
		c.add(Expression.eq("codigoUnidad", codigoUnidad));
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (UnidadVendedor[])list.toArray(new UnidadVendedor[0]);
	}
	
	
	
	public void removeByCodigoUnidad(String codigoUnidad)throws RemoteException {
		Session session = null;
		Transaction tx = null;
		try {
            session = HibernateUtil.abrirSession();
            tx = session.beginTransaction();

    		Criteria c = session.createCriteria(UnidadVendedor.class);
    		c.add(Expression.eq("codigoUnidad", codigoUnidad));
    		List list = c.list();
    		UnidadVendedor[] results = (UnidadVendedor[])list.toArray(new UnidadVendedor[0]);
    		for(int i = 0; i < results.length;i++){            
                results[i].setActivo("N");
                session.update(results[i]);
    		}
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

	public void update(UnidadVendedor unidadVendedor) throws RemoteException {
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();	

			session.saveOrUpdate(unidadVendedor);
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
