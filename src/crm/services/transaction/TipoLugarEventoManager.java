package crm.services.transaction;

import java.rmi.RemoteException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import crm.libraries.abm.entities.TipoLugarEvento;
import crm.services.sei.TipoLugarEventoManagerSEI;
import crm.services.util.CollectionUtil;
import crm.services.util.HibernateUtil;

public class TipoLugarEventoManager implements TipoLugarEventoManagerSEI,ManagerService {

	public TipoLugarEvento getTipoLugarEventoById(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();

		Criteria c = session.createCriteria(TipoLugarEvento.class);
		c.add(Expression.eq("codigo", codigo));
		c.add(Expression.eq("activo","S"));
		TipoLugarEvento a = (TipoLugarEvento) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		System.out.println("Servicio encontrad: " + a.getCodigo());
		return a;
	}
	
	public TipoLugarEvento getTipoLugarEventoByDescripcion(String descripcion) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(TipoLugarEvento.class);
		c.add(Expression.eq("descripcion", descripcion));
		TipoLugarEvento a = (TipoLugarEvento) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return a;
	}

	public TipoLugarEvento[] getAllTipoLugarEventos() throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(TipoLugarEvento.class);
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (TipoLugarEvento[])list.toArray(new TipoLugarEvento[0]);
	}

	public TipoLugarEvento[] getAllTipoLugarEventosTranslated(String lang) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public TipoLugarEvento[] findByField(String field,String value){
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(TipoLugarEvento.class);
		c.add(Expression.like(field,"%" + value + "%"));
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (TipoLugarEvento[])list.toArray(new TipoLugarEvento[0]);
	}
	
	public void remove(String codigo) throws RemoteException {
		TipoLugarEvento entity = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			
			entity = (TipoLugarEvento) session.get(TipoLugarEvento.class, codigo);						
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

	public void update(TipoLugarEvento tipoLugarEvento) throws RemoteException {
		TipoLugarEvento tle = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			if (StringUtils.isBlank(tipoLugarEvento.getCodigo())) {
				tle = new TipoLugarEvento();
				HibernateUtil.assignID(session,tle);
				
				//a.setCodigo(null);
			} else {
				tle = (TipoLugarEvento) session.get(TipoLugarEvento.class, tipoLugarEvento.getCodigo());
			}

			tle.setDescripcion(tipoLugarEvento.getDescripcion());
			tle.setActivo(tipoLugarEvento.getActivo());
		    
		    
			// if (groupId == 0){
			// session.save(group);
			// }
			session.saveOrUpdate(tle);

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

	public Object[] getTipoLugarEventosReport() throws RemoteException {

		Session session = HibernateUtil.abrirSession();

		List list = session.createQuery("select codigo,descripcion from TipoLugarEvento order by descripcion").list();

		HibernateUtil.cerrarSession(session);

		return CollectionUtil.listToObjectArray(list);
	}
}
