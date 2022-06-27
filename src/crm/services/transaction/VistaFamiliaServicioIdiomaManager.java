package crm.services.transaction;

import java.rmi.RemoteException;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;

import crm.libraries.abm.entities.VistaFamiliaServicioIdioma;
import crm.services.sei.VistaFamiliaServicioIdiomaManagerSEI;
import crm.services.util.HibernateUtil;

public class VistaFamiliaServicioIdiomaManager implements
		VistaFamiliaServicioIdiomaManagerSEI,ManagerService {

	public VistaFamiliaServicioIdioma getVistaFamiliaServicioIdiomaById(
			String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		// Servicio s = (Servicio)session.get(Servicio.class,codigo);
		Criteria c = session.createCriteria(VistaFamiliaServicioIdioma.class);
		c.add(Expression.eq("codigo", codigo));
		VistaFamiliaServicioIdioma a = (VistaFamiliaServicioIdioma) c
				.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return a;
	}

	public Object[] getDescripcionByFamiliaAndServicio(String codServ,
			String codFam) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		List list = session
				.createQuery(
						"select v.codigo, v.descripcion"
								+ " from VistaFamiliaServicioIdioma v "
								+ "where "
								+ "v.codigoServicio = :codigoServ or v.codigoFamilia = :codigoFam")
				.setString("codigoServ", codServ)
				.setString("codigoFam", codFam).list();

		Object[] results = new Object[list.size()];

		for (int i = 0; i < results.length; i++) {
			results[i] = (Object[]) list.get(i);
		}

		HibernateUtil.cerrarSession(session);

		return results;
	}

	public Object[] getDescripcionByServicio(String codServ) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		List list = session.createQuery(
						"select v.descripcion, v.codigoFamilia"
								+ " from VistaFamiliaServicioIdioma v "
								+ "where "
								+ "v.codigoServicio = :codigoServ ")
				.setString("codigoServ", codServ)
				.list();

		Object[] results = new Object[list.size()];

		for (int i = 0; i < results.length; i++) {
			results[i] = (Object[]) list.get(i);
		}

		HibernateUtil.cerrarSession(session);

		return results;
	}

	public VistaFamiliaServicioIdioma[] getAllVistaFamiliaServicioIdioma()
			throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(VistaFamiliaServicioIdioma.class);
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (VistaFamiliaServicioIdioma[]) list
				.toArray(new VistaFamiliaServicioIdioma[0]);
	}

	public VistaFamiliaServicioIdioma[] findByField(String field, String value)
			throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(VistaFamiliaServicioIdioma.class);
		c.add(Expression.like(field, "%" + value + "%"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (VistaFamiliaServicioIdioma[]) list
				.toArray(new VistaFamiliaServicioIdioma[0]);
	}

}
