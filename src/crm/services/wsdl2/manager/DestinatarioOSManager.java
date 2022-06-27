package crm.services.wsdl2.manager;

import java.rmi.RemoteException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import crm.libraries.abm.entities.AgendaPpto;
import crm.libraries.abm.entities.CondicionPago;
import crm.libraries.abm.entities.DestinatarioOS;
import crm.services.util.HibernateUtil;
import crm.services.wsdl2.sei.DestinatarioOSManagerSEI;

public class DestinatarioOSManager implements DestinatarioOSManagerSEI,WSDL2Service {

	public DestinatarioOS[] getAll() throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c= session.createCriteria(DestinatarioOS.class);
		c.add(Expression.eq("activo", "S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);		
		return (DestinatarioOS[])list.toArray(new DestinatarioOS[0]);
	}

	public DestinatarioOS getById(String id) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(DestinatarioOS.class);
		c.add(Expression.eq("codigo", id));
		c.add(Expression.eq("activo", "S"));
		DestinatarioOS result = (DestinatarioOS)c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return result;
	}
	
	public String update(DestinatarioOS destinatario) throws RemoteException{
		DestinatarioOS dest = null;
		Session session = null;
		Transaction tx = null;
		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();
			
			if (StringUtils.isBlank(destinatario.getCodigo())) {
				dest = new DestinatarioOS();
				HibernateUtil.assignID(session,dest);
			
			} else {			
				dest = (DestinatarioOS) session.get(DestinatarioOS.class, destinatario.getCodigo());	
			}
			if(dest == null){
				dest = new DestinatarioOS(destinatario.getCodigo(),"","S");
			}
			
			dest.setCodigo(destinatario.getCodigo());
			dest.setEmail(destinatario.getEmail());
			dest.setNombre(destinatario.getNombre());
			
			session.saveOrUpdate(dest);

			tx.commit();
			session.flush();
		} catch (HibernateException he) {
			if (tx != null && tx.isActive())
				tx.rollback();
			he.printStackTrace(System.err);
		} finally {
			HibernateUtil.cerrarSession(session);
		}
		return dest.getCodigo();
	}

}
