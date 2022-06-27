package crm.services.report.manager;

import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.hibernate.Session;

import crm.libraries.report.SubcontratadosGerencia;
import crm.services.report.sei.SubcontratadosGerenciaReportSEI;
import crm.services.util.HibernateUtil;

public class SubcontratadosGerenciaReport implements SubcontratadosGerenciaReportSEI,ReportService {
	private static final Log log = LogFactory.getLog(SubcontratadosGerenciaReport.class);
	
	public SubcontratadosGerencia[] findByRangeDate(int day1, int month1, int year1, 
												int day2, int month2, int year2, long codVendedor, long codUc,
												long codLugar, long codEstado) throws RemoteException{
		Calendar cal = new GregorianCalendar();
		
		cal.set(Calendar.YEAR, year1);
		cal.set(Calendar.MONTH, month1-1);
		cal.set(Calendar.DATE, day1);
		
		Date startDate = cal.getTime();
		cal.set(year2,month2-1,day2,23,59,59);
		Date endDate = cal.getTime();
		
		return findByDateRange(startDate,endDate, whereClause(codVendedor,codUc, codLugar, codEstado));
		//return findByDateRange(startDate,endDate);
	}
	
	public int findTotalPptos(int day1, int month1, int year1, 
			int day2, int month2, int year2) throws RemoteException{
		Calendar cal = new GregorianCalendar();
		
		cal.set(Calendar.YEAR, year1);
		cal.set(Calendar.MONTH, month1-1);
		cal.set(Calendar.DATE, day1);
		
		Date startDate = cal.getTime();
		cal.set(year2,month2-1,day2,23,59,59);
		Date endDate = cal.getTime();
		
		return getTotalPpto(startDate,endDate);
	}
	
	/*private String getEstadoActual(long codEstado){
		String st = "";
		if(codEstado == Long.parseLong(EstadoEventoManagerSEI.CODIGO_ESTADO_COBRADO))
			st=" and cobrado = 1 and cancelado = 0";
		else if(codEstado == Long.parseLong(EstadoEventoManagerSEI.CODIGO_ESTADO_FACTURADO))
			st=" and facturado = 1 and cobrado = 0 and cancelado = 0";
		else if(codEstado == Long.parseLong(EstadoEventoManagerSEI.CODIGO_ESTADO_ORDEN_FACTURACION))
			st=" and of = 1 and facturado = 0 and cancelado = 0";
		else if(codEstado == Long.parseLong(EstadoEventoManagerSEI.CODIGO_ESTADO_ORDEN_SERVICIO))
			st=" and os = 1 and of = 0 and cancelado = 0";
		else if(codEstado == Long.parseLong(EstadoEventoManagerSEI.CODIGO_ESTADO_CANCELADO))
			st=" and cancelado = 1";
		else if(codEstado == Long.parseLong(EstadoEventoManagerSEI.CODIGO_ESTADO_CONFIRMADO))
			st=" and confirmado = 1 and cancelado = 0";
		else if(codEstado == Long.parseLong(EstadoEventoManagerSEI.CODIGO_ESTADO_ACTUALIZADO))
			st=" and actualizado = 1 and confirmado = 0 and rechazado = 0";
		else if(codEstado == Long.parseLong(EstadoEventoManagerSEI.CODIGO_ESTADO_NUEVO))
			st=" and (nuevo = 1 or actualizado = 1) and confirmado = 0 and rechazado = 0";
		else if(codEstado == Long.parseLong(EstadoEventoManagerSEI.CODIGO_ESTADO_RECHAZADO))
			st=" and rechazado = 1";
		else if(codEstado == Long.parseLong(EstadoEventoManagerSEI.CODIGO_ESTADO_ORDEN_COMPRA))
			st=" and oc = 1";
		return st;
	}*/
	
	private String whereClause(long codVendedor, long codUc, long propio, long subcont){
			
		String st = "";
		
		if(codVendedor!=0 && codUc!=0 && propio!= 0 && subcont!= 0){
			st="and codVendedor = "+codVendedor+" and codUnidad = "+codUc;
		}
		else if(codVendedor!=0 && codUc!=0 && propio!= 0 && subcont == 0){
			st="and codVendedor = "+codVendedor+" and codUnidad = "+codUc+" and esPropio='Si'";
		}
		else if(codVendedor!=0 && codUc!=0 && propio== 0 && subcont!= 0){
			st="and codVendedor = "+codVendedor+" and codUnidad = "+codUc+" and esPropio='No'";
		}
		else if(codVendedor!=0 && codUc!=0 && propio== 0 && subcont== 0){
			st="and codVendedor = "+codVendedor+" and codUnidad = "+codUc;
		}
		//------------------------------------------------------------------------------------
		else if(codVendedor!=0 && codUc==0 && propio!= 0 && subcont!= 0){
			st="and codVendedor = "+codVendedor;
		}
		else if(codVendedor!=0 && codUc==0 && propio!= 0 && subcont == 0){
			st="and codVendedor = "+codVendedor+" and esPropio='Si'";
		}
		else if(codVendedor!=0 && codUc==0 && propio== 0 && subcont!= 0){
			st="and codVendedor = "+codVendedor+" and esPropio='No'";
		}
		else if(codVendedor!=0 && codUc==0 && propio== 0 && subcont== 0){
			st="and codVendedor = "+codVendedor;			
			if (log.isDebugEnabled()){
				log.info("codigo de Vendedor =============================== "+codVendedor+"string"+st);
			}
		}
		//------------------------------------------------------------------------------------
		else if(codVendedor==0 && codUc!=0 && propio!= 0 && subcont!= 0){
			st="and codUnidad = "+codUc;
		}
		else if(codVendedor==0 && codUc!=0 && propio!= 0 && subcont == 0){
			st="and codUnidad = "+codUc+" and esPropio='Si'";
		}
		else if(codVendedor==0 && codUc!=0 && propio== 0 && subcont!= 0){
			st="and codUnidad = "+codUc+" and esPropio='No'";
		}
		else if(codVendedor==0 && codUc!=0 && propio== 0 && subcont== 0){
			st="and codUnidad = "+codUc;
		}
		//------------------------------------------------------------------------------------
		else if(codVendedor==0 && codUc==0 && propio!= 0 && subcont!= 0){
			st="";
		}
		else if(codVendedor==0 && codUc==0 && propio!= 0 && subcont == 0){
			st=" and esPropio='Si'";
		}
		else if(codVendedor==0 && codUc==0 && propio== 0 && subcont!= 0){
			st=" and esPropio='No'";
		}	
		
		return st;
		
	}
	
	private int getTotalPpto(Date startDate, Date endDate){
		Session session = HibernateUtil.abrirSession();
		
		Integer result = (Integer)session.createSQLQuery
				(
				"select count(*) as cantidad "+
				"from vw_rpt_ppto_gerencia " +
				//"where fechaInicialEvento >= ? and fechaInicialEvento <= ? " +where+
				"where ((fechaInicialEvento >= ? and fechaFinalEvento <= ?) "+
				"or (fechaInicialEvento <= ? and fechaFinalEvento > ?) "+
				"or(fechaInicialEvento< ? and fechaFinalEvento>= ?)) "
				)
				.addScalar("cantidad", Hibernate.INTEGER)
				.setDate(0,startDate)
				.setDate(1,endDate)
				.setDate(2,endDate)
				.setDate(3,endDate)
				.setDate(4,startDate)
				.setDate(5,startDate)
				.uniqueResult();
		
		return result.intValue();
	}
	
	private SubcontratadosGerencia[] findByDateRange(Date startDate, Date endDate, String where){
	//private SubcontratadosGerencia[] findByDateRange(Date startDate, Date endDate){
		Session session = HibernateUtil.abrirSession();
		
		List list = session.createSQLQuery
				(
				"select cliente, numeroPresupuesto, vendedor, fechaInicialEvento, fechaFinalEvento, servicio, esPropio, precioVenta, precioCompra "+
				"from vw_rpt_subcontratado_gerencia " +
				"where ((fechaInicialEvento >= ? and fechaFinalEvento <= ?) "+
				"or (fechaInicialEvento <= ? and fechaFinalEvento > ?) "+
				"or(fechaInicialEvento< ? and fechaFinalEvento>= ?)) " +where+
				" order by fechaInicialEvento"
				)
				.addScalar("cliente",Hibernate.STRING)//0
				.addScalar("numeroPresupuesto",Hibernate.LONG)//1
				.addScalar("vendedor", Hibernate.STRING)//2
				.addScalar("fechaInicialEvento", Hibernate.DATE)//3
				.addScalar("fechaFinalEvento", Hibernate.DATE)//4
				.addScalar("servicio",Hibernate.STRING)//5
				.addScalar("esPropio",Hibernate.STRING)//6
				.addScalar("precioVenta", Hibernate.DOUBLE)//7
				.addScalar("precioCompra", Hibernate.DOUBLE)//8
				.setDate(0,startDate)
				.setDate(1,endDate)
				.setDate(2,endDate)
				.setDate(3,endDate)
				.setDate(4,startDate)
				.setDate(5,startDate)

				.list();
		
		SubcontratadosGerencia[] results = new SubcontratadosGerencia[list.size()];
		for (int i=0; i< results.length;i++){ 
			results[i] = new SubcontratadosGerencia();
			Object[] row = (Object[]) list.get(i);
			
			results[i].setCliente((String)row[0]);
			results[i].setNroPpto((Long)row[1]);
			results[i].setVendedor((String)row[2]);
			results[i].setFechaInicio((Date)row[3]);
			results[i].setFechaFin((Date)row[4]);
			results[i].setServicio((String)row[5]);
			results[i].setPropio((String)row[6]);
			results[i].setPrecioVenta((Double)row[7]);
			results[i].setPrecioCompra((Double)row[8]);
			
		}
		
		HibernateUtil.cerrarSession(session);
		
		return results;
	}

}
