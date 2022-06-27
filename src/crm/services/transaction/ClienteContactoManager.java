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

import crm.libraries.abm.entities.ClienteContacto;
import crm.services.sei.ClienteContactoManagerSEI;
import crm.services.util.CollectionUtil;
import crm.services.util.HibernateUtil;

public class ClienteContactoManager implements ClienteContactoManagerSEI,ManagerService{
private static final Log log = LogFactory.getLog(ClienteContactoManager.class);
	
	public ClienteContacto getClienteContactoById(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();

		Criteria c = session.createCriteria(ClienteContacto.class);
		c.add(Expression.eq("codigo", codigo));
		c.add(Expression.eq("activo", 'S'));
		ClienteContacto a = (ClienteContacto) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		
		return a;
	}
	
	public Object[] getClienteContactoByClienteCodeReport(String codCliente) throws RemoteException {
		Session session = HibernateUtil.abrirSession();

		if (log.isDebugEnabled())
			log.debug("Buscando contacto para cliente " + codCliente);
	
		List list = session.createQuery(
				"select codigo,apellidoYNombre " +
				"from ClienteContacto " +
				"where codigoCliente = :cliente and activo = 'S' " +
				"order by apellidoYNombre"
				)
				.setString("cliente",codCliente)
				.list();

		if (log.isDebugEnabled())
			log.debug("Se encontraron "+list.size()+" contactos");
		
		HibernateUtil.cerrarSession(session);
		
		return CollectionUtil.listToObjectArray(list);
	}
	
	public ClienteContacto[] getAllClienteContactos() throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(ClienteContacto.class);
		c.add(Expression.eq("activo",'S'));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (ClienteContacto[])list.toArray(new ClienteContacto[0]);
	}

	public ClienteContacto[] findByField(String field,String value) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(ClienteContacto.class);
		c.add(Expression.like(field,"%" + value + "%"));
		c.add(Expression.eq("activo",'S'));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (ClienteContacto[])list.toArray(new ClienteContacto[0]);
	}
	
	public ClienteContacto[] findByClientAndField(String client,String field,String value) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(ClienteContacto.class);
		c.add(Expression.eq("codigoCliente",client));
		c.add(Expression.like(field,"%" + value + "%"));
		c.add(Expression.eq("activo",'S'));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (ClienteContacto[])list.toArray(new ClienteContacto[0]);
	}
	
	public void remove(String codigo) throws RemoteException {
		ClienteContacto entity = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			
			entity = (ClienteContacto) session.get(ClienteContacto.class, codigo);						
			entity.setActivo('N');
		    
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
	public String update(ClienteContacto clienteContacto) throws RemoteException {
		ClienteContacto c = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			if (StringUtils.isBlank(clienteContacto.getCodigo())) {
				c = new ClienteContacto();
				HibernateUtil.assignID(session,c);
				c.setCodigoCliente(clienteContacto.getCodigoCliente());
				c.setCodigoContacto(getCodContacto(session,clienteContacto.getCodigoCliente()));			
			} else {
				c = (ClienteContacto) session.get(ClienteContacto.class, clienteContacto.getCodigo());
			}

			
			
			c.setTitulo(clienteContacto.getTitulo());
			c.setApellidoYNombre(clienteContacto.getApellidoYNombre());
			c.setDepartamento(clienteContacto.getDepartamento());
			c.setCargo(clienteContacto.getCargo());
			c.setTelefono1(clienteContacto.getTelefono1());
			c.setTelefono2(clienteContacto.getTelefono2());
			c.setFax(clienteContacto.getFax());
			c.setInterno(clienteContacto.getInterno());
			c.setFlotaNextel(clienteContacto.getFlotaNextel());
			c.setIdNextel(clienteContacto.getIdNextel());
			c.setEmail(clienteContacto.getEmail());
			c.setFechaUltimoContacto(clienteContacto.getFechaUltimoContacto());
			c.setActivo(clienteContacto.getActivo());			
		    
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

	public ClienteContacto[] getAllClienteContactosTranslated(String lang) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public int getCantidadClienteContactos() throws RemoteException {
		int q = 0;
		
		Session session = HibernateUtil.abrirSession();
		
		q = ((Integer)
				session.createQuery(
		        "select count(*) " +
		        "from ClienteContacto c " +
		        "where c.activo = 'S'"
		        )
		        .uniqueResult())
		        .intValue();
		
		HibernateUtil.cerrarSession(session);
		
		return q;
	}
	
	/**
	 * Trae los nombres de todos los ClienteContactos de la base de datos
	 */
	public Object[] getClienteContactosReport() throws RemoteException {
		return getClienteContactosReportLimited(0,0);
	}
	
	public Object[] getClienteContactosReportLimited(int firstResult, int maxResults) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		//Criteria c = session.createCriteria(ClienteContacto.class);
		//List list = c.list();
		long time = 0;
		if (log.isDebugEnabled()){
			time = System.currentTimeMillis();
			log.debug("buscando lista de ClienteContactos");
		}
		
		// TODO: no se porque pero esto tira out of memory.. investigar,
		// ya que esta opcion deberia ser mas rapida que la que esta funcionando..
		/*List list = session.createQuery(
				"select new ClienteContacto( c.codigo, c.empresa ) " +
				"from ClienteContacto c " +
				"order by c.empresa"
				)
				.list();*/
		List list = session.createQuery(
		        "select c.codigo, c.apellidoYNombre " +
		        "from ClienteContacto c " +
		        "where c.activo = 'S' " + 
		        "order by c.apellidoYNombre"
		        )
		        .setFirstResult(firstResult)
		        .setMaxResults(maxResults)		        
		        .list();

		if (log.isDebugEnabled()){
			time = System.currentTimeMillis()-time;
			log.debug("resultados obtenidos en " + time + "ms. "+list.size()+" ClienteContactos encontrados.");
			time = System.currentTimeMillis();
		}
		
		/*
		ClienteContacto[] results = new ClienteContacto[list.size()];
		for (int i=0; i< results.length;i++){ 
		    Object[] row = (Object[]) list.get(i);
		    results[i] = new ClienteContacto((String) row[0],(String) row[1]);
		}*/
		
		Object[] results = new Object[list.size()];
		for (int i=0; i< results.length;i++){ 
		    results[i] = (Object[]) list.get(i);
		}

		HibernateUtil.cerrarSession(session);
		
		if (log.isDebugEnabled()){
			time = System.currentTimeMillis()-time;
			log.debug("Resultados procesados en "+time+"ms. Enviando al ClienteContacto...");
		}
		
		return results;
		//return (ClienteContacto[])list.toArray(new ClienteContacto[0]);
	}
	

	private String getCodContacto(Session session,String codigoCliente) {
		String entityName = ClienteContacto.class.getSimpleName();
		
		String query = new String();
		
		query += "select max(codigoContacto) + 1 " ;
		query += "from "+ entityName + " ";
		query += "where codigoCliente = '" + codigoCliente + "' "  ;
		
		Object result = session.createQuery(query).uniqueResult();
		
		return result != null ? result.toString():"1";
	}
	
	private static ClienteContactoManager instance;
	
	public static synchronized ClienteContactoManager instance() {

			if (instance == null) 
				instance = new ClienteContactoManager();

		return instance;
	}
}
