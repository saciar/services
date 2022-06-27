package crm.services.transaction;

import java.rmi.RemoteException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import crm.libraries.abm.entities.NuevoCliente;
import crm.services.sei.NuevoClienteManagerSEI;
import crm.services.util.CollectionUtil;
import crm.services.util.HibernateUtil;

public class NuevoClienteManager implements NuevoClienteManagerSEI,ManagerService{
	
	public NuevoCliente getNuevoClienteById(String codigoCliente) throws RemoteException {
		Session session = HibernateUtil.abrirSession();

		Criteria c = session.createCriteria(NuevoCliente.class);
		c.add(Expression.eq("codigo", codigoCliente));
		c.add(Expression.eq("nuevo", "S"));
		NuevoCliente a = (NuevoCliente) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		
		return a;
	}
	
	public Object[] getNuevoClienteByClienteCodeReport(String codCliente) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
	
		List list = session.createQuery(
				"select fechaAlta, nuevo " +
				"from NuevoCliente " +
				"where codigo = :cliente " +
				"order by fechaAlta"
				)
				.setString("cliente",codCliente)
				.list();
		
		HibernateUtil.cerrarSession(session);
		
		return CollectionUtil.listToObjectArray(list);
	}
	
	private static final Log log = LogFactory.getLog(NuevoClienteManager.class);
	
	public String update(NuevoCliente nuevoCliente) throws RemoteException {
		//NuevoCliente c = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			/*if (StringUtils.isBlank(nuevoCliente.getCodigo())) {
				c = new NuevoCliente();
				HibernateUtil.assignID(session,c);
			} else {
				c = (NuevoCliente) session.get(NuevoCliente.class, nuevoCliente.getCodigo());
			}			
		    
			c.setFechaAlta(nuevoCliente.getFechaAlta());
			c.setNuevo(nuevoCliente.getNuevo());
			session.saveOrUpdate(c);*/
			
			session.saveOrUpdate(nuevoCliente);
			
			tx.commit();
			session.flush();
		} catch (HibernateException he) {
			if (tx != null && tx.isActive())
				tx.rollback();
			if (log.isDebugEnabled())
				log.debug("CLIENTE FECHA---------------------------:"+nuevoCliente.getFechaAlta());
			he.printStackTrace(System.err);
			// throw new SystemException(he);
		} finally {
			HibernateUtil.cerrarSession(session);
		}
		return "";
	}
	
	public boolean isNuevoContacto(String codCliente) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		
		String res = (String)session.createQuery
		("select nuevo from NuevoCliente where codigo = :cod")
		.setString("cod", codCliente)
		.uniqueResult();
		
		HibernateUtil.cerrarSession(session);
		/*TODO: esta validacion scarla cuando se migren datos*/
		if(res != null)
			return res.equals("S");
		return true;
	}
}
