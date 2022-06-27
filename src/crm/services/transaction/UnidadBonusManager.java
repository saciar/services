package crm.services.transaction;

import java.rmi.RemoteException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import crm.libraries.abm.entities.UnidadBonus;
import crm.services.sei.UnidadBonusManagerSEI;
import crm.services.util.HibernateUtil;

public class UnidadBonusManager implements UnidadBonusManagerSEI,ManagerService {
		
	public UnidadBonus getUnidadBonusById(String codigo,String nivel) throws RemoteException {
		Session session = HibernateUtil.abrirSession();

		Criteria c = session.createCriteria(UnidadBonus.class);
		c.add(Expression.eq("codigo", codigo));
		c.add(Expression.eq("nivel", nivel));
		c.add(Expression.eq("activo","S"));
		UnidadBonus a = (UnidadBonus) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return a;
	}

	public UnidadBonus[] getAllUnidadBonus() throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(UnidadBonus.class);
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (UnidadBonus[])list.toArray(new UnidadBonus[0]);
	}

	public UnidadBonus[] findByUnidadComercialId(String unidadComercial) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(UnidadBonus.class);
		c.add(Expression.eq("codigo",unidadComercial));
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (UnidadBonus[])list.toArray(new UnidadBonus[0]);
	}
	
	public UnidadBonus[] findByField(String field,String value){
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(UnidadBonus.class);
		c.add(Expression.like(field,"%" + value + "%"));
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (UnidadBonus[])list.toArray(new UnidadBonus[0]);
	}
	

	public void remove(String codigo,String nivel) throws RemoteException {
		UnidadBonus entity = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			
			entity = getUnidadBonusById(codigo,nivel);						
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

	
	public void update(UnidadBonus unidadBonus) throws RemoteException {
		UnidadBonus ub = null;		
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			if (StringUtils.isBlank(unidadBonus.getNivel())) {
				ub = new UnidadBonus();
				ub.setNivel(getNivel(session));
				ub.setCodigo(unidadBonus.getCodigo());
			} else {
				ub = getUnidadBonusById(unidadBonus.getCodigo(),unidadBonus.getNivel());
			}
			
			ub.setObjetivo(unidadBonus.getObjetivo());
			ub.setBonusEquipo(unidadBonus.getBonusEquipo());
			ub.setActivo(unidadBonus.getActivo());			
		    		
			session.saveOrUpdate(ub);

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

	private String getNivel(Session session) {
		String entityName = UnidadBonus.class.getSimpleName();
		String query = new String();
		
		query += "select max(nivel) + 1 " ;
		query += "from "+ entityName + " ";
		
		Object result = session.createQuery(query).uniqueResult();
		
		return result != null ? result.toString():"1";
	}
	
}
