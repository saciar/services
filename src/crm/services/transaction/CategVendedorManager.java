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
import crm.libraries.abm.entities.CategVendedor;
import crm.services.sei.CategVendedorManagerSEI;
import crm.services.util.HibernateUtil;

public class CategVendedorManager implements CategVendedorManagerSEI,ManagerService {

	public CategVendedor getCategVendedorById(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();

		Criteria c = session.createCriteria(CategVendedor.class);
		c.add(Expression.eq("codigo", codigo));
		c.add(Expression.eq("activo","S"));
		CategVendedor a = (CategVendedor) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return a;
	}

	public CategVendedor getCategVendedorByDescripcion(String descripcion) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(CategVendedor.class);
		c.add(Expression.eq("descripcion", descripcion));
		CategVendedor a = (CategVendedor) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return a;
	}
	
	public CategVendedor[] getAllCategVendedores() throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(CategVendedor.class);
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (CategVendedor[])list.toArray(new CategVendedor[0]);
	}

	public CategVendedor[] findByField(String field,String value){
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(CategVendedor.class);
		c.add(Expression.like(field,"%" + value + "%"));
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (CategVendedor[])list.toArray(new CategVendedor[0]);
	}
	
	public CategVendedor[] getAllCategVendedoresTranslated(String lang) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public void remove(String codigo) throws RemoteException {
		if(codigo.equals(CATEGORY_VENDEDOR) || 
		   codigo.equals(CATEGORY_SUPERVISOR) ||
	       codigo.equals(CATEGORY_GERENTE) ||
	       codigo.equals(CATEGORY_REFERENCIA) ||
	       codigo.equals(CATEGORY_LUGAR_EVENTO)){
				throw new RemoteException("No se permite borrar esta categoria");
	    } 
		
		CategVendedor entity = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			
			entity = (CategVendedor) session.get(CategVendedor.class, codigo);						
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

	public void update(CategVendedor categCategVendedor) throws RemoteException {
		if(categCategVendedor.getCodigo().equals(CATEGORY_VENDEDOR) || 
				categCategVendedor.getCodigo().equals(CATEGORY_SUPERVISOR) ||
				categCategVendedor.getCodigo().equals(CATEGORY_GERENTE) ||
				categCategVendedor.getCodigo().equals(CATEGORY_REFERENCIA) ||
				categCategVendedor.getCodigo().equals(CATEGORY_LUGAR_EVENTO)){
						throw new RemoteException("No se permite editar esta categoria");
			    } 		
		CategVendedor v = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			if (StringUtils.isBlank(categCategVendedor.getCodigo())) {
				v = new CategVendedor();
				// TODO: asignar ID
				assignID(session,v);
				
				//a.setCodigo(null);
			} else {
				v = (CategVendedor) session.get(CategVendedor.class, categCategVendedor.getCodigo());
			}

			v.setDescripcion(categCategVendedor.getDescripcion());
			v.setActivo(categCategVendedor.getActivo());
		    		
			session.saveOrUpdate(v);

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
