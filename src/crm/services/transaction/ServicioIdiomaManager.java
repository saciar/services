package crm.services.transaction;

import java.rmi.RemoteException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import crm.libraries.abm.entities.ServicioIdioma;
import crm.services.sei.ServicioIdiomaManagerSEI;
import crm.services.util.HibernateUtil;

public class ServicioIdiomaManager implements ServicioIdiomaManagerSEI,ManagerService {

	public ServicioIdioma getServicioIdiomaById(String codigoServicio,String codigoIdioma) throws RemoteException {
		Session session = HibernateUtil.abrirSession();

		Criteria c = session.createCriteria(ServicioIdioma.class);
		c.add(Expression.eq("codigoServicio", codigoServicio));
		c.add(Expression.eq("codigoIdioma", codigoIdioma));
		c.add(Expression.eq("activo","S"));
		ServicioIdioma s = (ServicioIdioma) c.uniqueResult();
		HibernateUtil.cerrarSession(session);

		return s;
	}
	
	public ServicioIdioma getServicioIdiomaByIdNoIdioma(String codigoServicio) throws RemoteException {
		Session session = HibernateUtil.abrirSession();

		Criteria c = session.createCriteria(ServicioIdioma.class);
		c.add(Expression.eq("codigoServicio", codigoServicio));
		c.add(Expression.eq("activo","S"));
		ServicioIdioma s = (ServicioIdioma) c.uniqueResult();
		HibernateUtil.cerrarSession(session);

		return s;
	}

	public ServicioIdioma[] getAllServicioIdiomas() throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(ServicioIdioma.class);
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (ServicioIdioma[])list.toArray(new ServicioIdioma[0]);
	}
	
	public ServicioIdioma[] findByField(String field,String value){
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(ServicioIdioma.class);
		c.add(Expression.like(field,"%" + value + "%"));
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (ServicioIdioma[])list.toArray(new ServicioIdioma[0]);
	}

	public ServicioIdioma[] getAllServicioIdiomasTranslated(String lang) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public void remove(String codigoServicio,String codigoIdioma) throws RemoteException {
		ServicioIdioma entity = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			
			entity = getServicioIdiomaById(codigoServicio,codigoIdioma);						
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

	public void update(ServicioIdioma servicioIdioma) throws RemoteException {
		ServicioIdioma s = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			if (StringUtils.isBlank(servicioIdioma.getCodigo())) {
				s = new ServicioIdioma();
				// TODO: asignar ID
				s.setCodigo(null);
			} else {
				s = (ServicioIdioma) session.get(ServicioIdioma.class, servicioIdioma.getCodigo());
			}
			
			s.setCodigoServicio(servicioIdioma.getCodigoServicio());
			s.setCodigoIdioma(servicioIdioma.getCodigoIdioma());
			s.setDescripcion(servicioIdioma.getDescripcion());
			s.setDescripcionAbreviada(servicioIdioma.getDescripcionAbreviada());
			s.setActivo(servicioIdioma.getActivo());
			
			session.saveOrUpdate(s);

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

	public String getDescripcionServicio(String codigoServicio, 
			String codigoIdioma) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		String result = (String)session.createQuery("select descripcionAbreviada from " +
				"ServicioIdioma where codigoServicio = '" + codigoServicio + 
				"' and codigoIdioma = '" + codigoIdioma + "'").uniqueResult();
		HibernateUtil.cerrarSession(session);		
		return result;
	}

}
