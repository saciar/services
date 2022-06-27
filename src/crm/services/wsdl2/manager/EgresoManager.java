package crm.services.wsdl2.manager;

import java.rmi.RemoteException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import crm.libraries.abm.entities.Egreso;
import crm.services.util.HibernateUtil;
import crm.services.wsdl2.sei.EgresoManagerSEI;

public class EgresoManager implements EgresoManagerSEI, WSDL2Service{
    public Egreso getById(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(Egreso.class);
		c.add(Expression.eq("codigo", codigo));
		Egreso result = (Egreso) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return result;
	}

	public Egreso[] getAll() throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(Egreso.class);
		List list = c.list();
		HibernateUtil.cerrarSession(session);
		return (Egreso[])list.toArray(new Egreso[0]);
	}

	public Egreso[] findByField(String field,String value) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(Egreso.class);
		c.add(Expression.like(field,"%" + value + "%"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);
		return (Egreso[])list.toArray(new Egreso[0]);
	}
	
	public Egreso[] findByFieldExactly(String field,String value) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(Egreso.class);
		c.add(Expression.eq(field,value));
		List list = c.list();
		HibernateUtil.cerrarSession(session);
		return (Egreso[])list.toArray(new Egreso[0]);
	}
	
	public Egreso[] findByFields(Object[] field,Object[] value) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(Egreso.class);
		for(int i=0; i<field.length;i++){
			c.add(Expression.like((String)field[i],"%" + (String)value[i] + "%"));
		}
		List list = c.list();
		HibernateUtil.cerrarSession(session);
		return (Egreso[])list.toArray(new Egreso[0]);
	}

	public void remove(String codigo) throws RemoteException {
		Egreso entity = null;
		Session session = null;
		Transaction tx = null;
		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();
			entity = (Egreso) session.get(Egreso.class, codigo);
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

	public String update(Egreso egreso) throws RemoteException {
		Egreso model = null;
		Session session = null;
		Transaction tx = null;
		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			if (StringUtils.isBlank(egreso.getCodigo())) {
				model = new Egreso();
				HibernateUtil.assignID(session,model);
			} else {
				model = (Egreso) session.get(Egreso.class, egreso.getCodigo());
			}
			model.setCodChofer(egreso.getCodChofer());
			model.setCodTransporte(egreso.getCodTransporte());
			model.setCodUsuario(egreso.getCodUsuario());
			model.setFecha_egreso(egreso.getFecha_egreso());
			model.setNroppto(egreso.getNroppto());
			model.setTipoEgreso(egreso.getTipoEgreso());

			session.saveOrUpdate(model);

			tx.commit();
			session.flush();
			return model.getCodigo();
		} catch (HibernateException he) {
			if (tx != null && tx.isActive())
				tx.rollback();
			he.printStackTrace(System.err);
			return null;
		} finally {
			HibernateUtil.cerrarSession(session);
		}
	}

}
