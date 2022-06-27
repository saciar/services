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
import crm.libraries.abm.entities.ModalidadContrat;
import crm.services.sei.ModalidadContratManagerSEI;
import crm.services.util.HibernateUtil;

public class ModalidadContratManager implements ModalidadContratManagerSEI,ManagerService {

	public ModalidadContrat getModalidadContratById(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();

		Criteria c = session.createCriteria(ModalidadContrat.class);
		c.add(Expression.eq("codigo", codigo));
		c.add(Expression.eq("activo","S"));
		ModalidadContrat a = (ModalidadContrat) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return a;
	}

	public ModalidadContrat getModalidadContratByDescripcion(String descripcion) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(ModalidadContrat.class);
		c.add(Expression.eq("descripcion", descripcion));
		ModalidadContrat mc = (ModalidadContrat) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return mc;
	}
	
	public ModalidadContrat[] getAllModalidadContrats() throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(ModalidadContrat.class);
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (ModalidadContrat[])list.toArray(new ModalidadContrat[0]);
	}

	public ModalidadContrat[] getAllModalidadContratsTranslated(String lang) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public ModalidadContrat[] findByField(String field,String value){
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(ModalidadContrat.class);
		c.add(Expression.like(field,"%" + value + "%"));
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (ModalidadContrat[])list.toArray(new ModalidadContrat[0]);
	}
	
	public void remove(String codigo) throws RemoteException {
		if(codigo.equals(MODALIDAD_INTERNO) || codigo.equals(MODALIDAD_EXTERNO)){
			throw new RemoteException("No se permite borrar esta modalidad");
		}		
		ModalidadContrat entity = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			
			entity = (ModalidadContrat) session.get(ModalidadContrat.class, codigo);						
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

	public void update(ModalidadContrat modalidadContrat) throws RemoteException {
		if(modalidadContrat.getCodigo().equals(MODALIDAD_INTERNO) || modalidadContrat.getCodigo().equals(MODALIDAD_EXTERNO)){
			throw new RemoteException("No se permite editar esta modalidad");
		}			
		ModalidadContrat m = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			if (StringUtils.isBlank(modalidadContrat.getCodigo())) {
				m = new ModalidadContrat();
				// TODO: asignar ID
				assignID(session,m);
				
				//a.setCodigo(null);
			} else {
				m = (ModalidadContrat) session.get(ModalidadContrat.class, modalidadContrat.getCodigo());
			}

			m.setDescripcion(modalidadContrat.getDescripcion());
			m.setActivo(modalidadContrat.getActivo());
		    
		    
			// if (groupId == 0){
			// session.save(group);
			// }
			session.saveOrUpdate(m);

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
