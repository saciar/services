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

import crm.libraries.report.ReferenciaGerencia;
import crm.services.report.sei.ReferenciaGerenciaReportSEI;
import crm.services.transaction.PresupuestosManager;
import crm.services.transaction.TipoContactoManager;
import crm.services.util.HibernateUtil;

public class ReferenciaGerenciaReport implements ReferenciaGerenciaReportSEI,ReportService{
private static final Log log = LogFactory.getLog(PorcentajeGerenciaReport.class);
	
	public ReferenciaGerencia[] findByRangeDate(int day1, int month1, int year1, 
												int day2, int month2, int year2, long codVendedor, long codUc,
												long codReferencia) throws RemoteException{
		Calendar cal = new GregorianCalendar();
		
		cal.set(Calendar.YEAR, year1);
		cal.set(Calendar.MONTH, month1-1);
		cal.set(Calendar.DATE, day1);
		
		Date startDate = cal.getTime();
		cal.set(year2,month2-1,day2,23,59,59);
		Date endDate = cal.getTime();
		
		return findByDateRange(startDate,endDate,codReferencia, whereClause(codVendedor,codUc));
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
	
	private String whereClause(long codVendedor, long codUc){
			
		String st = "";
		
		if(codVendedor!=0 && codUc!=0){
			st="and codVendedor = "+codVendedor+" and codUnidad = "+codUc;
		}
		//------------------------------------------------------------------------------------
		else if(codVendedor!=0 && codUc==0){
			st="and codVendedor = "+codVendedor;
		}
		//------------------------------------------------------------------------------------
		else if(codVendedor==0 && codUc!=0){
			st="and codUnidad = "+codUc;
		}
		//------------------------------------------------------------------------------------
		
		return st;
		
	}
	
	private int getTotalPpto(Date startDate, Date endDate){
		Session session = HibernateUtil.abrirSession();
		
		Integer result = (Integer)session.createSQLQuery
				(
				"select count(*) as cantidad "+
				"from VW_REFERENCIA_GERENCIA " +
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
	
	
	//private PorcentajeGerencia[] findByDateRange(Date startDate, Date endDate, String where){
		private ReferenciaGerencia[] findByDateRange(Date startDate, Date endDate, long codReferencia, String where){
			Session session = HibernateUtil.abrirSession();
			
			List list = session.createSQLQuery
			(
					"select nombre_fantasia, nombreEvento, numeroPresupuesto, clienteFac, vendedor, fechaInicialEvento, fechaFinalEvento, "+
					"cobrado, facturado, confirmado, os, of, adelanto, adelantado, cancelado, actualizado, nuevo, rechazado, oc, referencia, tc_descripcion "+
					"from VW_REFERENCIA_GERENCIA " +
					"where ((fechaInicialEvento >= ? and fechaFinalEvento <= ?) "+
					"or (fechaInicialEvento <= ? and fechaFinalEvento > ?) "+
					"or(fechaInicialEvento< ? and fechaFinalEvento>= ?)) " +where+ //" and referencia = ? "+
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
			.addScalar("referencia", Hibernate.LONG)//19
			.addScalar("tc_descripcion", Hibernate.STRING)//20
			.setDate(0,startDate)
			.setDate(1,endDate)
			.setDate(2,endDate)
			.setDate(3,endDate)
			.setDate(4,startDate)
			.setDate(5,startDate)
			
			.list();		
			
			ReferenciaGerencia[] results = new ReferenciaGerencia[list.size()];
			try{
				for (int i=0; i< results.length;i++){			
					results[i] = new ReferenciaGerencia();
					Object[] row = (Object[]) list.get(i);					
					
					//if(((Long)row[19]).longValue() == codReferencia){
						results[i].setCliente((String)row[0]);
						results[i].setEvento((String)row[1]);
						results[i].setNroPpto((Long)row[2]);
						results[i].setLugar((String)row[3]);
						double totalEvento = PresupuestosManager.instance().getFacturacionByPPto((Long)row[2]);
						results[i].setTotal(totalEvento);
						results[i].setVendedor((String)row[4]);
						results[i].setFechaInicio((Date)row[5]);
						results[i].setFechaFin((Date)row[6]);
						results[i].setCodReferencia((Long)row[19]);
						results[i].setReferencia((String)row[20]);
						
						if((Integer)row[7] == 1){
							results[i].setEstado("Cobrado");
						}
						else if((Integer)row[8] == 1 && (Integer)row[7] == 0 && (Integer)row[14] == 0){
							results[i].setEstado("Facturado");
						}
						else if((Integer)row[14] == 1){
							results[i].setEstado("Cancelado");
						}
						else if((Integer)row[11] == 1 && (Integer)row[8] == 0 && (Integer)row[14] == 0){
							results[i].setEstado("Orden de Facturacion");
						}
						else if((Integer)row[13] == 1 && (Integer)row[11] == 0 && (Integer)row[14] == 0){
							results[i].setEstado("Adelanto facturado");
						}
						else if((Integer)row[12] == 1 && (Integer)row[13] == 0 && (Integer)row[14] == 0){
							results[i].setEstado("Con adelanto");
						}
						else if((Integer)row[10] == 1 && (Integer)row[11] == 0 && (Integer)row[12] == 0 && (Integer)row[14] == 0){
							results[i].setEstado("Orden de Servicio");			
						}
						else if((Integer)row[9] == 1 && (Integer)row[7] == 0 && (Integer)row[8] == 0 && (Integer)row[14] == 0){
							results[i].setEstado("Confirmado");
						}
						else if((Integer)row[17] == 1){
							results[i].setEstado("Rechazado");
						}
						else if(((Integer)row[15] == 1 || (Integer)row[16] == 1) && (Integer)row[9] == 0 && (Integer)row[17] == 0){
							results[i].setEstado("Pendiente");
						}
						else {
							results[i].setEstado("Sin estado");
						}
					}
				//}
			}
			catch (Exception e) {
				
			}
		
		HibernateUtil.cerrarSession(session);
		
		return results;
	}
}
