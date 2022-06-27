package crm.services.transaction;

import java.rmi.RemoteException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import crm.libraries.abm.entities.GerenciaComercial;
import crm.services.sei.GerenciaComercialManagerSEI;
import crm.services.util.HibernateUtil;

public class GerenciaComercialManager implements GerenciaComercialManagerSEI,ManagerService {

	public GerenciaComercial getGerenciaComercialById(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();

		Criteria c = session.createCriteria(GerenciaComercial.class);
		c.add(Expression.eq("codigo", codigo));
		c.add(Expression.eq("activo","S"));
		GerenciaComercial a = (GerenciaComercial) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return a;
	}

	public GerenciaComercial[] getAllGerenciaComercials() throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(GerenciaComercial.class);
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (GerenciaComercial[])list.toArray(new GerenciaComercial[0]);
	}
	
	public GerenciaComercial[] findByDescripcionOrGerente(String descripcion,String codigoGerente) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(GerenciaComercial.class);
		c.add(Expression.eq("descripcion", descripcion));
		c.add(Expression.eq("codigoGerente", codigoGerente));
		List list = c.list();
		HibernateUtil.cerrarSession(session);
		return (GerenciaComercial[])list.toArray(new GerenciaComercial[0]);
	}

	public GerenciaComercial[] getAllGerenciaComercialsTranslated(String lang) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}
	
	public GerenciaComercial[] findByField(String field,String value){
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(GerenciaComercial.class);
		c.add(Expression.like(field,"%" + value + "%"));
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (GerenciaComercial[])list.toArray(new GerenciaComercial[0]);
	}

	public void remove(String codigo) throws RemoteException {
		GerenciaComercial entity = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			
			entity = (GerenciaComercial) session.get(GerenciaComercial.class, codigo);						
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

	public void update(GerenciaComercial gerenciaComercial) throws RemoteException {
		GerenciaComercial p = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			if (StringUtils.isBlank(gerenciaComercial.getCodigo())) {
				p = new GerenciaComercial();
				HibernateUtil.assignID(session,p);
			} else {
				p = (GerenciaComercial) session.get(GerenciaComercial.class, gerenciaComercial.getCodigo());
			}

			p.setDescripcion(gerenciaComercial.getDescripcion());
			p.setCodigoGerente(gerenciaComercial.getCodigoGerente());
			p.setActivo(gerenciaComercial.getActivo());
			    
			session.saveOrUpdate(p);

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
