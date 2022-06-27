package crm.services.transaction;

import java.rmi.RemoteException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import crm.libraries.abm.entities.TipoArmado;
import crm.services.sei.TipoArmadoManagerSEI;
import crm.services.util.CollectionUtil;
import crm.services.util.HibernateUtil;

public class TipoArmadoManager implements TipoArmadoManagerSEI,ManagerService {

	public TipoArmado getById(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(TipoArmado.class);
		c.add(Expression.eq("codigo", codigo));
		c.add(Expression.eq("activo","S"));
		TipoArmado result = (TipoArmado) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return result;
	}
	

	public TipoArmado[] getAll() throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(TipoArmado.class);
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);
		return (TipoArmado[])list.toArray(new TipoArmado[0]);
	}

	public TipoArmado[] findByField(String field,String value){		
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(TipoArmado.class);
		c.add(Expression.like(field,"%" + value + "%"));
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);
		return (TipoArmado[])list.toArray(new TipoArmado[0]);
	}

	public void remove(String codigo) throws RemoteException {
		TipoArmado entity = null;
		Session session = null;
		Transaction tx = null;
		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();
			entity = (TipoArmado) session.get(TipoArmado.class, codigo);						
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

	public void update(TipoArmado titulo) throws RemoteException {
		TipoArmado model = null;
		Session session = null;
		Transaction tx = null;
		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			if (StringUtils.isBlank(titulo.getCodigo())) {
				model = new TipoArmado();
				HibernateUtil.assignID(session,model);
			} else {
				model = (TipoArmado) session.get(TipoArmado.class, titulo.getCodigo());
			}
			model.setDescripcion(titulo.getDescripcion());
			model.setActivo(titulo.getActivo());
		    
			session.saveOrUpdate(model);

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
	
	public Object[] getTipoArmadoReport() throws RemoteException {

		Session session = HibernateUtil.abrirSession();

		List list = session.createQuery("select codigo,descripcion from TipoArmado").list();

		HibernateUtil.cerrarSession(session);

		return CollectionUtil.listToObjectArray(list);
	}
	
}
