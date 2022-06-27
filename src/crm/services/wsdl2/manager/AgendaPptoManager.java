package crm.services.wsdl2.manager;

import java.rmi.RemoteException;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import crm.libraries.abm.entities.AgendaPpto;
import crm.services.util.HibernateUtil;
import crm.services.wsdl2.sei.AgendaPptoManagerSEI;

public class AgendaPptoManager implements AgendaPptoManagerSEI,WSDL2Service {

	public String update(AgendaPpto cobrador) throws RemoteException {
		AgendaPpto cob = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			/*if (StringUtils.isBlank(cobrador.getCodigo())) {
				adm = new CobradorPpto("","");
				HibernateUtil.assignID(session,adm);
			} else {
				adm = (CobradorPpto) session.get(CobradorPpto.class, cobrador.getCodigo());				
			}*/

			cob = (AgendaPpto) session.get(AgendaPpto.class, cobrador.getCodigo());	
			
			if(cob == null){
				cob = new AgendaPpto(cobrador.getCodigo(),"","","","");
			}
			
			cob.setCodCobrador(cobrador.getCodCobrador());
			cob.setCodContacto(cobrador.getCodContacto());
			cob.setDireccionPago(cobrador.getDireccionPago());
			cob.setHorarioPago(cobrador.getHorarioPago());
			cob.setCodCobrador(cobrador.getCodTipoRecibo());
			cob.setNumeroRecibo(cobrador.getNumeroRecibo());
			cob.setCodTipoRecibo(cobrador.getCodTipoRecibo());
			
			session.saveOrUpdate(cob);

			tx.commit();
			session.flush();
		} catch (HibernateException he) {
			if (tx != null && tx.isActive())
				tx.rollback();
			he.printStackTrace(System.err);
		} finally {
			HibernateUtil.cerrarSession(session);
		}
		
		return cob.getCodigo();
		
	}

	public AgendaPpto getDataById(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		// Servicio s = (Servicio)session.get(Servicio.class,codigo);
		Criteria c = session.createCriteria(AgendaPpto.class);
		c.add(Expression.eq("codigo", codigo));
		AgendaPpto a = (AgendaPpto) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return a;
	} 
	
	public AgendaPpto[] findByField(String field,String value) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(AgendaPpto.class);
		c.add(Expression.eq(field,value));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (AgendaPpto[])list.toArray(new AgendaPpto[0]);
	}

}
