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
import crm.libraries.abm.entities.Localidad;
import crm.services.sei.LocalidadManagerSEI;
import crm.services.util.HibernateUtil;

public class LocalidadManager implements LocalidadManagerSEI,ManagerService {

	public Localidad getLocalidadById(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();

		Criteria c = session.createCriteria(Localidad.class);
		c.add(Expression.eq("codigo", codigo));
		c.add(Expression.eq("activo","S"));
		Localidad a = (Localidad) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return a;
	}

	public Localidad getLocalidadByCodLocalidad(String codigoLocalidad) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(Localidad.class);
		c.add(Expression.eq("codigoLocalidad", codigoLocalidad));
		Localidad a = (Localidad) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return a;
	}
	
	public Localidad getLocalidadByDescripcion(String descripcion) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(Localidad.class);
		c.add(Expression.eq("descripcion", descripcion));
		Localidad a = (Localidad) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return a;
	}
	
	public Localidad[] getAllLocalidades() throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(Localidad.class);
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (Localidad[])list.toArray(new Localidad[0]);
	}

	public Localidad[] findByField(String field,String value){
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(Localidad.class);
		c.add(Expression.like(field,"%" + value + "%"));
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (Localidad[])list.toArray(new Localidad[0]);
	}
	

	public Localidad[] findByPartidoId(String value){
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(Localidad.class);
		c.add(Expression.eq("codigoPartido",value));
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (Localidad[])list.toArray(new Localidad[0]);
	}
	
	public Localidad[] getAllLocalidadesTranslated(String lang) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public void remove(String codigo) throws RemoteException {
		Localidad entity = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			
			entity = (Localidad) session.get(Localidad.class, codigo);						
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

	public void update(Localidad localidad) throws RemoteException {
		Localidad l = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			if (StringUtils.isBlank(localidad.getCodigo())) {
				l = new Localidad();
				// TODO: asignar ID
				//assignID(session,l);
				HibernateUtil.assignID(session,l);
				HibernateUtil.assignIDForLocalidad(session,l);
				//a.setCodigo(null);
			} else {
				l = (Localidad) session.get(Localidad.class, localidad.getCodigo());
			}

			l.setDescripcion(localidad.getDescripcion());
			//l.setCodigoLocalidad(localidad.getCodigoLocalidad());
			l.setCodigoPartido(localidad.getCodigoPartido());
			l.setActivo(localidad.getActivo());
					    		
			session.saveOrUpdate(l);

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


	public String getNombreLocalidadById(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();

		String name = (String)session.createQuery(
				"select descripcion " +
				"from Localidad " +
				"where codigoLocalidad = :codigo and activo = 's'"
				)
				.setString("codigo", codigo)
				.uniqueResult();
		
		HibernateUtil.cerrarSession(session);

		return name;
	}
	
	public Object[] findNamesByPartidoId(String value) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		List list = session.createQuery
				("select c.codigoLocalidad, c.descripcion " +
				"from Localidad c " +
				"where " +
				"c.codigoPartido =:partido and c.activo = 'S' ")
				.setString("partido", value)
				.list();
		
		Object[] results = new Object[list.size()];
		
		for (int i=0; i< results.length;i++){ 
		    results[i] = (Object[]) list.get(i);
		}
		
		HibernateUtil.cerrarSession(session);
		
		return results;
	}
}
