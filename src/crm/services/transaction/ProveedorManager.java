package crm.services.transaction;

import java.rmi.RemoteException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;

import crm.libraries.abm.entities.ABMEntity;
import crm.libraries.abm.entities.Proveedor;
import crm.services.sei.ProveedorManagerSEI;
import crm.services.util.HibernateUtil;

public class ProveedorManager implements ProveedorManagerSEI,ManagerService {

	public Proveedor getProveedorById(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();

		Criteria c = session.createCriteria(Proveedor.class);
		c.add(Expression.eq("codigo", codigo));
		c.add(Expression.eq("activo","S"));
		Proveedor a = (Proveedor) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		System.out.println("Servicio encontrad: " + a.getCodigo());
		return a;
	}
	
	public Proveedor getProveedorByNombre(String nombre) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(Proveedor.class);
		c.add(Expression.eq("nombre", nombre));
		Proveedor a = (Proveedor) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return a;
	}
	

	public Proveedor[] getAllProveedores() throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(Proveedor.class);
		c.add(Expression.eq("activo","S"));
		c.addOrder(Order.asc("nombre"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (Proveedor[])list.toArray(new Proveedor[0]);
	}

	public Proveedor[] getAllProveedoresTranslated(String lang) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public Proveedor[] findByField(String field,String value){
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(Proveedor.class);
		c.add(Expression.like(field,"%" + value + "%"));
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (Proveedor[])list.toArray(new Proveedor[0]);
	}
	
	public String getDescrpcion(String codigo)throws RemoteException {
		String className = Proveedor.class.getSimpleName();	
		Session session = HibernateUtil.abrirSession();
		String query = "select nombre from " + className + " where codigo = :codigo "; 		
		Object result = session.createQuery(query).setString("codigo",codigo).uniqueResult();
		HibernateUtil.cerrarSession(session);
		
		return ((result == null)?null:result.toString());
	}
	
	public void remove(String codigo) throws RemoteException {
		Proveedor entity = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			
			entity = (Proveedor) session.get(Proveedor.class, codigo);						
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

	public void update(Proveedor proveedor) throws RemoteException {
		Proveedor p = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			if (StringUtils.isBlank(proveedor.getCodigo())) {
				p = new Proveedor();
				// TODO: asignar ID
				assignID(session,p);
				
				//a.setCodigo(null);
			} else {
				p = (Proveedor) session.get(Proveedor.class, proveedor.getCodigo());
			}

			p.setNombre(proveedor.getNombre());
			p.setContacto(proveedor.getContacto());
			p.setTelefono1(proveedor.getTelefono1());
			p.setTelefono2(proveedor.getTelefono2());
			p.setFax(proveedor.getFax());
			p.setFlotaNextel(proveedor.getFlotaNextel());
			p.setIdNextel(proveedor.getIdNextel());
			p.setEmail(proveedor.getEmail());			
			p.setCalle(proveedor.getCalle());
			p.setNumero(proveedor.getNumero());
			p.setPiso(proveedor.getPiso());
			p.setDepartamento(proveedor.getDepartamento());
			p.setCodigoPostal(proveedor.getCodigoPostal());
			p.setLocalidad(proveedor.getLocalidad());
			p.setPartido(proveedor.getPartido());
			p.setProvincia(proveedor.getProvincia());
			p.setPais(proveedor.getPais());
			p.setActivo(proveedor.getActivo());						
			
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
