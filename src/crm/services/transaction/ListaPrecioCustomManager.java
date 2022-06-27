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
import crm.libraries.abm.entities.ListaPrecioCustom;
import crm.services.sei.ListaPrecioCustomManagerSEI;
import crm.services.util.HibernateUtil;

public class ListaPrecioCustomManager implements ListaPrecioCustomManagerSEI,ManagerService {

	public ListaPrecioCustom getListaPrecioCustomById(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		// Servicio s = (Servicio)session.get(Servicio.class,codigo);
		Criteria c = session.createCriteria(ListaPrecioCustom.class);
		c.add(Expression.eq("codigo", codigo));
		c.add(Expression.eq("activo","S"));
		ListaPrecioCustom a = (ListaPrecioCustom) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return a;
	}

	public ListaPrecioCustom getListaPrecioCustomByCodLugar(String codigoLugar) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(ListaPrecioCustom.class);
		c.add(Expression.eq("codigoLugar", codigoLugar));
		ListaPrecioCustom a = (ListaPrecioCustom) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return a;
	}
	
	public ListaPrecioCustom[] getAllListaPrecioCustoms() throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(ListaPrecioCustom.class);
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (ListaPrecioCustom[])list.toArray(new ListaPrecioCustom[0]);
	}

	public ListaPrecioCustom[] findByField(String field,String value){
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(ListaPrecioCustom.class);
		c.add(Expression.like(field,"%" + value + "%"));
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (ListaPrecioCustom[])list.toArray(new ListaPrecioCustom[0]);
	}
	
	public ListaPrecioCustom[] getAllListaPrecioCustomsTranslated(String lang) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public void remove(String codigo) throws RemoteException {
		ListaPrecioCustom entity = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			
			entity = (ListaPrecioCustom) session.get(ListaPrecioCustom.class, codigo);						
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

	public void update(ListaPrecioCustom listaPrecioCustom) throws RemoteException {
		ListaPrecioCustom a = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			if (StringUtils.isBlank(listaPrecioCustom.getCodigo())) {
				a = new ListaPrecioCustom();
				assignID(session,a);				
			} else {
				a = (ListaPrecioCustom) session.get(ListaPrecioCustom.class, listaPrecioCustom.getCodigo());
			}

			a.setDescripcion(listaPrecioCustom.getDescripcion());
			a.setCodigoLugar(listaPrecioCustom.getCodigoLugar());
			a.setVariacion(listaPrecioCustom.getVariacion());
			a.setActivo(listaPrecioCustom.getActivo());
		    
			session.saveOrUpdate(a);

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
