package crm.services.transaction;

import java.rmi.RemoteException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import crm.libraries.abm.entities.TipoUniforme;
import crm.services.sei.TipoUniformeManagerSEI;
import crm.services.util.CollectionUtil;
import crm.services.util.HibernateUtil;

public class TipoUniformeManager implements TipoUniformeManagerSEI,ManagerService {

	public TipoUniforme getTipoUniformeById(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();

		Criteria c = session.createCriteria(TipoUniforme.class);
		c.add(Expression.eq("codigo", codigo));
		c.add(Expression.eq("activo","S"));
		TipoUniforme a = (TipoUniforme) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return a;
	}

	public TipoUniforme[] getAllTipoUniformes() throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(TipoUniforme.class);
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (TipoUniforme[])list.toArray(new TipoUniforme[0]);
	}

	public TipoUniforme[] getAllTipoUniformesTranslated(String lang) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}
	
	public TipoUniforme[] findByField(String field,String value){
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(TipoUniforme.class);
		c.add(Expression.like(field,"%" + value + "%"));
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (TipoUniforme[])list.toArray(new TipoUniforme[0]);
	}

	public void remove(String codigo) throws RemoteException {
		TipoUniforme entity = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			
			entity = (TipoUniforme) session.get(TipoUniforme.class, codigo);						
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

	public void update(TipoUniforme tipoUniforme) throws RemoteException {
		TipoUniforme tu = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			if (StringUtils.isBlank(tipoUniforme.getCodigo())) {
				tu = new TipoUniforme();
				// TODO: asignar ID
				HibernateUtil.assignID(session,tu);
				
				//a.setCodigo(null);
			} else {
				tu = (TipoUniforme) session.get(TipoUniforme.class, tipoUniforme.getCodigo());
			}

			tu.setDescripcion(tipoUniforme.getDescripcion());
			tu.setActivo(tipoUniforme.getActivo());

			session.saveOrUpdate(tu);

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
	
	public Object[] getTiposUniformeReport() throws RemoteException {

		Session session = HibernateUtil.abrirSession();

		List list = session.createQuery("select codigo,descripcion from TipoUniforme order by descripcion").list();

		HibernateUtil.cerrarSession(session);

		return CollectionUtil.listToObjectArray(list);
	}
}
