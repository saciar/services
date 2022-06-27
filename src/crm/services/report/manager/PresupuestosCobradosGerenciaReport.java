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

import crm.libraries.report.PresupuestosCobradosGerencia;
import crm.services.report.sei.PresupuestosCobradosGerenciaReportSEI;
import crm.services.util.HibernateUtil;

public class PresupuestosCobradosGerenciaReport implements PresupuestosCobradosGerenciaReportSEI,ReportService {
		private static final Log log = LogFactory.getLog(EventReport.class);
		
		public PresupuestosCobradosGerencia[] findByRangeDate(int day1, int month1, int year1, 
													int day2, int month2, int year2) throws RemoteException{
			Calendar cal = new GregorianCalendar();
			
			cal.set(Calendar.YEAR, year1);
			cal.set(Calendar.MONTH, month1-1);
			cal.set(Calendar.DATE, day1);
			
			Date startDate = cal.getTime();
			cal.set(year2,month2-1,day2,23,59,59);
			Date endDate = cal.getTime();
			
			return findByDateRange(startDate,endDate);
		}
		
		private PresupuestosCobradosGerencia[] findByDateRange(Date startDate, Date endDate){
			Session session = HibernateUtil.abrirSession();
			
			List list = session.createSQLQuery
					(
					"select nombre_fantasia, nombreEvento, numeroPresupuesto, monto, vendedor "+
					"from vw_rpt_ppto_cobrados_gerencia " +
					"where fechaModificacion >= ? and fechaModificacion <= ? "+
					" order by numeroPresupuesto"
					)
					.addScalar("nombre_fantasia",Hibernate.STRING)//0
					.addScalar("nombreEvento",Hibernate.STRING)//1
					.addScalar("numeroPresupuesto",Hibernate.LONG)//2					
					.addScalar("monto",Hibernate.DOUBLE)//3
					.addScalar("vendedor", Hibernate.STRING)//4
					.setDate(0,startDate)
					.setDate(1,endDate)

					.list();
			
			PresupuestosCobradosGerencia[] results = new PresupuestosCobradosGerencia[list.size()];
			for (int i=0; i< results.length;i++){ 
				results[i] = new PresupuestosCobradosGerencia();
				Object[] row = (Object[]) list.get(i);
				
				results[i].setCliente((String)row[0]);
				results[i].setEvento((String)row[1]);
				results[i].setNroPpto((Long)row[2]);
				results[i].setTotal((Double)row[3]);
				results[i].setVendedor((String)row[4]);

			}
			
			HibernateUtil.cerrarSession(session);
			
			return results;
		}
}
