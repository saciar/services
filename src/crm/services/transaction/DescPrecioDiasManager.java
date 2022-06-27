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
import crm.libraries.abm.entities.DescPrecioDias;
import crm.services.sei.DescPrecioDiasManagerSEI;
import crm.services.util.HibernateUtil;

public class DescPrecioDiasManager implements DescPrecioDiasManagerSEI,ManagerService {

	public DescPrecioDias getDescPrecioDiasById(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		// Servicio s = (Servicio)session.get(Servicio.class,codigo);
		Criteria c = session.createCriteria(DescPrecioDias.class);
		c.add(Expression.eq("codigo", codigo));
		c.add(Expression.eq("activo","S"));
		DescPrecioDias a = (DescPrecioDias) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return a;
	}
	
	public DescPrecioDias[] getDescPrecioDiasByServicio(String codigoServicio) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(DescPrecioDias.class);
		c.add(Expression.eq("codigoServicio", codigoServicio));
		c.add(Expression.eq("activo","S"));
		//DescPrecioDias a = (DescPrecioDias) c.uniqueResult();
		List list = c.list();
		HibernateUtil.cerrarSession(session);
		return (DescPrecioDias[])list.toArray(new DescPrecioDias[0]);
	}

	public DescPrecioDias[] getAllDescPrecioDias() throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(DescPrecioDias.class);
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (DescPrecioDias[])list.toArray(new DescPrecioDias[0]);
	}

	public DescPrecioDias[] findByField(String field,String value){
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(DescPrecioDias.class);
		c.add(Expression.like(field,"%" + value + "%"));
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (DescPrecioDias[])list.toArray(new DescPrecioDias[0]);
	}
	
	public DescPrecioDias[] getAllDescPrecioDiasTranslated(String lang) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public void remove(String codigo) throws RemoteException {
		DescPrecioDias entity = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			
			entity = (DescPrecioDias) session.get(DescPrecioDias.class, codigo);						
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

	public void update(DescPrecioDias descPrecioDias) throws RemoteException {
		DescPrecioDias dpd = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			if (StringUtils.isBlank(descPrecioDias.getCodigo())) {
				dpd = new DescPrecioDias();
				// TODO: asignar ID
				assignID(session,dpd);
				
				//a.setCodigo(null);
			} else {
				dpd = (DescPrecioDias) session.get(DescPrecioDias.class, descPrecioDias.getCodigo());
			}

			dpd.setCodigoServicio(descPrecioDias.getCodigoServicio());
			dpd.setTechoDias(descPrecioDias.getTechoDias());
			dpd.setPorcentaje(descPrecioDias.getPorcentaje());
			dpd.setActivo(descPrecioDias.getActivo());

			session.saveOrUpdate(dpd);

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
