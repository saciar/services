package crm.services.transaction;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;

import crm.libraries.abm.entities.EstadoEvento;
import crm.libraries.abm.entities.PptoCambioEstado;
import crm.libraries.abm.entities.PptoEstadoActual;
import crm.libraries.abm.entities.Ppto_Facturas;
import crm.libraries.abm.entities.Ppto_Sala;
import crm.libraries.abm.entities.Presupuesto;
import crm.libraries.abm.entities.Usuario;
import crm.libraries.abm.entities.Vendedor;
import crm.libraries.abm.helper.PresupuestoHelper;
import crm.services.report.manager.OrdenFacturacionReport;
import crm.services.sei.EstadoEventoManagerSEI;
import crm.services.sei.PresupuestosManagerSEI;
import crm.services.sei.TipoEventoManagerSEI;
import crm.services.util.CollectionUtil;
import crm.services.util.DateConverter;
import crm.services.util.HibernateUtil;
import crm.services.util.PresupuestoUtil;

public class PresupuestosManager implements PresupuestosManagerSEI,ManagerService {
	private static final Log log = LogFactory.getLog(PresupuestosManager.class);
	
	private static final long SYS_SETTINGS_VENCIMIENTO_OF = 4;
	
	private Session sesion;
	
	public Object[] buscar(String clienteEvt, String clienteFact, String fechaDesde, String fechaHasta, String lugar,
			String vendedor, String uc, String estado, String tipoEvt, String nombreEvt) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		
		StringBuffer query = new StringBuffer("SELECT t.ppto_nroppto AS nroppto, ven.apynom AS vendedor, "+
		"cli.nombre_fantasia AS nombreFantasia, t.ppto_evt_nombre AS evtNombre,ua_descripcion as unidadAdm, "+
		"t.ppto_fecinicio AS fecha, ea.estado AS estado FROM tx_ppto t "+
		"INNER JOIN tx_vendedor_ppto v ON v.vp_nroppto = t.ppto_nroppto "+
		"INNER JOIN mst_vendedores ven ON ven.vd_codvend = v.vp_vendedor "+
		"inner join mst_unidades_vendedores u on ven.vd_codvend=ucv_codvendedor "+
		//"inner join mst_unidades_comerciales u on ucv_codunidad=uc_codunidad "+
		"INNER JOIN vw_pptos_estados ea ON ea.nroppto = t.ppto_nroppto "+
		"inner join mst_cliente cli on cli.cl_codcliente = ppto_codcliente "+
		"left JOIN tx_ppto_facturacion tf ON tf.pf_nroppto = t.ppto_nroppto "+
		"left join mst_unidades_administrativas adm on tf.pf_codadmin = ua_codunidad "+
		//"inner join TX_RENTABILIDAD r on t.ppto_nroppto = r.rt_nroppto "+
		"WHERE ");
		
		if(clienteEvt != null)
			query.append("t.ppto_codcliente = "+ clienteEvt+" and ");
		if(clienteFact != null)
			query.append("cli.cl_codcliente = "+ clienteFact+" and ");
		if(fechaDesde != null && fechaHasta != null)
			//query.append("t.ppto_codcliente = "+ clienteEvt);
			query.append("((t.ppto_fecinicio >= '"+fechaDesde+"' and t.ppto_fecfin<= '"+fechaHasta+"') "+
					"or (t.ppto_fecinicio <= '"+fechaHasta+"' and t.ppto_fecfin > '"+fechaHasta+"') "+
					"or(t.ppto_fecinicio< '"+fechaDesde+"' and t.ppto_fecfin>= '"+fechaDesde+"')) and ");
		if(lugar != null)
			query.append("t.ppto_evt_lugar = "+ lugar+" and ");
		if(vendedor != null)
			query.append("v.vp_vendedor = "+ vendedor+" and ");
		if(uc != null)
			query.append("u.ucv_codunidad= "+ uc+" and ");
		if(estado != null)
			query.append("ea.estado = "+ estado+" and ");
		if(tipoEvt != null)
			query.append("t.ppto_evt_tipo = "+ tipoEvt+" and ");
		if(nombreEvt != null)
			query.append("t.ppto_evt_nombre like '%"+ nombreEvt+"%' and ");
		
		query.append("t.ppto_nroppto is not null group by nroppto ORDER BY vendedor,estado");
		List presupuestos = session.createSQLQuery(
				query.toString()
				)
				.addScalar("nroppto", Hibernate.LONG)
				.addScalar("vendedor", Hibernate.STRING)
				.addScalar("nombreFantasia", Hibernate.STRING)
				.addScalar("evtNombre", Hibernate.STRING)
				.addScalar("fecha", Hibernate.STRING)
				.addScalar("estado", Hibernate.STRING)
				//.addScalar("monto", Hibernate.DOUBLE)
				.addScalar("unidadAdm", Hibernate.STRING)
				.list();
		
		HibernateUtil.cerrarSession(session);
		
		return CollectionUtil.listToObjectArray(presupuestos);

	}
	
	public Object[] buscarParaReportesRentabilidadCostos(String nroppto) throws RemoteException{	
		Session session = HibernateUtil.abrirSession();
		
		StringBuffer query = new StringBuffer("SELECT ppto_ss_servicio, ppto_ss_preciodto, ppto_ss_cantidad, ppto_s_fecinicio, ppto_s_fecfin, "+
				"sc_armado, sc_costo_hs_hombre, sc_operacion, sc_costo_unitario, sc_duracion_hs, " +
				"c_horas_fijas, c_pesos_hs_hombre, c_pesos_hs_extras, c_margen_hs_evt, ppto_s_fechora_prueba, ppto_s_fechora_desarme, ppto_s_id FROM tx_ppto_salas_servicios t "+
				"inner join tx_ppto_salas on ppto_ss_pls=ppto_s_id "+
				"inner join mst_servicios_costos c on sc_id=ppto_ss_servicio "+
				"inner join mst_costos on c_id= 1 "+
				"where ");
				
		query.append("ppto_s_nroppto = "+ nroppto+" ORDER BY ppto_s_nroppto");
		
		List presupuestos = session.createSQLQuery(
				query.toString()
				)
				.addScalar("ppto_ss_servicio", Hibernate.LONG) //0
				.addScalar("ppto_ss_preciodto", Hibernate.DOUBLE) //1
				.addScalar("ppto_ss_cantidad", Hibernate.INTEGER) //2
				.addScalar("ppto_s_fecinicio", Hibernate.STRING) //3
				.addScalar("ppto_s_fecfin", Hibernate.STRING) //4
				.addScalar("sc_armado", Hibernate.DOUBLE) //5
				.addScalar("sc_costo_hs_hombre", Hibernate.DOUBLE) //6
				.addScalar("sc_operacion", Hibernate.DOUBLE) //7
				.addScalar("sc_costo_unitario", Hibernate.DOUBLE) //8
				.addScalar("sc_duracion_hs", Hibernate.DOUBLE) //9
				.addScalar("c_horas_fijas", Hibernate.DOUBLE) //10
				.addScalar("c_pesos_hs_hombre", Hibernate.DOUBLE) //11
				.addScalar("c_pesos_hs_extras", Hibernate.DOUBLE) //12
				.addScalar("c_margen_hs_evt", Hibernate.DOUBLE) //13
				.addScalar("ppto_s_fechora_prueba", Hibernate.STRING) //14
				.addScalar("ppto_s_fechora_desarme", Hibernate.STRING) //15
				.addScalar("ppto_s_id", Hibernate.LONG) //16			
				.list();
		
		HibernateUtil.cerrarSession(session);
		
		return CollectionUtil.listToObjectArray(presupuestos);
	}
	
	public Object[] buscarHorariosCostos(String nroppto) throws RemoteException{	
		Session session = HibernateUtil.abrirSession();
		
		StringBuffer query = new StringBuffer("SELECT sh_fecha, sh_hora_desde, sh_hora_hasta FROM tx_ppto_salas t "+
		"inner join tx_ppto_salas_horarios h on sh_codsala = ppto_s_id " +		
		"where ");
				
		query.append("ppto_s_nroppto = "+ nroppto+" ORDER BY ppto_s_nroppto");
		
		List presupuestos = session.createSQLQuery(
				query.toString()
				)
				.addScalar("sh_fecha", Hibernate.STRING)
				.addScalar("sh_hora_desde", Hibernate.STRING)
				.addScalar("sh_hora_hasta", Hibernate.STRING)	
				.list();
		
		HibernateUtil.cerrarSession(session);
		
		return CollectionUtil.listToObjectArray(presupuestos);
	}
	
	public Object[] buscarParaReportesComercial(String clienteEvt, String fechaDesde, String fechaHasta, String lugar,
			String estado, String vendedor) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		
		StringBuffer query = new StringBuffer("SELECT t.ppto_nroppto AS nroppto, "+
		"cli.nombre_fantasia AS nombreFantasia, t.ppto_evt_nombre AS evtNombre, "+
		"t.ppto_fecinicio AS fecha, ea.estado AS estado,r.rt_fact_orig + r.rt_fact_extras as monto, " +
		"v.vp_fec_creacion as fechacreacion, l.el_nombrelugar as lugar, t.ppto_fecfin AS fechafin, t.ppto_evt_lugar as codLugar FROM tx_ppto t "+
		"INNER JOIN tx_vendedor_ppto v ON v.vp_nroppto = t.ppto_nroppto "+
		"INNER JOIN vw_pptos_estados ea ON ea.nroppto = t.ppto_nroppto "+
		"inner join mst_cliente cli on cli.cl_codcliente = ppto_codcliente "+
		"inner join TX_RENTABILIDAD r on t.ppto_nroppto = r.rt_nroppto "+
		"inner join mst_evt_lugar l on t.ppto_evt_lugar = l.el_codlugar "+
		"WHERE ");
		if(vendedor != null)
			query.append("v.vp_vendedor = "+ vendedor + " and ");
		if(clienteEvt != null)
			query.append("t.ppto_codcliente = "+ clienteEvt+" and ");		
		if(fechaDesde != null && fechaHasta != null)
			query.append("((t.ppto_fecinicio >= '"+fechaDesde+"' and t.ppto_fecfin<= '"+fechaHasta+"') "+
					"or (t.ppto_fecinicio <= '"+fechaHasta+"' and t.ppto_fecfin > '"+fechaHasta+"') "+
					"or(t.ppto_fecinicio< '"+fechaDesde+"' and t.ppto_fecfin>= '"+fechaDesde+"')) and ");
		if(lugar != null)
			query.append("t.ppto_evt_lugar = "+ lugar+" and ");		
		if(estado != null){
			if(estado.equals("Confirmado"))
				query.append("(ea.estado <> 'Pendiente' and ea.estado <> 'Cancelado' and ea.estado <> 'Rechazado') and ");
			else if(estado.equals("Pendiente"))
				query.append("ea.estado = 'Pendiente' and ");
			else if(estado.equals("Cancelado"))
				query.append("ea.estado = 'Cancelado' and ");
			else if(estado.equals("Rechazado"))
				query.append("ea.estado = 'Rechazado' and ");
			else if(estado.equals("Orden de Facturacion"))
				query.append("ea.estado = 'Orden de Facturacion' and ");
			else if(estado.equals("Orden de Servicio"))
				query.append("ea.estado = 'Orden de Servicio' and ");
			else if(estado.equals("Cobrado"))
				query.append("ea.estado = 'Cobrado' and ");
			else if(estado.equals("Facturado"))
				query.append("ea.estado = 'Facturado' and ");
		}

		query.append("t.ppto_nroppto is not null group by nroppto ORDER BY estado,fecha");
		List presupuestos = session.createSQLQuery(
				query.toString()
				)
				.addScalar("nroppto", Hibernate.LONG)//0
				.addScalar("nombreFantasia", Hibernate.STRING)//1
				.addScalar("evtNombre", Hibernate.STRING)//2
				.addScalar("fecha", Hibernate.STRING)//3
				.addScalar("estado", Hibernate.STRING)//4
				.addScalar("monto", Hibernate.DOUBLE)//5
				.addScalar("fechacreacion", Hibernate.STRING)//6
				.addScalar("lugar", Hibernate.STRING)//7
				.addScalar("fechafin", Hibernate.STRING)//8
				.addScalar("codLugar", Hibernate.STRING)//9
				.list();
		
		HibernateUtil.cerrarSession(session);
		
		return CollectionUtil.listToObjectArray(presupuestos);

	}
	
	public Object[] buscarOperadoresPorPpto(int nroPpto, String fecha) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		
		StringBuffer query = new StringBuffer("SELECT t.ppto_op_operador AS operador "+
		"WHERE ");

		query.append("t.ppto_op_nroppto	= "+nroPpto+" and t.ppto_op_fecha = '"+fecha+"' ");

		List presupuestos = session.createSQLQuery(
				query.toString()
				)
				.addScalar("operador", Hibernate.LONG) //0
				.list();
		
		HibernateUtil.cerrarSession(session);
		
		return CollectionUtil.listToObjectArray(presupuestos);
	}
	
	public Object[] buscarPptosParaOperadores(String fechaDesde, String fechaHasta) throws RemoteException{
		
		Session session = HibernateUtil.abrirSession();
		
		StringBuffer query = new StringBuffer("SELECT t.ppto_nroppto AS nroppto, "+
		"t.ppto_evt_nombre AS evtNombre, "+
		"l.el_nombrelugar as lugar FROM tx_ppto t "+
		"inner join mst_evt_lugar l on t.ppto_evt_lugar = l.el_codlugar "+
		"INNER JOIN vw_pptos_estados ea ON ea.nroppto = t.ppto_nroppto "+
		"WHERE ");

		if(fechaDesde != null && fechaHasta != null)
			query.append("((t.ppto_fecinicio >= '"+fechaDesde+"' and t.ppto_fecfin<= '"+fechaHasta+"') "+
					"or (t.ppto_fecinicio <= '"+fechaHasta+"' and t.ppto_fecfin > '"+fechaHasta+"') "+
					"or(t.ppto_fecinicio< '"+fechaDesde+"' and t.ppto_fecfin>= '"+fechaDesde+"')) and ");

		query.append("(ea.estado <> 'Pendiente' and ea.estado <> 'Cancelado' and ea.estado <> 'Rechazado') and ");

		query.append("t.ppto_nroppto is not null group by nroppto ORDER BY nroppto");
		List presupuestos = session.createSQLQuery(
				query.toString()
				)
				.addScalar("nroppto", Hibernate.LONG) //0
				.addScalar("evtNombre", Hibernate.STRING) //1
				.addScalar("lugar", Hibernate.STRING) //2
				.list();
		
		HibernateUtil.cerrarSession(session);
		
		return CollectionUtil.listToObjectArray(presupuestos);

	}
	
	public Object[] buscarParaReportesGerencia(String clienteEvt,String fechaDesde, String fechaHasta, String lugar,
			String estado, String tipoEvt, String vend) throws RemoteException{
		
		String[] vendArray = vend.split(",");
		String[] tiposArray = tipoEvt.split(",");
		
		Session session = HibernateUtil.abrirSession();
		
		StringBuffer query = new StringBuffer("SELECT t.ppto_nroppto AS nroppto, ven.apynom AS vendedor, "+
		"cli.nombre_fantasia AS nombreFantasia, t.ppto_evt_nombre AS evtNombre, "+
		"t.ppto_fecinicio AS fecha, ea.estado AS estado,r.rt_fact_orig + r.rt_fact_extras as monto, " +
		"l.el_nombrelugar as lugar, t.ppto_fecfin AS fechafin, te.et_descripcion as tipoEvento FROM tx_ppto t "+
		"INNER JOIN tx_vendedor_ppto v ON v.vp_nroppto = t.ppto_nroppto "+
		"INNER JOIN mst_vendedores ven ON ven.vd_codvend = v.vp_vendedor "+
		"INNER JOIN vw_pptos_estados ea ON ea.nroppto = t.ppto_nroppto "+
		"inner join mst_cliente cli on cli.cl_codcliente = ppto_codcliente "+
		"inner join TX_RENTABILIDAD r on t.ppto_nroppto = r.rt_nroppto "+
		"inner join mst_evt_lugar l on t.ppto_evt_lugar = l.el_codlugar "+
		"inner join mst_evt_tipo te on t.ppto_evt_tipo = te.et_codtipo "+
		"WHERE ");
		
		if(clienteEvt != null)
			query.append("t.ppto_codcliente = "+ clienteEvt+" and ");

		if(fechaDesde != null && fechaHasta != null)
			query.append("((t.ppto_fecinicio >= '"+fechaDesde+"' and t.ppto_fecfin<= '"+fechaHasta+"') "+
					"or (t.ppto_fecinicio <= '"+fechaHasta+"' and t.ppto_fecfin > '"+fechaHasta+"') "+
					"or(t.ppto_fecinicio< '"+fechaDesde+"' and t.ppto_fecfin>= '"+fechaDesde+"')) and ");
		if(lugar != null){
			if(!lugar.equals("0"))
				query.append("t.ppto_evt_lugar = "+ lugar+" and ");	
			else query.append("t.ppto_evt_lugar <> 21 and ");	
		}
			
		if(estado != null){
			if(estado.equals("Confirmado"))
				query.append("(ea.estado <> 'Pendiente' and ea.estado <> 'Cancelado' and ea.estado <> 'Rechazado') and ");
			else if(estado.equals("Pendiente"))
				query.append("ea.estado = 'Pendiente' and ");
			else if(estado.equals("Cancelado"))
				query.append("ea.estado = 'Cancelado' and ");
			else if(estado.equals("Rechazado"))
				query.append("ea.estado = 'Rechazado' and ");
			else if(estado.equals("Orden de Facturacion"))
				query.append("ea.estado = 'Orden de Facturacion' and ");
			else if(estado.equals("Orden de Servicio"))
				query.append("ea.estado = 'Orden de Servicio' and ");
			else if(estado.equals("Cobrado"))
				query.append("ea.estado = 'Cobrado' and ");
			else if(estado.equals("Facturado"))
				query.append("ea.estado = 'Facturado' and ");
		}

		if(vendArray.length>0)
			query.append("(");
		for(int i =0;i<vendArray.length;i++){
			query.append("v.vp_vendedor = "+vendArray[i]);			
			if(i==vendArray.length-1){
				query.append(") and ");
			}
			else
				query.append(" or ");
		}
		
		if(tiposArray.length>0)
			query.append("(");
		for(int i =0;i<tiposArray.length;i++){
			query.append("t.ppto_evt_tipo= "+tiposArray[i]);			
			if(i==tiposArray.length-1){
				query.append(") and ");
			}
			else
				query.append(" or ");
		}
		
		query.append("t.ppto_nroppto is not null group by nroppto ORDER BY vendedor,estado");
		List presupuestos = session.createSQLQuery(
				query.toString()
				)
				.addScalar("nroppto", Hibernate.LONG) //0
				.addScalar("vendedor", Hibernate.STRING) //1
				.addScalar("nombreFantasia", Hibernate.STRING) //2
				.addScalar("evtNombre", Hibernate.STRING) //3
				.addScalar("fecha", Hibernate.STRING) //4
				.addScalar("estado", Hibernate.STRING) //5
				.addScalar("monto", Hibernate.DOUBLE) //6
				.addScalar("lugar", Hibernate.STRING) //7
				.addScalar("fechafin", Hibernate.STRING) //8
				.addScalar("tipoEvento", Hibernate.STRING) //9
				.list();
		
		HibernateUtil.cerrarSession(session);
		
		return CollectionUtil.listToObjectArray(presupuestos);

	}
	
	public Object[] buscarComparacionesGerenciaMeses(String vendedores, String meses, String estado, String tipos, int anio) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		
		String[] vendArray = vendedores.split(",");
		String[] mesesArray = meses.split(",");
		String[] tiposArray = tipos.split(",");
		
		
		StringBuffer query = new StringBuffer("SELECT ven.apynom AS vendedor, "+
				"sum(r.rt_fact_orig + r.rt_fact_extras) as monto, " +
				"count(ven.vd_codvend) as cantidad, month(ppto_fecinicio) as mes FROM tx_ppto t "+
				"INNER JOIN tx_vendedor_ppto v ON v.vp_nroppto = t.ppto_nroppto "+
				"INNER JOIN mst_vendedores ven ON ven.vd_codvend = v.vp_vendedor "+
				"INNER JOIN vw_pptos_estados ea ON ea.nroppto = t.ppto_nroppto "+
				"inner join TX_RENTABILIDAD r on t.ppto_nroppto = r.rt_nroppto "+
				"WHERE ");

		if(mesesArray.length>0)
			query.append("(");
		for(int i=0;i<mesesArray.length;i++){
			String fechaDesde;
			String fechaHasta;

			fechaDesde = anio+"-"+mesesArray[i]+"-01";
			fechaHasta = anio+"-"+mesesArray[i]+"-31";

			if(fechaDesde != null && fechaHasta != null)
				query.append("((t.ppto_fecinicio >= '"+fechaDesde+"' and t.ppto_fecfin<= '"+fechaHasta+"') "+
						"or (t.ppto_fecinicio <= '"+fechaHasta+"' and t.ppto_fecfin > '"+fechaHasta+"') "+
						"or(t.ppto_fecinicio< '"+fechaDesde+"' and t.ppto_fecfin>= '"+fechaDesde+"'))");
			if(i==mesesArray.length-1){
				query.append(") and ");
			}
			else
				query.append(" or ");
			
		}
		if(vendArray.length>0)
			query.append("(");
		for(int i =0;i<vendArray.length;i++){
			query.append("v.vp_vendedor = "+vendArray[i]);			
			if(i==vendArray.length-1){
				query.append(") and ");
			}
			else
				query.append(" or ");
		}
		
		if(tiposArray.length>0)
			query.append("(");
		for(int i =0;i<tiposArray.length;i++){
			query.append("t.ppto_evt_tipo= "+tiposArray[i]);			
			if(i==tiposArray.length-1){
				query.append(") and ");
			}
			else
				query.append(" or ");
		}
		if(estado != null){
			if(estado.equals("Confirmado"))
				query.append("(ea.estado <> 'Pendiente' and ea.estado <> 'Cancelado' and ea.estado <> 'Rechazado') and ");
			else if(estado.equals("Pendiente"))
				query.append("ea.estado = 'Pendiente' and ");
			else if(estado.equals("Cancelado"))
				query.append("ea.estado = 'Cancelado' and ");
			else if(estado.equals("Rechazado"))
				query.append("ea.estado = 'Rechazado' and ");
			else if(estado.equals("Orden de Facturacion"))
				query.append("ea.estado = 'Orden de Facturacion' and ");
			else if(estado.equals("Orden de Servicio"))
				query.append("ea.estado = 'Orden de Servicio' and ");
			else if(estado.equals("Cobrado"))
				query.append("ea.estado = 'Cobrado' and ");
			else if(estado.equals("Facturado"))
				query.append("ea.estado = 'Facturado' and ");
		}
		query.append("t.ppto_nroppto is not null group by vendedor,mes ORDER BY vendedor,mes");
		
		List presupuestos = session.createSQLQuery(query.toString())
				.addScalar("vendedor", Hibernate.STRING) //0
				.addScalar("monto", Hibernate.DOUBLE) //1
				.addScalar("cantidad", Hibernate.INTEGER) //2
				.addScalar("mes", Hibernate.STRING)
				.list();
		
		HibernateUtil.cerrarSession(session);
		
		return CollectionUtil.listToObjectArray(presupuestos);
		
	}
	
	public Object[] buscarComparacionesGerenciaAnio(String vendedores, String anios, String estado, String tipos, int mes) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		
		String[] vendArray = vendedores.split(",");
		String[] aniosArray = anios.split(",");
		String[] tiposArray = new String[0];
		if(!tipos.isEmpty())
			tiposArray = tipos.split(",");
		
		
		StringBuffer query = new StringBuffer("SELECT ven.apynom AS vendedor, "+
				"sum(r.rt_fact_orig + r.rt_fact_extras) as monto, " +
				"count(ven.vd_codvend) as cantidad, year(ppto_fecfin) as anio FROM tx_ppto t "+
				"INNER JOIN tx_vendedor_ppto v ON v.vp_nroppto = t.ppto_nroppto "+
				"INNER JOIN mst_vendedores ven ON ven.vd_codvend = v.vp_vendedor "+
				"INNER JOIN vw_pptos_estados ea ON ea.nroppto = t.ppto_nroppto "+
				"inner join TX_RENTABILIDAD r on t.ppto_nroppto = r.rt_nroppto "+
				"WHERE ");

		if(aniosArray.length>0)
			query.append("(");
		for(int i=0;i<aniosArray.length;i++){
			String fechaDesde;
			String fechaHasta;
			if(mes <0){
				fechaDesde = aniosArray[i]+"-01-01";
				fechaHasta = aniosArray[i]+"-12-31";
			}
			else{
				fechaDesde = aniosArray[i]+"-"+(mes+1)+"-01";
				fechaHasta = aniosArray[i]+"-"+(mes+1)+"-31";
			}
			if(fechaDesde != null && fechaHasta != null)
				query.append("((t.ppto_fecinicio >= '"+fechaDesde+"' and t.ppto_fecfin<= '"+fechaHasta+"') "+
						"or (t.ppto_fecinicio <= '"+fechaHasta+"' and t.ppto_fecfin > '"+fechaHasta+"') "+
						"or(t.ppto_fecinicio< '"+fechaDesde+"' and t.ppto_fecfin>= '"+fechaDesde+"'))");
			if(i==aniosArray.length-1){
				query.append(") and ");
			}
			else
				query.append(" or ");
			
		}
		if(vendArray.length>0)
			query.append("(");
		for(int i =0;i<vendArray.length;i++){
			query.append("v.vp_vendedor = "+vendArray[i]);			
			if(i==vendArray.length-1){
				query.append(") and ");
			}
			else
				query.append(" or ");
		}
		
		if(tiposArray.length>0)
			query.append("(");
		for(int i =0;i<tiposArray.length;i++){
			query.append("t.ppto_evt_tipo= "+tiposArray[i]);			
			if(i==tiposArray.length-1){
				query.append(") and ");
			}
			else
				query.append(" or ");
		}
		if(estado != null){
			if(estado.equals("Confirmado"))
				query.append("(ea.estado <> 'Pendiente' and ea.estado <> 'Cancelado' and ea.estado <> 'Rechazado') and ");
			else if(estado.equals("Pendiente"))
				query.append("ea.estado = 'Pendiente' and ");
			else if(estado.equals("Cancelado"))
				query.append("ea.estado = 'Cancelado' and ");
			else if(estado.equals("Rechazado"))
				query.append("ea.estado = 'Rechazado' and ");
			else if(estado.equals("Orden de Facturacion"))
				query.append("ea.estado = 'Orden de Facturacion' and ");
			else if(estado.equals("Orden de Servicio"))
				query.append("ea.estado = 'Orden de Servicio' and ");
			else if(estado.equals("Cobrado"))
				query.append("ea.estado = 'Cobrado' and ");
			else if(estado.equals("Facturado"))
				query.append("ea.estado = 'Facturado' and ");
		}
		query.append("t.ppto_nroppto is not null group by vendedor,anio ORDER BY vendedor,monto");
		
		List presupuestos = session.createSQLQuery(query.toString())
				.addScalar("vendedor", Hibernate.STRING) //0
				.addScalar("monto", Hibernate.DOUBLE) //1
				.addScalar("cantidad", Hibernate.INTEGER) //2
				.addScalar("anio", Hibernate.STRING)
				.list();
		
		HibernateUtil.cerrarSession(session);
		
		return CollectionUtil.listToObjectArray(presupuestos);
		
	}
	
	public Object[] buscarParaReportesGerenciaVendedores(String clienteEvt,String fechaDesde, String fechaHasta, String lugar,
			String estado, String tipoEvt, String vend) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		
		String[] tiposArray = tipoEvt.split(",");
		
		StringBuffer query = new StringBuffer("SELECT ven.apynom AS vendedor, "+
		"sum(r.rt_fact_orig + r.rt_fact_extras) as monto, " +
		"count(ven.vd_codvend) as cantidad  FROM tx_ppto t "+
		"INNER JOIN tx_vendedor_ppto v ON v.vp_nroppto = t.ppto_nroppto "+
		"INNER JOIN mst_vendedores ven ON ven.vd_codvend = v.vp_vendedor "+
		"INNER JOIN vw_pptos_estados ea ON ea.nroppto = t.ppto_nroppto "+
		"inner join TX_RENTABILIDAD r on t.ppto_nroppto = r.rt_nroppto "+
		"WHERE ");
		
		if(clienteEvt != null)
			query.append("t.ppto_codcliente = "+ clienteEvt+" and ");

		if(fechaDesde != null && fechaHasta != null)
			query.append("((t.ppto_fecinicio >= '"+fechaDesde+"' and t.ppto_fecfin<= '"+fechaHasta+"') "+
					"or (t.ppto_fecinicio <= '"+fechaHasta+"' and t.ppto_fecfin > '"+fechaHasta+"') "+
					"or(t.ppto_fecinicio< '"+fechaDesde+"' and t.ppto_fecfin>= '"+fechaDesde+"')) and ");
		if(lugar != null)
			query.append("t.ppto_evt_lugar = "+ lugar+" and ");		
		if(estado != null){
			if(estado.equals("Confirmado"))
				query.append("(ea.estado <> 'Pendiente' and ea.estado <> 'Cancelado' and ea.estado <> 'Rechazado') and ");
			else if(estado.equals("Pendiente"))
				query.append("ea.estado = 'Pendiente' and ");
			else if(estado.equals("Cancelado"))
				query.append("ea.estado = 'Cancelado' and ");
			else if(estado.equals("Rechazado"))
				query.append("ea.estado = 'Rechazado' and ");
			else if(estado.equals("Orden de Facturacion"))
				query.append("ea.estado = 'Orden de Facturacion' and ");
			else if(estado.equals("Orden de Servicio"))
				query.append("ea.estado = 'Orden de Servicio' and ");
			else if(estado.equals("Cobrado"))
				query.append("ea.estado = 'Cobrado' and ");
			else if(estado.equals("Facturado"))
				query.append("ea.estado = 'Facturado' and ");
		}
		
		if(tipoEvt != null)
			query.append("(");
		for(int i =0;i<tiposArray.length;i++){
			query.append("t.ppto_evt_tipo = "+tiposArray[i]);			
			if(i==tiposArray.length-1){
				query.append(") and ");
			}
			else
				query.append(" or ");
		}

		query.append("t.ppto_nroppto is not null group by vendedor ORDER BY vendedor,monto");
		List presupuestos = session.createSQLQuery(
				query.toString()
				)
				.addScalar("vendedor", Hibernate.STRING) //0
				.addScalar("monto", Hibernate.DOUBLE) //1
				.addScalar("cantidad", Hibernate.INTEGER) //2
				.list();
		
		HibernateUtil.cerrarSession(session);
		
		return CollectionUtil.listToObjectArray(presupuestos);

	}
	
	public Object[] buscarParaReportes(String clienteEvt, String clienteFact, String fechaDesde, String fechaHasta, String lugar,
			String condpago, String estado, String tipoEvt, String nombreEvt, String codFacturacion) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		
		StringBuffer query = new StringBuffer("SELECT t.ppto_nroppto AS nroppto, ven.apynom AS vendedor, "+
		"cli.nombre_fantasia AS nombreFantasia, t.ppto_evt_nombre AS evtNombre, "+
		"t.ppto_fecinicio AS fecha, ea.estado AS estado,r.rt_fact_orig + r.rt_fact_extras as monto,ven.vd_codvend as codVendedor, " +
		"v.vp_fec_creacion as fechacreacion, l.el_nombrelugar as lugar, t.ppto_fecfin AS fechafin, t.ppto_evt_lugar as codLugar, ff.fact_descripcion as facturadopor FROM tx_ppto t "+
		"INNER JOIN tx_vendedor_ppto v ON v.vp_nroppto = t.ppto_nroppto "+
		"INNER JOIN mst_vendedores ven ON ven.vd_codvend = v.vp_vendedor "+
		"INNER JOIN vw_pptos_estados ea ON ea.nroppto = t.ppto_nroppto "+
		"inner join mst_cliente cli on cli.cl_codcliente = ppto_codcliente "+
		"INNER JOIN tx_ppto_facturacion tf ON tf.pf_nroppto = t.ppto_nroppto "+
		"inner join TX_RENTABILIDAD r on t.ppto_nroppto = r.rt_nroppto "+
		"inner join mst_evt_lugar l on t.ppto_evt_lugar = l.el_codlugar "+
		"inner join tx_ppto_facturacion pf on pf.pf_nroppto=t.ppto_nroppto "+
		"left join mst_facturaciones ff on fact_id = pf.pf_codadmin "+
		"WHERE ");
		
		if(clienteEvt != null)
			query.append("t.ppto_codcliente = "+ clienteEvt+" and ");
		if(clienteFact != null)
			query.append("cli.cl_codcliente = "+ clienteFact+" and ");
		if(fechaDesde != null && fechaHasta != null)
			query.append("((t.ppto_fecinicio >= '"+fechaDesde+"' and t.ppto_fecfin<= '"+fechaHasta+"') "+
					"or (t.ppto_fecinicio <= '"+fechaHasta+"' and t.ppto_fecfin > '"+fechaHasta+"') "+
					"or(t.ppto_fecinicio< '"+fechaDesde+"' and t.ppto_fecfin>= '"+fechaDesde+"')) and ");
		if(lugar != null)
			query.append("t.ppto_evt_lugar = "+ lugar+" and ");		
		if(condpago != null)
			query.append("t.ppto_rpt_cond_pago= "+ condpago+" and ");
		if(estado != null){
			if(estado.equals("Confirmado"))
				query.append("(ea.estado <> 'Pendiente' and ea.estado <> 'Cancelado' and ea.estado <> 'Rechazado') and ");
			else if(estado.equals("Pendiente"))
				query.append("ea.estado = 'Pendiente' and ");
			else if(estado.equals("Cancelado"))
				query.append("ea.estado = 'Cancelado' and ");
			else if(estado.equals("Rechazado"))
				query.append("ea.estado = 'Rechazado' and ");
			else if(estado.equals("Orden de Facturacion"))
				query.append("ea.estado = 'Orden de Facturacion' and ");
			else if(estado.equals("Orden de Servicio"))
				query.append("ea.estado = 'Orden de Servicio' and ");
			else if(estado.equals("Cobrado"))
				query.append("ea.estado = 'Cobrado' and ");
			else if(estado.equals("Facturado"))
				query.append("ea.estado = 'Facturado' and ");
		}
		
		if(codFacturacion !=null)
			query.append("pf.pf_codadmin = "+ codFacturacion+" and ");
			
		query.append("t.ppto_nroppto is not null group by nroppto ORDER BY vendedor,estado");
		List presupuestos = session.createSQLQuery(
				query.toString()
				)
				.addScalar("nroppto", Hibernate.LONG)
				.addScalar("vendedor", Hibernate.STRING)
				.addScalar("nombreFantasia", Hibernate.STRING)
				.addScalar("evtNombre", Hibernate.STRING)
				.addScalar("fecha", Hibernate.STRING)
				.addScalar("estado", Hibernate.STRING)
				.addScalar("monto", Hibernate.DOUBLE)
				.addScalar("codVendedor", Hibernate.STRING)
				.addScalar("fechacreacion", Hibernate.STRING)
				.addScalar("lugar", Hibernate.STRING)
				.addScalar("fechafin", Hibernate.STRING)
				.addScalar("codLugar", Hibernate.STRING)
				.addScalar("facturadopor", Hibernate.STRING)
				.list();
		
		HibernateUtil.cerrarSession(session);
		
		return CollectionUtil.listToObjectArray(presupuestos);

	}
	
	public Object[] buscarParaReportesRentabilidad(String clienteEvt, String clienteFact, String fechaDesde, String fechaHasta, String lugar,
			String condpago, String estado, String tipoEvt, String nombreEvt, String vendedores) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		
		String[] vendedoresArray = vendedores.split(",");
		
		StringBuffer query = new StringBuffer("SELECT t.ppto_nroppto AS nroppto, ven.apynom AS vendedor, "+
		"t.ppto_evt_nombre AS evtNombre, "+
		"r.facturacion as monto, ven.vd_codvend as codVendedor, " +
		"sum(gc_costo) as subcontratado, r.OTROS as otros, r.COMISIONLUGAR as comlugar, r.COMISIONTERCEROS as comterceros, " +
		"r.COMISIONCOMERCIAL as comcomercial, r.REGALIAS as regalias FROM tx_ppto t "+
		"INNER JOIN tx_vendedor_ppto v ON v.vp_nroppto = t.ppto_nroppto "+
		"INNER JOIN mst_vendedores ven ON ven.vd_codvend = v.vp_vendedor "+
		"INNER JOIN vw_pptos_estados ea ON ea.nroppto = t.ppto_nroppto "+
		"inner join mst_cliente cli on cli.cl_codcliente = ppto_codcliente "+		
		"inner join vw_rentabilidad_report r on t.ppto_nroppto = r.presupuesto "+
		"inner join mst_evt_lugar l on t.ppto_evt_lugar = l.el_codlugar "+
		"left join tx_gastos_contrataciones sp on gc_ppto_nroppto = t.ppto_nroppto "+ 
		"WHERE ");
		if( vendedoresArray.length==0){
			query.append("(");
			for(int i=0; i<vendedoresArray.length;i++){
				query.append("ven.vd_codvend = "+vendedoresArray[i]+" ");
				if(i<vendedoresArray.length-1)
					query.append("or ");
			}
			query.append(") and ");
		}
		
		if(clienteEvt != null)
			query.append("t.ppto_codcliente = "+ clienteEvt+" and ");
		if(clienteFact != null)
			query.append("cli.cl_codcliente = "+ clienteFact+" and ");
		if(fechaDesde != null && fechaHasta != null)
			query.append("((t.ppto_fecinicio >= '"+fechaDesde+"' and t.ppto_fecfin<= '"+fechaHasta+"') "+
					"or (t.ppto_fecinicio <= '"+fechaHasta+"' and t.ppto_fecfin > '"+fechaHasta+"') "+
					"or(t.ppto_fecinicio< '"+fechaDesde+"' and t.ppto_fecfin>= '"+fechaDesde+"')) and ");
		if(lugar != null)
			query.append("t.ppto_evt_lugar = "+ lugar+" and ");		
		if(condpago != null)
			query.append("t.ppto_rpt_cond_pago= "+ condpago+" and ");
		if(estado != null){
			if(estado.equals("Confirmado"))
				query.append("(ea.estado <> 'Pendiente' and ea.estado <> 'Cancelado' and ea.estado <> 'Rechazado') and ");
			else if(estado.equals("Pendiente"))
				query.append("ea.estado = 'Pendiente' and ");
			else if(estado.equals("Cancelado"))
				query.append("ea.estado = 'Cancelado' and ");
			else if(estado.equals("Rechazado"))
				query.append("ea.estado = 'Rechazado' and ");
			else if(estado.equals("Orden de Facturacion"))
				query.append("ea.estado = 'Orden de Facturacion' and ");
			else if(estado.equals("Orden de Servicio"))
				query.append("ea.estado = 'Orden de Servicio' and ");
			else if(estado.equals("Cobrado"))
				query.append("ea.estado = 'Cobrado' and ");
			else if(estado.equals("Facturado"))
				query.append("ea.estado = 'Facturado' and ");
		}
		
		query.append("t.ppto_nroppto is not null group by nroppto ORDER BY vendedor,estado");
		List presupuestos = session.createSQLQuery(
				query.toString()
				)
				.addScalar("nroppto", Hibernate.LONG)//0
				.addScalar("vendedor", Hibernate.STRING)//1
				//.addScalar("nombreFantasia", Hibernate.STRING)
				.addScalar("evtNombre", Hibernate.STRING)//2
				//.addScalar("fecha", Hibernate.STRING)
				//.addScalar("estado", Hibernate.STRING)
				.addScalar("monto", Hibernate.DOUBLE)//3
				.addScalar("codVendedor", Hibernate.STRING)//4
				//.addScalar("fechacreacion", Hibernate.STRING)
				//.addScalar("lugar", Hibernate.STRING)
				//.addScalar("fechafin", Hibernate.STRING)
				.addScalar("subcontratado", Hibernate.DOUBLE)//5
				.addScalar("otros", Hibernate.DOUBLE)//6
				.addScalar("comlugar", Hibernate.DOUBLE)//7
				.addScalar("comterceros", Hibernate.DOUBLE)//8
				.addScalar("comcomercial", Hibernate.DOUBLE)//9
				.addScalar("regalias", Hibernate.DOUBLE)//10
				.list();
		
		HibernateUtil.cerrarSession(session);
		
		return CollectionUtil.listToObjectArray(presupuestos);

	}
	
	public Object[] buscarParaReportesRentabilidad2(String clienteEvt, String clienteFact, String fechaDesde, String fechaHasta, String lugar,
			String condpago, String estado, String tipoEvt, String nombreEvt) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		
		StringBuffer query = new StringBuffer("SELECT t.ppto_nroppto AS nroppto, ven.apynom AS vendedor, "+
		"cli.nombre_fantasia AS nombreFantasia, t.ppto_evt_nombre AS evtNombre, "+
		"t.ppto_fecinicio AS fecha, ea.estado AS estado,r.facturacion as monto,ven.vd_codvend as codVendedor, " +
		"v.vp_fec_creacion as fechacreacion, l.el_nombrelugar as lugar, t.ppto_fecfin AS fechafin, " +
		"r.SUBC as subcontratado, r.OTROS as otros, r.COMISIONLUGAR as comlugar, r.COMISIONTERCEROS as comterceros, " +
		"r.COMISIONCOMERCIAL as comcomercial, r.REGALIAS as regalias FROM tx_ppto t "+
		"INNER JOIN tx_vendedor_ppto v ON v.vp_nroppto = t.ppto_nroppto "+
		"INNER JOIN mst_vendedores ven ON ven.vd_codvend = v.vp_vendedor "+
		"INNER JOIN vw_pptos_estados ea ON ea.nroppto = t.ppto_nroppto "+
		"inner join mst_cliente cli on cli.cl_codcliente = ppto_codcliente "+
		"INNER JOIN tx_ppto_facturacion tf ON tf.pf_nroppto = t.ppto_nroppto "+
		"inner join vw_rentabilidad_report r on t.ppto_nroppto = r.presupuesto "+
		"inner join mst_evt_lugar l on t.ppto_evt_lugar = l.el_codlugar "+
		"WHERE ");
		
		if(clienteEvt != null)
			query.append("t.ppto_codcliente = "+ clienteEvt+" and ");
		if(clienteFact != null)
			query.append("cli.cl_codcliente = "+ clienteFact+" and ");
		if(fechaDesde != null && fechaHasta != null)
			query.append("((t.ppto_fecinicio >= '"+fechaDesde+"' and t.ppto_fecfin<= '"+fechaHasta+"') "+
					"or (t.ppto_fecinicio <= '"+fechaHasta+"' and t.ppto_fecfin > '"+fechaHasta+"') "+
					"or(t.ppto_fecinicio< '"+fechaDesde+"' and t.ppto_fecfin>= '"+fechaDesde+"')) and ");
		if(lugar != null)
			query.append("t.ppto_evt_lugar = "+ lugar+" and ");		
		if(condpago != null)
			query.append("t.ppto_rpt_cond_pago= "+ condpago+" and ");
		if(estado != null){
			if(estado.equals("Confirmado"))
				query.append("(ea.estado <> 'Pendiente' and ea.estado <> 'Cancelado' and ea.estado <> 'Rechazado') and ");
			else if(estado.equals("Pendiente"))
				query.append("ea.estado = 'Pendiente' and ");
			else if(estado.equals("Cancelado"))
				query.append("ea.estado = 'Cancelado' and ");
			else if(estado.equals("Rechazado"))
				query.append("ea.estado = 'Rechazado' and ");
			else if(estado.equals("Orden de Facturacion"))
				query.append("ea.estado = 'Orden de Facturacion' and ");
			else if(estado.equals("Orden de Servicio"))
				query.append("ea.estado = 'Orden de Servicio' and ");
			else if(estado.equals("Cobrado"))
				query.append("ea.estado = 'Cobrado' and ");
			else if(estado.equals("Facturado"))
				query.append("ea.estado = 'Facturado' and ");
		}
		
		query.append("t.ppto_nroppto is not null group by nroppto ORDER BY vendedor,estado");
		List presupuestos = session.createSQLQuery(
				query.toString()
				)
				.addScalar("nroppto", Hibernate.LONG)//0
				.addScalar("vendedor", Hibernate.STRING)//1
				.addScalar("nombreFantasia", Hibernate.STRING)//2
				.addScalar("evtNombre", Hibernate.STRING)//3
				.addScalar("fecha", Hibernate.STRING)//4
				.addScalar("estado", Hibernate.STRING)//5
				.addScalar("monto", Hibernate.DOUBLE)//6
				.addScalar("codVendedor", Hibernate.STRING)//7
				.addScalar("fechacreacion", Hibernate.STRING)//8
				.addScalar("lugar", Hibernate.STRING)//9
				.addScalar("fechafin", Hibernate.STRING)//10
				.addScalar("subcontratado", Hibernate.DOUBLE)//11
				.addScalar("otros", Hibernate.DOUBLE)//12
				.addScalar("comlugar", Hibernate.DOUBLE)//13
				.addScalar("comterceros", Hibernate.DOUBLE)//14
				.addScalar("comcomercial", Hibernate.DOUBLE)//15
				.addScalar("regalias", Hibernate.DOUBLE)//16
				.list();
		
		HibernateUtil.cerrarSession(session);
		
		return CollectionUtil.listToObjectArray(presupuestos);

	}
	
	
	
	public Object[] buscarParaReportesDeServicios(String clienteEvt,String fechaDesde, String fechaHasta, String lugar,
			String condpago, String estado, boolean subcontratado, String codServ, String nombreSub) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		
		StringBuffer query = new StringBuffer("SELECT ppto_ss_cantidad as cantidad , " +
				"t.ppto_ss_dias as dias, i.si_descripcion as servicio, s.ppto_s_nroppto as nroPpto, p.ppto_evt_nombre as evtNombre, " +
				"vn.apynom as vendedor, s.ppto_s_fecinicio as fechaInicial, t.ppto_ss_preciodto as montoServ, t.ppto_ss_descuento as dto, " +
				"r.rt_fact_orig + r.rt_fact_extras as montoEvt, ea.estado AS estado, " +
				"vn.vd_codvend as codVendedor,cli.nombre_fantasia AS nombreFantasia, t.ppto_ss_detalle as subcontratado, g.gc_costo as costo, s.ppto_s_fecfin as fechaFinal "+				
				"FROM tx_ppto_salas_servicios t "+
				"inner join mst_servicios_idioma i on i.si_codservicio = t.ppto_ss_servicio "+
				"inner join tx_ppto_salas s on s.ppto_s_id = t.ppto_ss_pls "+
				"inner join tx_vendedor_ppto v on v.vp_nroppto = s.ppto_s_nroppto "+
				"inner join mst_vendedores vn on v.vp_vendedor = vn.vd_codvend "+
				"inner join tx_ppto p on p.ppto_nroppto = s.ppto_s_nroppto "+
				"INNER JOIN vw_pptos_estados ea ON ea.nroppto = p.ppto_nroppto "+
				"inner join TX_RENTABILIDAD r on p.ppto_nroppto = r.rt_nroppto "+
				"inner join mst_cliente cli on cli.cl_codcliente = p.ppto_codcliente "+
				"left join tx_gastos_contrataciones g on t.ppto_ss_id = g.gc_ppto_ss_id "+
				"WHERE ");
		
		if(clienteEvt != null)
			query.append("p.ppto_codcliente = "+ clienteEvt+" and ");
		if(fechaDesde != null && fechaHasta != null)
			query.append("((p.ppto_fecinicio >= '"+fechaDesde+"' and p.ppto_fecfin<= '"+fechaHasta+"') "+
					"or (p.ppto_fecinicio <= '"+fechaHasta+"' and p.ppto_fecfin > '"+fechaHasta+"') "+
					"or(p.ppto_fecinicio< '"+fechaDesde+"' and p.ppto_fecfin>= '"+fechaDesde+"')) and ");
		if(lugar != null)
			query.append("p.ppto_evt_lugar = "+ lugar+" and ");		
		if(condpago != null)
			query.append("p.ppto_rpt_cond_pago= "+ condpago+" and ");
		if(estado != null){
			if(estado.equals("Confirmado"))
				query.append("(ea.estado <> 'Pendiente' and ea.estado <> 'Cancelado' and ea.estado <> 'Rechazado') and ");
			else if(estado.equals("Pendiente"))
				query.append("ea.estado = 'Pendiente' and ");
			else if(estado.equals("Cancelado"))
				query.append("ea.estado = 'Cancelado' and ");
			else if(estado.equals("Rechazado"))
				query.append("ea.estado = 'Rechazado' and ");
		}
		if(codServ != null && !subcontratado){
			query.append("t.ppto_ss_servicio = "+codServ+" and t.ppto_ss_modalidad = 1 and ");
		}
		else if(codServ != null && subcontratado){
			query.append("t.ppto_ss_servicio = "+codServ+" and t.ppto_ss_modalidad = 2 and ");
		}
		else if(codServ == null && subcontratado){
			query.append("t.ppto_ss_servicio = 1 and t.ppto_ss_detalle like '%"+nombreSub+"%' and ");
		}
		
		query.append("t.ppto_ss_id is not null ORDER BY servicio,nroPpto");
		List presupuestos = session.createSQLQuery(
				query.toString()
				)
				.addScalar("cantidad", Hibernate.INTEGER)	//0
				.addScalar("dias", Hibernate.INTEGER)	//1
				.addScalar("servicio", Hibernate.STRING)	//2	
				.addScalar("nroPpto", Hibernate.LONG)	//3
				.addScalar("evtNombre", Hibernate.STRING)	//4				
				.addScalar("vendedor", Hibernate.STRING)			//5	
				.addScalar("fechaInicial", Hibernate.STRING)	//6
				.addScalar("montoServ", Hibernate.DOUBLE)	//7
				.addScalar("dto", Hibernate.INTEGER)	//8
				.addScalar("montoEvt", Hibernate.DOUBLE)	//9
				.addScalar("estado", Hibernate.STRING)			//10	
				.addScalar("codVendedor", Hibernate.STRING)	//11
				.addScalar("nombreFantasia", Hibernate.STRING)	//12
				.addScalar("subcontratado", Hibernate.STRING)	//13
				.addScalar("costo", Hibernate.DOUBLE)	//14
				.addScalar("fechaFinal", Hibernate.STRING)	//15
				.list();
		
		HibernateUtil.cerrarSession(session);
		
		return CollectionUtil.listToObjectArray(presupuestos);

	}
	
	/*public Object[] buscarParaReportesDeServiciosSubc(String clienteEvt,String fechaDesde, String fechaHasta, String lugar,
			String condpago, String estado, boolean subcontratado, String codServ, String nombreSub) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		
		StringBuffer query = new StringBuffer("SELECT ppto_ss_cantidad as cantidad , " +
				"t.ppto_ss_dias as dias, i.si_descripcion as servicio, s.ppto_s_nroppto as nroPpto, p.ppto_evt_nombre as evtNombre, " +
				"vn.apynom as vendedor, s.ppto_s_fecinicio as fechaInicial, t.ppto_ss_preciodto as montoServ, t.ppto_ss_descuento as dto, " +
				"r.rt_fact_orig + r.rt_fact_extras as montoEvt, ea.estado AS estado, " +
				"vn.vd_codvend as codVendedor,cli.nombre_fantasia AS nombreFantasia, t.ppto_ss_detalle as subcontratado, g.gc_costo as costo, s.ppto_s_fecfin as fechaFinal, pr_nombreproveedor as proveedor "+				
				"FROM tx_ppto_salas_servicios t "+
				"inner join mst_servicios_idioma i on i.si_codservicio = t.ppto_ss_servicio "+
				"inner join tx_ppto_salas s on s.ppto_s_id = t.ppto_ss_pls "+
				"inner join tx_vendedor_ppto v on v.vp_nroppto = s.ppto_s_nroppto "+
				"inner join mst_vendedores vn on v.vp_vendedor = vn.vd_codvend "+
				"inner join tx_ppto p on p.ppto_nroppto = s.ppto_s_nroppto "+
				"INNER JOIN vw_pptos_estados ea ON ea.nroppto = p.ppto_nroppto "+
				"inner join TX_RENTABILIDAD r on p.ppto_nroppto = r.rt_nroppto "+
				"inner join mst_cliente cli on cli.cl_codcliente = p.ppto_codcliente "+
				"left join tx_gastos_contrataciones g on t.ppto_ss_id = g.gc_ppto_ss_id "+
				"inner join mst_proveedores prov on gc_codproveedor = pr_codproveedor "+
				"WHERE ");
		
		if(clienteEvt != null)
			query.append("p.ppto_codcliente = "+ clienteEvt+" and ");
		if(fechaDesde != null && fechaHasta != null)
			query.append("((p.ppto_fecinicio >= '"+fechaDesde+"' and p.ppto_fecfin<= '"+fechaHasta+"') "+
					"or (p.ppto_fecinicio <= '"+fechaHasta+"' and p.ppto_fecfin > '"+fechaHasta+"') "+
					"or(p.ppto_fecinicio< '"+fechaDesde+"' and p.ppto_fecfin>= '"+fechaDesde+"')) and ");
		if(lugar != null)
			query.append("p.ppto_evt_lugar = "+ lugar+" and ");		
		if(condpago != null)
			query.append("p.ppto_rpt_cond_pago= "+ condpago+" and ");
		if(estado != null){
			if(estado.equals("Confirmado"))
				query.append("(ea.estado <> 'Pendiente' and ea.estado <> 'Cancelado' and ea.estado <> 'Rechazado') and ");
			else if(estado.equals("Pendiente"))
				query.append("ea.estado = 'Pendiente' and ");
			else if(estado.equals("Cancelado"))
				query.append("ea.estado = 'Cancelado' and ");
			else if(estado.equals("Rechazado"))
				query.append("ea.estado = 'Rechazado' and ");
		}
		if(codServ != null && !subcontratado){
			query.append("t.ppto_ss_servicio = "+codServ+" and t.ppto_ss_modalidad = 1 and ");
		}
		else if(codServ != null && subcontratado){
			query.append("t.ppto_ss_servicio = "+codServ+" and t.ppto_ss_modalidad = 2 and ");
		}
		else if(codServ == null && subcontratado){
			query.append("t.ppto_ss_servicio = 1 and t.ppto_ss_detalle like '%"+nombreSub+"%' and ");
		}
		
		query.append("t.ppto_ss_id is not null ORDER BY servicio,nroPpto");
		List presupuestos = session.createSQLQuery(
				query.toString()
				)
				.addScalar("cantidad", Hibernate.INTEGER)	//0
				.addScalar("dias", Hibernate.INTEGER)	//1
				.addScalar("servicio", Hibernate.STRING)	//2	
				.addScalar("nroPpto", Hibernate.LONG)	//3
				.addScalar("evtNombre", Hibernate.STRING)	//4				
				.addScalar("vendedor", Hibernate.STRING)			//5	
				.addScalar("fechaInicial", Hibernate.STRING)	//6
				.addScalar("montoServ", Hibernate.DOUBLE)	//7
				.addScalar("dto", Hibernate.INTEGER)	//8
				.addScalar("montoEvt", Hibernate.DOUBLE)	//9
				.addScalar("estado", Hibernate.STRING)			//10	
				.addScalar("codVendedor", Hibernate.STRING)	//11
				.addScalar("nombreFantasia", Hibernate.STRING)	//12
				.addScalar("subcontratado", Hibernate.STRING)	//13
				.addScalar("costo", Hibernate.DOUBLE)	//14
				.addScalar("fechaFinal", Hibernate.STRING)	//15
				.addScalar("proveedor", Hibernate.STRING)	//16
				.list();
		
		HibernateUtil.cerrarSession(session);
		
		return CollectionUtil.listToObjectArray(presupuestos);

	}*/
	
	public Object[] buscarParaReportesDeServiciosSubc(String fechaDesde, String fechaHasta, int codEstado) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		
		StringBuffer query = new StringBuffer("SELECT ppto_ss_cantidad as cantidad , " +
				"t.ppto_ss_dias as dias, s.ppto_s_nroppto as nroPpto, p.ppto_evt_nombre as evtNombre, " +
				"vn.apynom as vendedor, s.ppto_s_fecinicio as fechaInicial, t.ppto_ss_preciodto as montoServ, " +
				"t.ppto_ss_descuento as dto, t.ppto_ss_detalle as subcontratado , gc.gc_estado_serv as estado, pr.pr_nombreproveedor as proveedor, gc.gc_costo as costo, si_descripcion as servicio "+				
				"FROM tx_ppto_salas_servicios t "+
				"inner join tx_ppto_salas s on s.ppto_s_id = t.ppto_ss_pls "+
				"inner join tx_vendedor_ppto v on v.vp_nroppto = s.ppto_s_nroppto "+
				"inner join mst_vendedores vn on v.vp_vendedor = vn.vd_codvend "+
				"inner join tx_ppto p on p.ppto_nroppto = s.ppto_s_nroppto "+
				"INNER JOIN vw_pptos_estados ea ON ea.nroppto = p.ppto_nroppto "+
				"inner join tx_gastos_contrataciones gc on gc.gc_ppto_ss_id = t.ppto_ss_id "+
				"inner join mst_proveedores pr on gc_codproveedor = pr_codproveedor "+
				"inner join mst_servicios_idioma on si_codservicio=t.ppto_ss_servicio "+
				"WHERE ");

		if(fechaDesde != null && fechaHasta != null)
			query.append("((p.ppto_fecinicio >= '"+fechaDesde+"' and p.ppto_fecfin<= '"+fechaHasta+"') "+
					"or (p.ppto_fecinicio <= '"+fechaHasta+"' and p.ppto_fecfin > '"+fechaHasta+"') "+
					"or(p.ppto_fecinicio< '"+fechaDesde+"' and p.ppto_fecfin>= '"+fechaDesde+"')) and ");

		query.append("(ea.estado <> 'Pendiente' or ea.estado <> 'Cancelado' or ea.estado <> 'Rechazado') and ");

		
		query.append("t.ppto_ss_modalidad = 2 and ");
		
		if(codEstado>0)
			query.append("gc.gc_estado_serv = "+codEstado+" and ");
		
		query.append("t.ppto_ss_id is not null ORDER BY nroPpto");
		List presupuestos = session.createSQLQuery(
				query.toString()
				)
				.addScalar("cantidad", Hibernate.INTEGER)	//0
				.addScalar("dias", Hibernate.INTEGER)	//1
				.addScalar("nroPpto", Hibernate.LONG)	//2
				.addScalar("evtNombre", Hibernate.STRING)	//3				
				.addScalar("vendedor", Hibernate.STRING)			//4	
				.addScalar("fechaInicial", Hibernate.STRING)	//5
				.addScalar("montoServ", Hibernate.DOUBLE)	//6
				.addScalar("dto", Hibernate.INTEGER)	//7	
				.addScalar("subcontratado", Hibernate.STRING)	//8
				.addScalar("estado", Hibernate.INTEGER)//9
				.addScalar("proveedor",Hibernate.STRING)//10
				.addScalar("costo",Hibernate.DOUBLE)//11
				.addScalar("servicio",Hibernate.STRING)//12
				//.addScalar("costo", Hibernate.DOUBLE)	//9
				//.addScalar("proveedor", Hibernate.STRING)	//10
				.list();
		
		HibernateUtil.cerrarSession(session);
		
		return CollectionUtil.listToObjectArray(presupuestos);

	}
	
	public Object[] buscarParaReportesDeFamServicios(String clienteEvt,String fechaDesde, String fechaHasta, String lugar,
			String condpago, String estado, boolean subcontratado, String codFam) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		
		StringBuffer query = new StringBuffer("SELECT ppto_ss_cantidad as cantidad , " +
				"t.ppto_ss_dias as dias, i.si_descripcion as servicio, s.ppto_s_nroppto as nroPpto, p.ppto_evt_nombre as evtNombre, " +
				"vn.apynom as vendedor, s.ppto_s_fecinicio as fechaInicial, t.ppto_ss_preciodto as montoServ, t.ppto_ss_descuento as dto, " +
				"r.rt_fact_orig + r.rt_fact_extras as montoEvt, ea.estado AS estado, " +
				"vn.vd_codvend as codVendedor,cli.nombre_fantasia AS nombreFantasia, t.ppto_ss_detalle as subcontratado, g.gc_costo as costo, s.ppto_s_fecfin as fechaFinal "+				
				"FROM tx_ppto_salas_servicios t "+
				"inner join mst_servicios_idioma i on i.si_codservicio = t.ppto_ss_servicio "+
				"inner join mst_servicios serv on serv.se_codservicio = t.ppto_ss_servicio "+
				"inner join tx_ppto_salas s on s.ppto_s_id = t.ppto_ss_pls "+
				"inner join tx_vendedor_ppto v on v.vp_nroppto = s.ppto_s_nroppto "+
				"inner join mst_vendedores vn on v.vp_vendedor = vn.vd_codvend "+
				"inner join tx_ppto p on p.ppto_nroppto = s.ppto_s_nroppto "+
				"INNER JOIN vw_pptos_estados ea ON ea.nroppto = p.ppto_nroppto "+
				"inner join TX_RENTABILIDAD r on p.ppto_nroppto = r.rt_nroppto "+
				"inner join mst_cliente cli on cli.cl_codcliente = p.ppto_codcliente "+
				"left join tx_gastos_contrataciones g on t.ppto_ss_id = g.gc_ppto_ss_id "+
				"WHERE ");
		
		if(clienteEvt != null)
			query.append("p.ppto_codcliente = "+ clienteEvt+" and ");
		if(fechaDesde != null && fechaHasta != null)
			query.append("((p.ppto_fecinicio >= '"+fechaDesde+"' and p.ppto_fecfin<= '"+fechaHasta+"') "+
					"or (p.ppto_fecinicio <= '"+fechaHasta+"' and p.ppto_fecfin > '"+fechaHasta+"') "+
					"or(p.ppto_fecinicio< '"+fechaDesde+"' and p.ppto_fecfin>= '"+fechaDesde+"')) and ");
		if(lugar != null)
			query.append("p.ppto_evt_lugar = "+ lugar+" and ");		
		if(condpago != null)
			query.append("p.ppto_rpt_cond_pago= "+ condpago+" and ");
		if(estado != null){
			if(estado.equals("Confirmado"))
				query.append("(ea.estado <> 'Pendiente' and ea.estado <> 'Cancelado' and ea.estado <> 'Rechazado') and ");
			else if(estado.equals("Pendiente"))
				query.append("ea.estado = 'Pendiente' and ");
			else if(estado.equals("Cancelado"))
				query.append("ea.estado = 'Cancelado' and ");
			else if(estado.equals("Rechazado"))
				query.append("ea.estado = 'Rechazado' and ");
		}
		if(codFam != null){
			query.append("serv.se_familia = "+codFam+" and ");
		}
		
		query.append("t.ppto_ss_id is not null ORDER BY servicio,nroPpto");
		List presupuestos = session.createSQLQuery(
				query.toString()
				)
				.addScalar("cantidad", Hibernate.INTEGER)	//0
				.addScalar("dias", Hibernate.INTEGER)	//1
				.addScalar("servicio", Hibernate.STRING)	//2	
				.addScalar("nroPpto", Hibernate.LONG)	//3
				.addScalar("evtNombre", Hibernate.STRING)	//4				
				.addScalar("vendedor", Hibernate.STRING)			//5	
				.addScalar("fechaInicial", Hibernate.STRING)	//6
				.addScalar("montoServ", Hibernate.DOUBLE)	//7
				.addScalar("dto", Hibernate.INTEGER)	//8
				.addScalar("montoEvt", Hibernate.DOUBLE)	//9
				.addScalar("estado", Hibernate.STRING)			//10	
				.addScalar("codVendedor", Hibernate.STRING)	//11
				.addScalar("nombreFantasia", Hibernate.STRING)	//12
				.addScalar("subcontratado", Hibernate.STRING)	//13
				.addScalar("costo", Hibernate.DOUBLE)	//14
				.addScalar("fechaFinal", Hibernate.STRING)	//15
				.list();
		
		HibernateUtil.cerrarSession(session);
		
		return CollectionUtil.listToObjectArray(presupuestos);

	}
	
	public Object[] buscarParaReportesFacturacion(long nroPpto, String nroFactura, String codUnidad) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		
		/*StringBuffer query = new StringBuffer("SELECT estact_nroppto,empresa, PRE.ppto_evt_nombre, "+
		"(select fac_factura FROM tx_ppto_factura t1 where t1.fac_estado=9 and t1.fac_nroppto=estact_nroppto) FACTURA_1, "+
		"(select fac_fecappend FROM tx_ppto_factura t1 where t1.fac_estado=9 and t1.fac_nroppto=estact_nroppto) FECHA_FACTURA_1, " +
		"(select fac_factura FROM tx_ppto_factura t1 where t1.fac_estado=12 and t1.fac_nroppto=estact_nroppto) FACTURA_2, " +
		"(select fac_fecappend FROM tx_ppto_factura t1 where t1.fac_estado=12 and t1.fac_nroppto=estact_nroppto) FECHA_FACTURA_2, " +
		"(select fac_id FROM tx_ppto_factura t1 where t1.fac_estado=9 and t1.fac_nroppto=estact_nroppto) FAC_ID_1, " +
		"(select fac_id FROM tx_ppto_factura t1 where t1.fac_estado=12 and t1.fac_nroppto=estact_nroppto) FAC_ID_2 " +
		"FROM tx_ppto_est_actual "+
		"INNER JOIN tx_ppto PRE ON PRE.ppto_nroppto = estact_nroppto "+
		"INNER JOIN tx_ppto_facturacion fact ON fact.pf_nroppto=estact_nroppto "+
		"inner join mst_cliente cli on cli.cl_codcliente = fact.pf_codcliente "+
		"WHERE estact_facturado=1 and estact_cancelado=0 and ");*/
		StringBuffer query = new StringBuffer("SELECT * "+
				"FROM vw_ppto_facturados_edit "+
				"WHERE ");
		if(nroPpto>0)
			query.append("estact_nroppto = "+ nroPpto+" and ");
		if(!StringUtils.isBlank(nroFactura))
			query.append("(FACTURA_1 like '%"+ nroFactura+"%' or FACTURA_2 like '%"+ nroFactura+"%') and ");
		if(codUnidad != null){
			query.append("pf_codadmin = "+ codUnidad+" and ");
		}
		
		query.append("estact_nroppto is not null ORDER BY estact_nroppto");
		List presupuestos = session.createSQLQuery(
				query.toString()
				)
				.addScalar("estact_nroppto", Hibernate.LONG)
				.addScalar("empresa", Hibernate.STRING)
				.addScalar("ppto_evt_nombre", Hibernate.STRING)
				.addScalar("FACTURA_1", Hibernate.STRING)
				.addScalar("FECHA_FACTURA_1", Hibernate.STRING)
				.addScalar("FACTURA_2", Hibernate.STRING)
				.addScalar("FECHA_FACTURA_2", Hibernate.STRING)
				.addScalar("FAC_ID_1", Hibernate.LONG)
				.addScalar("FAC_ID_2", Hibernate.LONG)
				.addScalar("pf_codadmin", Hibernate.INTEGER)
				.list();
		
		HibernateUtil.cerrarSession(session);
		
		return CollectionUtil.listToObjectArray(presupuestos);

	}
	
	public long nuevoPresupuesto(PresupuestoHelper pa) {

		if (log.isDebugEnabled())
			log.info("CREANDO PRESUPUESTO: \n" + pa.toString());

		Presupuesto entity = null;
		Transaction tx = null;
		Long pid = null;

		Session session = HibernateUtil.abrirSession();

		try {

			tx = session.beginTransaction();

			// crear presupeusto
			entity = PresupuestoUtil.instance(session).toPresupuesto(pa);

			if (log.isInfoEnabled())
				log.info("GRABANDO PRESUPUESTO CREADO");

			session.saveOrUpdate(entity);
			pid = entity.getNumeroDePresupuesto();

			if (log.isInfoEnabled())
				log.info("PRESUPUESTO CREADO CON ID " + pid);

			tx.commit();

			// entity = (Presupuesto)sesion.get(Presupuesto.class, pid);

			session.flush();
		} catch (HibernateException he) {

			if (log.isErrorEnabled())
				log.error("Se ha producido un error al grabar el presupuesto: "
						+ he.getMessage());

			if (tx != null && tx.isActive())
				tx.rollback();

			he.printStackTrace(System.err);
		} finally {
			HibernateUtil.cerrarSession(session);
		}

		if (log.isDebugEnabled())
			log.debug("DEVOLVIENDO AL CLIENTE");

		// return entity;

		return pid.longValue();
	}

	public long actualizarPresupuesto(PresupuestoHelper pa) {
		if (log.isDebugEnabled())
			log.debug("ACTUALIZANDO PRESUPUESTO: \n" + pa.toString());

		Presupuesto entity = null;
		boolean result = false;
		Transaction tx = null;
		
		Session session = HibernateUtil.abrirSession();

		try {

			tx = session.beginTransaction();

			// crear presupeusto
			entity = PresupuestoUtil.instance(session).toPresupuesto(pa);

			if (log.isInfoEnabled())
				log.info("ACTUALIZANDO PRESUPUESTO CON ID " + entity.getNumeroDePresupuesto());

			session.saveOrUpdate(entity);

			if (log.isInfoEnabled())
				log.info("PRESUPUESTO ACTUALIZADO CORRECTAMENTE");

			tx.commit();

			// entity = (Presupuesto)sesion.get(Presupuesto.class, pid);

			session.flush();
			
			result = true;
		} catch (HibernateException he) {

			if (log.isErrorEnabled())
				log.error("Se ha producido un error al grabar el presupuesto: "
						+ he.getMessage());

			if (tx != null && tx.isActive())
				tx.rollback();

			he.printStackTrace(System.err);
		} finally {
			HibernateUtil.cerrarSession(session);
		}

		if (log.isDebugEnabled())
			log.debug("DEVOLVIENDO AL CLIENTE");

		return (result)?entity.getNumeroDePresupuesto():0;
	}

/**********************************************************************************************/
	
	public Object[] buscarPorEstado(String estado) {
		
		Session session = HibernateUtil.abrirSession();
		
		List presupuestos = session.createSQLQuery(
				"SELECT t.ppto_nroppto AS nroppto, ven.apynom AS vendedor, " +
				"cli.nombre_fantasia AS nombreFantasia, t.ppto_evt_nombre AS evtNombre, " +
				"t.ppto_fecinicio AS fecha, ea.estado AS estado FROM mst_cliente cli "+
				"INNER JOIN tx_ppto t ON t.ppto_codcliente = cli.cl_codcliente "+
				"INNER JOIN tx_vendedor_ppto v ON v.vp_nroppto = t.ppto_nroppto "+
				"INNER JOIN mst_vendedores ven ON ven.vd_codvend = v.vp_vendedor "+				
				"INNER JOIN vw_pptos_estados ea ON ea.nroppto = t.ppto_nroppto "+
				"WHERE ea.estado = ?"+
				"ORDER BY t.ppto_nroppto"
				)
				.addScalar("nroppto", Hibernate.LONG)
				.addScalar("vendedor", Hibernate.STRING)
				.addScalar("nombreFantasia", Hibernate.STRING)
				.addScalar("evtNombre", Hibernate.STRING)
				.addScalar("fecha", Hibernate.STRING)
				.addScalar("estado", Hibernate.STRING)
				.setString(0,estado)
				.list();
		
		HibernateUtil.cerrarSession(session);
		
		return CollectionUtil.listToObjectArray(presupuestos);
	}

	public Object[] buscarPorClienteFac(long codCliente) {

		Session session = HibernateUtil.abrirSession();
		
		List presupuestos = session.createSQLQuery(
				"SELECT t.ppto_nroppto AS nroppto, ven.apynom AS vendedor, " +
				"cli.nombre_fantasia AS nombreFantasia, t.ppto_evt_nombre AS evtNombre, " +
				"t.ppto_fecinicio AS fecha, ea.estado AS estado FROM mst_cliente cli "+
				"INNER JOIN tx_ppto_facturacion tf ON tf.pf_codcliente = cli.cl_codcliente "+
				"INNER JOIN tx_vendedor_ppto v ON v.vp_nroppto = tf.pf_nroppto "+
				"INNER JOIN mst_vendedores ven ON ven.vd_codvend = v.vp_vendedor "+				
				"INNER JOIN vw_pptos_estados ea ON ea.nroppto = tf.pf_nroppto "+
				"INNER JOIN tx_ppto t ON t.ppto_nroppto = tf.pf_nroppto "+
				"WHERE cli.cl_codcliente = ? "+
				"group by nroppto ORDER BY vendedor,estado"
				)
				.addScalar("nroppto", Hibernate.LONG)
				.addScalar("vendedor", Hibernate.STRING)
				.addScalar("nombreFantasia", Hibernate.STRING)
				.addScalar("evtNombre", Hibernate.STRING)
				.addScalar("fecha", Hibernate.STRING)
				.addScalar("estado", Hibernate.STRING)
				.setLong(0,new Long(codCliente))
				.list();
		
		HibernateUtil.cerrarSession(session);
		
		return CollectionUtil.listToObjectArray(presupuestos);
	}
	
	public Object[] buscarPorCliente(long codCliente) {

		Session session = HibernateUtil.abrirSession();
		
		List presupuestos = session.createSQLQuery(
				"SELECT t.ppto_nroppto AS nroppto, ven.apynom AS vendedor, " +
				"cli.nombre_fantasia AS nombreFantasia, t.ppto_evt_nombre AS evtNombre, "+
				"t.ppto_fecinicio AS fecha, ea.estado AS estado FROM tx_ppto t "+
				"INNER JOIN tx_vendedor_ppto v ON v.vp_nroppto = t.ppto_nroppto "+
				"INNER JOIN mst_vendedores ven ON ven.vd_codvend = v.vp_vendedor "+		
				"INNER JOIN vw_pptos_estados ea ON ea.nroppto = t.ppto_nroppto "+
				"inner join mst_cliente cli on cli.cl_codcliente = ppto_codcliente "+
				"WHERE t.ppto_codcliente = ? "+
				"group by nroppto ORDER BY vendedor,estado" 
				)
				.addScalar("nroppto", Hibernate.LONG)
				.addScalar("vendedor", Hibernate.STRING)
				.addScalar("nombreFantasia", Hibernate.STRING)
				.addScalar("evtNombre", Hibernate.STRING)
				.addScalar("fecha", Hibernate.STRING)
				.addScalar("estado", Hibernate.STRING)
				.setLong(0,new Long(codCliente))
				.list();
		
		HibernateUtil.cerrarSession(session);
		
		return CollectionUtil.listToObjectArray(presupuestos);
	}
	
	public Object[] buscarPorClienteYFecha(long codCliente, String fecha) {

		Session session = HibernateUtil.abrirSession();
		
		List presupuestos = session.createSQLQuery(
				"SELECT t.ppto_nroppto AS nroppto, ven.apynom AS vendedor, " +
				"cli.nombre_fantasia AS nombreFantasia, t.ppto_evt_nombre AS evtNombre, " +
				"t.ppto_fecinicio AS fecha, ea.estado AS estado FROM mst_cliente cli "+
				"INNER JOIN tx_ppto t ON t.ppto_codcliente = cli.cl_codcliente "+
				"INNER JOIN tx_vendedor_ppto v ON v.vp_nroppto = t.ppto_nroppto "+
				"INNER JOIN mst_vendedores ven ON ven.vd_codvend = v.vp_vendedor "+				
				"INNER JOIN vw_pptos_estados ea ON ea.nroppto = t.ppto_nroppto "+
				"WHERE cli.cl_codcliente = ? and t.ppto_fecinicio = ?"+
				"group by t.ppto_nroppto ORDER BY t.ppto_nroppto"
				)
				.addScalar("nroppto", Hibernate.LONG)
				.addScalar("vendedor", Hibernate.STRING)
				.addScalar("nombreFantasia", Hibernate.STRING)
				.addScalar("evtNombre", Hibernate.STRING)
				.addScalar("fecha", Hibernate.STRING)
				.addScalar("estado", Hibernate.STRING)
				.setLong(0,new Long(codCliente))
				.setString(1, fecha)
				.list();
		
		HibernateUtil.cerrarSession(session);
		
		return CollectionUtil.listToObjectArray(presupuestos);
	}
	
	public Object[] buscarPorLugarYFecha(long codLugar, String fecha) {

		Session session = HibernateUtil.abrirSession();
		
		List presupuestos = session.createSQLQuery(
				"SELECT t.ppto_nroppto AS nroppto, ven.apynom AS vendedor, " +
				"cli.nombre_fantasia AS nombreFantasia, t.ppto_evt_nombre AS evtNombre, " +
				"t.ppto_fecinicio AS fecha, ea.estado AS estado FROM mst_cliente cli "+
				"INNER JOIN tx_ppto t ON t.ppto_codcliente = cli.cl_codcliente "+
				"INNER JOIN tx_vendedor_ppto v ON v.vp_nroppto = t.ppto_nroppto "+
				"INNER JOIN mst_vendedores ven ON ven.vd_codvend = v.vp_vendedor "+				
				"INNER JOIN vw_pptos_estados ea ON ea.nroppto = t.ppto_nroppto "+
				"WHERE t.ppto_evt_lugar = ? and t.ppto_fecinicio = ?"+
				"group by t.ppto_nroppto ORDER BY t.ppto_nroppto"
				)
				.addScalar("nroppto", Hibernate.LONG)
				.addScalar("vendedor", Hibernate.STRING)
				.addScalar("nombreFantasia", Hibernate.STRING)
				.addScalar("evtNombre", Hibernate.STRING)
				.addScalar("fecha", Hibernate.STRING)
				.addScalar("estado", Hibernate.STRING)
				.setLong(0,new Long(codLugar))
				.setString(1, fecha)
				.list();
		
		HibernateUtil.cerrarSession(session);
		
		return CollectionUtil.listToObjectArray(presupuestos);
	}

	public Object[] buscarPorLugar(long codigoLugar) {

		Session session = HibernateUtil.abrirSession();
		
		List presupuestos = session.createSQLQuery(
				"SELECT t.ppto_nroppto AS nroppto, ven.apynom AS vendedor, " +
				"cli.nombre_fantasia AS nombreFantasia, t.ppto_evt_nombre AS evtNombre, " +
				"t.ppto_fecinicio AS fecha, ea.estado AS estado FROM mst_cliente cli "+
				"INNER JOIN tx_ppto t ON t.ppto_codcliente = cli.cl_codcliente "+
				"INNER JOIN tx_vendedor_ppto v ON v.vp_nroppto = t.ppto_nroppto "+
				"INNER JOIN mst_vendedores ven ON ven.vd_codvend = v.vp_vendedor "+				
				"INNER JOIN vw_pptos_estados ea ON ea.nroppto = t.ppto_nroppto "+
				"WHERE t.ppto_evt_lugar = ? "+
				"group by t.ppto_nroppto ORDER BY t.ppto_nroppto"
				)
				.addScalar("nroppto", Hibernate.LONG)
				.addScalar("vendedor", Hibernate.STRING)
				.addScalar("nombreFantasia", Hibernate.STRING)
				.addScalar("evtNombre", Hibernate.STRING)
				.addScalar("fecha", Hibernate.STRING)
				.addScalar("estado", Hibernate.STRING)
				.setLong(0,new Long(codigoLugar))
				.list();
		
		HibernateUtil.cerrarSession(session);
		
		return CollectionUtil.listToObjectArray(presupuestos);
	}

	public Object[] buscarPorVendedor(long codVendedor) {

		Session session = HibernateUtil.abrirSession();
		
		List presupuestos = session.createSQLQuery(
				"SELECT t.ppto_nroppto AS nroppto, ven.apynom AS vendedor, " +
				"cli.nombre_fantasia AS nombreFantasia, t.ppto_evt_nombre AS evtNombre, " +
				"t.ppto_fecinicio AS fecha, ea.estado AS estado FROM mst_cliente cli "+
				"INNER JOIN tx_ppto t ON t.ppto_codcliente = cli.cl_codcliente "+
				"INNER JOIN tx_vendedor_ppto v ON v.vp_nroppto = t.ppto_nroppto "+
				"INNER JOIN mst_vendedores ven ON ven.vd_codvend = v.vp_vendedor "+				
				"INNER JOIN vw_pptos_estados ea ON ea.nroppto = t.ppto_nroppto "+
				"WHERE v.vp_vendedor = ? "+
				"group by t.ppto_nroppto ORDER BY t.ppto_nroppto"
				)
				.addScalar("nroppto", Hibernate.LONG)
				.addScalar("vendedor", Hibernate.STRING)
				.addScalar("nombreFantasia", Hibernate.STRING)
				.addScalar("evtNombre", Hibernate.STRING)
				.addScalar("fecha", Hibernate.STRING)
				.addScalar("estado", Hibernate.STRING)
				.setLong(0,new Long(codVendedor))
				.list();
		
		HibernateUtil.cerrarSession(session);
		
		return CollectionUtil.listToObjectArray(presupuestos);
	}

	public Object[] buscarPorFechaInicio(String fechaInicio) {

		Session session = HibernateUtil.abrirSession();
		
		List presupuestos = session.createSQLQuery(
				"SELECT t.ppto_nroppto AS nroppto, ven.apynom AS vendedor, " +
				"cli.nombre_fantasia AS nombreFantasia, t.ppto_evt_nombre AS evtNombre, " +
				"t.ppto_fecinicio AS fecha, ea.estado AS estado FROM mst_cliente cli "+
				"INNER JOIN tx_ppto t ON t.ppto_codcliente = cli.cl_codcliente "+
				"INNER JOIN tx_vendedor_ppto v ON v.vp_nroppto = t.ppto_nroppto "+
				"INNER JOIN mst_vendedores ven ON ven.vd_codvend = v.vp_vendedor "+				
				"INNER JOIN vw_pptos_estados ea ON ea.nroppto = t.ppto_nroppto "+
				"WHERE t.ppto_fecinicio = ? "+
				"group by t.ppto_nroppto ORDER BY t.ppto_nroppto"
				)
				.addScalar("nroppto", Hibernate.LONG)
				.addScalar("vendedor", Hibernate.STRING)
				.addScalar("nombreFantasia", Hibernate.STRING)
				.addScalar("evtNombre", Hibernate.STRING)
				.addScalar("fecha", Hibernate.STRING)
				.addScalar("estado", Hibernate.STRING)
				.setString(0, fechaInicio)
				.list();
		
		HibernateUtil.cerrarSession(session);
		
		return CollectionUtil.listToObjectArray(presupuestos);
	}

	public Presupuesto buscarPresupuesto(long numPpto) {
		Session session = HibernateUtil.abrirSession();

		Criteria c = session.createCriteria(Presupuesto.class);
		c.add(Expression.eq("numeroDePresupuesto", numPpto));
		Presupuesto p = (Presupuesto) c.uniqueResult();

		System.out.println("Numero de presupuesto puto:" + numPpto);
		HibernateUtil.cerrarSession(sesion);

		return p;
	}
	
	public Object[] buscarPorTipoEvt(long nro) throws RemoteException {
		Session session = HibernateUtil.abrirSession();

		List list = session.createSQLQuery(
				"SELECT t.ppto_nroppto AS nroppto, ven.apynom AS vendedor, " +
				"cli.nombre_fantasia AS nombreFantasia, t.ppto_evt_nombre AS evtNombre, "+
				"t.ppto_fecinicio AS fecha, ea.estado AS estado FROM tx_ppto t "+
				"INNER JOIN tx_vendedor_ppto v ON v.vp_nroppto = t.ppto_nroppto "+
				"INNER JOIN mst_vendedores ven ON ven.vd_codvend = v.vp_vendedor "+		
				"inner join mst_cliente cli on cli.cl_codcliente = ppto_codcliente "+
				"INNER JOIN vw_pptos_estados ea ON ea.nroppto = t.ppto_nroppto "+
				"WHERE t.ppto_evt_tipo = ? "+
				"group by nroppto ORDER BY vendedor" 
				)
				.addScalar("nroppto", Hibernate.LONG)
				.addScalar("vendedor", Hibernate.STRING)
				.addScalar("nombreFantasia", Hibernate.STRING)
				.addScalar("evtNombre", Hibernate.STRING)
				.addScalar("fecha", Hibernate.STRING)
				.addScalar("estado", Hibernate.STRING)
				.setLong(0,new Long(nro))
				.list();
		
		HibernateUtil.cerrarSession(sesion);
		return CollectionUtil.listToObjectArray(list);
	}
	
	public Object[] buscarPorNumero(long nro) throws RemoteException {
		Session session = HibernateUtil.abrirSession();

		List list = session.createSQLQuery(
				"SELECT t.ppto_nroppto AS nroppto, ven.apynom AS vendedor, " +
				"cli.nombre_fantasia AS nombreFantasia, t.ppto_evt_nombre AS evtNombre, "+
				"t.ppto_fecinicio AS fecha, ea.estado AS estado, ua_descripcion as unidadAdm FROM tx_ppto t "+
				"INNER JOIN tx_vendedor_ppto v ON v.vp_nroppto = t.ppto_nroppto "+
				"INNER JOIN mst_vendedores ven ON ven.vd_codvend = v.vp_vendedor "+		
				"INNER JOIN vw_pptos_estados ea ON ea.nroppto= t.ppto_nroppto "+
				"inner join mst_cliente cli on cli.cl_codcliente = ppto_codcliente "+
				"left join tx_ppto_facturacion fact on fact.pf_nroppto = t.ppto_nroppto "+
				"left join mst_unidades_administrativas adm on fact.pf_codadmin = ua_codunidad "+
				"WHERE t.ppto_nroppto = ? "+
				"group by nroppto ORDER BY vendedor" 
				)
				.addScalar("nroppto", Hibernate.LONG)//0
				.addScalar("vendedor", Hibernate.STRING)//1
				.addScalar("nombreFantasia", Hibernate.STRING)//2
				.addScalar("evtNombre", Hibernate.STRING)//3
				.addScalar("fecha", Hibernate.STRING)//4
				.addScalar("estado", Hibernate.STRING)//5
				.addScalar("unidadAdm", Hibernate.STRING)//6
				.setLong(0,new Long(nro))
				.list();
		
		HibernateUtil.cerrarSession(sesion);
		return CollectionUtil.listToObjectArray(list);
	}
	
	/*public Object[] buscarPorNumero(long nro) throws RemoteException {
		Session session = HibernateUtil.abrirSession();

		List list = session.createSQLQuery(
				"SELECT t.ppto_nroppto AS nroppto, ven.apynom AS vendedor, " +
				"cli.nombre_fantasia AS nombreFantasia, t.ppto_evt_nombre AS evtNombre, "+
				"t.ppto_fecinicio AS fecha, ea.*, ua_descripcion as unidadAdm FROM tx_ppto t "+
				"INNER JOIN tx_vendedor_ppto v ON v.vp_nroppto = t.ppto_nroppto "+
				"INNER JOIN mst_vendedores ven ON ven.vd_codvend = v.vp_vendedor "+		
				"INNER JOIN tx_ppto_est_actual ea ON ea.estact_nroppto = t.ppto_nroppto "+
				"inner join mst_cliente cli on cli.cl_codcliente = ppto_codcliente "+
				"left join tx_ppto_facturacion fact on fact.pf_nroppto = t.ppto_nroppto "+
				"left join mst_unidades_administrativas adm on fact.pf_codadmin = ua_codunidad "+
				"WHERE t.ppto_nroppto = ? "+
				"group by nroppto ORDER BY vendedor" 
				)
				.addScalar("nroppto", Hibernate.LONG)//0
				.addScalar("vendedor", Hibernate.STRING)//1
				.addScalar("nombreFantasia", Hibernate.STRING)//2
				.addScalar("evtNombre", Hibernate.STRING)//3
				.addScalar("fecha", Hibernate.STRING)//4
				.addScalar("estact_cobrado", Hibernate.INTEGER)//5
				.addScalar("estact_facturado", Hibernate.INTEGER)//6
				.addScalar("estact_confirmado", Hibernate.INTEGER)//7
				.addScalar("estact_os", Hibernate.INTEGER)//8
				.addScalar("estact_of", Hibernate.INTEGER)//9
				.addScalar("estact_adelanto", Hibernate.INTEGER)//10
				.addScalar("estact_adelantado", Hibernate.INTEGER)//11
				.addScalar("estact_cancelado", Hibernate.INTEGER)//12
				.addScalar("estact_actualizado", Hibernate.INTEGER)//13
				.addScalar("estact_nuevo", Hibernate.INTEGER)//14
				.addScalar("estact_rechazado", Hibernate.INTEGER)//15
				.addScalar("estact_oc", Hibernate.INTEGER)//16
				.addScalar("unidadAdm", Hibernate.STRING)//17
				.setLong(0,new Long(nro))
				.list();
		
		HibernateUtil.cerrarSession(sesion);
		return CollectionUtil.listToObjectArray(list);
	}*/
/*****************************************************/	
	public Object[] buscarPendientesPorVendedor(String codigoVendedor)
			throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		
		List list = session
		.createSQLQuery(
		"SELECT p.ppto_nroppto as numeroPresupuesto, v.apynom as vendedor, c.nombre_fantasia as nombre_fantasia, "+
		"p.ppto_evt_nombre as nombreEvento, p.ppto_fecinicio as fechaInicialEvento, r.facturacion as facturacion "+
		"from tx_ppto p "+
		"inner join TX_VENDEDOR_PPTO vp on p.ppto_nroppto = vp.vp_nroppto "+
		"inner join MST_VENDEDORES v on vp.vp_vendedor = v.vd_codvend "+
		"inner join MST_CLIENTE c on p.ppto_codcliente = cl_codcliente "+
		"inner join TX_PPTO_EST_ACTUAL ea on ea.estact_nroppto = p.ppto_nroppto "+
		"inner join VW_RENTABILIDAD r on r.presupuesto=p.ppto_nroppto "+
		"where ea.estact_confirmado = 0 and ea.estact_rechazado = 0 "+
		"AND vp.vp_vendedor = '" + codigoVendedor + "'"+
		"AND MONTH(p.ppto_fecinicio) = (SELECT MONTH(SYSDATE()) FROM DUAL) "+
		"AND YEAR(p.ppto_fecinicio) = (SELECT YEAR(SYSDATE()) FROM DUAL) "+
		"order by p.ppto_fecinicio")
		.addScalar("numeroPresupuesto",Hibernate.LONG)
		.addScalar("vendedor", Hibernate.STRING)
		.addScalar("nombre_fantasia", Hibernate.STRING)
		.addScalar("nombreEvento", Hibernate.STRING)
		.addScalar("fechaInicialEvento", Hibernate.STRING)
		.addScalar("facturacion", Hibernate.DOUBLE)
		.list();
		
		HibernateUtil.cerrarSession(sesion);
		return CollectionUtil.listToObjectArray(list);
		
	}

	public Object[] buscarDeHoyPorVendedor(String codigoVendedor)
			throws RemoteException {
		Session session = HibernateUtil.abrirSession();

		List list = session
		.createSQLQuery(
				"SELECT p.ppto_nroppto as numeroPresupuesto, v.apynom as vendedor, c.nombre_fantasia as nombre_fantasia, "+
				"p.ppto_evt_nombre as nombreEvento, p.ppto_fecinicio as fechaInicialEvento, r.facturacion as facturacion "+
				"from tx_ppto p "+
				"inner join TX_VENDEDOR_PPTO vp on p.ppto_nroppto = vp.vp_nroppto "+
				"inner join MST_VENDEDORES v on vp.vp_vendedor = v.vd_codvend "+
				"inner join MST_CLIENTE c on p.ppto_codcliente = cl_codcliente "+
				"inner join TX_PPTO_EST_ACTUAL ea on ea.estact_nroppto = p.ppto_nroppto "+
				"inner join VW_RENTABILIDAD r on r.presupuesto=p.ppto_nroppto "+
				"where ea.estact_confirmado = 1 and ea.estact_cancelado = 0 "+
				"AND vp.vp_vendedor = '" + codigoVendedor + "'"+
				" AND p.ppto_fecinicio <= (SELECT DATE_FORMAT(NOW(), '%Y-%m-%d')) and p.ppto_fecfin >= (SELECT DATE_FORMAT(NOW(), '%Y-%m-%d')) "+
				" order by p.ppto_fecinicio")
		.addScalar("numeroPresupuesto",Hibernate.LONG)
		.addScalar("vendedor", Hibernate.STRING)
		.addScalar("nombre_fantasia", Hibernate.STRING)
		.addScalar("nombreEvento", Hibernate.STRING)
		.addScalar("fechaInicialEvento", Hibernate.STRING)
		.addScalar("facturacion", Hibernate.DOUBLE)
		.list();
		

		HibernateUtil.cerrarSession(sesion);
		return CollectionUtil.listToObjectArray(list);
	}

	public Object[] buscarConfirmadosPorVendedor(String codigoVendedor)
			throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		
		List list = session
				.createSQLQuery(

						"SELECT p.ppto_nroppto as numeroPresupuesto, v.apynom as vendedor, c.nombre_fantasia as nombre_fantasia, "+
						"p.ppto_evt_nombre as nombreEvento, p.ppto_fecinicio as fechaInicialEvento, r.facturacion as facturacion "+
						"from tx_ppto p "+
						"inner join TX_VENDEDOR_PPTO vp on p.ppto_nroppto = vp.vp_nroppto "+
						"inner join MST_VENDEDORES v on vp.vp_vendedor = v.vd_codvend "+
						"inner join MST_CLIENTE c on p.ppto_codcliente = cl_codcliente "+
						"inner join TX_PPTO_EST_ACTUAL ea on ea.estact_nroppto = p.ppto_nroppto "+
						"inner join VW_RENTABILIDAD r on r.presupuesto=p.ppto_nroppto "+
						"where ea.estact_confirmado = 1 and ea.estact_cancelado = 0 "+
						"AND vp.vp_vendedor = '" + codigoVendedor + "'"+
						" AND MONTH(p.ppto_fecinicio) = (SELECT MONTH(SYSDATE()) FROM DUAL) "+
						" AND YEAR(p.ppto_fecinicio) = (SELECT YEAR(SYSDATE()) FROM DUAL)"+
						" order by p.ppto_fecinicio")
				.addScalar("numeroPresupuesto",Hibernate.LONG)
				.addScalar("vendedor", Hibernate.STRING)
				.addScalar("nombre_fantasia", Hibernate.STRING)
				.addScalar("nombreEvento", Hibernate.STRING)
				.addScalar("fechaInicialEvento", Hibernate.STRING)
				.addScalar("facturacion", Hibernate.DOUBLE)
				.list();

		HibernateUtil.cerrarSession(sesion);
		return CollectionUtil.listToObjectArray(list);
	}

	Session session = HibernateUtil.abrirSession();

	public Object[] buscarAVencerPorVendedor(String codigoVendedor)
			throws RemoteException {

		String pendiente = " ESTADO != 'CONFIRMADO' AND ESTADO != 'COBRADO' AND ESTADO != 'FACTURADO' ";
		String hoyMayorIgualAFechaVencimiento = " (VENCE < (SELECT DATE(SYSDATE()) FROM DUAL) OR "
				+ " VENCE = (SELECT DATE(SYSDATE()) FROM DUAL)) ";
		String hoyMenorIgualAFechaInicio = " (INICIO > (SELECT DATE(SYSDATE()) FROM DUAL) OR "
				+ "INICIO = (SELECT DATE(SYSDATE()) FROM DUAL)) ";
		String condicionPorVencer = pendiente + " AND "
				+ hoyMayorIgualAFechaVencimiento + " AND "
				+ hoyMenorIgualAFechaInicio;

		List list = session.createSQLQuery(
				"select NROPPTO,VENDEDOR,CLIENTE,EVENTO,INICIO, r.facturacion as facturacion "
				+ "from VW_PPTO_ESTADO_DETALLE inner join VW_RENTABILIDAD r on r.presupuesto=NROPPTO where vd_codvend = '" + codigoVendedor + "'"
				+ " and " + condicionPorVencer
				+ " and (MONTH(VENCE) = (SELECT MONTH(SYSDATE()) FROM DUAL) or MONTH(INICIO) = (SELECT MONTH(SYSDATE()) FROM DUAL)) "
				+ " and (YEAR(VENCE) = (SELECT YEAR(SYSDATE()) FROM DUAL) or YEAR(INICIO) = (SELECT YEAR(SYSDATE()) FROM DUAL)) order by NROPPTO")
				.addScalar("NROPPTO",Hibernate.LONG)
				//.addScalar("ESTADO", Hibernate.STRING)
				.addScalar("VENDEDOR", Hibernate.STRING)
				.addScalar("CLIENTE", Hibernate.STRING)
				.addScalar("EVENTO", Hibernate.STRING)
				.addScalar("INICIO", Hibernate.STRING)
				.addScalar("facturacion", Hibernate.DOUBLE)
				.list();

		HibernateUtil.cerrarSession(session);

		return CollectionUtil.listToObjectArray(list);
	}

	public Object[] buscarPendientesPorUC(String codigoVendedor)
	throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		
		String codigoUC = (String) session.createSQLQuery(
				"select CODUC" + " from VW_UC_VENDEDORES where CODVEND = '"
				+ codigoVendedor + "'")
				.addScalar("CODUC",Hibernate.STRING).uniqueResult();
		
		List list = session
		.createSQLQuery(
				"SELECT p.ppto_nroppto as numeroPresupuesto, v.apynom as vendedor, c.nombre_fantasia as nombre_fantasia, "+
				"p.ppto_evt_nombre as nombreEvento, p.ppto_fecinicio as fechaInicialEvento, r.facturacion as facturacion "+
				"from tx_ppto p "+
				"inner join TX_VENDEDOR_PPTO vp on p.ppto_nroppto = vp.vp_nroppto "+
				"inner join MST_VENDEDORES v on vp.vp_vendedor = v.vd_codvend "+
				"inner join MST_UNIDADES_VENDEDORES uv on uv.ucv_codvendedor = v.vd_codvend "+
				"inner join MST_UNIDADES_COMERCIALES uc on uc.uc_codunidad = uv.ucv_codunidad "+
				"inner join MST_CLIENTE c on p.ppto_codcliente = cl_codcliente "+
				"inner join TX_PPTO_EST_ACTUAL ea on ea.estact_nroppto = p.ppto_nroppto "+
				"inner join VW_RENTABILIDAD r on r.presupuesto=p.ppto_nroppto "+
				"where ea.estact_confirmado = 0 and ea.estact_rechazado = 0 "+
				"AND uc.uc_codunidad = '" + codigoUC + "'"+
				"AND MONTH(p.ppto_fecinicio) = (SELECT MONTH(SYSDATE()) FROM DUAL) "+
				"AND YEAR(p.ppto_fecinicio) = (SELECT YEAR(SYSDATE()) FROM DUAL) AND uv.activo='S' "+
				"order by p.ppto_fecinicio")
		.addScalar("numeroPresupuesto",Hibernate.LONG)
		.addScalar("vendedor", Hibernate.STRING)
		.addScalar("nombre_fantasia", Hibernate.STRING)
		.addScalar("nombreEvento", Hibernate.STRING)
		.addScalar("fechaInicialEvento", Hibernate.STRING)
		.addScalar("facturacion", Hibernate.DOUBLE)
		.list();
		
		HibernateUtil.cerrarSession(sesion);
		return CollectionUtil.listToObjectArray(list);
	}

	public Object[] buscarAVencerPorUC(String codigoVendedor)
			throws RemoteException {

		String pendiente = " ESTADO != 'CONFIRMADO' AND ESTADO != 'COBRADO' AND ESTADO != 'FACTURADO' ";
		String hoyMayorIgualAFechaVencimiento = " (VENCE < (SELECT DATE(SYSDATE()) FROM DUAL) OR "
			+ " VENCE = (SELECT DATE(SYSDATE()) FROM DUAL)) ";
		String hoyMenorIgualAFechaInicio = " (INICIO > (SELECT DATE(SYSDATE()) FROM DUAL) OR "
			+ "INICIO = (SELECT DATE(SYSDATE()) FROM DUAL)) ";
		String condicionPorVencer = pendiente + " AND "+ hoyMayorIgualAFechaVencimiento + " AND "+ hoyMenorIgualAFechaInicio;
		
		String codigoUC = (String) session.createSQLQuery(
				"select CODUC" + " from VW_UC_VENDEDORES where CODVEND = '"
				+ codigoVendedor + "'").addScalar("CODUC",
						Hibernate.STRING).uniqueResult();
		
		List list = session.createSQLQuery(
				"select NROPPTO,VENDEDOR,CLIENTE,EVENTO,INICIO, r.facturacion as facturacion "
				+ "from VW_PPTO_ESTADO_DETALLE inner join VW_RENTABILIDAD r on r.presupuesto=NROPPTO where CODUNIDAD = '"
				+ codigoUC + "' and " + condicionPorVencer
				+ " and (MONTH(VENCE) = (SELECT MONTH(SYSDATE()) FROM DUAL) or MONTH(INICIO) = (SELECT MONTH(SYSDATE()) FROM DUAL)) "
				+ " and (YEAR(VENCE) = (SELECT YEAR(SYSDATE()) FROM DUAL) or YEAR(INICIO) = (SELECT YEAR(SYSDATE()) FROM DUAL)) order by NROPPTO")
				.addScalar("NROPPTO",Hibernate.LONG)
				.addScalar("VENDEDOR", Hibernate.STRING)
				.addScalar("CLIENTE", Hibernate.STRING)
				.addScalar("EVENTO", Hibernate.STRING)
				.addScalar("INICIO", Hibernate.STRING)
				.addScalar("facturacion", Hibernate.DOUBLE)
				.list();
		
		HibernateUtil.cerrarSession(session);

		return CollectionUtil.listToObjectArray(list);
	}

	public Object[] buscarDeHoyPorUC(String codigoVendedor)
			throws RemoteException {
		Session session = HibernateUtil.abrirSession();

		String codigoUC = (String) session.createSQLQuery(
				"select CODUC" + " from VW_UC_VENDEDORES where CODVEND = '"
						+ codigoVendedor + "'").addScalar("CODUC",
				Hibernate.STRING).uniqueResult();
		
		List list = session
		.createSQLQuery(
				"SELECT p.ppto_nroppto as numeroPresupuesto, v.apynom as vendedor, c.nombre_fantasia as nombre_fantasia, "+
				"p.ppto_evt_nombre as nombreEvento, p.ppto_fecinicio as fechaInicialEvento, r.facturacion as facturacion "+
				"from tx_ppto p "+
				"inner join TX_VENDEDOR_PPTO vp on p.ppto_nroppto = vp.vp_nroppto "+
				"inner join MST_VENDEDORES v on vp.vp_vendedor = v.vd_codvend "+
				"inner join MST_UNIDADES_VENDEDORES uv on uv.ucv_codvendedor = v.vd_codvend "+
				"inner join MST_UNIDADES_COMERCIALES uc on uc.uc_codunidad = uv.ucv_codunidad "+
				"inner join MST_CLIENTE c on p.ppto_codcliente = cl_codcliente "+
				"inner join TX_PPTO_EST_ACTUAL ea on ea.estact_nroppto = p.ppto_nroppto "+
				"inner join VW_RENTABILIDAD r on r.presupuesto=p.ppto_nroppto "+
				"where ea.estact_confirmado = 1 and ea.estact_cancelado = 0 "+
				"AND uc.uc_codunidad = '" + codigoUC + "' AND uv.activo='S'" +
				" AND p.ppto_fecinicio <= (SELECT DATE_FORMAT(NOW(), '%Y-%m-%d')) and p.ppto_fecfin >= (SELECT DATE_FORMAT(NOW(), '%Y-%m-%d')) "+
				" order by p.ppto_fecinicio")
		.addScalar("numeroPresupuesto",Hibernate.LONG)
		.addScalar("vendedor", Hibernate.STRING)
		.addScalar("nombre_fantasia", Hibernate.STRING)
		.addScalar("nombreEvento", Hibernate.STRING)
		.addScalar("fechaInicialEvento", Hibernate.STRING)
		.addScalar("facturacion",Hibernate.DOUBLE)
		.list();

		HibernateUtil.cerrarSession(sesion);
		return CollectionUtil.listToObjectArray(list);
	}

	public Object[] buscarConfirmadosPorUC(String codigoVendedor)
			throws RemoteException {
		Session session = HibernateUtil.abrirSession();

		String codigoUC = (String) session.createSQLQuery(
				        "select CODUC" + " from VW_UC_VENDEDORES where CODVEND = '"
						+ codigoVendedor + "'")
						.addScalar("CODUC",	Hibernate.STRING).uniqueResult();

		List list = session
				.createSQLQuery(
						"SELECT p.ppto_nroppto as numeroPresupuesto, v.apynom as vendedor, c.nombre_fantasia as nombre_fantasia, "+
						"p.ppto_evt_nombre as nombreEvento, p.ppto_fecinicio as fechaInicialEvento, r.facturacion as facturacion "+
						"from tx_ppto p "+
						"inner join TX_VENDEDOR_PPTO vp on p.ppto_nroppto = vp.vp_nroppto "+
						"inner join MST_VENDEDORES v on vp.vp_vendedor = v.vd_codvend "+
						"inner join MST_UNIDADES_VENDEDORES uv on uv.ucv_codvendedor = v.vd_codvend "+
						"inner join MST_UNIDADES_COMERCIALES uc on uc.uc_codunidad = uv.ucv_codunidad "+
						"inner join MST_CLIENTE c on p.ppto_codcliente = cl_codcliente "+
						"inner join TX_PPTO_EST_ACTUAL ea on ea.estact_nroppto = p.ppto_nroppto "+
						"inner join VW_RENTABILIDAD r on r.presupuesto=p.ppto_nroppto "+
						"where ea.estact_confirmado = 1 and ea.estact_cancelado = 0 "+
						"AND uc.uc_codunidad = '" + codigoUC + "'"+
						" AND MONTH(p.ppto_fecinicio) = (SELECT MONTH(SYSDATE()) FROM DUAL) "+
						"AND YEAR(p.ppto_fecinicio) = (SELECT YEAR(SYSDATE()) FROM DUAL) AND uv.activo='S' "+
						"order by p.ppto_fecinicio")
						.addScalar("numeroPresupuesto",Hibernate.LONG)
						.addScalar("vendedor", Hibernate.STRING)
						.addScalar("nombre_fantasia", Hibernate.STRING)
						.addScalar("nombreEvento", Hibernate.STRING)
						.addScalar("fechaInicialEvento", Hibernate.STRING)
						.addScalar("facturacion",Hibernate.DOUBLE)
						.list();

		HibernateUtil.cerrarSession(sesion);
		return CollectionUtil.listToObjectArray(list);
	}
	
	public Object[] findCobradosByUnidadAdmNroPpto(String codUnidad, long nroppto) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		
		SQLQuery query = session.createSQLQuery("select nroppto,RAZONSOCIAL,fechainicio,fecfact,importe,clientefacturacion,factura_1,factura_2,factura_3,fecha_factura_2,fecha_factura_3,contacto,lugar,codigocliente,creador,condicionPago,observaciones,adelanto FROM VW_AGENDAS_COBRADOS "+
				"inner join mst_unidades_admin_comercial ON CODUNIDAD= uac_codunidad_comercial "+
				"where uac_codunidad_admin =:admin and nroppto =:ppto and tieneAgenda=1");
		
		query.addScalar("nroppto", Hibernate.STRING);	//0	
		query.addScalar("RAZONSOCIAL", Hibernate.STRING);//1
		query.addScalar("fechainicio", Hibernate.STRING);//2
		query.addScalar("fecfact", Hibernate.STRING);//3
		query.addScalar("importe", Hibernate.STRING);//4
		query.addScalar("clientefacturacion", Hibernate.STRING);//5
		query.addScalar("factura_1", Hibernate.STRING);//6
		query.addScalar("factura_2", Hibernate.STRING);//7
		query.addScalar("factura_3", Hibernate.STRING);//8
		query.addScalar("fecha_factura_2", Hibernate.STRING);//9
		query.addScalar("fecha_factura_3", Hibernate.STRING);//10
		query.addScalar("contacto", Hibernate.STRING);//11
		query.addScalar("lugar", Hibernate.STRING);//12
		query.addScalar("codigocliente", Hibernate.STRING);//13
		query.addScalar("creador", Hibernate.STRING);//14
		query.addScalar("condicionPago", Hibernate.STRING);//15
		query.addScalar("observaciones", Hibernate.STRING);//16
		query.addScalar("adelanto", Hibernate.STRING);//17
		query.setString("admin", codUnidad);
		query.setLong("ppto", nroppto);
		List list = query.list();

		HibernateUtil.cerrarSession(sesion);
		return CollectionUtil.listToObjectArray(list);
	}
	
	public Object[] findFacturadosNoCobradosByUnidadAdm(String codUnidad) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		
		SQLQuery query = session.createSQLQuery("select nroppto,RAZONSOCIAL,fechainicio,fecfact,importe,clientefacturacion,factura_1,factura_2,factura_3,contacto,lugar,codigocliente,creador,condicionPago,observaciones,adelanto,tieneAgenda,fecha_factura_2,fecha_factura_3,ESTADO FROM VW_COBRANZAS "+
				"inner join mst_unidades_admin_comercial ON CODUNIDAD= uac_codunidad_comercial "+
				"where uac_codunidad_admin =:admin ");
		
		query.addScalar("nroppto", Hibernate.STRING);	//0	
		query.addScalar("RAZONSOCIAL", Hibernate.STRING);//1
		query.addScalar("fechainicio", Hibernate.STRING);//2
		query.addScalar("fecfact", Hibernate.STRING);//3
		query.addScalar("importe", Hibernate.STRING);//4
		query.addScalar("clientefacturacion", Hibernate.STRING);//5
		query.addScalar("factura_1", Hibernate.STRING);//6
		query.addScalar("factura_2", Hibernate.STRING);//7
		query.addScalar("factura_3", Hibernate.STRING);//8
		query.addScalar("contacto", Hibernate.STRING);//9
		query.addScalar("lugar", Hibernate.STRING);//10
		query.addScalar("codigocliente", Hibernate.STRING);//11
		query.addScalar("creador", Hibernate.STRING);//12
		query.addScalar("condicionPago", Hibernate.STRING);//13
		query.addScalar("observaciones", Hibernate.STRING);//14
		query.addScalar("adelanto", Hibernate.STRING);//15
		query.addScalar("tieneAgenda", Hibernate.INTEGER);//16
		query.addScalar("fecha_factura_2", Hibernate.STRING);//17
		query.addScalar("fecha_factura_3", Hibernate.STRING);//18
		query.addScalar("ESTADO", Hibernate.STRING);//19
		query.setString("admin", codUnidad);
		List list = query.list();

		HibernateUtil.cerrarSession(sesion);
		return CollectionUtil.listToObjectArray(list);
	}
	
	public Object[] findFacturadosNoCobradosByUnidadAdmNroPpto(String codUnidad, long nroppto) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		
		SQLQuery query = session.createSQLQuery("select nroppto,RAZONSOCIAL,fechainicio,fecfact,importe,clientefacturacion,factura_1,factura_2,factura_3,fecha_factura_2,fecha_factura_3,contacto,lugar,codigocliente,creador,condicionPago,observaciones,adelanto FROM VW_COBRANZAS "+
				"inner join mst_unidades_admin_comercial ON CODUNIDAD= uac_codunidad_comercial "+
				"where uac_codunidad_admin =:admin and nroppto =:ppto ");
		
		query.addScalar("nroppto", Hibernate.STRING);		
		query.addScalar("RAZONSOCIAL", Hibernate.STRING);
		query.addScalar("fechainicio", Hibernate.STRING);
		query.addScalar("fecfact", Hibernate.STRING);
		query.addScalar("importe", Hibernate.STRING);
		query.addScalar("clientefacturacion", Hibernate.STRING);
		//query.addScalar("facturas", Hibernate.STRING);
		query.addScalar("factura_1", Hibernate.STRING);
		query.addScalar("factura_2", Hibernate.STRING);
		query.addScalar("factura_3", Hibernate.STRING);
		query.addScalar("fecha_factura_2", Hibernate.STRING);
		query.addScalar("fecha_factura_3", Hibernate.STRING);
		query.addScalar("contacto", Hibernate.STRING);
		query.addScalar("lugar", Hibernate.STRING);
		query.addScalar("codigocliente", Hibernate.STRING);
		query.addScalar("creador", Hibernate.STRING);
		query.addScalar("condicionPago", Hibernate.STRING);
		query.addScalar("observaciones", Hibernate.STRING);
		query.addScalar("adelanto", Hibernate.STRING);
		query.setString("admin", codUnidad);
		query.setLong("ppto", nroppto);
		List list = query.list();

		HibernateUtil.cerrarSession(sesion);
		return CollectionUtil.listToObjectArray(list);
	}
	
	public Object[] findOFNoFacturados() throws RemoteException {
		Session session = HibernateUtil.abrirSession();

		String queryString = new String();
		queryString += "select nroppto,CLIENTEFACT,confirmacion,importe,estado,vendedor ";
		queryString += "FROM VW_FACTURACION ";
		//queryString += "WHERE estado = :estado ";

		SQLQuery query = session.createSQLQuery(queryString);
		query.addScalar("nroppto", Hibernate.STRING);	
		query.addScalar("estado", Hibernate.STRING);
		query.addScalar("CLIENTEFACT", Hibernate.STRING);
		query.addScalar("confirmacion", Hibernate.STRING);
		query.addScalar("importe", Hibernate.STRING);
		query.addScalar("VENDEDOR", Hibernate.STRING);
		
		List list = query.list();

		HibernateUtil.cerrarSession(sesion);
		return CollectionUtil.listToObjectArray(list);
	}
	
	public Object[] findOFNoFacturadosByUnidadAdm2(String codUnidad) throws RemoteException {
		Session session = HibernateUtil.abrirSession();

		SQLQuery query = session.createSQLQuery("select nroppto,CLIENTEFACT,confirmacion,importe,estado,vendedor FROM VW_FACTURACION "+
				"inner join tx_ppto_facturacion ON pf_nroppto= nroppto "+
				"where pf_codadmin =:admin ");
		query.addScalar("nroppto", Hibernate.STRING);	
		query.addScalar("estado", Hibernate.STRING);
		query.addScalar("CLIENTEFACT", Hibernate.STRING);
		query.addScalar("confirmacion", Hibernate.STRING);
		query.addScalar("importe", Hibernate.STRING);
		query.addScalar("VENDEDOR", Hibernate.STRING);
		query.setString("admin",codUnidad);
		
		List list = query.list();

		HibernateUtil.cerrarSession(sesion);
		return CollectionUtil.listToObjectArray(list);
	}
	
	public boolean setUnidadAdministrativaByNroPpto(String nroppto, String codUnidad) throws RemoteException{
		Session session = null;
		Transaction tx = null;
		boolean result=false;
		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();
			String queryString = "from Presupuesto p where p.numeroDePresupuesto = '"
				+ nroppto + "'";
			Presupuesto p = (Presupuesto) session.createQuery(queryString)
				.uniqueResult();
			
			p.getFacturacion().setCodUnidadAdm(codUnidad);
			
			session.saveOrUpdate(p);
			tx.commit();
			session.flush();
			result = true;
		} catch (HibernateException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			e.printStackTrace(System.err);
			result = false;
		} finally {			
			HibernateUtil.cerrarSession(session);
			if(result)
				return true;
			else
				return false;
		}
	}
	
	public Object[] findOFNoFacturadosByUnidadAdm(String codUnidad) throws RemoteException {
		Session session = HibernateUtil.abrirSession();

		SQLQuery query = session.createSQLQuery("select nroppto,CLIENTEFACT,confirmacion,importe,estado,vendedor FROM VW_FACTURACION "+
				"inner join mst_unidades_admin_comercial ON CODUNIDAD= uac_codunidad_comercial "+
				"where uac_codunidad_admin =:admin ");
		query.addScalar("nroppto", Hibernate.STRING);	
		query.addScalar("estado", Hibernate.STRING);
		query.addScalar("CLIENTEFACT", Hibernate.STRING);
		query.addScalar("confirmacion", Hibernate.STRING);
		query.addScalar("importe", Hibernate.STRING);
		query.addScalar("VENDEDOR", Hibernate.STRING);
		query.setString("admin",codUnidad);
		
		List list = query.list();

		HibernateUtil.cerrarSession(sesion);
		return CollectionUtil.listToObjectArray(list);
	}
	
	public void setAsCobrado(String numeroPresupuesto, String usuarioId)
			throws RemoteException {
		Session session = null;
		Transaction tx = null;
		try {

			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			String queryString = "from Presupuesto p where p.numeroDePresupuesto = '"
					+ numeroPresupuesto + "'";
			Presupuesto p = (Presupuesto) session.createQuery(queryString)
					.uniqueResult();

			EstadoEvento estadoEvento = (EstadoEvento) session.load(
					EstadoEvento.class,
					EstadoEventoManagerSEI.CODIGO_ESTADO_COBRADO);
			Usuario usuario = (Usuario) session.load(Usuario.class, usuarioId);

			PptoCambioEstado cambioEstado = new PptoCambioEstado();
			cambioEstado.setEstado(estadoEvento);
			cambioEstado.setUsuario(usuario);
			cambioEstado.setFecha(DateConverter.convertDateToString(new Date(),
					"yyyy-MM-dd H:mm:ss"));
			cambioEstado.setPresupuesto(p);

			p.getEstadoActual().setCobrado(1);
			p.addCambioEstado(cambioEstado);

			session.saveOrUpdate(p);
			tx.commit();
			session.flush();
		} catch (HibernateException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			e.printStackTrace(System.err);
		} finally {
			HibernateUtil.cerrarSession(session);
		}
	}
	
	public void setAsCobradoAConfirmar(String numeroPresupuesto, String usuarioId)
	throws RemoteException {
		Session session = null;
		Transaction tx = null;
		try {
			
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();
			
			String queryString = "from Presupuesto p where p.numeroDePresupuesto = '"
				+ numeroPresupuesto + "'";
			Presupuesto p = (Presupuesto) session.createQuery(queryString)
			.uniqueResult();
			
			EstadoEvento estadoEvento = (EstadoEvento) session.load(
					EstadoEvento.class,
					EstadoEventoManagerSEI.CODIGO_ESTADO_A_COBRAR);
			Usuario usuario = (Usuario) session.load(Usuario.class, usuarioId);
			
			PptoCambioEstado cambioEstado = new PptoCambioEstado();
			cambioEstado.setEstado(estadoEvento);
			cambioEstado.setUsuario(usuario);
			cambioEstado.setFecha(DateConverter.convertDateToString(new Date(),
			"yyyy-MM-dd H:mm:ss"));
			cambioEstado.setPresupuesto(p);
			
			p.getEstadoActual().setAcobrar(1);
			p.addCambioEstado(cambioEstado);
			
			session.saveOrUpdate(p);
			tx.commit();
			session.flush();
		} catch (HibernateException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			e.printStackTrace(System.err);
		} finally {
			HibernateUtil.cerrarSession(session);
		}
	}
	
	public void setAnticipoAConfirmar(String numeroPresupuesto, String usuarioId)
	throws RemoteException {
		Session session = null;
		Transaction tx = null;
		try {
			
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();
			
			String queryString = "from Presupuesto p where p.numeroDePresupuesto = '"
				+ numeroPresupuesto + "'";
			Presupuesto p = (Presupuesto) session.createQuery(queryString)
			.uniqueResult();
			
			EstadoEvento estadoEvento = (EstadoEvento) session.load(
					EstadoEvento.class,
					EstadoEventoManagerSEI.CODIGO_ESTADO_ADELANTO_A_COBRAR);
			Usuario usuario = (Usuario) session.load(Usuario.class, usuarioId);
			
			PptoCambioEstado cambioEstado = new PptoCambioEstado();
			cambioEstado.setEstado(estadoEvento);
			cambioEstado.setUsuario(usuario);
			cambioEstado.setFecha(DateConverter.convertDateToString(new Date(),
			"yyyy-MM-dd H:mm:ss"));
			cambioEstado.setPresupuesto(p);
			
			p.getEstadoActual().setAdelantoacobrar(1);
			p.addCambioEstado(cambioEstado);
			
			session.saveOrUpdate(p);
			tx.commit();
			session.flush();
		} catch (HibernateException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			e.printStackTrace(System.err);
		} finally {
			HibernateUtil.cerrarSession(session);
		}
	}
	
	public void setAnticipoCobrado(String numeroPresupuesto, String usuarioId)
	throws RemoteException {
		Session session = null;
		Transaction tx = null;
		try {
			
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();
			
			String queryString = "from Presupuesto p where p.numeroDePresupuesto = '"
				+ numeroPresupuesto + "'";
			Presupuesto p = (Presupuesto) session.createQuery(queryString)
			.uniqueResult();
			
			EstadoEvento estadoEvento = (EstadoEvento) session.load(
					EstadoEvento.class,
					EstadoEventoManagerSEI.CODIGO_ESTADO_ADELANTO_COBRADO);
			Usuario usuario = (Usuario) session.load(Usuario.class, usuarioId);
			
			PptoCambioEstado cambioEstado = new PptoCambioEstado();
			cambioEstado.setEstado(estadoEvento);
			cambioEstado.setUsuario(usuario);
			cambioEstado.setFecha(DateConverter.convertDateToString(new Date(),
			"yyyy-MM-dd H:mm:ss"));
			cambioEstado.setPresupuesto(p);
			
			p.getEstadoActual().setAdelantocobrado(1);
			p.addCambioEstado(cambioEstado);
			
			session.saveOrUpdate(p);
			tx.commit();
			session.flush();
		} catch (HibernateException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			e.printStackTrace(System.err);
		} finally {
			HibernateUtil.cerrarSession(session);
		}
	}
	
	public Object[] getPresupuestoByFactura(String nrofactura) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		
		List presupuestos = session.createSQLQuery(
				"select fac_nroppto from tx_ppto_factura where fac_factura = ?")
				.addScalar("fac_nroppto",Hibernate.LONG)
				.setString(0, nrofactura)
				.list();
		HibernateUtil.cerrarSession(session);
		
		return CollectionUtil.listToObjectArray(presupuestos);
	}
	
	public Object[] getFacturasByNroPpto(long nroppto) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		
		List presupuestos = session.createSQLQuery(
				"select fac_factura from tx_ppto_factura where fac_nroppto = ?")
				.addScalar("fac_factura",Hibernate.STRING)
				.setLong(0, new Long(nroppto))
				.list();
		HibernateUtil.cerrarSession(session);
		
		return CollectionUtil.listToObjectArray(presupuestos);
	}
	
	public String getFacturaByNroPpto(long nroppto) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		
		String presupuestos = (String)session.createSQLQuery(
				"select fac_factura from tx_ppto_factura where fac_nroppto = ?")
				.addScalar("fac_factura",Hibernate.STRING)
				.setLong(0, new Long(nroppto))
				.uniqueResult();
		HibernateUtil.cerrarSession(session);
		
		if(presupuestos == null){
			presupuestos = "0";
		}
		
		return presupuestos;
	}
	
	public void setAsFacturado(String numeroPresupuesto, String usuarioId, String nrofactura) throws RemoteException {
		Session session = null;
		Transaction tx = null;
		try {

			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			String queryString = "from Presupuesto p where p.numeroDePresupuesto = '"
					+ numeroPresupuesto + "'";
			Presupuesto p = (Presupuesto) session.createQuery(queryString)
					.uniqueResult();

			EstadoEvento estadoEvento = (EstadoEvento) session.load(
					EstadoEvento.class,
					EstadoEventoManagerSEI.CODIGO_ESTADO_FACTURADO);
			Usuario usuario = (Usuario) session.load(Usuario.class, usuarioId);

			PptoCambioEstado cambioEstado = new PptoCambioEstado();
			cambioEstado.setEstado(estadoEvento);
			cambioEstado.setUsuario(usuario);
			cambioEstado.setFecha(DateConverter.convertDateToString(new Date(),
					"yyyy-MM-dd H:mm:ss"));
			cambioEstado.setPresupuesto(p);		
			
			Ppto_Facturas factura = new Ppto_Facturas();
			factura.setFactura(nrofactura);
			factura.setFechaAppend(DateConverter.convertDateToString(new Date(),
					"yyyy-MM-dd H:mm:ss"));
			factura.setEstado(EstadoEventoManagerSEI.CODIGO_ESTADO_FACTURADO);
			factura.setPresupuesto(p);
			
			p.getEstadoActual().setFacturado(1);
			p.addCambioEstado(cambioEstado);
			p.addFactura(factura);
			
			session.saveOrUpdate(p);
			tx.commit();
			session.flush();
		} catch (HibernateException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();				
			}
			e.printStackTrace(System.err);
		} finally {
			HibernateUtil.cerrarSession(session);
		}
		
	}
	
	public void setFactura(long id, String nroFactura) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		
		 session.createQuery(
				"update Ppto_Facturas set factura = :nrofact, fechaAppend = :fecha where id = :nro ")
				.setString("nrofact",nroFactura)
				.setString("fecha",DateConverter.convertDateToString(new Date(),
				"yyyy-MM-dd H:mm:ss"))				
				.setLong("nro", id)
				.executeUpdate();
		 
		 HibernateUtil.cerrarSession(session);
	}
	
	public void setDesConfirmado(String numeroPresupuesto, String usuarioId) throws RemoteException {
		Session session = null;
		Transaction tx = null;
		try {

			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			String queryString = "from Presupuesto p where p.numeroDePresupuesto = '"
					+ numeroPresupuesto + "'";
			Presupuesto p = (Presupuesto) session.createQuery(queryString)
					.uniqueResult();			

			EstadoEvento estadoEventoActualizado = (EstadoEvento) session.load(
					EstadoEvento.class,
					EstadoEventoManagerSEI.CODIGO_ESTADO_ACTUALIZADO);
			
			EstadoEvento estadoEvento = (EstadoEvento) session.load(
					EstadoEvento.class,
					EstadoEventoManagerSEI.CODIGO_ESTADO_DESCONFIRMADO);
			Usuario usuario = (Usuario) session.load(Usuario.class, usuarioId);
			
			PptoCambioEstado cambioEstado = new PptoCambioEstado();
			cambioEstado.setEstado(estadoEvento);
			cambioEstado.setUsuario(usuario);
			cambioEstado.setFecha(DateConverter.convertDateToString(new Date(),
					"yyyy-MM-dd H:mm:ss"));
			cambioEstado.setPresupuesto(p);

			for (PptoCambioEstado sdr : p.getCambiosEstado()){				
				if(sdr.getEstado().getCodigo().equals("6")){
					PptoCambioEstado ce = (PptoCambioEstado)session.load(PptoCambioEstado.class, sdr.getId());
					ce.setEstado(estadoEventoActualizado);
					ce.setUsuario(usuario);
					ce.setFecha(DateConverter.convertDateToString(new Date(),
							"yyyy-MM-dd H:mm:ss"));
					ce.setPresupuesto(p);
				}
			}			
			
			p.getEstadoActual().setAdelanto(0);
			p.getEstadoActual().setAcobrar(0);
			p.getEstadoActual().setAdelantado(0);
			p.getEstadoActual().setActualizado(0);
			p.getEstadoActual().setAdelantoacobrar(0);
			p.getEstadoActual().setAdelantocobrado(0);
			p.getEstadoActual().setAdicionales(0);
			p.getEstadoActual().setAdicionalesFacturados(0);
			p.getEstadoActual().setCancelado(0);
			p.getEstadoActual().setCobrado(0);
			p.getEstadoActual().setConfirmado(0);
			p.getEstadoActual().setFacturado(0);
			p.getEstadoActual().setNuevo(1);
			p.getEstadoActual().setOc(0);
			p.getEstadoActual().setOf(0);
			p.getEstadoActual().setOs(0);
			p.getEstadoActual().setRechazado(0);		
			
			//if (log.isDebugEnabled())
			//	log.info("--------------> cant de cambios de estado: \n" +p.getCambiosEstado().size());

			p.addCambioEstado(cambioEstado);
			session.saveOrUpdate(p);		
			
			tx.commit();
			session.flush();
		} catch (HibernateException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			e.printStackTrace(System.err);
		} finally {
			HibernateUtil.cerrarSession(session);
		}
	}
	
	public void setAsAdelantado(String numeroPresupuesto, String usuarioId, String nrofactura) throws RemoteException {
		Session session = null;
		Transaction tx = null;
		try {

			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			String queryString = "from Presupuesto p where p.numeroDePresupuesto = '"
					+ numeroPresupuesto + "'";
			Presupuesto p = (Presupuesto) session.createQuery(queryString)
					.uniqueResult();

			EstadoEvento estadoEvento = (EstadoEvento) session.load(
					EstadoEvento.class,
					EstadoEventoManagerSEI.CODIGO_ESTADO_ADELANTADO);
			Usuario usuario = (Usuario) session.load(Usuario.class, usuarioId);

			PptoCambioEstado cambioEstado = new PptoCambioEstado();
			cambioEstado.setEstado(estadoEvento);
			cambioEstado.setUsuario(usuario);
			cambioEstado.setFecha(DateConverter.convertDateToString(new Date(),
					"yyyy-MM-dd H:mm:ss"));
			cambioEstado.setPresupuesto(p);
			
			Ppto_Facturas factura = new Ppto_Facturas();
			factura.setFactura(nrofactura);
			factura.setFechaAppend(DateConverter.convertDateToString(new Date(),
					"yyyy-MM-dd H:mm:ss"));
			factura.setEstado(EstadoEventoManagerSEI.CODIGO_ESTADO_ADELANTADO);
			factura.setPresupuesto(p);

			p.getEstadoActual().setAdelantado(1);
			p.addCambioEstado(cambioEstado);
			p.addFactura(factura);

			session.saveOrUpdate(p);
			tx.commit();
			session.flush();
		} catch (HibernateException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			e.printStackTrace(System.err);
		} finally {
			HibernateUtil.cerrarSession(session);
		}
		
	}
	
	public void setAsAdicionalesFacturados(String numeroPresupuesto, String usuarioId, String nrofactura) throws RemoteException {
		Session session = null;
		Transaction tx = null;
		try {

			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			String queryString = "from Presupuesto p where p.numeroDePresupuesto = '"
					+ numeroPresupuesto + "'";
			Presupuesto p = (Presupuesto) session.createQuery(queryString)
					.uniqueResult();

			EstadoEvento estadoEvento = (EstadoEvento) session.load(
					EstadoEvento.class,
					EstadoEventoManagerSEI.CODIGO_ESTADO_ADICIONALES_FACTURADOS);
			Usuario usuario = (Usuario) session.load(Usuario.class, usuarioId);

			PptoCambioEstado cambioEstado = new PptoCambioEstado();
			cambioEstado.setEstado(estadoEvento);
			cambioEstado.setUsuario(usuario);
			cambioEstado.setFecha(DateConverter.convertDateToString(new Date(),
					"yyyy-MM-dd H:mm:ss"));
			cambioEstado.setPresupuesto(p);
			
			Ppto_Facturas factura = new Ppto_Facturas();
			factura.setFactura(nrofactura);
			factura.setFechaAppend(DateConverter.convertDateToString(new Date(),
					"yyyy-MM-dd H:mm:ss"));
			factura.setEstado(EstadoEventoManagerSEI.CODIGO_ESTADO_ADICIONALES_FACTURADOS);
			factura.setPresupuesto(p);

			p.getEstadoActual().setAdicionalesFacturados(1);
			p.addCambioEstado(cambioEstado);
			p.addFactura(factura);

			session.saveOrUpdate(p);
			tx.commit();
			session.flush();
		} catch (HibernateException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			e.printStackTrace(System.err);
		} finally {
			HibernateUtil.cerrarSession(session);
		}
		
	}
	
	public Object[] getPendientesPorUC(long codigoUC) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		
		List presupuestos = session.createSQLQuery(
				"SELECT t.ppto_nroppto as nroppto FROM tx_ppto t "+			
				"INNER JOIN tx_vendedor_ppto v ON v.vp_nroppto = t.ppto_nroppto "+ 
				"INNER JOIN vw_uc_vendedores ucv ON v.vp_vendedor = ucv.codvend "+
				"INNER JOIN tx_ppto_est_actual ea ON ea.estact_nroppto = t.ppto_nroppto "+				
				"WHERE (SELECT MONTH(t.ppto_fecinicio) FROM DUAL) = (SELECT MONTH(SYSDATE()) FROM DUAL) "+
				"AND (SELECT YEAR(t.ppto_fecinicio) FROM DUAL) = (SELECT YEAR(SYSDATE()) FROM DUAL) "+
				"AND ucv.coduc = ? "+
				"AND ea.estact_confirmado = ? ORDER BY t.ppto_nroppto"
				)
				.addScalar("nroppto", Hibernate.LONG)
				.setLong(0,new Long(codigoUC))
				.setInteger(1, new Integer(0))
				.list();
		
		HibernateUtil.cerrarSession(session);
		
		return CollectionUtil.listToObjectArray(presupuestos);
	}

	public Object[] getDeHoyPorUC(long codigoUC) throws RemoteException {

		Session session = HibernateUtil.abrirSession();
		
		List presupuestos = session.createSQLQuery(
				"SELECT t.ppto_nroppto AS nroppto FROM tx_ppto t "+
				"INNER JOIN tx_vendedor_ppto v ON v.vp_nroppto = t.ppto_nroppto "+	
				"INNER JOIN vw_uc_vendedores ucv ON v.vp_vendedor = ucv.codvend "+
				"INNER JOIN tx_ppto_est_actual ea ON ea.estact_nroppto = t.ppto_nroppto "+
				"WHERE (SELECT MONTH(t.ppto_fecinicio) FROM DUAL) = (SELECT MONTH(SYSDATE()) FROM DUAL) "+
				"AND (SELECT YEAR(t.ppto_fecinicio) FROM DUAL) = (SELECT YEAR(SYSDATE()) FROM DUAL) "+
				"AND (SELECT DAYOFYEAR(t.ppto_fecinicio)) = (SELECT DAYOFYEAR(CURDATE())) "+
				"AND ucv.coduc = ? "+
				"AND ea.estact_confirmado = ? ORDER BY t.ppto_nroppto"
				)
				.addScalar("nroppto", Hibernate.LONG)				
				.setLong(0,new Long(codigoUC))
				.setInteger(1, new Integer(1))
				.list();
		
		HibernateUtil.cerrarSession(session);
		
		return CollectionUtil.listToObjectArray(presupuestos);
	}

	public Object[] getConfirmadosPorUC(long codigoUC) throws RemoteException {

		Session session = HibernateUtil.abrirSession();
		
		List presupuestos = session.createSQLQuery(
				"SELECT t.ppto_nroppto as nroppto FROM tx_ppto t "+			
				"INNER JOIN tx_vendedor_ppto v ON v.vp_nroppto = t.ppto_nroppto "+ 
				"INNER JOIN vw_uc_vendedores ucv ON v.vp_vendedor = ucv.codvend "+
				"INNER JOIN tx_ppto_est_actual ea ON ea.estact_nroppto = t.ppto_nroppto "+				
				"WHERE (SELECT MONTH(t.ppto_fecinicio) FROM DUAL) = (SELECT MONTH(SYSDATE()) FROM DUAL) "+
				"AND (SELECT YEAR(t.ppto_fecinicio) FROM DUAL) = (SELECT YEAR(SYSDATE()) FROM DUAL) "+
				"AND ucv.coduc = ? "+
				"AND ea.estact_confirmado = ? ORDER BY t.ppto_nroppto"
				)
				.addScalar("nroppto", Hibernate.LONG)
				.setLong(0,new Long(codigoUC))
				.setInteger(1, new Integer(1))
				.list();
		
		HibernateUtil.cerrarSession(session);
		
		return CollectionUtil.listToObjectArray(presupuestos);
	}

	public Object[] getAVencerPorUC(long codigoUC) throws RemoteException {

		Session session = HibernateUtil.abrirSession();
		
		List presupuestos = session.createSQLQuery(
				"SELECT t.ppto_nroppto AS nroppto FROM tx_ppto t "+
				"INNER JOIN tx_vendedor_ppto v ON v.vp_nroppto = t.ppto_nroppto "+	
				"INNER JOIN vw_uc_vendedores ucv ON v.vp_vendedor = ucv.codvend "+
				"INNER JOIN tx_ppto_est_actual ea ON ea.estact_nroppto = t.ppto_nroppto "+
				"WHERE (SELECT MONTH(t.ppto_fecinicio) FROM DUAL) = (SELECT MONTH(SYSDATE()) FROM DUAL) "+
				"AND (SELECT YEAR(t.ppto_fecinicio) FROM DUAL) = (SELECT YEAR(SYSDATE()) FROM DUAL) "+
				"AND (SELECT DAYOFYEAR(t.ppto_fecinicio)) = (SELECT DAYOFYEAR(CURDATE())+ ?) "+
				"AND ucv.coduc = ? "+
				"AND ea.estact_confirmado = ? ORDER BY t.ppto_nroppto"
				)
				.addScalar("nroppto", Hibernate.LONG)
				.setInteger(0, new Integer(getDiasVencimientoSysSettingById(SYS_SETTINGS_VENCIMIENTO_OF)))
				.setLong(1,new Long(codigoUC))
				.setInteger(2, new Integer(0))
				.list();
		
		HibernateUtil.cerrarSession(session);
		
		return CollectionUtil.listToObjectArray(presupuestos);
	}

	public double getFacturacionByPPto(long nroppto) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		
		Double presupuesto = (Double)session.createSQLQuery(
				"SELECT facturacion AS facturacion FROM vw_rentabilidad t "+
				"WHERE presupuesto = ? "
				)
				.addScalar("facturacion", Hibernate.DOUBLE)				
				.setLong(0,new Long(nroppto))				
				.uniqueResult();
		
		HibernateUtil.cerrarSession(session);
		
		return presupuesto.doubleValue();
	}

	public Object[] getPendientesPorVendedor(String codigoVendedor) throws RemoteException {
	
		Session session = HibernateUtil.abrirSession();
		
		List presupuestos = session.createSQLQuery(
				"SELECT t.ppto_nroppto AS nroppto FROM tx_ppto t "+ 
				"INNER JOIN tx_vendedor_ppto v ON v.vp_nroppto = t.ppto_nroppto "+ 
				"INNER JOIN tx_ppto_est_actual ea ON ea.estact_nroppto = t.ppto_nroppto "+				
				"WHERE (SELECT MONTH(t.ppto_fecinicio) FROM DUAL) = (SELECT MONTH(SYSDATE()) FROM DUAL) "+
				"AND (SELECT YEAR(t.ppto_fecinicio) FROM DUAL) = (SELECT YEAR(SYSDATE()) FROM DUAL) "+
				"AND v.vp_vendedor = ? AND ea.estact_confirmado = ? "+ 
				"ORDER BY t.ppto_nroppto"
				)
				.addScalar("nroppto", Hibernate.LONG)
				.setLong(0,new Long(codigoVendedor))
				.setInteger(1, new Integer(0))
				.list();
		
		HibernateUtil.cerrarSession(session);
		
		return CollectionUtil.listToObjectArray(presupuestos);
	}
	
	public Object[] getAVencerPorVendedor(String codigoVendedor) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		
		List presupuestos = session.createSQLQuery(
				"SELECT t.ppto_nroppto AS nroppto FROM tx_ppto t "+
				"INNER JOIN tx_vendedor_ppto v ON t.ppto_nroppto = v.vp_nroppto "+				
				"INNER JOIN tx_ppto_est_actual ea ON ea.estact_nroppto = t.ppto_nroppto "+
				"WHERE (SELECT MONTH(t.ppto_fecinicio) FROM DUAL) = (SELECT MONTH(SYSDATE()) FROM DUAL) "+
				"AND (SELECT YEAR(t.ppto_fecinicio) FROM DUAL) = (SELECT YEAR(SYSDATE()) FROM DUAL) "+
				"AND (SELECT DAYOFYEAR(t.ppto_fecinicio)) = (SELECT DAYOFYEAR(CURDATE())+ ?) "+
				"AND v.vp_vendedor = ? "+
				"AND ea.estact_confirmado = ? ORDER BY t.ppto_nroppto"
				)
				.addScalar("nroppto", Hibernate.LONG)
				.setInteger(0, new Integer(getDiasVencimientoSysSettingById(SYS_SETTINGS_VENCIMIENTO_OF)))
				.setLong(1,new Long(codigoVendedor))
				.setInteger(2, new Integer(0))
				.list();
		
		HibernateUtil.cerrarSession(session);
		
		return CollectionUtil.listToObjectArray(presupuestos);
	}
	
	private String getDiasVencimientoSysSettingById(long id){
		Session session = HibernateUtil.abrirSession();
		
		String result = (String)session.createSQLQuery
		("select ss_valor from SYS_SETTINGS where ss_id = ?")
		.addScalar("ss_valor", Hibernate.STRING)
		.setLong(0,id)
		.uniqueResult();
		
		HibernateUtil.cerrarSession(session);
		
		return result;
	}
	
	public Object[] getDeHoyPorVendedor(String codigoVendedor) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		
		List presupuestos = session.createSQLQuery(
				"SELECT t.ppto_nroppto AS nroppto FROM tx_ppto t "+
				"INNER JOIN tx_vendedor_ppto v ON t.ppto_nroppto = v.vp_nroppto "+				
				"INNER JOIN tx_ppto_est_actual ea ON ea.estact_nroppto = t.ppto_nroppto "+
				"WHERE (SELECT MONTH(t.ppto_fecinicio) FROM DUAL) = (SELECT MONTH(SYSDATE()) FROM DUAL) "+
				"AND (SELECT YEAR(t.ppto_fecinicio) FROM DUAL) = (SELECT YEAR(SYSDATE()) FROM DUAL) "+
				"AND (SELECT DAYOFYEAR(t.ppto_fecinicio)) = (SELECT DAYOFYEAR(CURDATE())) "+
				"AND v.vp_vendedor = ? "+
				"AND ea.estact_confirmado = ? ORDER BY t.ppto_nroppto"
				)
				.addScalar("nroppto", Hibernate.LONG)				
				.setLong(0,new Long(codigoVendedor))
				.setInteger(1, new Integer(1))
				.list();
		
		HibernateUtil.cerrarSession(session);
		
		return CollectionUtil.listToObjectArray(presupuestos);
	}

	public Object[] getConfirmadosPorVendedor(String codigoVendedor) throws RemoteException {

		Session session = HibernateUtil.abrirSession();
		
		List presupuestos = session.createSQLQuery(
				"SELECT t.ppto_nroppto AS nroppto FROM tx_ppto t "+ 
				"INNER JOIN tx_vendedor_ppto v ON v.vp_nroppto = t.ppto_nroppto "+ 
				"INNER JOIN tx_ppto_est_actual ea ON ea.estact_nroppto = t.ppto_nroppto "+				
				"WHERE (SELECT MONTH(t.ppto_fecinicio) FROM DUAL) = (SELECT MONTH(SYSDATE()) FROM DUAL) "+
				"AND (SELECT YEAR(t.ppto_fecinicio) FROM DUAL) = (SELECT YEAR(SYSDATE()) FROM DUAL) "+
				"AND v.vp_vendedor = ? AND ea.estact_confirmado = ? "+ 
				"ORDER BY t.ppto_nroppto"
				)
				.addScalar("nroppto", Hibernate.LONG)
				.setLong(0,new Long(codigoVendedor))
				.setInteger(1, new Integer(1))
				.list();
		
		HibernateUtil.cerrarSession(session);
		
		return CollectionUtil.listToObjectArray(presupuestos);
	}


	public String getFechaByNroPptoAndStateAndUser(long nroPpto, int codEstado)throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		
		String result = (String) session.createSQLQuery(
				"select max(chest_fecha) as max from TX_CAMBIO_ESTADO where chest_nroppto = :ppto "+
						"and chest_estado = :estado")
						.addScalar("max", Hibernate.STRING)
						.setLong("ppto", nroPpto)
						.setInteger("estado", codEstado).uniqueResult();
		
		HibernateUtil.cerrarSession(session);
		
		return result;
						
	}
	
	public String getMaxFechaByNroPptoAndState(long nroPpto, int codEstado)throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		
		String mayorSt = "1999-01-01 00:00:00";
		try{
		Date mayor = DateConverter.convertStringToDate(mayorSt, "yyyy-MM-dd HH:mm:ss");
		
		List result = session.createSQLQuery(
				"select max(chest_fecha) as max from TX_CAMBIO_ESTADO where chest_nroppto = :ppto "+
						"and chest_estado = :estado")
						.addScalar("max", Hibernate.STRING)
						.setLong("ppto", nroPpto)
						.setInteger("estado", codEstado).list();
		
		if (result != null){
			Iterator it = result.iterator();
			while(it.hasNext()){				
				Date actual = DateConverter.convertStringToDate((String)it.next(), "yyyy-MM-dd HH:mm:ss");
				if (actual.after(mayor)){					
					mayor = actual; 
				}
			}
		}
		
		HibernateUtil.cerrarSession(session);
		
		return DateConverter.convertDateToString(mayor, "yyyy-MM-dd HH:mm:ss");
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public boolean canEdit(long nroPpto,String loggedUser)throws RemoteException{
				
		Session session = HibernateUtil.abrirSession();
		
		UsuarioManager usuarioManager = new UsuarioManager();
		Usuario usuario = usuarioManager.getUsuarioById(loggedUser);
		
		//verifico que el usuario exista		
		if(usuario == null){
			return false;
		}
		//si no es vendedor o supervisor le doy permisos
		//if(!usuario.getCodigo().equals(PerfilManager.PERFIL_VENDEDOR) || !usuario.getCodigo().equals(PerfilManager.PERFIL_SUPERVISOR)){
		//si es administrador no puede editar presupuesto
		if(usuario.getPerfil().equals(PerfilManager.PERFIL_ADMIN)){
			return false;
		}
		
		//si es de gerencia comercial puede editar presupuesto
		if(usuario.getPerfil().equals(PerfilManager.PERFIL_GERENCIA_COMERCIAL)){
			return true;
		}
		
		//si es del cold puede editar presupuesto
		if(usuario.getPerfil().equals(PerfilManager.PERFIL_COLD)){
			return true;
		}
		
		Presupuesto presupuesto = (Presupuesto)session.load(Presupuesto.class,nroPpto);
		if(presupuesto != null && presupuesto.getVendedor() != null && presupuesto.getVendedor().getVendedor() != null){
			//codigo del vendedor de presupuesto
			Vendedor vendedor = presupuesto.getVendedor().getVendedor();
			String pptoVendedorId = vendedor.getCodigo();
			
			//codigo del vendedor logueado
			VendedorUsuarioManager vendedorUsuarioManager = new VendedorUsuarioManager();
			String loggedVendedorId = vendedorUsuarioManager.getCodigoVendedor(loggedUser);
			
			//codigo de unidad comercial del presupuesto
			UnidadVendedorManager unidadVendedorManager = new UnidadVendedorManager();
			String pptoUnidadComercialId = unidadVendedorManager.getCodigoUnidad(pptoVendedorId);
			
			//codigo de unidad comercial del usuario logueado
			String logguedUnidadComercialId = unidadVendedorManager.getCodigoUnidad(loggedVendedorId);
			
			//si pertenecen a la misma UC retorno true
			if(logguedUnidadComercialId != null && pptoUnidadComercialId != null &&
					logguedUnidadComercialId.equals(pptoUnidadComercialId)){
				return true;
			}
			else return false;
		}			
		HibernateUtil.cerrarSession(session);
			
		return false;
	}
	
	public boolean isPptoActualizado(long nroPpto) throws RemoteException{
		Session session = HibernateUtil.abrirSession(); 
		
		Integer res = (Integer)session.createSQLQuery(
				"select estact_actualizado from TX_PPTO_EST_ACTUAL where "+
				"estact_nroppto = :nro"
				).addScalar("estact_actualizado", Hibernate.INTEGER)
				.setLong("nro",nroPpto)
				.uniqueResult();
		
		HibernateUtil.cerrarSession(session);
		
		return res.intValue() == 1;
	}
	
	public boolean isPptoCancelado(long nroPpto) throws RemoteException{
		Session session = HibernateUtil.abrirSession(); 
		
		Integer res = (Integer)session.createSQLQuery(
				"select estact_cancelado from TX_PPTO_EST_ACTUAL where "+
				"estact_nroppto = :nro"
				).addScalar("estact_cancelado", Hibernate.INTEGER)
				.setLong("nro",nroPpto)
				.uniqueResult();
		
		HibernateUtil.cerrarSession(session);
		
		return res.intValue() == 1;
	}
	
	public void modificarActivo(long nroPpto, String activo, int codUsuario, String ip, String mac) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		
		 session.createQuery(
				"update Presupuesto set activo = :act, usuarioActivo = :us, ip = :ipaddress, macAddress = :mc where numeroDePresupuesto = :nro")
				.setString("act",activo)
				.setLong("nro", nroPpto)
				.setInteger("us", codUsuario)
				.setString("ipaddress", ip)
				.setString("mc", mac)
				.executeUpdate();

		 HibernateUtil.cerrarSession(session);
	}
	
	public Object[] buscarPresupuestosAbiertosVendedor(long codVendedor) throws RemoteException{

		Session session = HibernateUtil.abrirSession();
		
		List presupuestos = session.createSQLQuery(
				"SELECT t.ppto_nroppto AS nroppto, ven.apynom AS vendedor, " +
				"cli.nombre_fantasia AS nombreFantasia, t.ppto_evt_nombre AS evtNombre, " +
				"t.ppto_fecinicio AS fecha, ea.estado AS estado FROM mst_cliente cli "+
				"INNER JOIN tx_ppto t ON t.ppto_codcliente = cli.cl_codcliente "+
				"INNER JOIN tx_vendedor_ppto v ON v.vp_nroppto = t.ppto_nroppto "+
				"INNER JOIN mst_vendedores ven ON ven.vd_codvend = v.vp_vendedor "+				
				"INNER JOIN vw_pptos_estados ea ON ea.nroppto = t.ppto_nroppto "+
				"WHERE v.vp_vendedor = ? and t.ppto_activo ='S' "+
				"group by t.ppto_nroppto ORDER BY t.ppto_nroppto"
				)
				.addScalar("nroppto", Hibernate.LONG)
				.addScalar("vendedor", Hibernate.STRING)
				.addScalar("nombreFantasia", Hibernate.STRING)
				.addScalar("evtNombre", Hibernate.STRING)
				.addScalar("fecha", Hibernate.STRING)
				.addScalar("estado", Hibernate.STRING)
				.setLong(0,new Long(codVendedor))
				.list();
		
		HibernateUtil.cerrarSession(session);
		
		return CollectionUtil.listToObjectArray(presupuestos);
	}

	public int getCantPresupuestosByClientes(long codCliente) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		Integer result = (Integer)session.createSQLQuery(
				"select count(*) as cant from TX_PPTO where ppto_codcliente = :codigo"
				)
				.addScalar("cant", Hibernate.INTEGER)
				.setLong("codigo", codCliente)
				.uniqueResult();
		
		HibernateUtil.cerrarSession(session);
		
		return result.intValue();
		
	}
	
	public long getCodCondicionPagoByNroPpto(long nroPpto){
		Session session = HibernateUtil.abrirSession();
		Long result = (Long)session.createSQLQuery(
				"select pp_condicionpago as cond from TX_PPTO_PAGO where pp_nroppto = :codigo"
				)
				.addScalar("cond", Hibernate.LONG)
				.setLong("codigo", nroPpto)
				.uniqueResult();
		
		HibernateUtil.cerrarSession(session);
		
		return result.longValue();
	}
	
	private static PresupuestosManager instance;
	
	public static synchronized PresupuestosManager instance() {

			if (instance == null) 
				instance = new PresupuestosManager();

		return instance;
	}

	public Object[] buscarEstadoActual(long nroPpto) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		
		List presupuestos = session.createSQLQuery(
				"select t.estact_nuevo as nuevo, t.estact_actualizado as actualizado, "+
				"t.estact_confirmado as confirmado, t.estact_cancelado as cancelado, "+
				"t.estact_rechazado as rechazado, t.estact_os as os, t.estact_of as of, "+
				"t.estact_oc as oc, t.estact_facturado as facturado, t.estact_cobrado as cobrado, "+
				"t.estact_adelanto as adelanto, t.estact_adelantado as adelantado, t.estact_cobrado_confirmar as cobradoAConfirmar, "+
				"t.estact_anticipo_confirmar as anticipoAConfirmar, t.estact_anticipo_cobrado as anticipoCobrado " +
				"from TX_PPTO_EST_ACTUAL t where t.estact_nroppto = :nro"
				)
				.addScalar("nuevo", Hibernate.INTEGER)//0
				.addScalar("actualizado", Hibernate.INTEGER)//1				
				.addScalar("confirmado", Hibernate.INTEGER)//2
				.addScalar("cancelado", Hibernate.INTEGER)//3
				.addScalar("rechazado", Hibernate.INTEGER)//4				
				.addScalar("os", Hibernate.INTEGER)//5
				.addScalar("of", Hibernate.INTEGER)//6
				.addScalar("oc", Hibernate.INTEGER)//7
				.addScalar("facturado", Hibernate.INTEGER)//8				
				.addScalar("cobrado", Hibernate.INTEGER)//9
				.addScalar("adelanto", Hibernate.INTEGER)//10
				.addScalar("adelantado", Hibernate.INTEGER)//11
				.addScalar("cobradoAConfirmar", Hibernate.INTEGER)//12
				.addScalar("anticipoAConfirmar", Hibernate.INTEGER)//13
				.addScalar("anticipoCobrado", Hibernate.INTEGER)//14
				.setLong("nro", nroPpto)
				.list();
		
		HibernateUtil.cerrarSession(session);
		
		return CollectionUtil.listToObjectArray(presupuestos);
		
	}
}
