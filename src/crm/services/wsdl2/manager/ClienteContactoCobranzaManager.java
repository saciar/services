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

import crm.libraries.abm.entities.ClienteContactoCobranza;
import crm.services.transaction.ManagerService;
import crm.services.util.CollectionUtil;
import crm.services.util.HibernateUtil;
import crm.services.wsdl2.sei.ClienteContactoCobranzaManagerSEI;

public class ClienteContactoCobranzaManager implements ClienteContactoCobranzaManagerSEI,WSDL2Service{
private static final Log log = LogFactory.getLog(ClienteContactoCobranzaManager.class);
	
	public ClienteContactoCobranza getClienteContactoById(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();

		Criteria c = session.createCriteria(ClienteContactoCobranza.class);
		c.add(Expression.eq("codigo", codigo));
		c.add(Expression.eq("activo", 'S'));
		ClienteContactoCobranza a = (ClienteContactoCobranza) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		
		return a;
	}
	
	public Object[] getClienteContactoByClienteCodeReport(String codCliente) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
	
		List list = session.createQuery(
				"select codigo,apellidoYNombre " +
				"from ClienteContactoCobranza " +
				"where codigoCliente = :cliente and activo = 'S' " +
				"order by apellidoYNombre"
				)
				.setString("cliente",codCliente)
				.list();
		
		HibernateUtil.cerrarSession(session);
		
		return CollectionUtil.listToObjectArray(list);
	}
	
	public ClienteContactoCobranza[] getAllClienteContactos() throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(ClienteContactoCobranza.class);
		c.add(Expression.eq("activo",'S'));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (ClienteContactoCobranza[])list.toArray(new ClienteContactoCobranza[0]);
	}

	public ClienteContactoCobranza[] findByField(String field,String value) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(ClienteContactoCobranza.class);
		c.add(Expression.like(field,"%" + value + "%"));
		c.add(Expression.eq("activo",'S'));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (ClienteContactoCobranza[])list.toArray(new ClienteContactoCobranza[0]);
	}
	
	public ClienteContactoCobranza[] findByClientAndField(String client,String field,String value) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(ClienteContactoCobranza.class);
		c.add(Expression.eq("codigoCliente",client));
		c.add(Expression.like(field,"%" + value + "%"));
		c.add(Expression.eq("activo",'S'));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (ClienteContactoCobranza[])list.toArray(new ClienteContactoCobranza[0]);
	}
	
	public void remove(String codigo) throws RemoteException {
		ClienteContactoCobranza entity = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			
			entity = (ClienteContactoCobranza) session.get(ClienteContactoCobranza.class, codigo);						
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
	public String update(ClienteContactoCobranza clienteContacto) throws RemoteException {
		ClienteContactoCobranza c = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			if (StringUtils.isBlank(clienteContacto.getCodigo())) {
				c = new ClienteContactoCobranza();
				HibernateUtil.assignID(session,c);
				c.setCodigoCliente(clienteContacto.getCodigoCliente());
				c.setCodigoContacto(getCodContacto(session,clienteContacto.getCodigoCliente()));			
			} else {
				c = (ClienteContactoCobranza) session.get(ClienteContactoCobranza.class, clienteContacto.getCodigo());
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

	public ClienteContactoCobranza[] getAllClienteContactosTranslated(String lang) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public int getCantidadClienteContactos() throws RemoteException {
		int q = 0;
		
		Session session = HibernateUtil.abrirSession();
		
		q = ((Integer)
				session.createQuery(
		        "select count(*) " +
		        "from ClienteContactoCobranza c " +
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

		List list = session.createQuery(
		        "select c.codigo, c.apellidoYNombre " +
		        "from ClienteContacto c " +
		        "where c.activo = 'S' " + 
		        "order by c.apellidoYNombre"
		        )
		        .setFirstResult(firstResult)
		        .setMaxResults(maxResults)		        
		        .list();

		Object[] results = new Object[list.size()];
		for (int i=0; i< results.length;i++){ 
		    results[i] = (Object[]) list.get(i);
		}

		HibernateUtil.cerrarSession(session);

		return results;
	}
	

	private String getCodContacto(Session session,String codigoCliente) {
		String entityName = ClienteContactoCobranza.class.getSimpleName();
		
		String query = new String();
		
		query += "select max(codigoContacto) + 1 " ;
		query += "from "+ entityName + " ";
		query += "where codigoCliente = '" + codigoCliente + "' "  ;
		
		Object result = session.createQuery(query).uniqueResult();
		
		return result != null ? result.toString():"1";
	}

}
