package crm.services.wsdl2.manager;

import java.rmi.RemoteException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import crm.libraries.abm.entities.DestinatarioOF;
import crm.services.util.HibernateUtil;
import crm.services.wsdl2.sei.DestinatarioOFManagerSEI;

public class DestinatarioOFManager implements DestinatarioOFManagerSEI,WSDL2Service {

	public DestinatarioOF[] getAll() throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c= session.createCriteria(DestinatarioOF.class);
		c.add(Expression.eq("activo", "S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);		
		return (DestinatarioOF[])list.toArray(new DestinatarioOF[0]);
	}

	public DestinatarioOF getById(String id) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(DestinatarioOF.class);
		c.add(Expression.eq("codigo", id));
		c.add(Expression.eq("activo", "S"));
		DestinatarioOF result = (DestinatarioOF)c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return result;
	}
	
	public String update(DestinatarioOF destinatario) throws RemoteException{
		DestinatarioOF dest = null;
		Session session = null;
		Transaction tx = null;
		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();
			
			if (StringUtils.isBlank(destinatario.getCodigo())) {
				dest = new DestinatarioOF();
				HibernateUtil.assignID(session,dest);
			
			} else {			
				dest = (DestinatarioOF) session.get(DestinatarioOF.class, destinatario.getCodigo());	
			}
			if(dest == null){
				dest = new DestinatarioOF(destinatario.getCodigo(),"","S");
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
