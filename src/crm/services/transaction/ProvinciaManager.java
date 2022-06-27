package crm.services.transaction;
     
import java.rmi.RemoteException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import crm.libraries.abm.entities.Provincia;
import crm.services.sei.ProvinciaManagerSEI;
import crm.services.util.CollectionUtil;
import crm.services.util.HibernateUtil;

public class ProvinciaManager implements ProvinciaManagerSEI,ManagerService {

	public Provincia getProvinciaById(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();

		Criteria c = session.createCriteria(Provincia.class);
		c.add(Expression.eq("codigo", codigo));
		c.add(Expression.eq("activo","S"));
		Provincia a = (Provincia) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		System.out.println("Servicio encontrad: " + a.getCodigo());
		return a;
	}
	
	public Provincia getProvinciaByCodProvincia(String codigoProvincia) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(Provincia.class);
		c.add(Expression.eq("codigoProvincia", codigoProvincia));
		Provincia a = (Provincia) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return a;
	}
	
	public Provincia getProvinciaByDescripcion(String desc) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(Provincia.class);
		c.add(Expression.eq("descripcion", desc));
		Provincia a = (Provincia) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return a;
	}

	public Provincia[] getAllProvincias() throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(Provincia.class);
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (Provincia[])list.toArray(new Provincia[0]);
	}

	
	public Provincia[] findByPaisId(String value){
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(Provincia.class);
		c.add(Expression.eq("codigoPais",value));
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (Provincia[])list.toArray(new Provincia[0]);
	}
	
	public Object[] findCodAndDescriptionByPaisId(String value) throws RemoteException{
		/*Session session = HibernateUtil.abrirSession();

		String queryString = new String();
		queryString += "select descripcion,codigoProvincia from " + Provincia.class.getSimpleName() + " ";
		queryString += "where codigoPais = :codigoPais  and activo = 'S'";
		
		Query query = session.createQuery(queryString);
		query.setString("codigoPais", value);
		
		List list = query.list();		
		HibernateUtil.cerrarSession(session);
		
    	return CollectionUtil.listToObjectArray(list);*/
		Session session = HibernateUtil.abrirSession();
		List list = session.createQuery
				("select c.codigoProvincia, c.descripcion " +
				"from Provincia c " +
				"where " +
				"c.codigoPais =:pais and c.activo = 'S' ")
				.setString("pais", value)
				.list();
		
		Object[] results = new Object[list.size()];
		
		for (int i=0; i< results.length;i++){ 
		    results[i] = (Object[]) list.get(i);
		}
		
		HibernateUtil.cerrarSession(session);
		
		return results;
	}
	
	
	public Provincia[] getAllProvinciasTranslated(String lang) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public Provincia[] findByField(String field,String value){
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(Provincia.class);
		c.add(Expression.like(field,"%" + value + "%"));
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (Provincia[])list.toArray(new Provincia[0]);
	}
	
	public void remove(String codigo) throws RemoteException {
		Provincia entity = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			
			entity = (Provincia) session.get(Provincia.class, codigo);						
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

	public void update(Provincia provincia) throws RemoteException {
		Provincia p = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			if (StringUtils.isBlank(provincia.getCodigo())) {
				p = new Provincia();
				HibernateUtil.assignID(session,p);
				HibernateUtil.assignIDForProvincia(session, p);
				//a.setCodigo(null);
			} else {
				p = (Provincia) session.get(Provincia.class, provincia.getCodigo());
			}

			p.setDescripcion(provincia.getDescripcion());
			//p.setCodigoProvincia(provincia.getCodigoProvincia());
			p.setCodigoPais(provincia.getCodigoPais());
			p.setActivo(provincia.getActivo());
		    
		    
			// if (groupId == 0){
			// session.save(group);
			// }
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

	
	
	public String getNombreProvinciaById(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();

		String name = (String)session.createQuery(
				"select descripcion " +
				"from Provincia " +
				"where codigoProvincia = :codigo and activo = 's'"
				)
				.setString("codigo", codigo)
				.uniqueResult();
		
		HibernateUtil.cerrarSession(session);

		return name;
	}
}
