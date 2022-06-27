package crm.services.wsdl2.manager;

import java.rmi.RemoteException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import crm.libraries.abm.entities.Rack;
import crm.services.transaction.ManagerService;
import crm.services.util.CollectionUtil;
import crm.services.util.HibernateUtil;
import crm.services.wsdl2.sei.RacksManagerSEI;

public class RacksManager implements RacksManagerSEI,WSDL2Service{
	
	public Rack getClienteContactoById(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();

		Criteria c = session.createCriteria(Rack.class);
		c.add(Expression.eq("codigo", codigo));
		c.add(Expression.eq("activo", 'S'));
		Rack a = (Rack) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		
		return a;
	}

	public Rack[] findByField(String field,String value) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(Rack.class);
		c.add(Expression.like(field,"%" + value + "%"));
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (Rack[])list.toArray(new Rack[0]);
	}
	
	/*public ClienteContacto[] findByClientAndField(String client,String field,String value) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(ClienteContacto.class);
		c.add(Expression.eq("codigoCliente",client));
		c.add(Expression.like(field,"%" + value + "%"));
		c.add(Expression.eq("activo",'S'));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (ClienteContacto[])list.toArray(new ClienteContacto[0]);
	}*/
	
	public void remove(String codigo) throws RemoteException {
		Rack entity = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			
			entity = (Rack) session.get(Rack.class, codigo);						
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

	/**
	 * Modifica un ClienteContacto
	 */
	public String update(Rack rack) throws RemoteException {
		Rack c = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			if (StringUtils.isBlank(rack.getCodigo())) {
				c = new Rack();
				HibernateUtil.assignID(session,c);
				
			} else {
				c = (Rack) session.get(Rack.class, rack.getCodigo());
			}
			c.setCodRack(rack.getCodRack());
			c.setCodEquipamiento(rack.getCodEquipamiento());
			c.setActivo(rack.getActivo());			
		    
			session.saveOrUpdate(c);

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
		return c.getCodigo();
	}
	
	public void removeRack(int codigo) throws RemoteException{
		Rack c = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			c = (Rack) session.get(Rack.class, String.valueOf(codigo));			
		    
			session.delete(c);

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

}
