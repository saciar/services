package crm.services.transaction;

import java.rmi.RemoteException;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import crm.libraries.abm.entities.ABMEntity;
import crm.libraries.abm.entities.ClienteFacturacion;
import crm.services.sei.ClienteFacturacionManagerSEI;
import crm.services.util.HibernateUtil;

public class ClienteFacturacionManager implements ClienteFacturacionManagerSEI,ManagerService {

	public ClienteFacturacion getClienteFacturacionById(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();

		Criteria c = session.createCriteria(ClienteFacturacion.class);
		c.add(Expression.eq("codigo", codigo));
		ClienteFacturacion a = (ClienteFacturacion) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return a;
	}

	public ClienteFacturacion[] getAllClienteFacturaciones() throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(ClienteFacturacion.class);
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (ClienteFacturacion[])list.toArray(new ClienteFacturacion[0]);
	}

	public ClienteFacturacion[] findByField(String field,String value){
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(ClienteFacturacion.class);
		c.add(Expression.like(field,"%" + value + "%"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (ClienteFacturacion[])list.toArray(new ClienteFacturacion[0]);
	}
	
	public ClienteFacturacion[] getAllClienteFacturacionesTranslated(String lang) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public void remove(String codigo) throws RemoteException {
		ClienteFacturacion entity = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			
			entity = (ClienteFacturacion) session.get(ClienteFacturacion.class, codigo);						
		    
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
	
	public void update(ClienteFacturacion clienteFacturacion) throws RemoteException {
		ClienteFacturacion cf = new ClienteFacturacion();
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();
			
			cf.setCodigo(clienteFacturacion.getCodigo());						
			cf.setCalle(clienteFacturacion.getCalle());
			cf.setNumero(clienteFacturacion.getNumero());
			cf.setPiso(clienteFacturacion.getPiso());
			cf.setDepto(clienteFacturacion.getDepto());
			cf.setCodigoPostal(clienteFacturacion.getCodigoPostal());
			cf.setLocalidad(clienteFacturacion.getLocalidad());
			cf.setPartido(clienteFacturacion.getPartido());
			cf.setProvincia(clienteFacturacion.getProvincia());
			cf.setPais(clienteFacturacion.getPais());
			cf.setDiaHoraPago(clienteFacturacion.getDiaHoraPago());
			cf.setDomicilioPago(clienteFacturacion.getDomicilioPago());
			cf.setCodProveedor(clienteFacturacion.getCodProveedor());						
			
			session.saveOrUpdate(cf);

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
