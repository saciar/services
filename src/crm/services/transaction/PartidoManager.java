package crm.services.transaction;

import java.rmi.RemoteException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import crm.libraries.abm.entities.Partido;
import crm.services.sei.PartidoManagerSEI;
import crm.services.util.HibernateUtil;

public class PartidoManager implements PartidoManagerSEI,ManagerService {

	public Partido getPartidoById(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();

		Criteria c = session.createCriteria(Partido.class);
		c.add(Expression.eq("codigo", codigo));
		c.add(Expression.eq("activo","S"));
		Partido a = (Partido) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return a;
	}

	public Partido getPartidoByCodPartido(String codPartido) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(Partido.class);
		c.add(Expression.eq("codigoPartido", codPartido));
		Partido a = (Partido) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return a;
	}
	
	public Partido getPartidoByDescripcion(String desc) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(Partido.class);
		c.add(Expression.eq("descripcion", desc));
		Partido a = (Partido) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return a;
	}
	
	public Partido[] getAllPartidos() throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(Partido.class);
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (Partido[])list.toArray(new Partido[0]);
	}

	
	public Partido[] findByProvinciaId(String value){
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(Partido.class);
		c.add(Expression.eq("codigoProvincia",value));
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (Partido[])list.toArray(new Partido[0]);
	}
	
	public Object[] findNamesByProvinciaId(String value) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		List list = session.createQuery
				("select c.codigoPartido, c.descripcion " +
				"from Partido c " +
				"where " +
				"c.codigoProvincia =:prov and c.activo = 'S' ")
				.setString("prov", value)
				.list();
		
		Object[] results = new Object[list.size()];
		
		for (int i=0; i< results.length;i++){ 
		    results[i] = (Object[]) list.get(i);
		}
		
		HibernateUtil.cerrarSession(session);
		
		return results;
	}
	
	public Partido[] getAllPartidosTranslated(String lang) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public Partido[] findByField(String field,String value){
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(Partido.class);
		c.add(Expression.like(field,"%" + value + "%"));
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (Partido[])list.toArray(new Partido[0]);
	}
	
	public void remove(String codigo) throws RemoteException {
		Partido entity = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			
			entity = (Partido) session.get(Partido.class, codigo);						
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

	public void update(Partido partido) throws RemoteException {
		Partido p = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			if (StringUtils.isBlank(partido.getCodigo())) {
				p = new Partido();
				// TODO: asignar ID
				HibernateUtil.assignID(session,p);
				HibernateUtil.assignIDForPartido(session,p);
				//a.setCodigo(null);
			} else {
				p = (Partido) session.get(Partido.class, partido.getCodigo());
			}

			p.setDescripcion(partido.getDescripcion());
			//p.setCodigoPartido(partido.getCodigoPartido());
			p.setCodigoProvincia(partido.getCodigoProvincia());
			p.setActivo(partido.getActivo());
		    		    
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

	public String getNombrePartidoById(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();

		String name = (String)session.createQuery(
				"select descripcion " +
				"from Partido " +
				"where codigoPartido = :codigo and activo = 'S'"
				)
				.setString("codigo", codigo)
				.uniqueResult();
		
		HibernateUtil.cerrarSession(session);

		return name;
	}
}
