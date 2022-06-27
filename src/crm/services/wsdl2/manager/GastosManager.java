package crm.services.wsdl2.manager;

import java.rmi.RemoteException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import crm.libraries.abm.entities.ABMEntity;
import crm.libraries.abm.entities.GastoSubcontratacion;
import crm.libraries.abm.entities.Subcontratado;
import crm.services.transaction.PresupuestosManager;
import crm.services.util.CollectionUtil;
import crm.services.util.DateConverter;
import crm.services.util.HibernateUtil;
import crm.services.wsdl2.sei.GastosManagerSEI;

public class GastosManager implements GastosManagerSEI, WSDL2Service{

	public Object[] getSubcontratadoByServ(long cosServ) throws RemoteException{
		Session session = HibernateUtil.abrirSession();

		List list = session.createSQLQuery(
				"select gc_codproveedor, gc_costo, gc_precio, gc_estado_serv from tx_gastos_contrataciones where gc_ppto_ss_id = ?"
				).addScalar("gc_codproveedor", Hibernate.LONG)
				.addScalar("gc_costo", Hibernate.DOUBLE)
				.addScalar("gc_precio", Hibernate.DOUBLE)
				.addScalar("gc_estado_serv",Hibernate.LONG)
				.setLong(0,cosServ)
				.list();
		HibernateUtil.cerrarSession(session);
		return CollectionUtil.listToObjectArray(list);
	}

	public Object[] buscarPorNumero(long nro) throws RemoteException {
		Session session = HibernateUtil.abrirSession();

		List list = session.createSQLQuery(
				/*"select nombre_fantasia, nombreEvento, numeroPresupuesto, " +
				"fechaInicialEvento, fechaFinalEvento, " +
				"fechaInstalacion, vendedor, nombreSala, " +
				"observacionesEvento, codigoSala " +
				"from VW_RPT_ORDSERVICIO_GENERAL " +
				"where numeroPresupuesto = ? " +
				"order by fechaInicialEvento"*/
				"select nombre_fantasia, ppto_evt_nombre as nombreEvento, ppto_nroppto as numeroPresupuesto, ppto_fecinicio as fechaInicialEvento, ppto_fecfin as fechaFinalEvento, " +
				"apynom as vendedor " +
				"from tx_ppto " +
				"inner join mst_cliente on cl_codcliente = ppto_codcliente " +
				"inner join tx_vendedor_ppto on vp_nroppto = ppto_nroppto " +
				"inner join mst_vendedores on vp_vendedor = vd_codvend " +				
				"where ppto_nroppto = ? " +
				"order by fechaInicialEvento"
				)
				.addScalar("nombre_fantasia",Hibernate.STRING)
				.addScalar("nombreEvento",Hibernate.STRING)
				.addScalar("numeroPresupuesto",Hibernate.LONG)			
				.addScalar("fechaInicialEvento",Hibernate.STRING)
				.addScalar("fechaFinalEvento", Hibernate.STRING)
				.addScalar("vendedor",Hibernate.STRING)
				.setLong(0,nro)
				.list();
		
		HibernateUtil.cerrarSession(session);
		return CollectionUtil.listToObjectArray(list);
	}
	
	public Object[] buscarPorFecha(String startDate, String endDate) throws RemoteException {
		Session session = HibernateUtil.abrirSession();

		Date desde=null;
		Date hasta=null;
		try {
			desde = DateConverter.convertStringToDate(startDate, "yyyy-MM-dd HH:mm:ss");
			hasta = DateConverter.convertStringToDate(endDate, "yyyy-MM-dd HH:mm:ss");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		List list = session.createSQLQuery(
				"select nombre_fantasia, ppto_evt_nombre as nombreEvento, ppto_nroppto as numeroPresupuesto, ppto_fecinicio as fechaInicialEvento, ppto_fecfin as fechaFinalEvento, " +
						"apynom as vendedor " +
						"from tx_ppto " +
						"inner join mst_cliente on cl_codcliente = ppto_codcliente " +
						"inner join tx_vendedor_ppto on vp_nroppto = ppto_nroppto " +
						"inner join mst_vendedores on vp_vendedor = vd_codvend " +
						"where ((ppto_fecinicio >= ? and ppto_fecfin <= ?)) " +
						"order by fechaInicialEvento"
				/*"select nombre_fantasia, ppto_evt_nombre as nombreEvento, ppto_nroppto as numeroPresupuesto, ppto_s_fecinicio as fechaInicialEvento, ppto_s_fecfin as fechaFinalEvento, " +
				"apynom as vendedor, els_descripcion as nombreSala, ppto_s_codlugsala as codigoSala " +
				"from tx_ppto_salas " +
				"inner join tx_ppto on ppto_s_nroppto = ppto_nroppto " +
				"inner join mst_cliente on cl_codcliente = ppto_codcliente " +
				"inner join tx_vendedor_ppto on vp_nroppto = ppto_nroppto " +
				"inner join mst_vendedores on vp_vendedor = vd_codvend " +
				"inner join mst_evt_lugar_salas on els_codlugsala = ppto_s_codlugsala " +
				"where ((ppto_s_fecinicio >= ? and ppto_s_fecfin <= ?)) " +
				"order by fechaInicialEvento"*/
				)
				.addScalar("nombre_fantasia",Hibernate.STRING)//0
				.addScalar("nombreEvento",Hibernate.STRING)//1
				.addScalar("numeroPresupuesto",Hibernate.LONG)//2			
				.addScalar("fechaInicialEvento",Hibernate.STRING)//3
				.addScalar("fechaFinalEvento", Hibernate.STRING)//4
				.addScalar("vendedor",Hibernate.STRING)//5
				.setDate(0,desde)
				.setDate(1,hasta)
				.list();
		
		HibernateUtil.cerrarSession(session);
		return CollectionUtil.listToObjectArray(list);
	}
	
	public int haveSubcontratados(long nropto) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		
		/*Object result = session.createSQLQuery("SELECT count(*) as cant FROM tx_gastos_contrataciones "+
			"inner join tx_ppto_salas_servicios on gc_ppto_nroppto = ppto_ss_id "+
			"inner join tx_ppto_salas on ppto_ss_pls = ppto_s_id "+
			"where ppto_s_codlugsala=?")*/
		Object result = session.createSQLQuery("SELECT count(*) as cant FROM tx_ppto_salas_servicios "+
				"inner join tx_ppto_salas on ppto_ss_pls = ppto_s_id "+
				"where ppto_s_nroppto= ? and ppto_ss_modalidad = 2")
		.addScalar("cant", Hibernate.INTEGER)
		.setLong(0, nropto)
		.uniqueResult();
		if(result!=null)
			return (Integer)result;
		else
			return 0;
	}
	
	public Object[] getServiciosSucontratados(long nro, long nropto) throws RemoteException {
		/*Session session = HibernateUtil.abrirSession();

		List list = session.createSQLQuery("SELECT ppto_ss_cantidad, ppto_ss_servicio, ppto_ss_dias, "+
				"ppto_ss_descuento, ppto_ss_preciodto, ppto_ss_detalle, " +
				"(select gc_costo from tx_gastos_contrataciones where ppto_s_nroppto = gc_ppto_nroppto and els_descripcion = gc_nombre_sala and (select substring(gc_detalle,17))=ppto_ss_detalle) as costo, " +
				"(select gc_codproveedor from tx_gastos_contrataciones where ppto_s_nroppto = gc_ppto_nroppto and els_descripcion = gc_nombre_sala and (select substring(gc_detalle,17))=ppto_ss_detalle) as proveedor, " +
				"(select gc_id from tx_gastos_contrataciones where ppto_s_nroppto = gc_ppto_nroppto and els_descripcion = gc_nombre_sala and (select substring(gc_detalle,17))=ppto_ss_detalle) as codSubcontratado " +
				"FROM tx_ppto_salas_servicios "+
				"inner join tx_ppto_salas on ppto_ss_pls = ppto_s_id "+
				"inner join mst_evt_lugar_salas on ppto_s_codlugsala=els_codlugsala "+
				"where ppto_s_codlugsala=? and ppto_s_nroppto= ? and ppto_ss_modalidad = 2")
		.addScalar("ppto_ss_cantidad", Hibernate.STRING)//0
		.addScalar("ppto_ss_servicio", Hibernate.STRING)//1
		.addScalar("ppto_ss_dias", Hibernate.STRING)//2
		.addScalar("ppto_ss_descuento", Hibernate.STRING)//3
		.addScalar("ppto_ss_preciodto", Hibernate.STRING)//4
		.addScalar("ppto_ss_detalle", Hibernate.STRING)//5
		.addScalar("costo", Hibernate.DOUBLE)//6
		.addScalar("proveedor", Hibernate.LONG)//7
		.addScalar("codSubcontratado", Hibernate.LONG)//8
		.setLong(0, nro)
		.setLong(1, nropto)
		.list();
		
		HibernateUtil.cerrarSession(session);
		return CollectionUtil.listToObjectArray(list);*/
		Session session = HibernateUtil.abrirSession();

		List list = session.createSQLQuery("SELECT ppto_ss_cantidad, ppto_ss_servicio, ppto_ss_dias, "+
				"ppto_ss_descuento, ppto_ss_preciodto, ppto_ss_detalle, " +				
				"gc_costo,gc_codproveedor,gc_id, "+
				"ppto_ss_modalidad, si_descripcion, ppto_ss_id, es.descripcion  "+
				"FROM tx_ppto_salas_servicios "+
				"inner join tx_ppto_salas on ppto_ss_pls = ppto_s_id "+
				"inner join mst_evt_lugar_salas on ppto_s_codlugsala=els_codlugsala "+
				"inner join mst_servicios_idioma on si_codservicio = ppto_ss_servicio "+
				"left join tx_gastos_contrataciones on (select substring(gc_detalle,17))=ppto_ss_detalle and gc_ppto_nroppto= ? "+
				"left join mst_estado_servicio es on id_est_servicio=gc_estado_serv "+
				"where ppto_s_codlugsala=? and ppto_s_nroppto= ?")
		.addScalar("ppto_ss_cantidad", Hibernate.STRING)//0
		.addScalar("ppto_ss_servicio", Hibernate.STRING)//1
		.addScalar("ppto_ss_dias", Hibernate.STRING)//2
		.addScalar("ppto_ss_descuento", Hibernate.STRING)//3
		.addScalar("ppto_ss_preciodto", Hibernate.DOUBLE)//4
		.addScalar("ppto_ss_detalle", Hibernate.STRING)//5
		.addScalar("gc_costo", Hibernate.DOUBLE)//6
		.addScalar("gc_codproveedor", Hibernate.LONG)//7
		.addScalar("gc_id", Hibernate.LONG)//8
		.addScalar("ppto_ss_modalidad", Hibernate.INTEGER)//9
		.addScalar("si_descripcion", Hibernate.STRING)//10
		.addScalar("ppto_ss_id", Hibernate.LONG)//11
		.addScalar("es.descripcion", Hibernate.STRING)//12
		.setLong(0, nropto)
		.setLong(1, nro)
		.setLong(2, nropto)
		.list();
		
		HibernateUtil.cerrarSession(session);
		return CollectionUtil.listToObjectArray(list);
	}
	
	public boolean grabarGastoSubcontratacion(long codGasto, double costo, long codProv, String estado) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		
		int result = session.createQuery("update Ppto_GastoSC set costo = ?, " +
				"proveedor = ?, estado = ? where id = ? ")				
				.setDouble(0, costo)
				.setLong(1, codProv)
				.setString(2, estado)
				.setLong(3, codGasto)
				.executeUpdate();
		HibernateUtil.cerrarSession(session);
		if(result>0)
			return true;
		return false;
	}
	
	public String assignID(Session session) {

		String query = "select max(g.id) + 1 from Ppto_GastoSC g";
		
		Object result = session.createQuery(query)		
						.uniqueResult();
		
		String codigo = result != null ? result.toString():"1";

		return codigo;
	}
	
	private static final Log log = LogFactory.getLog(GastosManager.class);
	
	public void guardarServicioSubcontratado(Subcontratado subc, long codSalaServicio) throws RemoteException{
		Subcontratado suc = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			if (StringUtils.isBlank(subc.getCodigo())) {
				suc = new Subcontratado();				
				HibernateUtil.assignID(session,suc);
				
			} else {
				suc = (Subcontratado) session.get(Subcontratado.class, subc.getCodigo());
			}

			suc.setCantidad(subc.getCantidad());
			suc.setCosto(subc.getCosto());
			suc.setDetalle(subc.getDetalle());
			suc.setEstado(subc.getEstado());
			suc.setPrecio(subc.getPrecio());
			if(subc.getProveedor() != null)
				suc.setProveedor(subc.getProveedor());
			suc.setSala(subc.getSala());
		    suc.setNroPpto(subc.getNroPpto());
		    if(marcarSubcontratado(codSalaServicio,subc.getDetalle().substring(16, subc.getDetalle().length()),session))
		    	session.saveOrUpdate(suc);			
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
	
	private boolean marcarSubcontratado(long codSalaServicio, String nombre, Session session) throws HibernateException{

			int result = session.createQuery("update Ppto_Sala_Servicio set " +
			"modalidad = 2, detalle =:nom where id=:nro ")
			.setString("nom",nombre)
			.setLong("nro", codSalaServicio)			
			.executeUpdate();				
			

			if(result>0)
				return true;
			else return false;
		
	}
	
	public Object[] getCostosXServicio(long codServicio) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
				
				StringBuffer query = new StringBuffer("SELECT sc_armado, sc_costo_hs_hombre, sc_operacion, sc_costo_unitario, sc_duracion_hs, " +
						"c_horas_fijas, c_pesos_hs_hombre, c_pesos_hs_extras, c_margen_hs_evt FROM mst_servicios_costos c "+
						"inner join mst_costos on c_id= 1 "+
						"where ");
						
				query.append("sc_id = "+ codServicio);
				
				List presupuestos = session.createSQLQuery(
						query.toString()
						)
						.addScalar("sc_armado", Hibernate.DOUBLE) //1
						.addScalar("sc_costo_hs_hombre", Hibernate.DOUBLE) //2
						.addScalar("sc_operacion", Hibernate.DOUBLE) //3
						.addScalar("sc_costo_unitario", Hibernate.DOUBLE) //4
						.addScalar("sc_duracion_hs", Hibernate.DOUBLE) //5
						.addScalar("c_horas_fijas", Hibernate.DOUBLE) //6
						.addScalar("c_pesos_hs_hombre", Hibernate.DOUBLE) //7
						.addScalar("c_pesos_hs_extras", Hibernate.DOUBLE) //8
						.addScalar("c_margen_hs_evt", Hibernate.DOUBLE) //9		
						.list();
				
				HibernateUtil.cerrarSession(session);
				
				return CollectionUtil.listToObjectArray(presupuestos);
			}
}
