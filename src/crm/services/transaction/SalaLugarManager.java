package crm.services.transaction;

import java.rmi.RemoteException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import crm.libraries.abm.entities.SalaLugar;
import crm.libraries.abm.helper.SalaHelper;
import crm.services.sei.SalaLugarManagerSEI;
import crm.services.util.CollectionUtil;
import crm.services.util.HibernateUtil;

public class SalaLugarManager implements SalaLugarManagerSEI,ManagerService {

	private static final String SALA_UNICA = "Sala Unica";
	private static final Log log = LogFactory.getLog(SalaLugarManager.class);
	
	public SalaLugar getSalaLugarById(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();

		Criteria c = session.createCriteria(SalaLugar.class);
		c.add(Expression.eq("codigo", codigo));
		c.add(Expression.eq("activo","S"));
		SalaLugar a = (SalaLugar) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return a;
	}

	public SalaLugar getSalaLugarByCodSala(String codigoSala) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(SalaLugar.class);
		c.add(Expression.eq("codigoSala", codigoSala));
		SalaLugar a = (SalaLugar) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return a;
	}
	
	public SalaLugar getSalaLugarByCodSalaAndLugar(String codigoSala, String codigoLugar) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(SalaLugar.class);
		c.add(Expression.eq("codigoSala", codigoSala));
		c.add(Expression.eq("codigoLugar", codigoLugar));
		c.add(Expression.eq("activo", "S"));
		SalaLugar a = (SalaLugar) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return a;
	}
	
	public SalaLugar[] getAllSalaLugares() throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(SalaLugar.class);
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (SalaLugar[])list.toArray(new SalaLugar[0]);
	}

	public SalaLugar[] findByField(String field,String value){
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(SalaLugar.class);
		c.add(Expression.like(field,"%" + value + "%"));
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (SalaLugar[])list.toArray(new SalaLugar[0]);
	}
	
	public SalaLugar[] getAllSalaLugaresTranslated(String lang) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public void remove(String codigo) throws RemoteException {
		SalaLugar entity = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			
			entity = (SalaLugar) session.get(SalaLugar.class, codigo);						
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

	public void makeUniqueSala(String codigoLugar){
		try {
			SalaLugar sl = getUniqueSalaByCodigoLugar(codigoLugar);
			if(sl == null){
				sl = new SalaLugar();
			
				sl.setCodigoLugar(codigoLugar);
				sl.setDescripcion(SALA_UNICA);
				sl.setLargo("0");
				sl.setAncho("0");
				sl.setAltura("0");
				sl.setCapacidad("0");
				sl.setActivo("S");	
			
				update(sl);
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	public void update(SalaLugar salaLugar) throws RemoteException {
		SalaLugar s = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			//---busco si existe una sala unica para este lugar
			SalaLugar unique = getUniqueSalaByCodigoLugar(salaLugar.getCodigoLugar());
			if(unique != null){
				if (log.isDebugEnabled())
					log.info(".....................................tiene sala unica ");
				session.delete(unique);
			}
			
			if (StringUtils.isBlank(salaLugar.getCodigo())) {
				s = new SalaLugar();
				HibernateUtil.assignID(session,s);			
				s.setCodigoSala(getCodigoSala(session,salaLugar.getCodigoLugar()));				
			} else {
				s = (SalaLugar) session.get(SalaLugar.class, salaLugar.getCodigo());
				if(!s.getCodigoLugar().equals(salaLugar.getCodigoLugar())){
					s.setCodigoSala(getCodigoSala(session,salaLugar.getCodigoLugar()));
				}
			}

			if (log.isDebugEnabled()){
				log.info(".....................................s.codLugar = "+s.getCodigoLugar()+" salahelper.codlugar = "+ salaLugar.getCodigoLugar());
			}
			s.setCodigoLugar(salaLugar.getCodigoLugar());
			s.setDescripcion(salaLugar.getDescripcion());
			s.setLargo(salaLugar.getLargo());
			s.setAncho(salaLugar.getAncho());
			s.setAltura(salaLugar.getAltura());
			s.setCapacidad(salaLugar.getCapacidad());
			s.setActivo(salaLugar.getActivo());
			
			session.saveOrUpdate(s);

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

	public SalaLugar getUniqueSalaByCodigoLugar(String codigoLugar) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(SalaLugar.class);		
		c.add(Expression.eq("codigoLugar", codigoLugar));
		c.add(Expression.eq("descripcion", SALA_UNICA));
		SalaLugar a = (SalaLugar) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return a;
	}
	
	public Object[] getSalaLugarReport() throws RemoteException {

		Session session = HibernateUtil.abrirSession();

		List list = session.createQuery("select codigo,descripcion from SalaLugar where acyivo = 'S' order by descripcion").list();

		HibernateUtil.cerrarSession(session);

		return CollectionUtil.listToObjectArray(list);
	}

	public Object[] getSalaLugarReportByLugar(String codigoLugar) throws RemoteException {
		Session session = HibernateUtil.abrirSession();

		List list = session.createQuery(
				"select codigo,descripcion,codigoSala " +
				"from SalaLugar " +
				"where codigoLugar = :codigoLugar and activo = 'S' " +
				"order by descripcion"
				)
				.setString("codigoLugar",codigoLugar)
				.list();

		HibernateUtil.cerrarSession(session);

		return CollectionUtil.listToObjectArray(list);
	}
	
	
	
	
	private String getCodigoSala(Session session,String codigoLugar) {
		String entityName = SalaLugar.class.getSimpleName();
		String query = new String();
		
		query += "select max(codigoSala) + 1 " ;
		query += "from "+ entityName + " ";
		query += "where codigoLugar = '"+ codigoLugar + "' ";
		
		Object result = session.createQuery(query).uniqueResult();
		
		return result != null ? result.toString():"1";
	}
}
