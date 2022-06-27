package crm.services.transaction;

import java.rmi.RemoteException;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import crm.libraries.abm.entities.DiasAVencer;
import crm.services.sei.DiasAVencerManagerSEI;
import crm.services.util.HibernateUtil;

public class DiasAVencerManager implements DiasAVencerManagerSEI,ManagerService {

	public DiasAVencer getDiasAVenver() throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(DiasAVencer.class);
		DiasAVencer dav = (DiasAVencer) c.uniqueResult();		
		HibernateUtil.cerrarSession(session);
		if(dav == null){
			dav = new DiasAVencer();
		}		
		return dav;
	}

	public void update(DiasAVencer diasAVencer) throws RemoteException {
		Session session = null;
		Transaction tx = null;		
		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			DiasAVencer dav = getDiasAVenver();
			if(StringUtils.isBlank(dav.getCodigo())){
				HibernateUtil.assignID(session,dav);
			}
			dav.setDias(diasAVencer.getDias());		    		    
			session.saveOrUpdate(dav);

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
