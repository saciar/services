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
import crm.libraries.abm.entities.MarcosLiquidacion;
import crm.services.sei.MarcosLiquidacionManagerSEI;
import crm.services.util.HibernateUtil;

public class MarcosLiquidacionManager implements MarcosLiquidacionManagerSEI,ManagerService {

	public MarcosLiquidacion getMarcosLiquidacionById(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();

		Criteria c = session.createCriteria(MarcosLiquidacion.class);
		c.add(Expression.eq("codigo", codigo));
		c.add(Expression.eq("activo","S"));
		MarcosLiquidacion a = (MarcosLiquidacion) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return a;
	}

	public MarcosLiquidacion getMarcosLiquidacionByDescripcion(String descripcion) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(MarcosLiquidacion.class);
		c.add(Expression.eq("descripcion", descripcion));
		MarcosLiquidacion a = (MarcosLiquidacion) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return a;
	}
	
	public MarcosLiquidacion[] getAllMarcosLiquidaciones() throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(MarcosLiquidacion.class);
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (MarcosLiquidacion[])list.toArray(new MarcosLiquidacion[0]);
	}

	public MarcosLiquidacion[] findByField(String field,String value){
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(MarcosLiquidacion.class);
		c.add(Expression.like(field,"%" + value + "%"));
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (MarcosLiquidacion[])list.toArray(new MarcosLiquidacion[0]);
	}
	
	public MarcosLiquidacion[] getAllMarcosLiquidacionesTranslated(String lang) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public void remove(String codigo) throws RemoteException {
		MarcosLiquidacion entity = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			
			entity = (MarcosLiquidacion) session.get(MarcosLiquidacion.class, codigo);						
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

	public void update(MarcosLiquidacion marcosLiquidacion) throws RemoteException {
		MarcosLiquidacion ml = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			if (StringUtils.isBlank(marcosLiquidacion.getCodigo())) {
				ml = new MarcosLiquidacion();
				// TODO: asignar ID
				assignID(session,ml);
				
				//a.setCodigo(null);
			} else {
				ml = (MarcosLiquidacion) session.get(MarcosLiquidacion.class, marcosLiquidacion.getCodigo());
			}

			ml.setDescripcion(marcosLiquidacion.getDescripcion());
			ml.setActivo(marcosLiquidacion.getActivo());
		    		
			session.saveOrUpdate(ml);

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
}
