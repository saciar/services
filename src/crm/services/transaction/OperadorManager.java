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
import crm.libraries.abm.entities.Operador;
import crm.services.sei.OperadorManagerSEI;
import crm.services.util.HibernateUtil;

public class OperadorManager implements OperadorManagerSEI,ManagerService {

	public Operador getOperadorById(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();

		Criteria c = session.createCriteria(Operador.class);
		c.add(Expression.eq("codigo", codigo));
		c.add(Expression.eq("activo","S"));
		Operador a = (Operador) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return a;
	}
	
	public Operador getOperadorByApYNom(String apellidoYNombre) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(Operador.class);
		c.add(Expression.eq("apellidoYNombre", apellidoYNombre));
		Operador a = (Operador) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return a;
	}
	
	public Operador[] getOperadorByModalidad(String modalidad) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(Operador.class);
		c.add(Expression.eq("modalidad",modalidad));
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (Operador[])list.toArray(new Operador[0]);
	}

	public Operador[] getAllOperadores() throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(Operador.class);
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (Operador[])list.toArray(new Operador[0]);
	}

	public Operador[] findByField(String field,String value){
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(Operador.class);
		c.add(Expression.like(field,"%" + value + "%"));
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (Operador[])list.toArray(new Operador[0]);
	}
	
	public String getDescrpcion(String codigo)throws RemoteException {
		String className = Operador.class.getSimpleName();	
		Session session = HibernateUtil.abrirSession();
		String query = "select apellidoYNombre from " + className + " where codigo = :codigo "; 		
		Object result = session.createQuery(query).setString("codigo",codigo).uniqueResult();				
		HibernateUtil.cerrarSession(session);
		return ((result == null)?null:result.toString());
	}
	
	public Operador[] getAllOperadoresTranslated(String lang) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public void remove(String codigo) throws RemoteException {
		Operador entity = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			
			entity = (Operador) session.get(Operador.class, codigo);						
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

	public void update(Operador operador) throws RemoteException {
		Operador o = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			if (StringUtils.isBlank(operador.getCodigo())) {
				o = new Operador();
				// TODO: asignar ID
				assignID(session,o);
				
				//a.setCodigo(null);
			} else {
				o = (Operador) session.get(Operador.class, operador.getCodigo());
			}

			o.setApellidoYNombre(operador.getApellidoYNombre());
			o.setModalidad(operador.getModalidad());
			o.setActivo(operador.getActivo());
		    		
			session.saveOrUpdate(o);

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
