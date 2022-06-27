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

import crm.libraries.report.LimitesGerencia;
import crm.services.report.sei.LimitesGerenciaReportSEI;
import crm.services.sei.EstadoEventoManagerSEI;
import crm.services.util.HibernateUtil;

public class LimitesGerenciaReport implements LimitesGerenciaReportSEI,ReportService {
	private static final Log log = LogFactory.getLog(LimitesGerenciaReport.class);
	
	public LimitesGerencia[] findByRangeDate(int day1, int month1, int year1, 
												int day2, int month2, int year2, long codVendedor, long codUc,
												long mayorMenor, double montoLimite) throws RemoteException{
		Calendar cal = new GregorianCalendar();
		
		cal.set(Calendar.YEAR, year1);
		cal.set(Calendar.MONTH, month1-1);
		cal.set(Calendar.DATE, day1);
		
		Date startDate = cal.getTime();
		cal.set(year2,month2-1,day2,23,59,59);
		Date endDate = cal.getTime();
		
		return findByDateRange(startDate,endDate, whereClause(codVendedor,codUc, mayorMenor, montoLimite));
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
	
	private String whereClause(long codVendedor, long codUc, long mayorMenor, double montoLimite){
			
		String st = "";
		
		if(codVendedor!=0 && codUc!=0 && mayorMenor!= 0 && montoLimite!= 0){
			st="and codVendedor = "+codVendedor+" and codUnidad = "+codUc+" and monto >= '"+montoLimite+"'";
		}
		else if(codVendedor!=0 && codUc!=0 && mayorMenor!= 0 && montoLimite == 0){
			st="and codVendedor = "+codVendedor+" and codUnidad = "+codUc+" and monto >= '"+montoLimite+"'";
		}
		else if(codVendedor!=0 && codUc!=0 && mayorMenor== 0 && montoLimite!= 0){
			st="and codVendedor = "+codVendedor+" and codUnidad = "+codUc+" and monto < '"+montoLimite+"'";
		}
		else if(codVendedor!=0 && codUc!=0 && mayorMenor== 0 && montoLimite== 0){
			st="and codVendedor = "+codVendedor+" and codUnidad = "+codUc+" and monto < '"+montoLimite+"'";
		}
		//------------------------------------------------------------------------------------
		else if(codVendedor!=0 && codUc==0 && mayorMenor!= 0 && montoLimite!= 0){
			st="and codVendedor = "+codVendedor+" and monto >= '"+montoLimite+"'";
		}
		else if(codVendedor!=0 && codUc==0 && mayorMenor!= 0 && montoLimite == 0){
			st="and codVendedor = "+codVendedor+" and monto >= '"+montoLimite+"'";
		}
		else if(codVendedor!=0 && codUc==0 && mayorMenor== 0 && montoLimite!= 0){
			st="and codVendedor = "+codVendedor+" and monto < '"+montoLimite+"'";
		}
		else if(codVendedor!=0 && codUc==0 && mayorMenor== 0 && montoLimite== 0){
			st="and codVendedor = "+codVendedor+" and monto < '"+montoLimite+"'";			
			
		}
		//------------------------------------------------------------------------------------
		else if(codVendedor==0 && codUc!=0 && mayorMenor!= 0 && montoLimite!= 0){
			st="and codUnidad = "+codUc+" and monto >= '"+montoLimite+"'";
		}
		else if(codVendedor==0 && codUc!=0 && mayorMenor!= 0 && montoLimite == 0){
			st="and codUnidad = "+codUc+" and monto >= '"+montoLimite+"'";
		}
		else if(codVendedor==0 && codUc!=0 && mayorMenor== 0 && montoLimite!= 0){
			st="and codUnidad = "+codUc+" and monto < '"+montoLimite+"'";
		}
		else if(codVendedor==0 && codUc!=0 && mayorMenor== 0 && montoLimite== 0){
			st="and codUnidad = "+codUc+" and monto < '"+montoLimite+"'";
		}
		//------------------------------------------------------------------------------------
		else if(codVendedor==0 && codUc==0 && mayorMenor!= 0 && montoLimite!= 0){
			st=" and monto >= '"+montoLimite+"'";
		}
		else if(codVendedor==0 && codUc==0 && mayorMenor!= 0 && montoLimite == 0){
			st=" and monto >= '"+montoLimite+"'";
		}
		else if(codVendedor==0 && codUc==0 && mayorMenor== 0 && montoLimite!= 0){
			st=" and monto < '"+montoLimite+"'";
		}	
		
		return st;
		
	}
	
	private LimitesGerencia[] findByDateRange(Date startDate, Date endDate, String where){
		Session session = HibernateUtil.abrirSession();
		
		List list = session.createSQLQuery
				(
				"select nombre_fantasia, nombreEvento, numeroPresupuesto, lugarEvento, monto, vendedor, fechaInicialEvento, fechaFinalEvento, "+
				"cobrado, facturado, confirmado, os, of, adelanto, adelantado, cancelado, actualizado, nuevo, rechazado, oc "+
				"from vw_rpt_limites_gerencia " +
				//"where fechaInicialEvento >= ? and fechaInicialEvento <= ? " +where+
				"where ((fechaInicialEvento >= ? and fechaFinalEvento <= ?) "+
				"or (fechaInicialEvento <= ? and fechaFinalEvento > ?) "+
				"or(fechaInicialEvento< ? and fechaFinalEvento>= ?)) " +where+
				" order by fechaInicialEvento"
				)
				.addScalar("nombre_fantasia",Hibernate.STRING)//0
				.addScalar("nombreEvento",Hibernate.STRING)//1
				.addScalar("numeroPresupuesto",Hibernate.LONG)//2
				.addScalar("lugarEvento",Hibernate.STRING)//3
				.addScalar("monto",Hibernate.DOUBLE)//4
				.addScalar("vendedor", Hibernate.STRING)//5
				.addScalar("fechaInicialEvento", Hibernate.DATE)//6
				.addScalar("fechaFinalEvento", Hibernate.DATE)//7
				.addScalar("cobrado", Hibernate.INTEGER)//8
				.addScalar("facturado", Hibernate.INTEGER)//9
				.addScalar("confirmado", Hibernate.INTEGER)//10
				.addScalar("os", Hibernate.INTEGER)//11
				.addScalar("of", Hibernate.INTEGER)//12
				.addScalar("adelanto", Hibernate.INTEGER)//13
				.addScalar("adelantado", Hibernate.INTEGER)//14
				.addScalar("cancelado", Hibernate.INTEGER)//15
				.addScalar("actualizado", Hibernate.INTEGER)//16
				.addScalar("nuevo", Hibernate.INTEGER)//17
				.addScalar("rechazado", Hibernate.INTEGER)//18
				.addScalar("oc", Hibernate.INTEGER)//19	
				.setDate(0,startDate)
				.setDate(1,endDate)
				.setDate(2,endDate)
				.setDate(3,endDate)
				.setDate(4,startDate)
				.setDate(5,startDate)

				.list();
		
		LimitesGerencia[] results = new LimitesGerencia[list.size()];
		for (int i=0; i< results.length;i++){ 
			results[i] = new LimitesGerencia();
			Object[] row = (Object[]) list.get(i);
			
			results[i].setCliente((String)row[0]);
			results[i].setEvento((String)row[1]);
			results[i].setNroPpto((Long)row[2]);
			results[i].setLugar((String)row[3]);
			results[i].setTotal((Double)row[4]);
			results[i].setVendedor((String)row[5]);
			results[i].setFechaInicio((Date)row[6]);
			results[i].setFechaFin((Date)row[7]);
			
			if((Integer)row[8] == 1)
				results[i].setEstado("Cobrado");
			else if((Integer)row[9] == 1 && (Integer)row[8] == 0 && (Integer)row[15] == 0)
				results[i].setEstado("Facturado");
			else if((Integer)row[15] == 1)
				results[i].setEstado("Cancelado");
			else if((Integer)row[12] == 1 && (Integer)row[9] == 0 && (Integer)row[15] == 0)
				results[i].setEstado("Orden de Facturacion");
			else if((Integer)row[14] == 1 && (Integer)row[12] == 0 && (Integer)row[15] == 0)
				results[i].setEstado("Adelanto facturado");
			else if((Integer)row[13] == 1 && (Integer)row[14] == 0 && (Integer)row[15] == 0)
				results[i].setEstado("Con adelanto");
			else if((Integer)row[11] == 1 && (Integer)row[12] == 0 && (Integer)row[13] == 0 && (Integer)row[15] == 0)
				results[i].setEstado("Orden de Servicio");			
			else if((Integer)row[10] == 1 && (Integer)row[8] == 0 && (Integer)row[9] == 0 && (Integer)row[15] == 0)
				results[i].setEstado("Confirmado");
			else if((Integer)row[18] == 1)
				results[i].setEstado("Rechazado");
			else if(((Integer)row[16] == 1 || (Integer)row[17] == 1) && (Integer)row[10] == 0 && (Integer)row[18] == 0)
				results[i].setEstado("Pendiente");			
			else results[i].setEstado("Sin estado");
		}
		
		HibernateUtil.cerrarSession(session);
		
		return results;
	}

}
