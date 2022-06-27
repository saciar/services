package crm.services.transaction;

import java.rmi.RemoteException;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;

import crm.libraries.abm.entities.VistaPrecioServicioIdioma;
import crm.services.sei.VistaPrecioServicioIdiomaManagerSEI;
import crm.services.util.HibernateUtil;

public class VistaPrecioServicioIdiomaManager implements VistaPrecioServicioIdiomaManagerSEI,ManagerService{
	
	public VistaPrecioServicioIdioma getVistaPrecioServicioIdiomaById(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		// Servicio s = (Servicio)session.get(Servicio.class,codigo);
		Criteria c = session.createCriteria(VistaPrecioServicioIdioma.class);
		c.add(Expression.eq("codigo", codigo));
		VistaPrecioServicioIdioma a = (VistaPrecioServicioIdioma) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		System.out.println("Servicio encontrad: " + a.getCodigo());
		return a;
	}
	
	public int getCountVistaPrecioServicioByLugar(String codLugar) throws RemoteException{
		int q = 0;
		
		Session session = HibernateUtil.abrirSession();
		
		q = ((Integer)
				session.createQuery(
		        "select count(*) " +
		        "from VistaPrecioServicioIdioma v " +
		        "where v.codigoLugar = :lugar"
		        )
		        .setString("lugar", codLugar)
		        .uniqueResult())
		        .intValue();
		
		HibernateUtil.cerrarSession(session);
		
		return q;
		
	}
	
	public double getVistaPrecioServicioIdiomaByServicioYLugar(
										String codServ, String codLugar) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		double precio = 
			Double.parseDouble((String)
				session.createQuery 
				("select v.precioLugar" +
				" from VistaPrecioServicioIdioma v " +
				"where " +
				"v.codigoServicio = :codigoServ and v.codigoLugar = :codigoLug")
				.setString("codigoServ", codServ)
				.setString("codigoLug", codLugar)
				.uniqueResult());
		
		HibernateUtil.cerrarSession(session);
		
		return precio;
	}
	
	public VistaPrecioServicioIdioma[] getAllVistaPrecioServicioIdioma() throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(VistaPrecioServicioIdioma.class);
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (VistaPrecioServicioIdioma[])list.toArray(new VistaPrecioServicioIdioma[0]);
	}
	
	public VistaPrecioServicioIdioma[] findByField(String field,String value)throws RemoteException{		
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(VistaPrecioServicioIdioma.class);
		c.add(Expression.like(field,"%" + value + "%"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (VistaPrecioServicioIdioma[])list.toArray(new VistaPrecioServicioIdioma[0]);
	}
}
