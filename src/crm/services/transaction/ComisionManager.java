package crm.services.transaction;

import java.rmi.RemoteException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import crm.libraries.abm.entities.ABMEntity;
import crm.libraries.abm.entities.Comision;
import crm.services.sei.ComisionManagerSEI;
import crm.services.util.HibernateUtil;

public class ComisionManager implements ComisionManagerSEI,ManagerService {

	public Comision getComisionById(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();

		Criteria c = session.createCriteria(Comision.class);
		c.add(Expression.eq("codigo", codigo));
		c.add(Expression.eq("activo","S"));
		Comision a = (Comision) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return a;
	}

	public Comision getComisionByVendedor(String codigoVendedor) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(Comision.class);
		c.add(Expression.eq("codigoVendedor", codigoVendedor));
		Comision a = (Comision) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return a;
	}
	
	public Comision[] getAllComisiones() throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(Comision.class);
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (Comision[])list.toArray(new Comision[0]);
	}
	public Comision[] findByField(String field,String value){
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(Comision.class);
		c.add(Expression.like(field,"%" + value + "%"));
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (Comision[])list.toArray(new Comision[0]);
	}
	
		
	public Comision[] getAllComisionesTranslated(String lang) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	
	public void removeByVendedor(String codigoVendedor)throws RemoteException {
		Session session = null;
		Transaction tx = null;
		try {
            session = HibernateUtil.abrirSession();
            tx = session.beginTransaction();

    		Criteria c = session.createCriteria(Comision.class);
    		c.add(Expression.eq("codigoVendedor", codigoVendedor));
    		List list = c.list();
    		Comision[] results = (Comision[])list.toArray(new Comision[0]);
    		for(int i = 0; i < results.length;i++){            
                session.delete(results[i]);
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
	
	
	public void remove(String codigo) throws RemoteException {
		Comision entity = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			
			entity = (Comision) session.get(Comision.class, codigo);						
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

	public void update(Comision comision) throws RemoteException {
		Comision c = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			if (StringUtils.isBlank(comision.getCodigo())) {
				c = new Comision();
				// TODO: asignar ID
				assignID(session,c);
				
				//a.setCodigo(null);
			} else {
				c = (Comision) session.get(Comision.class, comision.getCodigo());
			}

			
			c.setCodigoVendedor(comision.getCodigoVendedor());
			c.setPorcentaje(comision.getPorcentaje());
			c.setMarcoLiquidacion(comision.getMarcoLiquidacion());
			c.setActivo(comision.getActivo());
		    		
			session.saveOrUpdate(c);

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

	
	
	private void assignID(Session session,ABMEntity e) {

		String className = e.getClass().toString();
		String entityType = className.substring(className.lastIndexOf(".") + 1,
				className.length());
		String entityAlias = entityType.toLowerCase();

		// ID = MAX_ID_UNTIL_NOW + 1
		String query = "select max(" + entityAlias + ".codigo) + 1 from " 
		+ entityType + " " + entityAlias;
		
		Object result = session.createQuery(query)		
						.uniqueResult();
		
		String codigo = result != null ? result.toString():"1";

		e.setCodigo(codigo);

	}
	
	public String getMarcoLiquidacionByCodVendedor(String codVendedor) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		
		String st = (String)session.createQuery
			("select marcoLiquidacion from Comision where codigoVendedor = :cod and activo = 'S'")
			.setString("cod", codVendedor)
			.uniqueResult();
		
		HibernateUtil.cerrarSession(session);
		return st;
	}
	
	public String getPorcentajeByCodVendedor(String codVendedor) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		
		String st = (String)session.createQuery
			("select porcentaje from Comision where codigoVendedor = :cod and activo = 'S'")
			.setString("cod", codVendedor)
			.uniqueResult();
		
		HibernateUtil.cerrarSession(session);
		return st;
	}
	
	public boolean isLugarComisionable(String codLugar) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		
		Integer i = (Integer)session.createQuery(
				"select count(*) from Comision where codigoVendedor = :cod and activo = 'S'"
				).setString("cod", codLugar)
				.uniqueResult();
		
		HibernateUtil.cerrarSession(session);
		
		return i.intValue() > 0;
	}
}
