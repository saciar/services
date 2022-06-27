package crm.services.wsdl2.manager;

import java.rmi.RemoteException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import crm.libraries.abm.entities.ABMEntity;
import crm.libraries.abm.entities.TipoFactura;
import crm.services.transaction.ManagerService;
import crm.services.util.CollectionUtil;
import crm.services.util.HibernateUtil;
import crm.services.wsdl2.sei.TipoFacturaManagerSEI;

public class TipoFacturaManager implements TipoFacturaManagerSEI,WSDL2Service {

	public TipoFactura getTipoFacturaById(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();

		Criteria c = session.createCriteria(TipoFactura.class);
		c.add(Expression.eq("codigo", codigo));
		c.add(Expression.eq("activo","S"));
		TipoFactura a = (TipoFactura) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		System.out.println("Servicio encontrad: " + a.getCodigo());
		return a;
	}
	
	public TipoFactura getTipoFacturaByDescripcion(String descripcion) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(TipoFactura.class);
		c.add(Expression.eq("descripcion", descripcion));
		TipoFactura a = (TipoFactura) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return a;
	}

	public TipoFactura[] getAllTipoFacturas() throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(TipoFactura.class);
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (TipoFactura[])list.toArray(new TipoFactura[0]);
	}

	public TipoFactura[] getAllTipoFacturasTranslated(String lang) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public TipoFactura[] findByField(String field,String value){
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(TipoFactura.class);
		c.add(Expression.like(field,"%" + value + "%"));
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (TipoFactura[])list.toArray(new TipoFactura[0]);
	}
	
	public void remove(String codigo) throws RemoteException {
		TipoFactura entity = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			
			entity = (TipoFactura) session.get(TipoFactura.class, codigo);						
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

	public void update(TipoFactura SubServicio) throws RemoteException {
		TipoFactura te = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			if (StringUtils.isBlank(SubServicio.getCodigo())) {
				te = new TipoFactura();
				assignID(session,te);
			} else {
				te = (TipoFactura) session.get(TipoFactura.class, SubServicio.getCodigo());
			}

			te.setDescripcion(SubServicio.getDescripcion());
			te.setActivo(SubServicio.getActivo());
		    
			session.saveOrUpdate(te);

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

	public Object[] getTipoFacturasReport() throws RemoteException {

		Session session = HibernateUtil.abrirSession();

		List list = session.createQuery("select codigo,descripcion from TipoFactura order by descripcion").list();

		HibernateUtil.cerrarSession(session);

		return CollectionUtil.listToObjectArray(list);
	}

}
