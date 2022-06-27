package crm.services.report.manager;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.hibernate.Session;

import crm.libraries.report.DiaryPlannerOperador;
import crm.libraries.report.DiaryPlannerSala;
import crm.libraries.report.DiaryPlannerServicio;
import crm.libraries.report.Evento;
import crm.libraries.report.EventoOperador;
import crm.libraries.report.EventoSala;
import crm.libraries.report.EventoServicio;
import crm.services.report.sei.DiaryPlannerReportSEI;
import crm.services.sei.ModalidadContratManagerSEI;
import crm.services.util.HibernateUtil;

public class DiaryPlannerReport implements DiaryPlannerReportSEI, ReportService{
	
	private static final Log log = LogFactory.getLog(DiaryPlannerReport.class);
	
	public DiaryPlannerSala[] findByDay(int day,int month, int year, String tipo) throws RemoteException{
		Calendar cal = new GregorianCalendar();
		
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month-1);
		cal.set(Calendar.DATE, day);
		
		Date startDate = cal.getTime();

		cal.set(Calendar.DATE, day+1);
		
		Date endDate = cal.getTime();		
		
		return findByDate(startDate,endDate, tipo);
	}
	
	public DiaryPlannerSala[] findByDateRange(int day1,int month1, int year1, int day2,int month2, int year2) throws RemoteException{
		Calendar cal = new GregorianCalendar();
		
		cal.set(Calendar.YEAR, year1);
		cal.set(Calendar.MONTH, month1-1);
		cal.set(Calendar.DATE, day1);
		
		Date startDate = cal.getTime();
		
		cal.set(Calendar.YEAR, year2);
		cal.set(Calendar.MONTH, month2-1);
		cal.set(Calendar.DATE, day2);
		
		Date endDate = cal.getTime();		
		
		return findByDateRange(startDate,endDate);
	}
	
	private DiaryPlannerSala[] findByDate(Date startDate, Date endDate, String tipo) {
		Session session = HibernateUtil.abrirSession();
		String condicion = " ";
                if(tipo.equals("Rural"))
                    condicion=" v.lugarEvento like '%rural%' and ";
                else if(tipo.equals("MDQ"))
                    condicion=" v.lugarEvento like '%mar del plata%' and ";
                else if(tipo.equals("Baires"))
                	condicion = " v.lugarEvento not like '%mar del plata%' and v.lugarEvento not like '%rural%' and";
		List list = session.createSQLQuery
				(
				"select v.codigoSala, v.cliente, v.nombreEvento, v.numeroPresupuesto, v.lugarEvento, v.fechaInicialEvento, " +
				"v.fechaInstalacion, v.vendedor, v.fechaFinalEvento, v.nombreSala, v.fechaInicialSala, v.fechaFinalaSala " +
				"from VW_RPT_DIARYPLANNER v " +
				"where"+condicion+"(v.fechaInicialSala < ? and v.fechaFinalaSala >= ? "+
				"or v.fechaInstalacion >= ? and v.fechaInstalacion < ?) "+
				"order by v.lugarEvento" 
				)
				.addScalar("codigoSala", Hibernate.LONG)
				.addScalar("cliente",Hibernate.STRING)
				.addScalar("nombreEvento",Hibernate.STRING)
				.addScalar("numeroPresupuesto",Hibernate.LONG)
				.addScalar("lugarEvento",Hibernate.STRING)
				.addScalar("fechaInicialEvento",Hibernate.TIMESTAMP)
				.addScalar("fechaInstalacion",Hibernate.TIMESTAMP)
				.addScalar("vendedor",Hibernate.STRING)
				.addScalar("fechaFinalEvento", Hibernate.TIMESTAMP)
				.addScalar("nombreSala", Hibernate.STRING)
				.addScalar("fechaInicialSala",Hibernate.TIMESTAMP)
				.addScalar("fechaFinalaSala",Hibernate.TIMESTAMP)
				.setDate(0,endDate)
				.setDate(1,startDate)
				.setDate(2,startDate)
				.setDate(3,endDate)
				.list();
		
		DiaryPlannerSala[] results = new DiaryPlannerSala[list.size()];
		for (int i=0; i< results.length;i++){ 
			results[i] = new DiaryPlannerSala();
			Object[] row = (Object[]) list.get(i);		
			
			results[i].setNombreFantasia((String)row[1]);
			results[i].setNombreEvento((String)row[2]);
			results[i].setNumeroPresupuesto((Long)row[3]);
			results[i].setLugarEvento((String)row[4]);       
	        results[i].setFechaInicialEvento(((Timestamp)row[5]).toString());
			results[i].setFechaInstalacion(((Timestamp)row[6]).toString());
			results[i].setVendedor((String)row[7]);
			results[i].setFechaFinalEvento(((Timestamp)row[8]).toString());
			results[i].setNombreSala((String)row[9]);
			results[i].setFechaInicialSala(((Timestamp)row[10]).toString());
			results[i].setFechaFinalSala(((Timestamp)row[11]).toString());
			results[i].setEstado("OS");
			
			results[i].setServicios(findServiciosBySala(session,(Long)row[0]));
			
			//results[i].setOperadores(findOperadoresbyPresupuesto(session,results[i].getNumeroPresupuesto()));
		}
		
		HibernateUtil.cerrarSession(session);
		
		return results;
	}
	
	private DiaryPlannerSala[] findByDateRange(Date startDate, Date endDate) {	
		Session session = HibernateUtil.abrirSession();
		
		List list = session.createSQLQuery
				(
				"select v.codigoSala, v.cliente, v.nombreEvento, v.numeroPresupuesto, v.lugarEvento, v.fechaInicialEvento, " +
				"v.fechaInstalacion, v.vendedor, v.fechaFinalEvento, v.nombreSala, v.fechaInicialSala, v.fechaFinalaSala, v.estado " +
				"from VW_RPT_DIARYPLANNER_COMPLETO v " +
				"where v.fechaInicialSala < ? and v.fechaFinalaSala >= ? "+
				"or v.fechaInstalacion >= ? and v.fechaInstalacion < ? "+
				"order by v.lugarEvento" 
				)
				.addScalar("codigoSala", Hibernate.LONG)
				.addScalar("cliente",Hibernate.STRING)
				.addScalar("nombreEvento",Hibernate.STRING)
				.addScalar("numeroPresupuesto",Hibernate.LONG)
				.addScalar("lugarEvento",Hibernate.STRING)
				.addScalar("fechaInicialEvento",Hibernate.TIMESTAMP)
				.addScalar("fechaInstalacion",Hibernate.TIMESTAMP)
				.addScalar("vendedor",Hibernate.STRING)
				.addScalar("fechaFinalEvento", Hibernate.TIMESTAMP)
				.addScalar("nombreSala", Hibernate.STRING)
				.addScalar("fechaInicialSala",Hibernate.TIMESTAMP)
				.addScalar("fechaFinalaSala",Hibernate.TIMESTAMP)
				.addScalar("estado", Hibernate.STRING)
				.setDate(0,endDate)
				.setDate(1,startDate)
				.setDate(2,startDate)
				.setDate(3,endDate)
				.list();
		
		DiaryPlannerSala[] results = new DiaryPlannerSala[list.size()];
		for (int i=0; i< results.length;i++){ 
			results[i] = new DiaryPlannerSala();
			Object[] row = (Object[]) list.get(i);		
			
			results[i].setNombreFantasia((String)row[1]);
			results[i].setNombreEvento((String)row[2]);
			results[i].setNumeroPresupuesto((Long)row[3]);
			results[i].setLugarEvento((String)row[4]);       
	        results[i].setFechaInicialEvento(((Timestamp)row[5]).toString());
			results[i].setFechaInstalacion(((Timestamp)row[6]).toString());
			results[i].setVendedor((String)row[7]);
			results[i].setFechaFinalEvento(((Timestamp)row[8]).toString());
			results[i].setNombreSala((String)row[9]);
			results[i].setFechaInicialSala(((Timestamp)row[10]).toString());
			results[i].setFechaFinalSala(((Timestamp)row[11]).toString());
			results[i].setEstado((String)row[12]);
			
			results[i].setServicios(findServiciosBySala(session,(Long)row[0]));
			
			//results[i].setOperadores(findOperadoresbyPresupuesto(session,results[i].getNumeroPresupuesto()));
		}
		
		HibernateUtil.cerrarSession(session);
		
		return results;
	}
	
	private DiaryPlannerServicio[] findServiciosBySala(Session session,long num_sala) {
		List list = session.createSQLQuery
			(
			"select ss.ppto_ss_id as id,ss.ppto_ss_cantidad  as cantidad, servi.si_descripcion_abreviada as servicio, "+
			"ss.ppto_ss_dias as dias, ss.ppto_ss_detalle as detalle "+
			"from TX_PPTO_SALAS_SERVICIOS ss "+
			"inner join MST_SERVICIOS serv on ss.ppto_ss_servicio = serv.se_codservicio "+
			"inner join MST_SERVICIOS_IDIOMA servi on serv.se_codservicio = servi.si_codservicio "+
			"where ss.ppto_ss_pls = ? and (ss.ppto_ss_modalidad = ? or ss.ppto_ss_modalidad = ?) " +
			"ORDER BY ss.ppto_ss_orden"
			)
			.addScalar("id",Hibernate.LONG)
			.addScalar("cantidad",Hibernate.LONG)
			.addScalar("servicio",Hibernate.STRING)
			.addScalar("dias",Hibernate.INTEGER)
			.addScalar("detalle", Hibernate.STRING)
			.setLong(0,new Long(num_sala))
			.setInteger(1,Integer.parseInt(ModalidadContratManagerSEI.MODALIDAD_INTERNO))
			.setInteger(2,Integer.parseInt(ModalidadContratManagerSEI.MODALIDAD_EXTERNO))
			.list();

		DiaryPlannerServicio[] results = new DiaryPlannerServicio[list.size()];
		for (int i=0; i< results.length;i++){ 
			results[i] = new DiaryPlannerServicio();
			Object[] row = (Object[]) list.get(i);
		
			results[i].setServicioId(((Long)row[0]).longValue());
			results[i].setCantidad(((Long)row[1]).longValue());
			
			/*if((Long)row[0]==1)
				results[i].setServicio((String)row[4]);
			else results[i].setServicio((String)row[2]);*/
			
			if(((String)row[2]).equals("Subcontratado"))
				results[i].setServicio((String)row[4]);
			else results[i].setServicio((String)row[2]);
			
			
			results[i].setDias(((Integer)row[3]).intValue());
		
		}
	
		return results;
	}
	
	private DiaryPlannerOperador[] findOperadoresbyPresupuesto(Session session,long num_presupuesto){
		
		List list = session.createSQLQuery
				(
				/*"select t.geo_id as operador_id, o.apynom as nombre_operador "+ 
				"from TX_EVT_OPER t "+
				"inner join MST_OPERADORES o on t.geo_codoperador = o.op_codoper " +
				"where t.geo_nroppto = ? " +
				"order by t.geo_id "*/
				"select OPERADOR, PUESTO " + 
				"from VW_RPT_EVT_OPERADORES " +
				"where NROPPTO = ?"
				)
				//.addScalar("operador_id",Hibernate.LONG)
				//.addScalar("nombre_operador", Hibernate.STRING)
				.addScalar("OPERADOR", Hibernate.STRING)
				.addScalar("PUESTO", Hibernate.STRING)
				.setLong(0,new Long(num_presupuesto))
				.list();
		
		DiaryPlannerOperador[] results = new DiaryPlannerOperador[list.size()];
		for (int i=0; i< results.length;i++){ 
			results[i] = new DiaryPlannerOperador();
			Object[] row = (Object[]) list.get(i);
			
			results[i].setNombreyApellido((String)row[0]);
			results[i].setPuesto(((String)row[1]));			
		}
		
		return results;
	}
}
