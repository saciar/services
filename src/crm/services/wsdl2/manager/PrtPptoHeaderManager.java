package crm.services.wsdl2.manager;

import java.rmi.RemoteException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import crm.libraries.abm.entities.PrtPptoHeader;
import crm.services.util.HibernateUtil;
import crm.services.wsdl2.sei.PrtPptoHeaderManagerSEI;

public class PrtPptoHeaderManager implements PrtPptoHeaderManagerSEI,WSDL2Service {

	public PrtPptoHeader getById(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(PrtPptoHeader.class);
		c.add(Expression.eq("codigo", codigo));
		c.add(Expression.eq("activo","S"));
		PrtPptoHeader result = (PrtPptoHeader) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return result;
	}
	

	public PrtPptoHeader[] getAll() throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(PrtPptoHeader.class);
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);
		return (PrtPptoHeader[])list.toArray(new PrtPptoHeader[0]);
	}

	public PrtPptoHeader[] findByField(String field,String value){		
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(PrtPptoHeader.class);
		c.add(Expression.like(field,"%" + value + "%"));
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);
		return (PrtPptoHeader[])list.toArray(new PrtPptoHeader[0]);
	}

	public void remove(String codigo) throws RemoteException {
		PrtPptoHeader entity = null;
		Session session = null;
		Transaction tx = null;
		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();
			entity = (PrtPptoHeader) session.get(PrtPptoHeader.class, codigo);						
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

	public void update(PrtPptoHeader pph) throws RemoteException {
		PrtPptoHeader model = null;
		Session session = null;
		Transaction tx = null;
		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			if (StringUtils.isBlank(pph.getCodigo())) {
				model = new PrtPptoHeader();
				HibernateUtil.assignID(session,model);
			} else {
				model = (PrtPptoHeader) session.get(PrtPptoHeader.class, pph.getCodigo());
			}
			model.setTitulo(pph.getTitulo());
			model.setDescripcion(pph.getDescripcion());
			model.setActivo(pph.getActivo());
		    
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
