package crm.services.transaction;

import java.rmi.RemoteException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import crm.libraries.abm.entities.ABMEntity;
import crm.libraries.abm.entities.ResultadoSeguimiento;
import crm.services.sei.ResultadoSeguimientoManagerSEI;
import crm.services.util.CollectionUtil;
import crm.services.util.HibernateUtil;

public class ResultadoSeguimientoManager implements ResultadoSeguimientoManagerSEI,ManagerService {

	public ResultadoSeguimiento getResultadoSeguimientoById(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();

		Criteria c = session.createCriteria(ResultadoSeguimiento.class);
		c.add(Expression.eq("codigo", codigo));
		c.add(Expression.eq("activo","S"));
		ResultadoSeguimiento a = (ResultadoSeguimiento) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		System.out.println("Servicio encontrad: " + a.getCodigo());
		return a;
	}
	
	public ResultadoSeguimiento getResultadoSeguimientoByDescripcion(String descripcion) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(ResultadoSeguimiento.class);
		c.add(Expression.eq("descripcion", descripcion));
		ResultadoSeguimiento a = (ResultadoSeguimiento) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return a;	
	}

	public ResultadoSeguimiento[] getAllResultadoSeguimientos() throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(ResultadoSeguimiento.class);
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (ResultadoSeguimiento[])list.toArray(new ResultadoSeguimiento[0]);
	}

	public ResultadoSeguimiento[] getAllResultadoSeguimientosTranslated(String lang) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}
	
	public ResultadoSeguimiento[] findByField(String field,String value){
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(ResultadoSeguimiento.class);
		c.add(Expression.like(field,"%" + value + "%"));
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (ResultadoSeguimiento[])list.toArray(new ResultadoSeguimiento[0]);
	}

	public void remove(String codigo) throws RemoteException {
		ResultadoSeguimiento entity = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			
			entity = (ResultadoSeguimiento) session.get(ResultadoSeguimiento.class, codigo);						
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

	public void update(ResultadoSeguimiento resultadoSeguimiento) throws RemoteException {
		ResultadoSeguimiento p = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			if (StringUtils.isBlank(resultadoSeguimiento.getCodigo())) {
				p = new ResultadoSeguimiento();
				assignID(session,p);				
			} else {
				p = (ResultadoSeguimiento) session.get(ResultadoSeguimiento.class, resultadoSeguimiento.getCodigo());
			}

			p.setDescripcion(resultadoSeguimiento.getDescripcion());
			p.setCodSeguimiento(resultadoSeguimiento.getCodSeguimiento());
			p.setActivo(resultadoSeguimiento.getActivo());
			    
			session.saveOrUpdate(p);

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
	
	public Object[] getResultadosReportByAccion(String codAccion) throws RemoteException {

		Session session = HibernateUtil.abrirSession();

		List list = session.createQuery(
				"select codigo, descripcion " +
				"from ResultadoSeguimiento " +
				"where activo = 'S' and codSeguimiento = :cod " +
				"order by descripcion"
				)
				.setString("cod", codAccion)
				.list();

		HibernateUtil.cerrarSession(session);

		return CollectionUtil.listToObjectArray(list);
	}
}
