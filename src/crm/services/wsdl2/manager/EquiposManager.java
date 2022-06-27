/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package crm.services.wsdl2.manager;

import crm.libraries.abm.entities.Equipos;
import crm.services.util.HibernateUtil;
import crm.services.wsdl2.sei.EquiposManagerSEI;

import java.rmi.RemoteException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;

/**
 *
 * @author saciar
 */
public class EquiposManager implements EquiposManagerSEI, WSDL2Service{
    public Equipos getById(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(Equipos.class);
		c.add(Expression.eq("codigo", codigo));
		c.add(Expression.eq("eqActivo","S"));
		Equipos result = (Equipos) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return result;
	}

	public Equipos[] getAll() throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(Equipos.class);
		c.add(Expression.eq("eqActivo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);
		return (Equipos[])list.toArray(new Equipos[0]);
	}

	public Equipos[] findByField(String field,String value) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(Equipos.class);
		c.add(Expression.like(field,"%" + value + "%"));
		c.add(Expression.eq("eqActivo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);
		return (Equipos[])list.toArray(new Equipos[0]);
	}
	
	public Integer getMaxCodigoBarras() throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(Equipos.class);
		c.setProjection(Projections.max("codigoBarras"));
		String max = (String)c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return Integer.parseInt(max);
	}
	
	public Equipos[] findByFieldExactly(String field,String value) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(Equipos.class);
		c.add(Expression.eq(field,value));
		c.add(Expression.eq("eqActivo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);
		return (Equipos[])list.toArray(new Equipos[0]);
	}
	
	public Equipos[] findByFields(Object[] field,Object[] value) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(Equipos.class);
		for(int i=0; i<field.length;i++){
			c.add(Expression.like((String)field[i],"%" + (String)value[i] + "%"));
		}
		c.add(Expression.eq("eqActivo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);
		return (Equipos[])list.toArray(new Equipos[0]);
	}

	public void remove(String codigo) throws RemoteException {
		Equipos entity = null;
		Session session = null;
		Transaction tx = null;
		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();
			entity = (Equipos) session.get(Equipos.class, codigo);
			entity.setEqActivo("N");
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


	public Equipos update(Equipos equipo) throws RemoteException {
		Equipos model = null;
		Session session = null;
		Transaction tx = null;
		boolean result = false;
		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			if (StringUtils.isBlank(equipo.getCodigo())) {
				model = new Equipos();
				HibernateUtil.assignID(session,model);
			} else {
				model = (Equipos) session.get(Equipos.class, equipo.getCodigo());
			}
			model.setEqDescripcion(equipo.getEqDescripcion());
			model.setEqNroserie(equipo.getEqNroserie());
			model.setEqActivo(equipo.getEqActivo());
            model.setInvDepositos(equipo.getInvDepositos());
            model.setInvEquiposFamilias(equipo.getInvEquiposFamilias());
            model.setObservaciones(equipo.getObservaciones());
            if(StringUtils.isBlank(equipo.getCodigoBarras())){
            	model.setCodigoBarras(String.valueOf(getMaxCodigoBarras()+1));
            }
            else
            	model.setCodigoBarras(equipo.getCodigoBarras());
            model.setEqEstado(equipo.getEqEstado());
            model.setModelo(equipo.getModelo());
            model.setAlto(equipo.getAlto());
            model.setAncho(equipo.getAncho());
            model.setLargo(equipo.getLargo());
            model.setPeso(equipo.getPeso());
			session.saveOrUpdate(model);

			tx.commit();
			session.flush();
			result=true;
		} catch (HibernateException he) {
			if (tx != null && tx.isActive())
				tx.rollback();
			he.printStackTrace(System.err);
		} finally {
			HibernateUtil.cerrarSession(session);
		}
		return (result)?model:null;
	}

}
