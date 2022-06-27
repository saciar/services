package crm.services.transaction;

import java.rmi.RemoteException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import crm.libraries.abm.entities.ABMEntity;
import crm.libraries.abm.entities.TxSeguimiento;
import crm.services.sei.TxSeguimientoManagerSEI;
import crm.services.util.CollectionUtil;
import crm.services.util.HibernateUtil;

public class TxSeguimientoManager implements TxSeguimientoManagerSEI,ManagerService {

	public TxSeguimiento[] getAllTxSeguimientos() throws RemoteException {
		Session session = HibernateUtil.abrirSession();

		List list = session.createQuery("from TxSeguimiento").list();

		HibernateUtil.cerrarSession(session);

		return (TxSeguimiento[])list.toArray(new TxSeguimiento[0]);
	}
	
	public void update(TxSeguimiento txseg) throws RemoteException {
		TxSeguimiento t = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			if (StringUtils.isBlank(txseg.getCodigo())) {
				t = new TxSeguimiento();
				// TODO: asignar ID
				assignID(session,t);
				
				//a.setCodigo(null);
			} else {
				t = (TxSeguimiento) session.get(TxSeguimiento.class, txseg.getCodigo());
			}

			t.setCodigoSeguimiento(txseg.getCodigoSeguimiento());
			t.setCodigoSeguimientoRespuesta(txseg.getCodigoSeguimientoRespuesta());
			t.setCodigoUsuario(txseg.getCodigoUsuario());
			t.setFechaYHora(txseg.getFechaYHora());
			t.setNumeroPresupuesto(txseg.getNumeroPresupuesto());
			t.setObservaciones(txseg.getObservaciones());		    

			session.saveOrUpdate(t);

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
	
	public TxSeguimiento[] findByField(String field,String value) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(TxSeguimiento.class);
		c.add(Expression.like(field,"%" + value + "%"));
		List list = c.list(); 
		HibernateUtil.cerrarSession(session);

		return (TxSeguimiento[])list.toArray(new TxSeguimiento[0]);
	}
	
	public Object[] getSeguimientosByNroPpto(long nroPpto) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		
		List list = session.createSQLQuery
		("SELECT seg_fechahora,seg_usuario,seg_item_seg,seg_item_res,seg_observaciones " +
				"FROM TX_SEGUIMIENTO " +
				"WHERE seg_nroppto = :numero" )
		.addScalar("seg_fechahora",Hibernate.STRING)
		.addScalar("seg_usuario",Hibernate.STRING)
		.addScalar("seg_item_seg",Hibernate.STRING)
		.addScalar("seg_item_res",Hibernate.STRING)
		.addScalar("seg_observaciones",Hibernate.STRING)
		.setLong("numero",nroPpto)
		.list();
		
		HibernateUtil.cerrarSession(session);
		
		return CollectionUtil.listToObjectArray(list);
	}
}
