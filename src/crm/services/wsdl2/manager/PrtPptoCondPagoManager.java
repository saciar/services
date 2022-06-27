package crm.services.wsdl2.manager;

import java.rmi.RemoteException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import crm.libraries.abm.entities.PrtPptoCondPago;
import crm.services.transaction.PresupuestosManager;
import crm.services.util.HibernateUtil;
import crm.services.wsdl2.sei.PrtPptoCondPagoManagerSEI;

public class PrtPptoCondPagoManager implements PrtPptoCondPagoManagerSEI,WSDL2Service {

	public PrtPptoCondPago getById(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(PrtPptoCondPago.class);
		c.add(Expression.eq("codigo", codigo));
		c.add(Expression.eq("activo","S"));
		PrtPptoCondPago result = (PrtPptoCondPago) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return result;
	}
	
	public String getDescripcionById(long codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		
		String result = (String) session.createSQLQuery(
				"select ppcp_desc_abreviada as cond from mst_prt_ppto_cond_pago where ppcp_id = ? and ppcp_activo='S'")
				.addScalar("cond", Hibernate.STRING)
				.setLong(0, codigo)
				.uniqueResult();		
		
		HibernateUtil.cerrarSession(session);
		return result;
	}
	

	public PrtPptoCondPago[] getAll() throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(PrtPptoCondPago.class);
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);
		return (PrtPptoCondPago[])list.toArray(new PrtPptoCondPago[0]);
	}

	public PrtPptoCondPago[] findByField(String field,String value){		
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(PrtPptoCondPago.class);
		c.add(Expression.like(field,"%" + value + "%"));
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);
		return (PrtPptoCondPago[])list.toArray(new PrtPptoCondPago[0]);
	}

	public void remove(String codigo) throws RemoteException {
		PrtPptoCondPago entity = null;
		Session session = null;
		Transaction tx = null;
		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();
			entity = (PrtPptoCondPago) session.get(PrtPptoCondPago.class, codigo);						
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

	public void update(PrtPptoCondPago ppfp) throws RemoteException {
		PrtPptoCondPago model = null;
		Session session = null;
		Transaction tx = null;
		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			if (StringUtils.isBlank(ppfp.getCodigo())) {
				model = new PrtPptoCondPago();
				HibernateUtil.assignID(session,model);
			} else {
				model = (PrtPptoCondPago) session.get(PrtPptoCondPago.class, ppfp.getCodigo());
			}
			model.setTitulo(ppfp.getTitulo());
			model.setDescripcion(ppfp.getDescripcion());
			model.setActivo(ppfp.getActivo());
		    
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
	
private static PrtPptoCondPagoManager instance;
	
	public static synchronized PrtPptoCondPagoManager instance() {

			if (instance == null) 
				instance = new PrtPptoCondPagoManager();

		return instance;
	}

}
