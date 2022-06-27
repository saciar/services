package crm.services.transaction;

import java.rmi.RemoteException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import crm.libraries.abm.entities.CodigoPostal;
import crm.services.sei.CodigoPostalManagerSEI;
import crm.services.util.HibernateUtil;

public class CodigoPostalManager implements CodigoPostalManagerSEI,ManagerService {

	public CodigoPostal getCodigoPostalById(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();

		Criteria c = session.createCriteria(CodigoPostal.class);
		c.add(Expression.eq("codigo", codigo));
		c.add(Expression.eq("activo","S"));
		CodigoPostal a = (CodigoPostal) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return a;
	}

	public CodigoPostal getCodigoPostalByCP(String cp) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(CodigoPostal.class);
		c.add(Expression.eq("codigoPostal", cp));
		CodigoPostal a = (CodigoPostal) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return a;
	}
	
	public CodigoPostal[] getAllCodigoPostales() throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(CodigoPostal.class);
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (CodigoPostal[])list.toArray(new CodigoPostal[0]);
	}

	public CodigoPostal[] findByLocalidadId(String value){
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(CodigoPostal.class);
		c.add(Expression.eq("codLocalidad",value));
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (CodigoPostal[])list.toArray(new CodigoPostal[0]);
	}
	
	public Object[] findNamesByLocalidadId(String value) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		List list = session.createQuery
				("select c.codigo, c.codigoPostal " +
				"from CodigoPostal c " +
				"where " +
				"c.codLocalidad =:partido and c.activo = 'S' ")
				.setString("partido", value)
				.list();
		
		Object[] results = new Object[list.size()];
		
		for (int i=0; i< results.length;i++){ 
		    results[i] = (Object[]) list.get(i);
		}
		
		HibernateUtil.cerrarSession(session);
		
		return results;
	}
	
	public CodigoPostal[] findByField(String field,String value){
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(CodigoPostal.class);
		c.add(Expression.like(field,"%" + value + "%"));
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (CodigoPostal[])list.toArray(new CodigoPostal[0]);
	}
	
	public CodigoPostal[] getAllCodigoPostalesTranslated(String lang) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public void remove(String codigo) throws RemoteException {
		CodigoPostal entity = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			
			entity = (CodigoPostal) session.get(CodigoPostal.class, codigo);						
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

	public void update(CodigoPostal codigoPostal) throws RemoteException {
		CodigoPostal cp = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			if (StringUtils.isBlank(codigoPostal.getCodigo())) {
				cp = new CodigoPostal();
				// TODO: asignar ID
				HibernateUtil.assignID(session,cp);
				
				//a.setCodigo(null);
			} else {
				cp = (CodigoPostal) session.get(CodigoPostal.class, codigoPostal.getCodigo());
			}

			
			cp.setCodigoPostal(codigoPostal.getCodigoPostal());
			cp.setCodLocalidad(codigoPostal.getCodLocalidad());
			cp.setActivo(codigoPostal.getActivo());
			 		
			session.saveOrUpdate(cp);

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
