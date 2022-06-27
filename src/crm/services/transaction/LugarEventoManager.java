package crm.services.transaction;

import java.rmi.RemoteException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import crm.libraries.abm.entities.LugarEvento;
import crm.services.sei.LugarEventoManagerSEI;
import crm.services.util.CollectionUtil;
import crm.services.util.HibernateUtil;

public class LugarEventoManager implements LugarEventoManagerSEI,ManagerService {

	public LugarEvento getLugarEventoById(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();

		Criteria c = session.createCriteria(LugarEvento.class);
		c.add(Expression.eq("codigo", codigo));
		c.add(Expression.eq("activo","S"));
		LugarEvento a = (LugarEvento) c.uniqueResult();
		HibernateUtil.cerrarSession(session);		
		return a;
	}


	public LugarEvento getLugarEventoByNombre(String nombreLugar) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(LugarEvento.class);
		c.add(Expression.eq("nombreLugar", nombreLugar));
		LugarEvento a = (LugarEvento) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return a;
	}
	
	public LugarEvento[] getAllLugarEventos() throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(LugarEvento.class);
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (LugarEvento[])list.toArray(new LugarEvento[0]);
	}

	public LugarEvento[] getAllLugarEventosTranslated(String lang) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}
	public LugarEvento[] findByField(String field,String value){
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(LugarEvento.class);
		c.add(Expression.like(field,"%" + value + "%"));
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (LugarEvento[])list.toArray(new LugarEvento[0]);
	}

	public void remove(String codigo) throws RemoteException {
		LugarEvento entity = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			
			entity = (LugarEvento) session.get(LugarEvento.class, codigo);						
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

	public String update(LugarEvento lugarEvento) throws RemoteException {
		LugarEvento l = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			if (StringUtils.isBlank(lugarEvento.getCodigo())) {
				l = new LugarEvento();
				HibernateUtil.assignID(session,l);
				
				//a.setCodigo(null);
			} else {
				l = (LugarEvento) session.get(LugarEvento.class, lugarEvento.getCodigo());
			}

			
			l.setActivo(lugarEvento.getActivo());
		    
			
			
			l.setNombreLugar(lugarEvento.getNombreLugar());
			l.setContacto(lugarEvento.getContacto());
			l.setTelefono1(lugarEvento.getTelefono1());
			l.setTelefono2(lugarEvento.getTelefono2());
			l.setFax(lugarEvento.getFax());
			l.setFlotaNextel(lugarEvento.getFlotaNextel());
			l.setIdNextel(lugarEvento.getIdNextel());
			l.setEmail(lugarEvento.getEmail());
			l.setCalle(lugarEvento.getCalle());
			l.setNumero(lugarEvento.getNumero());
			l.setPiso(lugarEvento.getPiso());
			l.setDepartamento(lugarEvento.getDepartamento());
			l.setCodigoPostal(lugarEvento.getCodigoPostal());
			l.setLocalidad(lugarEvento.getLocalidad());
			l.setCodigoPartido(lugarEvento.getCodigoPartido());
			l.setCodigoProvincia(lugarEvento.getCodigoProvincia());
			l.setCodigoPais(lugarEvento.getCodigoPais());
			l.setCodigoComision(lugarEvento.getCodigoComision());
			l.setEmailOS(lugarEvento.getEmailOS());
			
			session.saveOrUpdate(l);
			tx.commit();
			session.flush();
			
			//---------creo una sala unica--------------------------
			SalaLugarManager salaLugarManager = new SalaLugarManager();
			salaLugarManager.makeUniqueSala(l.getCodigo());
			//------------------------------------------------------
			
			//return l.getCodigo();
		} catch (HibernateException he) {
			if (tx != null && tx.isActive())
				tx.rollback();
			he.printStackTrace(System.err);
		} finally {
			HibernateUtil.cerrarSession(session);
		}
		return l.getCodigo();
		//return null;
	}

	public Object[] getLugarEventosReport() throws RemoteException {

		Session session = HibernateUtil.abrirSession();

		List list = session.createQuery(
				"select codigo,nombreLugar " +
				"from LugarEvento " +
				"where activo = 'S' " +
				"order by nombreLugar"
				)
				.list();

		HibernateUtil.cerrarSession(session);

		return CollectionUtil.listToObjectArray(list);
	}
	
	public String getCodigoLugarComisionById(String codLugar) throws RemoteException{
		
		Session session = HibernateUtil.abrirSession();
		
		String st  = (String)session.createQuery(
				"select codigoComision from LugarEvento " +
				"where codigo = :cod and activo = 'S'"
				).setString("cod", codLugar)
				.uniqueResult();
		
		HibernateUtil.cerrarSession(session);
		
		return st;
	}
	
	public String getEmailOSById(String codLugar){
		Session session = HibernateUtil.abrirSession();
		
		String st  = (String)session.createQuery(
				"select emailOS from LugarEvento " +
				"where codigo = :cod and activo = 'S'"
				).setString("cod", codLugar)
				.uniqueResult();
		
		HibernateUtil.cerrarSession(session);
		
		return st;
	}
	
	public String getNombreLugarOSById(String codLugar){
		Session session = HibernateUtil.abrirSession();
		
		String st  = (String)session.createQuery(
				"select nombreLugar from LugarEvento " +
				"where codigo = :cod and activo = 'S'"
				).setString("cod", codLugar)
				.uniqueResult();
		
		HibernateUtil.cerrarSession(session);
		
		return st;
	}
	
	private static LugarEventoManager instance;
	
	public static synchronized LugarEventoManager instance() {

			if (instance == null) 
				instance = new LugarEventoManager();

		return instance;
	}
}
