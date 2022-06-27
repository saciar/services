package crm.services.transaction;

import java.rmi.RemoteException;

import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import crm.libraries.abm.entities.ABMEntity;
import crm.libraries.abm.entities.LiquidacionComisiones;
import crm.services.sei.LiquidacionComisionesManagerSEI;
import crm.services.util.HibernateUtil;

public class LiquidacionComisionesManager implements LiquidacionComisionesManagerSEI,ManagerService {
	
	public void update(LiquidacionComisiones l) throws RemoteException {
		LiquidacionComisiones liq = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			if (StringUtils.isBlank(l.getCodigo())) {
				liq = new LiquidacionComisiones();
				// TODO: asignar ID
				assignID(session,liq);
				
				//a.setCodigo(null);
			} else {
				liq = (LiquidacionComisiones) session.get(LiquidacionComisiones.class, l.getCodigo());
			}

			liq.setCobrado(l.getCobrado());
			liq.setCodigoUnidad(l.getCodigoUnidad());
			liq.setCodigoVendedor(l.getCodigoVendedor());
			liq.setFacturado(l.getFacturado());
			liq.setFechaDesde(l.getFechaDesde());
			liq.setFechaHasta(l.getFechaHasta());
			liq.setLiquidado(l.getLiquidado());
			liq.setNumeroPresupuesto(l.getNumeroPresupuesto());
			liq.setTipoComision(l.getTipoComision());

			session.saveOrUpdate(liq);

			tx.commit();
			session.flush();
		} catch (HibernateException he) {
			if (tx != null && tx.isActive())
				tx.rollback();

			he.printStackTrace(System.err);
			// throw new SystemException(he);
		} finally {
			HibernateUtil.cerrarSession(session);
		}
	}
	
	private void assignID(Session session,ABMEntity e) {

		String className = e.getClass().toString();
		String entityType = className.substring(className.lastIndexOf(".") + 1,
				className.length());
		String entityAlias = entityType.toLowerCase();

		// ID = MAX_ID_UNTIL_NOW + 1
		String query = "select max(" + entityAlias + ".codigo) + 1 from " 
		+ entityType + " " + entityAlias;
		
		Object result = session.createQuery(query)		
						.uniqueResult();
		
		String codigo = result != null ? result.toString():"1";

		e.setCodigo(codigo);

	}
	
	public boolean isPptoLiquidado(String nroPpto) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		
		Integer i = (Integer)session.createQuery(
				"select count(*) from LiquidacionComisiones where " +
				"numeroPresupuesto = :nro"
				).setString("nro", nroPpto)
				.uniqueResult();
		
		HibernateUtil.cerrarSession(session);
		
		return i.intValue() > 0;
	}
	
	public String getTipoComisionByNroPpto(String nroPpto) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		
		String st = (String)session.createQuery(
				"select tipoComision from LiquidacionComisiones where " +
				"numeroPresupuesto = :nro"
				).setString("nro", nroPpto)
				.uniqueResult();
		
		HibernateUtil.cerrarSession(session);
		
		return st;
	}
}