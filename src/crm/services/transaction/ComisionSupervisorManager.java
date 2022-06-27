package crm.services.transaction;

import java.rmi.RemoteException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import crm.libraries.abm.entities.ComisionSupervisor;
import crm.services.sei.ComisionSupervisorManagerSEI;
import crm.services.util.HibernateUtil;

public class ComisionSupervisorManager implements ComisionSupervisorManagerSEI,ManagerService {

	public ComisionSupervisor getComisionSupervisorById(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();

		Criteria c = session.createCriteria(ComisionSupervisor.class);
		c.add(Expression.eq("codigo", codigo));
		c.add(Expression.eq("activo","S"));
		ComisionSupervisor a = (ComisionSupervisor) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return a;
	}

	public ComisionSupervisor getComisionSupervisorByVendedor(String codigoVendedor) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(ComisionSupervisor.class);
		c.add(Expression.eq("codigoVendedor", codigoVendedor));
		ComisionSupervisor a = (ComisionSupervisor) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return a;
	}
	
	public ComisionSupervisor[] getAllComisionesSupervisores() throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(ComisionSupervisor.class);
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (ComisionSupervisor[])list.toArray(new ComisionSupervisor[0]);
	}
	public ComisionSupervisor[] findByField(String field,String value){
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(ComisionSupervisor.class);
		c.add(Expression.like(field,"%" + value + "%"));
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (ComisionSupervisor[])list.toArray(new ComisionSupervisor[0]);
	}
	
		
	public ComisionSupervisor[] getAllComisionesSupervisoresTranslated(String lang) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	
	public void removeByVendedor(String codigoVendedor)throws RemoteException {
		Session session = null;
		Transaction tx = null;
		try {
            session = HibernateUtil.abrirSession();
            tx = session.beginTransaction();

    		Criteria c = session.createCriteria(ComisionSupervisor.class);
    		c.add(Expression.eq("codigoVendedor", codigoVendedor));
    		List list = c.list();
    		ComisionSupervisor[] results = (ComisionSupervisor[])list.toArray(new ComisionSupervisor[0]);
    		for(int i = 0; i < results.length;i++){            
                session.delete(results[i]);
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
	
	
	public void remove(String codigo) throws RemoteException {
		ComisionSupervisor entity = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			
			entity = (ComisionSupervisor) session.get(ComisionSupervisor.class, codigo);						
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

	public void update(ComisionSupervisor comision) throws RemoteException {
		ComisionSupervisor c = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			if (StringUtils.isBlank(comision.getCodigo())) {
				c = new ComisionSupervisor();
				c.setNivel(getNivel(session));
				HibernateUtil.assignID(session,c);
			} else {
				c = (ComisionSupervisor) session.get(ComisionSupervisor.class, comision.getCodigo());
			}

			
			c.setCodigoVendedor(comision.getCodigoVendedor());
			c.setPorcentaje(comision.getPorcentaje());
			c.setMarcoLiquidacion(comision.getMarcoLiquidacion());
			c.setObjetivo(comision.getObjetivo());
			c.setActivo(comision.getActivo());
		    		
			session.saveOrUpdate(c);

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


	
	private String getNivel(Session session) {
		String entityName = ComisionSupervisor.class.getSimpleName();
		String query = new String();
		
		query += "select max(nivel) + 1 " ;
		query += "from "+ entityName + " ";
		
		Object result = session.createQuery(query).uniqueResult();
		
		return result != null ? result.toString():"1";
	}
}
