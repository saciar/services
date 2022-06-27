package crm.services.wsdl2.manager;

import java.rmi.RemoteException;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;

import crm.libraries.abm.entities.VariacionMes;
import crm.services.util.HibernateUtil;
import crm.services.wsdl2.sei.VariacionMesManagerSEI;

public class VariacionMesManager implements WSDL2Service, VariacionMesManagerSEI{
	public VariacionMes getById(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(VariacionMes.class);
		c.add(Expression.eq("codigo", codigo));
		c.add(Expression.eq("activo","S"));
		VariacionMes result = (VariacionMes) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return result;
	}
	

	public VariacionMes[] getAll() throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(VariacionMes.class);
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);
		return (VariacionMes[])list.toArray(new VariacionMes[0]);
	}

	public VariacionMes[] findByField(String field,String value) throws RemoteException{		
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(VariacionMes.class);
		c.add(Expression.like(field,"%" + value + "%"));
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);
		return (VariacionMes[])list.toArray(new VariacionMes[0]);
	}
	
	public String getVariacionById(String codigo) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		
		String st  = (String)session.createQuery(
				"select variacion from VariacionMes " +
				"where codigo = :cod and activo = 'S'"
				).setString("cod", codigo)
				.uniqueResult();
		
		HibernateUtil.cerrarSession(session);
		
		return st;
	}
	
	public int getVariacionByMes(int mes) throws RemoteException{		
			Session session = HibernateUtil.abrirSession();
			
			Integer st  = (Integer)session.createSQLQuery(
					"select vm_variacion from mst_variacion_mes " +
					"where vm_mes = :m and vm_activo = 'S'"
			).addScalar("vm_variacion", Hibernate.INTEGER)
			.setInteger("m", mes)
			.uniqueResult();
			
			HibernateUtil.cerrarSession(session);
			
			return st;
	}
}
