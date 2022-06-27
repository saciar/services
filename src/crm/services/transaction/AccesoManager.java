package crm.services.transaction;

import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import crm.libraries.abm.entities.Acceso;
import crm.libraries.abm.entities.Usuario;
import crm.services.sei.AccesoManagerSEI;
import crm.services.util.HibernateUtil;

public class AccesoManager implements AccesoManagerSEI,ManagerService {

	public Acceso getAccesoById(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		// Servicio s = (Servicio)session.get(Servicio.class,codigo);
		Criteria c = session.createCriteria(Acceso.class);
		c.add(Expression.eq("codigo", codigo));
		c.add(Expression.eq("activo","S"));
		Acceso a = (Acceso) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		/*if(a != null){
			loadUsuariosString(a);
		}*/
		return a;
	}
	
	
	/**
	 * Este metodo retorna el acceso sin importar el estado de activo
	 */
	public Acceso getAccesoByDescripcion(String descripcion) throws RemoteException {
		Session session = HibernateUtil.abrirSession();	
		Criteria c = session.createCriteria(Acceso.class);
		c.add(Expression.eq("descripcion", descripcion));
		Acceso a = (Acceso) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		/*if(a != null){
			loadUsuariosString(a);
		}*/
		return a;
	}

	public Acceso[] getAllAccesos() throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(Acceso.class);
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);
		//loadUsuariosString(list);
		return (Acceso[])list.toArray(new Acceso[0]);
	}

	public Acceso[] findByField(String field,String value){
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(Acceso.class);
		c.add(Expression.like(field,"%" + value + "%"));
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);
		//loadUsuariosString(list);
		return (Acceso[])list.toArray(new Acceso[0]);
	}
	
	public Acceso[] getAllAccesosTranslated(String lang) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public void remove(String codigo) throws RemoteException {		
		Acceso acceso = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			
			acceso = (Acceso) session.get(Acceso.class, codigo);						
			acceso.setActivo("N");
		    
			session.saveOrUpdate(acceso);

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

	public void update(Acceso acceso) throws RemoteException {
		Acceso a = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			if (StringUtils.isBlank(acceso.getCodigo())) {
				a = new Acceso();
				// TODO: asignar ID
				HibernateUtil.assignID(session,a);
				
				//a.setCodigo(null);
			} else {
				a = (Acceso) session.get(Acceso.class, acceso.getCodigo());
			}

			a.setDescripcion(acceso.getDescripcion());
			a.setActivo(acceso.getActivo());
		    
		    
			// if (groupId == 0){
			// session.save(group);
			// }
			session.saveOrUpdate(a);

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

	
	
	
	/*private void loadUsuariosString(List accesos) {
		Iterator it = accesos.iterator();
		while (it.hasNext()) {
			Acceso acceso = (Acceso) it.next();
			loadUsuariosString(acceso);
		}
	}

	private void loadUsuariosString(Acceso acceso) {
		String usuarioIds = new String();
		int i = 0;
		Iterator it = acceso.getUsuarios().iterator();
		while (it.hasNext()) {
			Usuario usuario = (Usuario) it.next();
			if (i > 0) {
				usuarioIds += ",";
			}
			usuarioIds += usuario.getCodigo();
			i++;
		}
		acceso.setUsuariosString(usuarioIds);
		acceso.setUsuarios(null);
	}*/
}
