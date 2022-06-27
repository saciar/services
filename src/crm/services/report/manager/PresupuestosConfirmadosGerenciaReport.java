package crm.services.report.manager;

import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.Session;

import crm.libraries.report.PresupuestosConfirmadosGerencia;
import crm.services.report.sei.PresupuestosConfirmadosGerenciaReportSEI;
import crm.services.util.HibernateUtil;

public class PresupuestosConfirmadosGerenciaReport implements PresupuestosConfirmadosGerenciaReportSEI,ReportService{
	
	public PresupuestosConfirmadosGerencia[] findByRangeDate(int day1, int month1, int year1, 
			int day2, int month2, int year2) throws RemoteException{
		Calendar cal = new GregorianCalendar();
		
		cal.set(Calendar.YEAR, year1);
		cal.set(Calendar.MONTH, month1-1);
		cal.set(Calendar.DATE, day1);
		
		Date startDate = cal.getTime();
		cal.set(year2,month2-1,day2+1,23,59,59);
		Date endDate = cal.getTime();
		
		return findByDateRange(startDate,endDate);
	}
	
	private PresupuestosConfirmadosGerencia[] findByDateRange(Date startDate, Date endDate){
		Session session = HibernateUtil.abrirSession();
		
		List list = session.createSQLQuery
		(
				"select nombre_fantasia, nombreEvento, numeroPresupuesto, monto, vendedor, estado "+
				"from vw_rpt_ppto_confirmados_gerencia " +
				"inner join vw_pptos_estados on numeropresupuesto= nroppto "+
				"where fechaModificacion >= ? and fechaModificacion < ? "+
				" order by numeroPresupuesto"
		)
		.addScalar("nombre_fantasia",Hibernate.STRING)//0
		.addScalar("nombreEvento",Hibernate.STRING)//1
		.addScalar("numeroPresupuesto",Hibernate.LONG)//2					
		.addScalar("monto",Hibernate.DOUBLE)//3
		.addScalar("vendedor", Hibernate.STRING)//4
		.addScalar("estado",Hibernate.STRING)
		.setDate(0,startDate)
		.setDate(1,endDate)
		
		.list();
		
		PresupuestosConfirmadosGerencia[] results = new PresupuestosConfirmadosGerencia[list.size()];
		for (int i=0; i< results.length;i++){ 
			results[i] = new PresupuestosConfirmadosGerencia();
			Object[] row = (Object[]) list.get(i);
			
			results[i].setCliente((String)row[0]);
			results[i].setEvento((String)row[1]);
			results[i].setNroPpto((Long)row[2]);
			results[i].setTotal((Double)row[3]);
			results[i].setVendedor((String)row[4]);
			results[i].setEstado((String)row[5]);
		}
		
		HibernateUtil.cerrarSession(session);
		
		return results;
	}
}
