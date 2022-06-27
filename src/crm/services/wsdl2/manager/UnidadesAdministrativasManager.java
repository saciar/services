package crm.services.wsdl2.manager;

import java.rmi.RemoteException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import crm.libraries.abm.entities.UnidadAdministrativa;
import crm.services.util.HibernateUtil;
import crm.services.wsdl2.sei.UnidadesAdministrativasManagerSEI;

public class UnidadesAdministrativasManager implements UnidadesAdministrativasManagerSEI,WSDL2Service {
	
	public UnidadAdministrativa getUnidadComercialById(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();

		Criteria c = session.createCriteria(UnidadAdministrativa.class);
		c.add(Expression.eq("codigo", codigo));
		c.add(Expression.eq("activo","S"));
		UnidadAdministrativa a = (UnidadAdministrativa) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return a;
	}
	
	public UnidadAdministrativa[] getAll()throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(UnidadAdministrativa.class);
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (UnidadAdministrativa[])list.toArray(new UnidadAdministrativa[0]);
	}
	
	public UnidadAdministrativa[] findByField(String field,String value)throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(UnidadAdministrativa.class);
		c.add(Expression.like(field,"%" + value + "%"));
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (UnidadAdministrativa[])list.toArray(new UnidadAdministrativa[0]);
	}
	
	public void remove(String codigo) throws RemoteException {
		UnidadAdministrativa entity = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			
			entity = (UnidadAdministrativa) session.get(UnidadAdministrativa.class, codigo);						
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

	
	public String update(UnidadAdministrativa unidadAdministrativa) throws RemoteException {
		UnidadAdministrativa uc = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			if (StringUtils.isBlank(unidadAdministrativa.getCodigo())) {
				uc = new UnidadAdministrativa();
				HibernateUtil.assignID(session,uc);
			} else {
				uc = (UnidadAdministrativa) session.get(UnidadAdministrativa.class, unidadAdministrativa.getCodigo());
			}

		
			uc.setCodigoSucursal(unidadAdministrativa.getCodigoSucursal());
			uc.setDescripcion(unidadAdministrativa.getDescripcion());
			uc.setActivo(unidadAdministrativa.getActivo());			
		    		
			session.saveOrUpdate(uc);
			
			//if(!StringUtils.isBlank(uc.getCodigo())){
				//UnidadAdministradorManager adm = new UnidadAdministradorManager();
				//adm.removeByCodigoUnidad(uc.getCodigo());
        		///for(int i = 0;i < administradores.length;i++){
        			//UnidadAdministrador unidadVendedor = new UnidadAdministrador(uc.getCodigo(),administradores,"S");
        			//adm.update(unidadVendedor);	
        		//}		
			//}
			
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
		return uc.getCodigo();
	}
	
	/*public String update(UnidadAdministrativa unidadAdministrativa, String[] administradores) throws RemoteException {
		UnidadAdministrativa uc = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			if (StringUtils.isBlank(unidadAdministrativa.getCodigo())) {
				uc = new UnidadAdministrativa();
				HibernateUtil.assignID(session,uc);
			} else {
				uc = (UnidadAdministrativa) session.get(UnidadAdministrativa.class, unidadAdministrativa.getCodigo());
			}

		
			uc.setCodigoSucursal(unidadAdministrativa.getCodigoSucursal());
			uc.setDescripcion(unidadAdministrativa.getDescripcion());
			uc.setActivo(unidadAdministrativa.getActivo());			
		    		
			session.saveOrUpdate(uc);
			
			if(!StringUtils.isBlank(uc.getCodigo())){
				UnidadAdministradorManager adm = new UnidadAdministradorManager();
				adm.removeByCodigoUnidad(uc.getCodigo());
        		for(int i = 0;i < administradores.length;i++){
        			UnidadAdministrador unidadVendedor = new UnidadAdministrador(uc.getCodigo(),administradores[i],"S");
        			adm.update(unidadVendedor);	
        		}		
			}
			
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
		return uc.getCodigo();
	}*/
}
