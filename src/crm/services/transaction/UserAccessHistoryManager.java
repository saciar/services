package crm.services.transaction;

import java.rmi.RemoteException;
import java.util.Date;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import crm.libraries.abm.entities.UserAccessHistory;
import crm.services.sei.UserAccessHistoryManagerSEI;
import crm.services.util.HibernateUtil;

public class UserAccessHistoryManager implements UserAccessHistoryManagerSEI,ManagerService {

	
	public void saveHistory(String userId, String accessId, String type) throws RemoteException {
		UserAccessHistory userAccessHistory = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			userAccessHistory.setUserId(userId);
			userAccessHistory.setAccessId(accessId);
			userAccessHistory.setType(type);
			userAccessHistory.setDate(new Date());
			
			session.save(userAccessHistory);

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
