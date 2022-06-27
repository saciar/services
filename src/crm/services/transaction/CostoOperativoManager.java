package crm.services.transaction;

import java.rmi.RemoteException;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import crm.libraries.abm.entities.CostoOperativo;
import crm.services.sei.CostoOperativoManagerSEI;
import crm.services.util.HibernateUtil;

public class CostoOperativoManager implements CostoOperativoManagerSEI,ManagerService {


	public CostoOperativo getCostoOperativo() throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(CostoOperativo.class);
		CostoOperativo co = (CostoOperativo) c.uniqueResult();		
		HibernateUtil.cerrarSession(session);
		if(co == null){
			co = new CostoOperativo();
		}		
		return co;
	}


	
	public void update(CostoOperativo costoOperativo) throws RemoteException {		
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			CostoOperativo co = getCostoOperativo();
			if(StringUtils.isBlank(co.getCodigo())){
				HibernateUtil.assignID(session,co);
			}
			co.setCosto(costoOperativo.getCosto());		    		    
			session.saveOrUpdate(co);

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
}
