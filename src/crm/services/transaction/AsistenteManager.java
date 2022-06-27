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
import crm.libraries.abm.entities.Asistente;
import crm.services.sei.AsistenteManagerSEI;
import crm.services.util.HibernateUtil;

public class AsistenteManager  implements AsistenteManagerSEI,ManagerService {

	public Asistente getAsistenteById(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();

		Criteria c = session.createCriteria(Asistente.class);
		c.add(Expression.eq("codigo", codigo));
		c.add(Expression.eq("activo","S"));
		Asistente a = (Asistente) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return a;
	}
	
	public Asistente getAsistenteByApYNom(String apellidoYNombre) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(Asistente.class);
		c.add(Expression.eq("apellidoYNombre", apellidoYNombre));
		Asistente a = (Asistente) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return a;
	}

	public Asistente[] getAllAsistentes() throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(Asistente.class);
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (Asistente[])list.toArray(new Asistente[0]);
	}

	public Asistente[] getAllAsistentesTranslated(String lang) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public Asistente[] findByField(String field,String value){
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(Asistente.class);
		c.add(Expression.like(field,"%" + value + "%"));
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (Asistente[])list.toArray(new Asistente[0]);
	}
	
	public String getDescrpcion(String codigo)throws RemoteException {
		String className = Asistente.class.getSimpleName();	
		Session session = HibernateUtil.abrirSession();
		String query = "select apellidoYNombre from " + className + " where codigo = :codigo "; 		
		Object result = session.createQuery(query).setString("codigo",codigo).uniqueResult();		
		HibernateUtil.cerrarSession(session);
		return ((result == null)?null:result.toString());
	}
	
	public void remove(String codigo) throws RemoteException {
		Asistente entity = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			
			entity = (Asistente) session.get(Asistente.class, codigo);						
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

	public void update(Asistente perfil) throws RemoteException {
		Asistente p = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			if (StringUtils.isBlank(perfil.getCodigo())) {
				p = new Asistente();
				// TODO: asignar ID
				assignID(session,p);
				
				//a.setCodigo(null);
			} else {
				p = (Asistente) session.get(Asistente.class, perfil.getCodigo());
			}

			p.setApellidoYNombre(perfil.getApellidoYNombre());
			p.setModalidad(perfil.getModalidad());
			p.setActivo(perfil.getActivo());
		    
		    
		
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
