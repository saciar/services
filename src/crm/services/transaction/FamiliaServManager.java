package crm.services.transaction;

import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;

import crm.libraries.abm.entities.FamiliaServ;
import crm.services.sei.FamiliaServManagerSEI;
import crm.services.util.CollectionUtil;
import crm.services.util.HibernateUtil;

public class FamiliaServManager implements FamiliaServManagerSEI,ManagerService {

	public FamiliaServ getFamiliaServById(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(FamiliaServ.class);
		c.add(Expression.eq("codigo", codigo));
		c.add(Expression.eq("activo","S"));
		FamiliaServ a = (FamiliaServ) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
	
		return a;
	}
	
	public FamiliaServ getFamiliaServByDescripcion(String descripcion) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(FamiliaServ.class);
		c.add(Expression.eq("descripcion", descripcion));
		FamiliaServ a = (FamiliaServ) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
			
		return a;		
	}

	public FamiliaServ[] getAllFamiliaServs() throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(FamiliaServ.class);
		c.add(Expression.eq("activo","S"));
		c.addOrder(Order.asc("descripcion"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);
		return (FamiliaServ[])list.toArray(new FamiliaServ[0]);
	}

	public FamiliaServ[] getAllFamiliaServsTranslated(String lang) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public FamiliaServ[] findByField(String field,String value){
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(FamiliaServ.class);
		c.add(Expression.like(field,"%" + value + "%"));
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);
		return (FamiliaServ[])list.toArray(new FamiliaServ[0]);
	}
	
	public void remove(String codigo) throws RemoteException {
		if(codigo.equals(FAMILIA_SERVICIO_SUBCONTRATADO)){
			throw new RemoteException("No se permite borrar esta familia");
		}
				
		
		FamiliaServ entity = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			
			entity = (FamiliaServ) session.get(FamiliaServ.class, codigo);						
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

	public void update(FamiliaServ familiaServ) throws RemoteException {
		FamiliaServ f = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			if (StringUtils.isBlank(familiaServ.getCodigo())) {
				f = new FamiliaServ();
				HibernateUtil.assignID(session,f);
				
				//a.setCodigo(null);
			} else {
				f = (FamiliaServ) session.get(FamiliaServ.class, familiaServ.getCodigo());
			}

			f.setDescripcion(familiaServ.getDescripcion());
			f.setActivo(familiaServ.getActivo());
		    
		    
			// if (groupId == 0){
			// session.save(group);
			// }
			session.saveOrUpdate(f);

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
	
	public Object[] getFamiliaReport() throws RemoteException {
		Session session = HibernateUtil.abrirSession();

		List list = session.createSQLQuery
				(
				"select fs_codfamilia as codigo, fs_descripcion as descripcion " +
				"from MST_FAM_SERVICIOS " +
				"where activo = 'S' " +
				"order by descripcion"
				)
				.addScalar("codigo",Hibernate.LONG)
				.addScalar("descripcion",Hibernate.STRING)
				.list();

		HibernateUtil.cerrarSession(session);
		
		return CollectionUtil.listToObjectArray(list);
	}
	
	public String getDescripcionByServicio(String codFamServ) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		String list = (String)session.createQuery(
						"select v.descripcion"
								+ " from FamiliaServ v "
								+ "where "
								+ "v.codigo = :codigoFamServ ")
				.setString("codigoFamServ", codFamServ)
				.uniqueResult();

		HibernateUtil.cerrarSession(session);

		return list;
	}

}
