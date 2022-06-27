package crm.services.transaction;

import java.rmi.RemoteException;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import crm.libraries.abm.entities.VendedorUsuario;
import crm.services.sei.VendedorUsuarioManagerSEI;
import crm.services.util.HibernateUtil;

public class VendedorUsuarioManager implements VendedorUsuarioManagerSEI,ManagerService {
    public String getCodigoVendedor(String codigoUsuario) throws RemoteException{
		Session session = HibernateUtil.abrirSession();

		Query query = 
			session.createQuery("select codigoVendedor from VendedorUsuario where codigoUsuario = :codigoUsuario");
		
		query.setString("codigoUsuario", codigoUsuario);
		
		String desc = (String)query.uniqueResult();
		
		HibernateUtil.cerrarSession(session);
		
    	return desc;
    }
    
    public String getCodigoUsuario(String codigoVendedor) throws RemoteException{
		Session session = HibernateUtil.abrirSession();

		Query query = 
			session.createQuery("select codigoUsuario from VendedorUsuario where codigoVendedor = :codigovendedor");
		
		query.setString("codigovendedor", codigoVendedor);
		
		String desc = (String)query.uniqueResult();
		
		HibernateUtil.cerrarSession(session);
		
    	return desc;
    }
    
	public VendedorUsuario getVendedorUsuarioById(String vendedor,String usuario) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(VendedorUsuario.class);
		c.add(Expression.eq("codigoVendedor", vendedor));
		c.add(Expression.eq("codigoUsuario", usuario));
		VendedorUsuario vu = (VendedorUsuario) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		System.out.println("Servicio encontrad: " + vu.getCodigo());
		return vu;
	}
	
	
	
	public void remove(String vendedor,String usuario){
		Session session = null;
		Transaction tx = null;

		try {
            session = HibernateUtil.abrirSession();
            tx = session.beginTransaction();
            
    		Criteria c = session.createCriteria(VendedorUsuario.class);
    		c.add(Expression.eq("codigoVendedor", vendedor));
    		c.add(Expression.eq("codigoUsuario", usuario));
    		VendedorUsuario vu = (VendedorUsuario) c.uniqueResult();
            if (vu != null){
                session.delete(vu);
                tx.commit();
                session.flush();
            }
            else {
                tx.rollback();
            }
		} catch (HibernateException he) {
			if (tx != null && tx.isActive())
				tx.rollback();

			he.printStackTrace(System.err);
		} finally {
			HibernateUtil.cerrarSession(session);
		}	
		
	}	


	public void removeByUsuario(String usuario){
		Session session = null;
		Transaction tx = null;

		try {
            session = HibernateUtil.abrirSession();
            tx = session.beginTransaction();
            
    		Criteria c = session.createCriteria(VendedorUsuario.class);
    		c.add(Expression.eq("codigoUsuario", usuario));
    		VendedorUsuario vu = (VendedorUsuario) c.uniqueResult();
            if (vu != null){
                session.delete(vu);
                tx.commit();
                session.flush();
            }
            else {
                tx.rollback();
            }
		} catch (HibernateException he) {
			if (tx != null && tx.isActive())
				tx.rollback();

			he.printStackTrace(System.err);
		} finally {
			HibernateUtil.cerrarSession(session);
		}	
		
	}	

	
	public void update(VendedorUsuario vendedorUsuario) throws RemoteException {
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();	

			session.saveOrUpdate(vendedorUsuario);
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
	
	
	
	public boolean isVendedor(String codigoUsuario)throws RemoteException{
		String codVend = getCodigoVendedor(codigoUsuario);
		return (!StringUtils.isBlank(codVend));
	}
}


