package crm.services.wsdl2.manager;

import java.rmi.RemoteException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Projections;

import crm.libraries.abm.entities.Equipamientos;
import crm.services.util.CollectionUtil;
import crm.services.util.HibernateUtil;
import crm.services.wsdl2.sei.EquipamientosManagerSEI;;

public class EquipamientosManager implements EquipamientosManagerSEI, WSDL2Service{
    public Equipamientos getById(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(Equipamientos.class);
		c.add(Expression.eq("codigo", codigo));
		c.add(Expression.eq("activo","S"));
		Equipamientos result = (Equipamientos) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return result;
	}

	public Equipamientos[] getAll() throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(Equipamientos.class);
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);
		return (Equipamientos[])list.toArray(new Equipamientos[0]);
	}

	public Equipamientos[] findByField(String field,String value) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(Equipamientos.class);
		c.add(Expression.eq(field,value));
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);
		return (Equipamientos[])list.toArray(new Equipamientos[0]);
	}
	
	public Object[] buscarEquipamientoxCodigoBarras(int codigo, String valor) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		String condicion="";
		if(codigo==1){
			condicion="eq_cod_barras";
		}
		else if(codigo==2){
			condicion="eq_nroserie";
		}
		else if(codigo==3){
			condicion="inv_sub_codfamilia";
		}
		else if(codigo==4){
			condicion="eq_subfamilia";
		}
		
		List list = session.createSQLQuery(
				"SELECT eq.*, fam.eqfam_descripcion, sub.inv_sub_descripcion, marca.marc_descripcion FROM crm.inv_equipamiento eq "
				+ "inner join inv_subfamilias sub on eq_subfamilia=id_subfamilia "
				+ "inner join inv_equipos_familias fam on inv_sub_codfamilia = eqfam_idfamilia "
				+ "inner join inv_equipos_marcas marca on eq_marca = marc_id "
				+ "where eq_activo='S' and "+condicion+" = ?"
				)
				.addScalar("eq_idequipo",Hibernate.INTEGER)//0
				.addScalar("eq_marca",Hibernate.INTEGER)//1
				.addScalar("eq_subfamilia",Hibernate.INTEGER)//2			
				.addScalar("eq_nroserie",Hibernate.STRING)//3
				.addScalar("eq_deposito_inicial", Hibernate.INTEGER)//4
				.addScalar("eq_activo", Hibernate.STRING)//5
				.addScalar("eq_observaciones",Hibernate.STRING)//6			
				.addScalar("eq_estado",Hibernate.INTEGER)//7
				.addScalar("eq_cod_barras", Hibernate.INTEGER)//8
				.addScalar("eq_modelo", Hibernate.STRING)//9				
				.addScalar("eq_alto", Hibernate.INTEGER)//10
				.addScalar("eq_ancho", Hibernate.INTEGER)//11
				.addScalar("eq_largo",Hibernate.INTEGER)//12			
				.addScalar("eq_peso",Hibernate.INTEGER)//13
				.addScalar("eqfam_descripcion", Hibernate.STRING)//14
				.addScalar("inv_sub_descripcion", Hibernate.STRING)//15
				.addScalar("marc_descripcion", Hibernate.STRING)//16
				.setString(0,valor)
				.list();
		
		HibernateUtil.cerrarSession(session);
		return CollectionUtil.listToObjectArray(list);
	}
	
	public Integer getMaxCodigoBarras() throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(Equipamientos.class);
		c.setProjection(Projections.max("codigoBarras"));
		String max = (String)c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return Integer.parseInt(max);
	}
	
	public Equipamientos[] findByFieldExactly(String field,String value) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(Equipamientos.class);
		c.add(Expression.eq(field,value));
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);
		return (Equipamientos[])list.toArray(new Equipamientos[0]);
	}
	
	public Equipamientos[] findByFields(Object[] field,Object[] value) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(Equipamientos.class);
		for(int i=0; i<field.length;i++){
			c.add(Expression.like((String)field[i],"%" + (String)value[i] + "%"));
		}
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);
		return (Equipamientos[])list.toArray(new Equipamientos[0]);
	}

	public void remove(String codigo) throws RemoteException {
		Equipamientos entity = null;
		Session session = null;
		Transaction tx = null;
		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();
			entity = (Equipamientos) session.get(Equipamientos.class, codigo);
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


	public Equipamientos update(Equipamientos equipo) throws RemoteException {
		Equipamientos model = null;
		Session session = null;
		Transaction tx = null;
		boolean result = false;
		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			if (StringUtils.isBlank(equipo.getCodigo())) {
				model = new Equipamientos();
				HibernateUtil.assignID(session,model);
			} else {
				model = (Equipamientos) session.get(Equipamientos.class, equipo.getCodigo());
			}
			model.setMarca(equipo.getMarca());
			model.setNroSerie(equipo.getNroSerie());
			model.setActivo(equipo.getActivo());
            model.setDeposito(equipo.getDeposito());
            model.setSubfamilia(equipo.getSubfamilia());
            model.setObservaciones(equipo.getObservaciones());
            if(StringUtils.isBlank(equipo.getCodigoBarras())){
            	model.setCodigoBarras(String.valueOf(getMaxCodigoBarras()+1));
            }
            else
            	model.setCodigoBarras(equipo.getCodigoBarras());
            model.setEstado(equipo.getEstado());
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
