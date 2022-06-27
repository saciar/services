package crm.services.wsdl2.manager;

import java.rmi.RemoteException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import crm.libraries.abm.entities.Administrador;
import crm.libraries.abm.entities.UnidadAdministrador;
import crm.libraries.abm.entities.UnidadAdministrativa;
import crm.services.sei.CategVendedorManagerSEI;
import crm.services.transaction.ManagerService;
import crm.services.util.CollectionUtil;
import crm.services.util.HibernateUtil;
import crm.services.wsdl2.sei.UnidadAdministradorManagerSEI;

public class UnidadAdministradorManager implements UnidadAdministradorManagerSEI,WSDL2Service {
   	public String getCodigoUnidad(String codigoAdministrador){
		Session session = HibernateUtil.abrirSession();

		Query query = 
			session.createQuery("select codigoUnidad from UnidadAdministrador where codigoAdministrador = :codigoAdm and activo = 'S'");
		
		query.setString("codigoAdm", codigoAdministrador);
		
		String desc = (String)query.uniqueResult();
		
		HibernateUtil.cerrarSession(session);
		
    	return desc;
    }  	
   	
	public UnidadAdministrador[] getUnidadByCodigoUnidad(String codigoUnidad)throws RemoteException {
	
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(UnidadAdministrador.class);
		c.add(Expression.eq("codigoUnidad", codigoUnidad));
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (UnidadAdministrador[])list.toArray(new UnidadAdministrador[0]);
	}
	
	public Object[] getAdministradoresByUnidadAdministrativa(String codUnidad) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
   		
   		List result = session.createSQLQuery(
   				"SELECT ad_codadmin, apynom FROM mst_administradores a " +
   				"inner join mst_unidades_administradores m on a.ad_codadmin = m.ucu_codadmin " +
   				"where m.ucu_codunidad = :codUnidad ")
   				.addScalar("ad_codadmin", Hibernate.STRING)
   				.addScalar("apynom", Hibernate.STRING)
   				.setString("codUnidad",codUnidad)
   				.list(); 
   		
   		HibernateUtil.cerrarSession(session);
   		
   		return CollectionUtil.listToObjectArray(result);
	}
	
	public void removeByCodigoUnidad(String codigoUnidad)throws RemoteException {
		Session session = null;
		Transaction tx = null;
		try {
            session = HibernateUtil.abrirSession();
            tx = session.beginTransaction();

    		Criteria c = session.createCriteria(UnidadAdministrador.class);
    		c.add(Expression.eq("codigoUnidad", codigoUnidad));
    		List list = c.list();
    		UnidadAdministrador[] results = (UnidadAdministrador[])list.toArray(new UnidadAdministrador[0]);
    		for(int i = 0; i < results.length;i++){            
                results[i].setActivo("N");
                session.update(results[i]);
    		}
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

	public String update(UnidadAdministrador unidadAdministrador) throws RemoteException {
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();		
		    		
			session.saveOrUpdate(unidadAdministrador);
			tx.commit();
			session.flush();
		} catch (HibernateException he) {
			if (tx != null && tx.isActive())
				tx.rollback();

			he.printStackTrace(System.err);
			return null;
		} finally {
			HibernateUtil.cerrarSession(session);
		}
		return unidadAdministrador.getCodigoUnidad();
	}

}
