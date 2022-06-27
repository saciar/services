package crm.services.report.manager;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.hibernate.Session;

import crm.libraries.report.Evento;
import crm.libraries.report.EventoAccesorio;
import crm.libraries.report.EventoOperador;
import crm.libraries.report.EventoSala;
import crm.libraries.report.EventoServicio;
import crm.services.report.sei.EventReportSEI;
import crm.services.transaction.ClienteManager;
import crm.services.util.HibernateUtil;

public class EventReport implements EventReportSEI,ReportService {
	private static final Log log = LogFactory.getLog(EventReport.class);
	/**
	 * Busca eventos por semana
	 */
	public Evento[] findByWeek(int week, int year) throws RemoteException {
		
		//Calendar cal = new GregorianCalendar();
		Calendar cal = Calendar.getInstance();
		
		cal.set(Calendar.YEAR, year);
		cal.setFirstDayOfWeek(Calendar.SUNDAY);
		cal.setMinimalDaysInFirstWeek(1);
		cal.set(Calendar.WEEK_OF_YEAR, week);
		cal.set(Calendar.DAY_OF_WEEK, 1);
		Date startDate = cal.getTime();
		
		cal.set(Calendar.WEEK_OF_YEAR, week+1);
		Date endDate = cal.getTime();
			
		return findByDateRange(startDate,endDate);
	}
	
	/**
	 * Busca eventos por semana y vendedor
	 */
	public Evento[] findByWeekAndVendedor(int week,int year, long codVendedor) throws RemoteException {
		
		//Calendar cal = new GregorianCalendar();
		Calendar cal = Calendar.getInstance();
		
		cal.set(Calendar.YEAR, year);
		cal.setFirstDayOfWeek(Calendar.SUNDAY);
		cal.setMinimalDaysInFirstWeek(1);
		cal.set(Calendar.WEEK_OF_YEAR, week);
		cal.set(Calendar.DAY_OF_WEEK, 1);
		Date startDate = cal.getTime();
		
		cal.set(Calendar.WEEK_OF_YEAR, week+1);
		Date endDate = cal.getTime();
			
		return findByDateRangeAndVendedor(startDate,endDate,codVendedor);
	}
	
	/**
	 * Busca eventos por semana y UC
	 */
	public Evento[] findByWeekAndUC(int week,int year, long codUC) throws RemoteException {
		
		//Calendar cal = new GregorianCalendar();
		Calendar cal = Calendar.getInstance();
		
		cal.set(Calendar.YEAR, year);
		cal.setFirstDayOfWeek(Calendar.SUNDAY);
		cal.setMinimalDaysInFirstWeek(1);
		cal.set(Calendar.WEEK_OF_YEAR, week);
		cal.set(Calendar.DAY_OF_WEEK, 1);
		Date startDate = cal.getTime();
		
		cal.set(Calendar.WEEK_OF_YEAR, week+1);
		Date endDate = cal.getTime();
			
		return findByDateRangeAndUC(startDate,endDate, codUC);
	}

	/**
	 * busca eventos por dia.
	 */
	public Evento[] findByDay(int day, int month, int year) throws RemoteException {
		Calendar cal = new GregorianCalendar();
		
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month-1);
		cal.set(Calendar.DATE, day);
		
		Date startDate = cal.getTime();
		//cal.set(Calendar.DATE, day+1);
		cal.set(year,month-1,day,23,59,59);
		Date endDate = cal.getTime();
		
		return findByDateRange(startDate,endDate);
	}
	
	/**
	 * busca eventos por dia y vendedor.
	 */
	public Evento[] findByDayAndVendedor(int day, int month, int year, long codVend) throws RemoteException {
		Calendar cal = new GregorianCalendar();
		
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month-1);
		cal.set(Calendar.DATE, day);
		
		Date startDate = cal.getTime();
		//cal.set(Calendar.DATE, day+1);
		cal.set(year,month-1,day,23,59,59);
		Date endDate = cal.getTime();
		
		return findByDateRangeAndVendedor(startDate,endDate,codVend);
	}
	
	/**
	 * busca eventos por diay UC.
	 */
	public Evento[] findByDayAndUC(int day, int month, int year, long codUc) throws RemoteException {
		Calendar cal = new GregorianCalendar();
		
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month-1);
		cal.set(Calendar.DATE, day);
		
		Date startDate = cal.getTime();
		//cal.set(Calendar.DATE, day+1);
		cal.set(year,month-1,day,23,59,59);
		Date endDate = cal.getTime();
		
		return findByDateRangeAndUC(startDate,endDate,codUc);
	}
	
	/**
	 * Busca eventos en un rango de fechas determinado
	 * 
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws RemoteException
	 */
	private Evento[] findByDateRange(Date startDate, Date endDate) {
		//"SELECT * FROM VW_RPT_WEEK where fechaEvento >= '2006-04-23' and fechaEvento <= '2006-04-25'";
		Session session = HibernateUtil.abrirSession();
		
		List list = session.createSQLQuery
				(
				"select nombre_fantasia, nombreEvento, numeroPresupuesto, lugarEvento, tipoEvento, fechaInicialEvento, cantidadSalas, " +
				//"fechaInstalacion, un_comercial, vendedor, sinPrecio, precioModificado, subcontrataciones, monto, " +
				"fechaInstalacion, un_comercial, vendedor, monto, " +
				"cobrado, facturado, confirmado, os, of, adelanto, adelantado, fechaFinalEvento, nuevo " +
				"from VW_RPT_WEEK " +
				"where ((fechaInicialEvento >= ? and fechaFinalEvento <= ?) "+
				"or (fechaInicialEvento <= ? and fechaFinalEvento > ?) "+
				"or(fechaInicialEvento< ? and fechaFinalEvento>= ?)) and " +
				"confirmado = 1 and cancelado = 0 "+
				"order by fechaInicialEvento"
				)
				.addScalar("nombre_fantasia",Hibernate.STRING)
				.addScalar("nombreEvento",Hibernate.STRING)
				.addScalar("numeroPresupuesto",Hibernate.LONG)
				.addScalar("lugarEvento",Hibernate.STRING)
				.addScalar("tipoEvento",Hibernate.STRING)
				.addScalar("fechaInicialEvento",Hibernate.TIMESTAMP)
				.addScalar("cantidadSalas",Hibernate.LONG)
				.addScalar("fechaInstalacion",Hibernate.TIMESTAMP)
				.addScalar("un_comercial",Hibernate.STRING)
				.addScalar("vendedor",Hibernate.STRING)
				//.addScalar("sinPrecio",Hibernate.LONG)
				//.addScalar("precioModificado",Hibernate.LONG)
				//.addScalar("subcontrataciones",Hibernate.LONG)
				.addScalar("monto",Hibernate.DOUBLE)
				.addScalar("cobrado",Hibernate.BOOLEAN)
				.addScalar("facturado",Hibernate.BOOLEAN)
				.addScalar("confirmado",Hibernate.BOOLEAN)
				.addScalar("os",Hibernate.BOOLEAN)
				.addScalar("of",Hibernate.BOOLEAN)
				.addScalar("adelanto",Hibernate.BOOLEAN)
				.addScalar("adelantado",Hibernate.BOOLEAN)
				.addScalar("fechaFinalEvento", Hibernate.TIMESTAMP)
				.addScalar("nuevo", Hibernate.STRING)
				.setDate(0,startDate)
				.setDate(1,endDate)
				.setDate(2,endDate)
				.setDate(3,endDate)
				.setDate(4,startDate)
				.setDate(5,startDate)

				.list();
				
		Evento[] results = new Evento[list.size()];
		for (int i=0; i< results.length;i++){ 
			results[i] = new Evento();
			Object[] row = (Object[]) list.get(i);
			
			Calendar cal = Calendar.getInstance();	
			
			results[i].setNombreFantasia((String)row[0]);
			results[i].setNombreEvento((String)row[1]);
			results[i].setNumeroPresupuesto((Long)row[2]);
			results[i].setLugarEvento((String)row[3]);
			results[i].setTipoEvento((String)row[4]);	        
	        results[i].setFechaInicialEvento(((Timestamp)row[5]).toString());
			results[i].setCantidadSalas(((Long)row[6]).longValue());
			results[i].setFechaInstalacion(((Timestamp)row[7]).toString());
			results[i].setUnidad((String)row[8]);
			results[i].setVendedor((String)row[9]);
			/*results[i].setSinPrecio(((Long)row[10]).longValue());
			results[i].setPrecioModificado(((Long)row[11]).longValue());
			results[i].setSubcontrataciones(((Long)row[12]).longValue());
			results[i].setMonto(((Double)row[13]).doubleValue());
			results[i].setCobrado(((Boolean)row[14]).booleanValue());
			results[i].setFacturado(((Boolean)row[15]).booleanValue());
			
			results[i].setConfirmado(((Boolean)row[16]).booleanValue());
			results[i].setOs(((Boolean)row[17]).booleanValue());
			results[i].setOf(((Boolean)row[18]).booleanValue());
			results[i].setAdelanto(((Boolean)row[19]).booleanValue());
			results[i].setAdelantado(((Boolean)row[20]).booleanValue());
			
			results[i].setFechaFinalEvento(((Timestamp)row[21]).toString());
			results[i].setNuevo((String)row[22] == "S");
			results[i].setOrigen("N/A");*/
			results[i].setSinPrecio(getSinPrecio(session,((Long)row[2]).longValue()));
			results[i].setPrecioModificado(getModificado(session,((Long)row[2]).longValue()));
			results[i].setSubcontrataciones(getSubcontrataciones(session,((Long)row[2]).longValue()));			
			results[i].setMonto(((Double)row[10]).doubleValue());
			results[i].setCobrado(((Boolean)row[11]).booleanValue());
			results[i].setFacturado(((Boolean)row[12]).booleanValue());
			
			results[i].setConfirmado(((Boolean)row[13]).booleanValue());
			results[i].setOs(((Boolean)row[14]).booleanValue());
			results[i].setOf(((Boolean)row[15]).booleanValue());
			results[i].setAdelanto(((Boolean)row[16]).booleanValue());
			results[i].setAdelantado(((Boolean)row[17]).booleanValue());
			
			results[i].setFechaFinalEvento(((Timestamp)row[18]).toString());
			results[i].setNuevo((String)row[19] == "S");
			results[i].setOrigen("N/A");
			
			results[i].setSalas(findSalasByPresupuesto(session,results[i].getNumeroPresupuesto()));
			
			results[i].setOperadores(findOperadoresbyPresupuesto(session,results[i].getNumeroPresupuesto()));
		}
		
		HibernateUtil.cerrarSession(session);
		
		return results;
	}
	
	private Evento[] findByDateRangeAndVendedor(Date startDate, Date endDate, long codVendedor) {
		//"SELECT * FROM VW_RPT_WEEK where fechaEvento >= '2006-04-23' and fechaEvento <= '2006-04-25'";
		Session session = HibernateUtil.abrirSession();
		
		List list = session.createSQLQuery
				(
				"select nombre_fantasia, nombreEvento, numeroPresupuesto, lugarEvento, tipoEvento, fechaInicialEvento, cantidadSalas, " +
				//"fechaInstalacion, un_comercial, vendedor, sinPrecio, precioModificado, subcontrataciones, monto, " +
				"fechaInstalacion, un_comercial, vendedor, monto, " +
				"cobrado, facturado, confirmado, os, of, adelanto, adelantado, fechaFinalEvento, nuevo " +
				"from VW_RPT_WEEK " +
				"inner join TX_VENDEDOR_PPTO on numeroPresupuesto = vp_nroppto "+
				"where ((fechaInicialEvento >= ? and fechaFinalEvento <= ?) "+
				"or (fechaInicialEvento <= ? and fechaFinalEvento > ?) "+
				"or(fechaInicialEvento< ? and fechaFinalEvento>= ?)) and vp_vendedor = ? and " +
				"confirmado = 1 and cancelado = 0 "+
				"order by fechaInicialEvento"
				)
				.addScalar("nombre_fantasia",Hibernate.STRING)
				.addScalar("nombreEvento",Hibernate.STRING)
				.addScalar("numeroPresupuesto",Hibernate.LONG)
				.addScalar("lugarEvento",Hibernate.STRING)
				.addScalar("tipoEvento",Hibernate.STRING)
				.addScalar("fechaInicialEvento",Hibernate.TIMESTAMP)
				.addScalar("cantidadSalas",Hibernate.LONG)
				.addScalar("fechaInstalacion",Hibernate.TIMESTAMP)
				.addScalar("un_comercial",Hibernate.STRING)
				.addScalar("vendedor",Hibernate.STRING)
				//.addScalar("sinPrecio",Hibernate.LONG)
				//.addScalar("precioModificado",Hibernate.LONG)
				//.addScalar("subcontrataciones",Hibernate.LONG)
				.addScalar("monto",Hibernate.DOUBLE)
				.addScalar("cobrado",Hibernate.BOOLEAN)
				.addScalar("facturado",Hibernate.BOOLEAN)
				.addScalar("confirmado",Hibernate.BOOLEAN)
				.addScalar("os",Hibernate.BOOLEAN)
				.addScalar("of",Hibernate.BOOLEAN)
				.addScalar("adelanto",Hibernate.BOOLEAN)
				.addScalar("adelantado",Hibernate.BOOLEAN)
				.addScalar("fechaFinalEvento", Hibernate.TIMESTAMP)
				.addScalar("nuevo", Hibernate.STRING)
				.setDate(0,startDate)
				.setDate(1,endDate)
				.setDate(2,endDate)
				.setDate(3,endDate)
				.setDate(4,startDate)
				.setDate(5,startDate)
				.setLong(6,codVendedor)

				.list();
				
		Evento[] results = new Evento[list.size()];
		for (int i=0; i< results.length;i++){ 
			results[i] = new Evento();
			Object[] row = (Object[]) list.get(i);
			
			Calendar cal = Calendar.getInstance();	
			
			results[i].setNombreFantasia((String)row[0]);
			results[i].setNombreEvento((String)row[1]);
			results[i].setNumeroPresupuesto((Long)row[2]);
			results[i].setLugarEvento((String)row[3]);
			results[i].setTipoEvento((String)row[4]);	        
	        results[i].setFechaInicialEvento(((Timestamp)row[5]).toString());
			results[i].setCantidadSalas(((Long)row[6]).longValue());
			results[i].setFechaInstalacion(((Timestamp)row[7]).toString());
			results[i].setUnidad((String)row[8]);
			results[i].setVendedor((String)row[9]);
			//results[i].setSinPrecio(((Long)row[10]).longValue());
			//results[i].setPrecioModificado(((Long)row[11]).longValue());
			//results[i].setSubcontrataciones(((Long)row[12]).longValue());
			results[i].setSinPrecio(getSinPrecio(session,((Long)row[2]).longValue()));
			results[i].setPrecioModificado(getModificado(session,((Long)row[2]).longValue()));
			results[i].setSubcontrataciones(getSubcontrataciones(session,((Long)row[2]).longValue()));			
			results[i].setMonto(((Double)row[10]).doubleValue());
			results[i].setCobrado(((Boolean)row[11]).booleanValue());
			results[i].setFacturado(((Boolean)row[12]).booleanValue());
			
			results[i].setConfirmado(((Boolean)row[13]).booleanValue());
			results[i].setOs(((Boolean)row[14]).booleanValue());
			results[i].setOf(((Boolean)row[15]).booleanValue());
			results[i].setAdelanto(((Boolean)row[16]).booleanValue());
			results[i].setAdelantado(((Boolean)row[17]).booleanValue());
			
			results[i].setFechaFinalEvento(((Timestamp)row[18]).toString());
			results[i].setNuevo((String)row[19] == "S");
			results[i].setOrigen("N/A");
			
			results[i].setSalas(findSalasByPresupuesto(session,results[i].getNumeroPresupuesto()));
			
			results[i].setOperadores(findOperadoresbyPresupuesto(session,results[i].getNumeroPresupuesto()));
		}
		
		HibernateUtil.cerrarSession(session);
		
		return results;
	}
	
	private Evento[] findByDateRangeAndUC(Date startDate, Date endDate, long codUC) {
		//"SELECT * FROM VW_RPT_WEEK where fechaEvento >= '2006-04-23' and fechaEvento <= '2006-04-25'";
		Session session = HibernateUtil.abrirSession();
		
		List list = session.createSQLQuery
				(
				"select nombre_fantasia, nombreEvento, numeroPresupuesto, lugarEvento, tipoEvento, fechaInicialEvento, cantidadSalas, " +
				//"fechaInstalacion, un_comercial, vendedor, sinPrecio, precioModificado, subcontrataciones, monto, " +
				"fechaInstalacion, un_comercial, vendedor, monto, " +
				"cobrado, facturado, confirmado, os, of, adelanto, adelantado, fechaFinalEvento, nuevo " +
				"from VW_RPT_WEEK " +
				"inner join TX_VENDEDOR_PPTO on numeroPresupuesto = vp_nroppto "+
				"inner join VW_UC_VENDEDORES on codvend = vp_vendedor "+
				"where ((fechaInicialEvento >= ? and fechaFinalEvento <= ?) "+
				"or (fechaInicialEvento <= ? and fechaFinalEvento > ?) "+
				"or(fechaInicialEvento< ? and fechaFinalEvento>= ?)) and coduc = ? and " +
				"confirmado = 1 and cancelado = 0 "+
				"order by fechaInicialEvento"
				)
				.addScalar("nombre_fantasia",Hibernate.STRING)
				.addScalar("nombreEvento",Hibernate.STRING)
				.addScalar("numeroPresupuesto",Hibernate.LONG)
				.addScalar("lugarEvento",Hibernate.STRING)
				.addScalar("tipoEvento",Hibernate.STRING)
				.addScalar("fechaInicialEvento",Hibernate.TIMESTAMP)
				.addScalar("cantidadSalas",Hibernate.LONG)
				.addScalar("fechaInstalacion",Hibernate.TIMESTAMP)
				.addScalar("un_comercial",Hibernate.STRING)
				.addScalar("vendedor",Hibernate.STRING)
				//.addScalar("sinPrecio",Hibernate.LONG)
				//.addScalar("precioModificado",Hibernate.LONG)
				//.addScalar("subcontrataciones",Hibernate.LONG)
				.addScalar("monto",Hibernate.DOUBLE)
				.addScalar("cobrado",Hibernate.BOOLEAN)
				.addScalar("facturado",Hibernate.BOOLEAN)
				.addScalar("confirmado",Hibernate.BOOLEAN)
				.addScalar("os",Hibernate.BOOLEAN)
				.addScalar("of",Hibernate.BOOLEAN)
				.addScalar("adelanto",Hibernate.BOOLEAN)
				.addScalar("adelantado",Hibernate.BOOLEAN)
				.addScalar("fechaFinalEvento", Hibernate.TIMESTAMP)
				.addScalar("nuevo", Hibernate.STRING)
				.setDate(0,startDate)
				.setDate(1,endDate)
				.setDate(2,endDate)
				.setDate(3,endDate)
				.setDate(4,startDate)
				.setDate(5,startDate)
				.setLong(6,codUC)

				.list();
				
		Evento[] results = new Evento[list.size()];
		for (int i=0; i< results.length;i++){ 
			results[i] = new Evento();
			Object[] row = (Object[]) list.get(i);
			
			Calendar cal = Calendar.getInstance();	
			
			results[i].setNombreFantasia((String)row[0]);
			results[i].setNombreEvento((String)row[1]);
			results[i].setNumeroPresupuesto((Long)row[2]);
			results[i].setLugarEvento((String)row[3]);
			results[i].setTipoEvento((String)row[4]);	        
	        results[i].setFechaInicialEvento(((Timestamp)row[5]).toString());
			results[i].setCantidadSalas(((Long)row[6]).longValue());
			results[i].setFechaInstalacion(((Timestamp)row[7]).toString());
			results[i].setUnidad((String)row[8]);
			results[i].setVendedor((String)row[9]);
			/*results[i].setSinPrecio(((Long)row[10]).longValue());
			results[i].setPrecioModificado(((Long)row[11]).longValue());
			results[i].setSubcontrataciones(((Long)row[12]).longValue());
			results[i].setMonto(((Double)row[13]).doubleValue());
			results[i].setCobrado(((Boolean)row[14]).booleanValue());
			results[i].setFacturado(((Boolean)row[15]).booleanValue());
			
			results[i].setConfirmado(((Boolean)row[16]).booleanValue());
			results[i].setOs(((Boolean)row[17]).booleanValue());
			results[i].setOf(((Boolean)row[18]).booleanValue());
			results[i].setAdelanto(((Boolean)row[19]).booleanValue());
			results[i].setAdelantado(((Boolean)row[20]).booleanValue());
			
			results[i].setFechaFinalEvento(((Timestamp)row[21]).toString());
			results[i].setNuevo((String)row[22] == "S");
			results[i].setOrigen("N/A");*/
			results[i].setSinPrecio(getSinPrecio(session,((Long)row[2]).longValue()));
			results[i].setPrecioModificado(getModificado(session,((Long)row[2]).longValue()));
			results[i].setSubcontrataciones(getSubcontrataciones(session,((Long)row[2]).longValue()));			
			results[i].setMonto(((Double)row[10]).doubleValue());
			results[i].setCobrado(((Boolean)row[11]).booleanValue());
			results[i].setFacturado(((Boolean)row[12]).booleanValue());
			
			results[i].setConfirmado(((Boolean)row[13]).booleanValue());
			results[i].setOs(((Boolean)row[14]).booleanValue());
			results[i].setOf(((Boolean)row[15]).booleanValue());
			results[i].setAdelanto(((Boolean)row[16]).booleanValue());
			results[i].setAdelantado(((Boolean)row[17]).booleanValue());
			
			results[i].setFechaFinalEvento(((Timestamp)row[18]).toString());
			results[i].setNuevo((String)row[19] == "S");
			results[i].setOrigen("N/A");
			
			results[i].setSalas(findSalasByPresupuesto(session,results[i].getNumeroPresupuesto()));
			
			results[i].setOperadores(findOperadoresbyPresupuesto(session,results[i].getNumeroPresupuesto()));
		}
		
		HibernateUtil.cerrarSession(session);
		
		return results;
	}

	private EventoSala[] findSalasByPresupuesto(Session session,long num_presupuesto) {
		//"SELECT * FROM VW_RPT_WEEK where fechaEvento >= '2006-04-23' and fechaEvento <= '2006-04-25'";

		
		List list = session.createSQLQuery
				(
				"select s.ppto_s_id as sala_id, sl.els_descripcion as nombre_sala, s.ppto_s_fecinicio as fecha_inicio, "+ 
				"s.ppto_s_fecfin as fecha_fin, s.ppto_s_totpersonas as total_personas "+
				"from TX_PPTO_SALAS s "+
				"inner join MST_EVT_LUGAR_SALAS sl on s.ppto_s_codlugsala = sl.els_codlugsala " +
				"where s.ppto_s_nroppto = ? " +
				"order by s.ppto_s_id "
				)
				.addScalar("sala_id",Hibernate.LONG)
				.addScalar("nombre_sala",Hibernate.STRING)
				.addScalar("fecha_inicio",Hibernate.TIMESTAMP)
				.addScalar("fecha_fin",Hibernate.TIMESTAMP)
				//.addScalar("fecha_instalacion",Hibernate.DATE)
				.addScalar("total_personas",Hibernate.LONG)
				.setLong(0,new Long(num_presupuesto))
				.list();

		EventoSala[] results = new EventoSala[list.size()];
		for (int i=0; i< results.length;i++){ 
			results[i] = new EventoSala();
			Object[] row = (Object[]) list.get(i);
			
			Calendar cal = Calendar.getInstance();
			
			results[i].setSalaId(((Long)row[0]).longValue());
			results[i].setNombreSala((String)row[1]);
			results[i].setFechaInicio(((Timestamp)row[2]).toString());
			results[i].setFechaFin(((Timestamp)row[3]).toString());
			//results[i].setFechaInstalacion((Date)row[4]);
			results[i].setTotalPersonas(((Long)row[4]).longValue());
			
			results[i].setServicios(findServiciosBySala(session,results[i].getSalaId()));
		}
		
		return results;
	}
	
	private EventoServicio[] findServiciosBySala(Session session,long num_sala) {
		//"SELECT * FROM VW_RPT_WEEK where fechaEvento >= '2006-04-23' and fechaEvento <= '2006-04-25'";

		
		List list = session.createSQLQuery
				(
				"select ss.ppto_ss_id as id,ss.ppto_ss_cantidad  as cantidad, servi.si_descripcion_abreviada as servicio, "+
				"ss.ppto_ss_dias as dias "+
				"from TX_PPTO_SALAS_SERVICIOS ss "+
				"inner join MST_SERVICIOS serv on ss.ppto_ss_servicio = serv.se_codservicio "+
				"inner join MST_SERVICIOS_IDIOMA servi on serv.se_codservicio = servi.si_codservicio "+
				"where ss.ppto_ss_pls = ? " +
				"ORDER BY ss.ppto_ss_orden"
				)
				.addScalar("id",Hibernate.LONG)
				.addScalar("cantidad",Hibernate.LONG)
				.addScalar("servicio",Hibernate.STRING)
				.addScalar("dias",Hibernate.INTEGER)
				.setLong(0,new Long(num_sala))
				.list();

		EventoServicio[] results = new EventoServicio[list.size()];
		for (int i=0; i< results.length;i++){ 
			results[i] = new EventoServicio();
			Object[] row = (Object[]) list.get(i);
			
			results[i].setServicioId(((Long)row[0]).longValue());
			results[i].setCantidad(((Long)row[1]).longValue());
			results[i].setServicio((String)row[2]);
			results[i].setDias(((Integer)row[3]).intValue());
			
			results[i].setAccesorios(findAccesoriosByServicio(session,results[i].getServicioId()));
		}
		
		return results;
	}
	
	private EventoAccesorio[] findAccesoriosByServicio(Session session,long num_servicio) {
		//"SELECT * FROM VW_RPT_WEEK where fechaEvento >= '2006-04-23' and fechaEvento <= '2006-04-25'";

		
		List list = session.createSQLQuery
				(
				"select a.ppto_ssa_id as id,a.ppto_ssa_cantidad as cantidad, ai.ai_descrip_abreviada as accesorio " +
				"from TX_PPTO_SALAS_SERVICIOS_ACC a " +
				"inner join MST_ACC_INSTALACION ai on a.ppto_ssa_accesorio = ai.ai_codint " +
				"where a.ppto_ssa_plsa = ? " + 
				"ORDER BY ai.ai_descrip_abreviada"
				)
				.addScalar("id",Hibernate.LONG)
				.addScalar("cantidad",Hibernate.LONG)
				.addScalar("accesorio",Hibernate.STRING)
				.setLong(0,new Long(num_servicio))
				.list();

		EventoAccesorio[] results = new EventoAccesorio[list.size()];
		for (int i=0; i< results.length;i++){ 
			results[i] = new EventoAccesorio();
			Object[] row = (Object[]) list.get(i);
			
			results[i].setAccesorioId(((Long)row[0]).longValue());
			results[i].setCantidad(((Long)row[1]).longValue());
			results[i].setAccesorio((String)row[2]);
		}
		
		return results;
	}
	
	private EventoOperador[] findOperadoresbyPresupuesto(Session session,long num_presupuesto){
		
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
		
		EventoOperador[] results = new EventoOperador[list.size()];
		for (int i=0; i< results.length;i++){ 
			results[i] = new EventoOperador();
			Object[] row = (Object[]) list.get(i);
			
			results[i].setNombreyApellido((String)row[0]);
			results[i].setPuesto(((String)row[1]));			
		}
		
		return results;
	}
	
	private long getSinPrecio(Session session, long pptoNro){
		Long cant = (Long)session.createSQLQuery
			(
			"select count(*) as cuenta from TX_PPTO_SALAS_SERVICIOS ss "+
			"where ss.ppto_ss_pls in "+
			"(select s.ppto_s_id from TX_PPTO_SALAS s where s.ppto_s_nroppto = ?) "+
			"and (ss.ppto_ss_preciolista = 0)"
			)
			.addScalar("cuenta",Hibernate.LONG)
			.setLong(0,new Long(pptoNro))
			.uniqueResult();
		if(cant != null)
			return cant.longValue();
		else return 0;
	}
	
	private long getModificado(Session session, long pptoNro){
		Long cant = (Long)session.createSQLQuery
			(
			"select count(*) as cuenta from TX_PPTO_SALAS_SERVICIOS ss "+
			"where ss.ppto_ss_pls in "+
			"(select s.ppto_s_id from TX_PPTO_SALAS s where s.ppto_s_nroppto = ?) "+
			"and (ss.ppto_ss_descuento <> 0)"
			)
			.addScalar("cuenta",Hibernate.LONG)
			.setLong(0,new Long(pptoNro))
			.uniqueResult();
		if(cant != null)
			return cant.longValue();
		else return 0;
	}
	
	private long getSubcontrataciones(Session session, long pptoNro){
		Long cant = (Long)session.createSQLQuery
			(
			"select count(*) as cuenta from TX_PPTO_SALAS_SERVICIOS ss "+
			"where ss.ppto_ss_pls in "+
			"(select s.ppto_s_id from TX_PPTO_SALAS s where s.ppto_s_nroppto = ?) "+
			"and ((ss.ppto_ss_modalidad = 2) or(ss.ppto_ss_modalidad = 4))"
			)
			.addScalar("cuenta",Hibernate.LONG)
			.setLong(0,new Long(pptoNro))
			.uniqueResult();
		if(cant != null)
			return cant.longValue();
		else return 0;
	}
}
