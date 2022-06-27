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
import crm.libraries.abm.entities.UnidadNegocio;
import crm.services.sei.UnidadNegocioManagerSEI;
import crm.services.util.HibernateUtil;

public class UnidadNegocioManager implements UnidadNegocioManagerSEI,ManagerService {

	public UnidadNegocio getUnidadNegocioById(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();

		Criteria c = session.createCriteria(UnidadNegocio.class);
		c.add(Expression.eq("codigo", codigo));
		c.add(Expression.eq("activo","S"));
		UnidadNegocio a = (UnidadNegocio) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		System.out.println("Servicio encontrad: " + a.getCodigo());
		return a;
	}

	public UnidadNegocio getUnidadNegocioByDescripcion(String descripcion) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(UnidadNegocio.class);
		c.add(Expression.eq("descripcion", descripcion));
		UnidadNegocio a = (UnidadNegocio) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return a;
	}
	
	public UnidadNegocio[] getAllUnidadNegocios() throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(UnidadNegocio.class);
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (UnidadNegocio[])list.toArray(new UnidadNegocio[0]);
	}

	public UnidadNegocio[] getAllUnidadNegociosTranslated(String lang) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}
	
	public UnidadNegocio[] findByField(String field,String value){
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(UnidadNegocio.class);
		c.add(Expression.like(field,"%" + value + "%"));
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (UnidadNegocio[])list.toArray(new UnidadNegocio[0]);
	}
	
	public void remove(String codigo) throws RemoteException {
		UnidadNegocio entity = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			
			entity = (UnidadNegocio) session.get(UnidadNegocio.class, codigo);						
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

	public void update(UnidadNegocio unidadNegocio) throws RemoteException {
		UnidadNegocio un = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			if (StringUtils.isBlank(unidadNegocio.getCodigo())) {
				un = new UnidadNegocio();
				// TODO: asignar ID
				assignID(session,un);
				
				//a.setCodigo(null);
			} else {
				un = (UnidadNegocio) session.get(UnidadNegocio.class, unidadNegocio.getCodigo());
			}

			un.setDescripcion(unidadNegocio.getDescripcion());
			un.setActivo(unidadNegocio.getActivo());
		    
		    
			// if (groupId == 0){
			// session.save(group);
			// }
			session.saveOrUpdate(un);

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
