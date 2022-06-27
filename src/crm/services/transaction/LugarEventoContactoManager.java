package crm.services.transaction;

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

import crm.libraries.abm.entities.LugarEventoContacto;
import crm.services.sei.LugarEventoContactoManagerSEI;
import crm.services.util.CollectionUtil;
import crm.services.util.HibernateUtil;

public class LugarEventoContactoManager implements LugarEventoContactoManagerSEI,ManagerService{
private static final Log log = LogFactory.getLog(LugarEventoContactoManager.class);
	
	public LugarEventoContacto getLugarEventoContactoById(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();

		Criteria c = session.createCriteria(LugarEventoContacto.class);
		c.add(Expression.eq("codigo", codigo));
		c.add(Expression.eq("activo", "S"));
		LugarEventoContacto a = (LugarEventoContacto) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		
		return a;
	}
	

	public LugarEventoContacto[] getAllLugarEventoContactos() throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(LugarEventoContacto.class);
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (LugarEventoContacto[])list.toArray(new LugarEventoContacto[0]);
	}

	public LugarEventoContacto[] findByField(String field,String value) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(LugarEventoContacto.class);
		c.add(Expression.like(field,"%" + value + "%"));
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (LugarEventoContacto[])list.toArray(new LugarEventoContacto[0]);
	}
	
	public LugarEventoContacto[] findByLugarAndField(String codLugar,String field,String value) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(LugarEventoContacto.class);
		c.add(Expression.eq("codLugar",codLugar));
		c.add(Expression.like(field,"%" + value + "%"));
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (LugarEventoContacto[])list.toArray(new LugarEventoContacto[0]);
	}
	
	public void remove(String codigo) throws RemoteException {
		LugarEventoContacto entity = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			
			entity = (LugarEventoContacto) session.get(LugarEventoContacto.class, codigo);						
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
	 * Modifica un LugarEventoContacto
	 */
	public String update(LugarEventoContacto lugarEventoContacto) throws RemoteException {
		LugarEventoContacto c = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			if (StringUtils.isBlank(lugarEventoContacto.getCodigo())) {
				c = new LugarEventoContacto();
				HibernateUtil.assignID(session,c);
				c.setCodLugar(lugarEventoContacto.getCodLugar());
				c.setCodigoContacto(getCodContacto(session,lugarEventoContacto.getCodLugar()));			
			} else {
				c = (LugarEventoContacto) session.get(LugarEventoContacto.class, lugarEventoContacto.getCodigo());
			}

			c.setTitulo(lugarEventoContacto.getTitulo());
			c.setApellidoYNombre(lugarEventoContacto.getApellidoYNombre());
			c.setDepartamento(lugarEventoContacto.getDepartamento());
			c.setCargo(lugarEventoContacto.getCargo());
			c.setTelefono1(lugarEventoContacto.getTelefono1());
			c.setTelefono2(lugarEventoContacto.getTelefono2());
			c.setFax(lugarEventoContacto.getFax());
			c.setInterno(lugarEventoContacto.getInterno());
			c.setNextelFota(lugarEventoContacto.getNextelFota());
			c.setNextelId(lugarEventoContacto.getNextelId());
			c.setEmail(lugarEventoContacto.getEmail());
			c.setFecha(lugarEventoContacto.getFecha());			
			c.setActivo(lugarEventoContacto.getActivo());			
		    
			session.saveOrUpdate(c);

			tx.commit();
			session.flush();
		} catch (HibernateException he) {
			if (tx != null && tx.isActive())
				tx.rollback();

			he.printStackTrace(System.err);
		} finally {
			HibernateUtil.cerrarSession(session);
		}
		return c.getCodigo();
	}

	
	private String getCodContacto(Session session,String codLugar) {
		String entityName = LugarEventoContacto.class.getSimpleName();
		
		String query = new String();
		
		query += "select max(codigoContacto) + 1 " ;
		query += "from "+ entityName + " ";
		query += "where codLugar = '" + codLugar + "' "  ;
		
		Object result = session.createQuery(query).uniqueResult();
		
		return result != null ? result.toString():"1";
	}
	
	public Object[] getLugarContactoByClienteCodeReport(String codLugar) throws RemoteException {
		Session session = HibernateUtil.abrirSession();

		if (log.isDebugEnabled())
			log.debug("Buscando contacto para cliente " + codLugar);
	
		List list = session.createQuery(
				"select codigo,apellidoYNombre " +
				"from LugarEventoContacto " +
				"where codLugar = :cod " +
				"order by apellidoYNombre"
				)
				.setString("cod",codLugar)
				.list();

		if (log.isDebugEnabled())
			log.debug("Se encontraron "+list.size()+" contactos");
		
		HibernateUtil.cerrarSession(session);
		
		return CollectionUtil.listToObjectArray(list);
	}
}
