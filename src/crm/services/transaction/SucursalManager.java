package crm.services.transaction;

import java.rmi.RemoteException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import crm.libraries.abm.entities.Sucursal;
import crm.services.sei.SucursalManagerSEI;
import crm.services.util.HibernateUtil;

public class SucursalManager implements SucursalManagerSEI,ManagerService {
    public String getSucursalNameByCodigo(String codigo){
		Session session = HibernateUtil.abrirSession();

		Query query = 
			session.createQuery("select descripcion from Sucursal where codigo = :codigo and activo = 's'");
		
		query.setString("codigo", codigo);
		
		String desc = (String)query.uniqueResult();
		
		HibernateUtil.cerrarSession(session);
		
		if (desc == null)
			desc = "No hay nada";
		
    	return desc;
    }
    
    
    

	public Sucursal getSucursalById(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();

		Criteria c = session.createCriteria(Sucursal.class);
		c.add(Expression.eq("codigo", codigo));
		c.add(Expression.eq("activo","S"));
		Sucursal a = (Sucursal) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return a;
	}
	
	public Sucursal getSucursalByDescripcion(String descripcion) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(Sucursal.class);
		c.add(Expression.eq("descripcion", descripcion));
		Sucursal a = (Sucursal) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return a;
	}

	public Sucursal[] getAllSucursales() throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(Sucursal.class);
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (Sucursal[])list.toArray(new Sucursal[0]);
	}

	public Sucursal[] findByField(String field,String value){
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(Sucursal.class);
		c.add(Expression.like(field,"%" + value + "%"));
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (Sucursal[])list.toArray(new Sucursal[0]);
	}
	
	public Sucursal[] getAllSucursalesTranslated(String lang) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public void remove(String codigo) throws RemoteException {
		Sucursal entity = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			
			entity = (Sucursal) session.get(Sucursal.class, codigo);						
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

	public void update(Sucursal sucursal) throws RemoteException {
		Sucursal suc = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			if (StringUtils.isBlank(sucursal.getCodigo())) {
				suc = new Sucursal();				
				HibernateUtil.assignID(session,suc);
				
			} else {
				suc = (Sucursal) session.get(Sucursal.class, sucursal.getCodigo());
			}

			suc.setDescripcion(sucursal.getDescripcion());
			suc.setActivo(sucursal.getActivo());
		    		
			session.saveOrUpdate(suc);

			tx.commit();
			session.flush();
		} catch (HibernateException he) {
			if (tx != null && tx.isActive())
				tx.rollback();

			he.printStackTrace(System.err);
			// throw new SystemException(he);
		} finally {
			HibernateUtil.cerrarSession(session);
		}
	}

}
