package crm.services.wsdl2.manager;

import java.rmi.RemoteException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import crm.libraries.abm.entities.PrtPptoTipoPresupuesto;
import crm.services.util.HibernateUtil;
import crm.services.wsdl2.sei.PrtPptoTipoPresupuestoManagerSEI;

public class PrtPptoTipoPresupuestoManager implements PrtPptoTipoPresupuestoManagerSEI,WSDL2Service {

	public PrtPptoTipoPresupuesto getById(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(PrtPptoTipoPresupuesto.class);
		c.add(Expression.eq("codigo", codigo));
		c.add(Expression.eq("activo","S"));
		PrtPptoTipoPresupuesto result = (PrtPptoTipoPresupuesto) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return result;
	}
	

	public PrtPptoTipoPresupuesto[] getAll() throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(PrtPptoTipoPresupuesto.class);
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);
		return (PrtPptoTipoPresupuesto[])list.toArray(new PrtPptoTipoPresupuesto[0]);
	}

	public PrtPptoTipoPresupuesto[] findByField(String field,String value){		
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(PrtPptoTipoPresupuesto.class);
		c.add(Expression.like(field,"%" + value + "%"));
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);
		return (PrtPptoTipoPresupuesto[])list.toArray(new PrtPptoTipoPresupuesto[0]);
	}

	public void remove(String codigo) throws RemoteException {
		PrtPptoTipoPresupuesto entity = null;
		Session session = null;
		Transaction tx = null;
		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();
			entity = (PrtPptoTipoPresupuesto) session.get(PrtPptoTipoPresupuesto.class, codigo);						
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

	public void update(PrtPptoTipoPresupuesto pps) throws RemoteException {
		PrtPptoTipoPresupuesto model = null;
		Session session = null;
		Transaction tx = null;
		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			if (StringUtils.isBlank(pps.getCodigo())) {
				model = new PrtPptoTipoPresupuesto();
				HibernateUtil.assignID(session,model);
			} else {
				model = (PrtPptoTipoPresupuesto) session.get(PrtPptoTipoPresupuesto.class, pps.getCodigo());
			}
			model.setTitulo(pps.getTitulo());
			model.setDescripcion(pps.getDescripcion());
			model.setActivo(pps.getActivo());
		    
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
