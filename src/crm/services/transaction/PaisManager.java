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
import crm.libraries.abm.entities.Pais;
import crm.services.sei.PaisManagerSEI;
import crm.services.util.HibernateUtil;

public class PaisManager implements PaisManagerSEI,ManagerService {

	public Pais getPaisById(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();

		Criteria c = session.createCriteria(Pais.class);
		c.add(Expression.eq("codigo", codigo));
		c.add(Expression.eq("activo","S"));
		Pais a = (Pais) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return a;
	}
	
	
	public Pais getPaisByDescripcion(String descripcion) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(Pais.class);
		c.add(Expression.eq("descripcion", descripcion));
		Pais a = (Pais) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return a;
	}

	public Pais[] getAllPaises() throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(Pais.class);
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (Pais[])list.toArray(new Pais[0]);
	}

	public Pais[] findByField(String field,String value){
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(Pais.class);
		c.add(Expression.like(field,"%" + value + "%"));
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (Pais[])list.toArray(new Pais[0]);
	}
	
	public Pais[] getAllPaisesTranslated(String lang) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public void remove(String codigo) throws RemoteException {
		Pais entity = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			
			entity = (Pais) session.get(Pais.class, codigo);						
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

	public void update(Pais pais) throws RemoteException {
		Pais p = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			if (StringUtils.isBlank(pais.getCodigo())) {
				p = new Pais();
				// TODO: asignar ID
				assignID(session,p);
				
				//a.setCodigo(null);
			} else {
				p = (Pais) session.get(Pais.class, pais.getCodigo());
			}

			p.setDescripcion(pais.getDescripcion());
			p.setActivo(pais.getActivo());
		    		
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
	
	public String getNombrePaisById(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();

		String name = (String)session.createQuery(
				"select descripcion " +
				"from Pais " +
				"where codigo = :codigo and activo = 's'"
				)
				.setString("codigo", codigo)
				.uniqueResult();
		
		HibernateUtil.cerrarSession(session);

		return name;
	}
}
