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

import crm.libraries.report.LimitesYEstadoGerencia;
import crm.services.report.sei.LimitesYEstadosGerenciaReportSEI;
import crm.services.sei.EstadoEventoManagerSEI;
import crm.services.transaction.PresupuestosManager;
import crm.services.util.DateConverter;
import crm.services.util.HibernateUtil;

public class LimitesYEstadosGerenciaReport implements LimitesYEstadosGerenciaReportSEI,ReportService {
	private static final Log log = LogFactory.getLog(LimitesGerenciaReport.class);
	
	public LimitesYEstadoGerencia[] findByRangeDate(int day1, int month1, int year1, 
												int day2, int month2, int year2, long codVendedor, long codUc,
												long mayorMenor, double montoLimite, long fechaMayorMenor, long codEstado) throws RemoteException{
		Calendar cal = new GregorianCalendar();
		
		cal.set(Calendar.YEAR, year1);
		cal.set(Calendar.MONTH, month1-1);
		cal.set(Calendar.DATE, day1);
		
		Date startDate = cal.getTime();
		cal.set(year2,month2-1,day2,23,59,59);
		Date endDate = cal.getTime();
		
		return findByDateRange(startDate,endDate, whereClause(codVendedor,codUc, codEstado), mayorMenor, montoLimite,fechaMayorMenor, ((Long)codEstado).intValue());
	}
	
	private String getEstadoActual(long codEstado){
		String st = "";
		if(codEstado == Long.parseLong(EstadoEventoManagerSEI.CODIGO_ESTADO_COBRADO))
			st=" and cobrado = 1 and cancelado = 0";
		else if(codEstado == Long.parseLong(EstadoEventoManagerSEI.CODIGO_ESTADO_FACTURADO))
			st=" and facturado = 1 and cancelado = 0";
		else if(codEstado == Long.parseLong(EstadoEventoManagerSEI.CODIGO_ESTADO_ORDEN_FACTURACION))
			st=" and of = 1 and cancelado = 0";
		else if(codEstado == Long.parseLong(EstadoEventoManagerSEI.CODIGO_ESTADO_ORDEN_SERVICIO))
			st=" and os = 1 and cancelado = 0";
		else if(codEstado == Long.parseLong(EstadoEventoManagerSEI.CODIGO_ESTADO_CANCELADO))
			st=" and cancelado = 1";
		else if(codEstado == Long.parseLong(EstadoEventoManagerSEI.CODIGO_ESTADO_CONFIRMADO))
			st=" and confirmado = 1 and cancelado = 0";
		else if(codEstado == Long.parseLong(EstadoEventoManagerSEI.CODIGO_ESTADO_ACTUALIZADO))
			st=" and actualizado = 1 and rechazado = 0";
		else if(codEstado == Long.parseLong(EstadoEventoManagerSEI.CODIGO_ESTADO_NUEVO))
			st=" and (nuevo = 1 or actualizado = 1) and confirmado = 0 and rechazado = 0";
		else if(codEstado == Long.parseLong(EstadoEventoManagerSEI.CODIGO_ESTADO_RECHAZADO))
			st=" and rechazado = 1";
		else if(codEstado == Long.parseLong(EstadoEventoManagerSEI.CODIGO_ESTADO_ORDEN_COMPRA))
			st=" and oc = 1";
		else if(codEstado == Long.parseLong(EstadoEventoManagerSEI.CODIGO_ESTADO_ADELANTO))
			st=" and adelanto = 1 and cancelado = 0";
		else if(codEstado == Long.parseLong(EstadoEventoManagerSEI.CODIGO_ESTADO_ADELANTADO))
			st=" and adelantado = 1 and cancelado = 0";
		return st;
	}
	
	private String whereClause(long codVendedor, long codUc, long codEstado){
		String st = "";
		
		if(codVendedor!=0 && codUc!=0 && codEstado!= 0){
			st="and codVendedor = "+codVendedor+" and codUnidad = "+codUc + getEstadoActual(codEstado);
		}
		else if(codVendedor!=0 && codUc!=0 && codEstado== 0){
			st="and codVendedor = "+codVendedor+" and codUnidad = "+codUc;
		}
		//------------------------------------------------------------------------------------
		else if(codVendedor!=0 && codUc==0 && codEstado!= 0){
			st="and codVendedor = "+codVendedor + getEstadoActual(codEstado);
		}
		else if(codVendedor!=0 && codUc==0 && codEstado== 0){
			st="and codVendedor = "+codVendedor;
		}
		//------------------------------------------------------------------------------------
		else if(codVendedor==0 && codUc!=0 && codEstado!= 0){
			st="and codUnidad = "+codUc + getEstadoActual(codEstado);
		}
		else if(codVendedor==0 && codUc!=0 && codEstado== 0){
			st="and codUnidad = "+codUc;
		}
		//------------------------------------------------------------------------------------
		else if(codVendedor==0 && codUc==0 && codEstado!= 0){
			st=getEstadoActual(codEstado);
		}
		
		return st;
	}
	
	/*private String whereClause(long codVendedor, long codUc, long mayorMenor, double montoLimite, long codEstado){
			
		String st = "";
		
		if(codVendedor!=0 && codUc!=0 && mayorMenor!= 0 && codEstado!= 0){
			st="and codVendedor = "+codVendedor+" and codUnidad = "+codUc+" and monto >= '"+montoLimite + "'" + getEstadoActual(codEstado);
		}
		else if(codVendedor!=0 && codUc!=0 && mayorMenor!= 0 && codEstado == 0){
			st="and codVendedor = "+codVendedor+" and codUnidad = "+codUc+" and monto >= '"+montoLimite;
		}
		else if(codVendedor!=0 && codUc!=0 && mayorMenor== 0 && codEstado!= 0){
			st="and codVendedor = "+codVendedor+" and codUnidad = "+codUc+" and monto < '"+montoLimite + "'" + getEstadoActual(codEstado);
		}
		else if(codVendedor!=0 && codUc!=0 && mayorMenor== 0 && codEstado== 0){
			st="and codVendedor = "+codVendedor+" and codUnidad = "+codUc+" and monto < '"+montoLimite;
		}
		//------------------------------------------------------------------------------------
		else if(codVendedor!=0 && codUc==0 && mayorMenor!= 0 && montoLimite!= 0){
			st="and codVendedor = "+codVendedor+" and monto >= '"+montoLimite + "'" + getEstadoActual(codEstado);
		}
		else if(codVendedor!=0 && codUc==0 && mayorMenor!= 0 && montoLimite == 0){
			st="and codVendedor = "+codVendedor+" and monto >= '"+montoLimite;
		}
		else if(codVendedor!=0 && codUc==0 && mayorMenor== 0 && montoLimite!= 0){
			st="and codVendedor = "+codVendedor+" and monto < '"+montoLimite + "'" + getEstadoActual(codEstado);
		}
		else if(codVendedor!=0 && codUc==0 && mayorMenor== 0 && montoLimite== 0){
			st="and codVendedor = "+codVendedor+" and monto < '"+montoLimite;			
			if (log.isDebugEnabled()){
				log.info("codigo de Vendedor =============================== "+codVendedor+"string"+st);
			}
		}
		//------------------------------------------------------------------------------------
		else if(codVendedor==0 && codUc!=0 && mayorMenor!= 0 && montoLimite!= 0){
			st="and codUnidad = "+codUc+" and monto >= '"+montoLimite + "'" + getEstadoActual(codEstado);
		}
		else if(codVendedor==0 && codUc!=0 && mayorMenor!= 0 && montoLimite == 0){
			st="and codUnidad = "+codUc+" and monto >= '"+montoLimite;
		}
		else if(codVendedor==0 && codUc!=0 && mayorMenor== 0 && montoLimite!= 0){
			st="and codUnidad = "+codUc+" and monto < '"+montoLimite + "'" + getEstadoActual(codEstado);
		}
		else if(codVendedor==0 && codUc!=0 && mayorMenor== 0 && montoLimite== 0){
			st="and codUnidad = "+codUc+" and monto < '"+montoLimite;
		}
		//------------------------------------------------------------------------------------
		else if(codVendedor==0 && codUc==0 && mayorMenor!= 0 && montoLimite!= 0){
			st=" and monto >= '"+montoLimite + "'" + getEstadoActual(codEstado);
		}
		else if(codVendedor==0 && codUc==0 && mayorMenor!= 0 && montoLimite == 0){
			st=" and monto >= '"+montoLimite;
		}
		else if(codVendedor==0 && codUc==0 && mayorMenor== 0 && montoLimite!= 0){
			st=" and monto < '"+montoLimite + "'" + getEstadoActual(codEstado);
		}
		else if(codVendedor==0 && codUc==0 && mayorMenor== 0 && montoLimite== 0){
			st=" and monto < '"+montoLimite;
		}		
		
		return st;
		
	}*/
	
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
	
	private int getTotalPpto(Date startDate, Date endDate){
		Session session = HibernateUtil.abrirSession();
		
		Integer result = (Integer)session.createSQLQuery
				(
				"select count(*) as cantidad "+
				"from vw_rpt_limites_estado_gerencia " +
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
	
	private LimitesYEstadoGerencia[] findByDateRange(Date startDate, Date endDate, String where, long mayorMenor, double montoLimite, long fechaMayorMenor, int codEstado){
		Session session = HibernateUtil.abrirSession();
		
		List list = session.createSQLQuery
				(
				"select nombre_fantasia, nombreEvento, numeroPresupuesto, clienteFac, vendedor, fechaInicialEvento, fechaFinalEvento, "+
				"cobrado, facturado, confirmado, os, of, adelanto, adelantado, cancelado, actualizado, nuevo, rechazado, oc "+
				"from vw_rpt_limites_estado_gerencia " +
				"where ((fechaInicialEvento >= ? and fechaFinalEvento <= ?) "+
				"or (fechaInicialEvento <= ? and fechaFinalEvento > ?) "+
				"or(fechaInicialEvento< ? and fechaFinalEvento>= ?)) " +where+
				" order by fechaInicialEvento"
				)
				.addScalar("nombre_fantasia",Hibernate.STRING)//0
				.addScalar("nombreEvento",Hibernate.STRING)//1
				.addScalar("numeroPresupuesto",Hibernate.LONG)//2
				.addScalar("clienteFac",Hibernate.STRING)//3
				.addScalar("vendedor", Hibernate.STRING)//4
				.addScalar("fechaInicialEvento", Hibernate.DATE)//5
				.addScalar("fechaFinalEvento", Hibernate.DATE)//6
				.addScalar("cobrado", Hibernate.INTEGER)//7
				.addScalar("facturado", Hibernate.INTEGER)//8
				.addScalar("confirmado", Hibernate.INTEGER)//9
				.addScalar("os", Hibernate.INTEGER)//10
				.addScalar("of", Hibernate.INTEGER)//11
				.addScalar("adelanto", Hibernate.INTEGER)//12
				.addScalar("adelantado", Hibernate.INTEGER)//13
				.addScalar("cancelado", Hibernate.INTEGER)//14
				.addScalar("actualizado", Hibernate.INTEGER)//15
				.addScalar("nuevo", Hibernate.INTEGER)//16
				.addScalar("rechazado", Hibernate.INTEGER)//17
				.addScalar("oc", Hibernate.INTEGER)//18
				.setDate(0,startDate)
				.setDate(1,endDate)
				.setDate(2,endDate)
				.setDate(3,endDate)
				.setDate(4,startDate)
				.setDate(5,startDate)

				.list();		
		
		LimitesYEstadoGerencia[] results = new LimitesYEstadoGerencia[list.size()];
		try{
		for (int i=0; i< results.length;i++){			
			results[i] = new LimitesYEstadoGerencia();
			Object[] row = (Object[]) list.get(i);
			
			double totalEvento = PresupuestosManager.instance().getFacturacionByPPto((Long)row[2]);
			Date fechaCambioEstado = DateConverter.convertStringToDate(PresupuestosManager.instance().getMaxFechaByNroPptoAndState((Long)row[2], codEstado), "yyyy-MM-dd HH:mm:ss");
			
			if(((mayorMenor == 0 && totalEvento < montoLimite) || (mayorMenor > 0 && totalEvento >= montoLimite)) &&
					((fechaMayorMenor == 0 && ((Date)row[5]).after(fechaCambioEstado)) ||(fechaMayorMenor > 0 && ((Date)row[5]).before(fechaCambioEstado)))){
				results[i].setCliente((String)row[0]);
				results[i].setEvento((String)row[1]);
				results[i].setNroPpto((Long)row[2]);
				results[i].setLugar((String)row[3]);
				results[i].setTotal(totalEvento);
				//results[i].setTotal((Double)row[4]);
				results[i].setVendedor((String)row[4]);
				results[i].setFechaInicio((Date)row[5]);
				results[i].setFechaFin((Date)row[6]);
				
				if((Integer)row[7] == 1){
					results[i].setEstado("Cobrado");
					results[i].setConfirmado("Confirmado");
				}
				else if((Integer)row[8] == 1 && (Integer)row[7] == 0 && (Integer)row[14] == 0){
					results[i].setEstado("Facturado");
					results[i].setConfirmado("Confirmado");
				}
				else if((Integer)row[14] == 1){
					results[i].setEstado("Cancelado");
					results[i].setConfirmado("Confirmado");
				}
				else if((Integer)row[11] == 1 && (Integer)row[8] == 0 && (Integer)row[14] == 0){
					results[i].setEstado("Orden de Facturacion");
					results[i].setConfirmado("Confirmado");
				}
				else if((Integer)row[13] == 1 && (Integer)row[11] == 0 && (Integer)row[14] == 0){
					results[i].setEstado("Adelanto facturado");
					results[i].setConfirmado("Confirmado");
				}
				else if((Integer)row[12] == 1 && (Integer)row[13] == 0 && (Integer)row[14] == 0){
					results[i].setEstado("Con adelanto");
					results[i].setConfirmado("Confirmado");
				}
				else if((Integer)row[10] == 1 && (Integer)row[11] == 0 && (Integer)row[12] == 0 && (Integer)row[14] == 0){
					results[i].setEstado("Orden de Servicio");			
					results[i].setConfirmado("Confirmado");
				}
				else if((Integer)row[9] == 1 && (Integer)row[7] == 0 && (Integer)row[8] == 0 && (Integer)row[14] == 0){
					results[i].setEstado("Confirmado");
					results[i].setConfirmado("Confirmado");
				}
				else if((Integer)row[17] == 1){
					results[i].setEstado("Rechazado");
					results[i].setConfirmado("No confirmado");
				}
				else if(((Integer)row[15] == 1 || (Integer)row[16] == 1) && (Integer)row[9] == 0 && (Integer)row[17] == 0){
					results[i].setEstado("Pendiente");
					results[i].setConfirmado("No confirmado");
				}
				else {
					results[i].setEstado("Sin estado");
					results[i].setConfirmado("No confirmado");
				}
			}
		}
		}
		catch (Exception e) {
			
		}
		HibernateUtil.cerrarSession(session);
		
		return results;
	}

}
