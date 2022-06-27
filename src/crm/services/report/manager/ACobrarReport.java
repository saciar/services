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

import crm.libraries.report.ACobrar;
import crm.services.report.sei.ACobrarReportSEI;
import crm.services.util.HibernateUtil;

public class ACobrarReport implements ACobrarReportSEI,ReportService {
	private static final Log log = LogFactory.getLog(EventReport.class);
	
	public ACobrar[] findByDate(int day1, int month1, int year1) throws RemoteException{
		Calendar cal = new GregorianCalendar();
		
		cal.set(Calendar.YEAR, year1);
		cal.set(Calendar.MONTH, month1-1);
		cal.set(Calendar.DATE, day1);
		
		Date startDate = cal.getTime();
		
		return findByDate(startDate);
	}
	
	private ACobrar[] findByDate(Date startDate){
		Session session = HibernateUtil.abrirSession();
		
		List list = session.createSQLQuery
				(
				"select nroppto, acobrar,empresa, evento, nroFactura, fechaFactura, direccionPago, fechaHoraPago "+
				"from vw_a_cobrar " +
				"where fechaVencimiento = ? "+
				"order by numeroPresupuesto"
				)
				.addScalar("nroppto",Hibernate.LONG)//0
				.addScalar("acobrar",Hibernate.DOUBLE)//1
				.addScalar("empresa",Hibernate.STRING)//2					
				.addScalar("evento",Hibernate.STRING)//3
				.addScalar("nroFactura", Hibernate.STRING)//4
				.addScalar("fechaFactura", Hibernate.DATE)//5
				.addScalar("direccionPago", Hibernate.STRING)//6
				.addScalar("fechaHoraPago", Hibernate.STRING)//7
				.setDate(0,startDate)
				.list();
		
		ACobrar[] results = new ACobrar[list.size()];
		for (int i=0; i< results.length;i++){ 
			results[i] = new ACobrar();
			Object[] row = (Object[]) list.get(i);
			
			results[i].setNroppto((Long)row[0]);
			results[i].setACobrar((Double)row[1]);
			results[i].setCliente((String)row[2]);
			results[i].setEvento((String)row[3]);
			results[i].setFactura((String)row[4]);
			results[i].setFechaFactura((Date)row[5]);
			results[i].setDireccion((String)row[6]);
			results[i].setFechaHora((String)row[7]);

		}
		
		HibernateUtil.cerrarSession(session);
		
		return results;
	}

}
