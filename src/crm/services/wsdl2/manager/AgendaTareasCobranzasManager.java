package crm.services.wsdl2.manager;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import crm.libraries.abm.entities.AgendaTareasCobranzas;
import crm.services.util.CollectionUtil;
import crm.services.util.DateConverter;
import crm.services.util.HibernateUtil;
import crm.services.wsdl2.sei.AgendaTareasCobranzasManagerSEI;

public class AgendaTareasCobranzasManager implements AgendaTareasCobranzasManagerSEI,WSDL2Service{
	
	public void remove(String codigo) throws RemoteException {
		AgendaTareasCobranzas entity = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			
			entity = (AgendaTareasCobranzas) session.get(AgendaTareasCobranzas.class, codigo);						
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

	public String update(AgendaTareasCobranzas administrador) throws RemoteException {
		AgendaTareasCobranzas adm = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			if (StringUtils.isBlank(administrador.getCodigo())) {
				adm = new AgendaTareasCobranzas("",1,"","0","","","","","",null);
				adm.setFechaIngreso(administrador.getFechaIngreso());
				HibernateUtil.assignID(session,adm);
			} else {
				adm = (AgendaTareasCobranzas) session.get(AgendaTareasCobranzas.class, administrador.getCodigo());				
			}
			
			adm.setFechaVencimiento(administrador.getFechaVencimiento());
			adm.setAlerta(administrador.getAlerta());			
			adm.setAsunto(administrador.getAsunto());
			adm.setActivo(administrador.getActivo());
			adm.setNumeroPresupuesto(administrador.getNumeroPresupuesto());
			adm.setCompleta(administrador.getCompleta());
			adm.setMonto(administrador.getMonto());
			adm.setFactura(administrador.getFactura());
			
			session.saveOrUpdate(adm);

			tx.commit();
			session.flush();
		} catch (HibernateException he) {
			if (tx != null && tx.isActive())
				tx.rollback();
			he.printStackTrace(System.err);
		} finally {
			HibernateUtil.cerrarSession(session);
		}
		
		return adm.getCodigo();
		
	}

	public AgendaTareasCobranzas getAgendaById(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		// Servicio s = (Servicio)session.get(Servicio.class,codigo);
		Criteria c = session.createCriteria(AgendaTareasCobranzas.class);
		c.add(Expression.eq("codigo", codigo));
		c.add(Expression.eq("activo","S"));
		AgendaTareasCobranzas a = (AgendaTareasCobranzas) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return a;
	} 
	
	public AgendaTareasCobranzas[] findByField(String field,String value) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(AgendaTareasCobranzas.class);
		c.add(Expression.eq(field,value));
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (AgendaTareasCobranzas[])list.toArray(new AgendaTareasCobranzas[0]);
	}
	
	public AgendaTareasCobranzas[] findAlertaToday(String date) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(AgendaTareasCobranzas.class);		
		//c.add(Expression.eq("alerta",0));
		c.add(Expression.or(Expression.eq("alerta",0),Expression.eq("alerta",2)));
		c.add(Expression.eq("completa","N"));
		c.add(Expression.eq("activo","S"));
		c.add(Expression.eq("fechaVencimiento",date));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (AgendaTareasCobranzas[])list.toArray(new AgendaTareasCobranzas[0]);
	}
	
	public Object[] getClienteEventoToAgenda(long nroppto) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		
		List presupuestos = session.createSQLQuery(
				"select empresa, ppto_evt_nombre from tx_ppto inner join mst_cliente on ppto_codcliente = cl_codcliente " +
				"where ppto_nroppto = ?")
				.addScalar("empresa",Hibernate.STRING)
				.addScalar("ppto_evt_nombre",Hibernate.STRING)
				.setLong(0, new Long(nroppto))
				.list();
		HibernateUtil.cerrarSession(session);
		
		return CollectionUtil.listToObjectArray(presupuestos);
	}

}
