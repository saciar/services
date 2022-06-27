package crm.services.report.manager;

import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.hibernate.Session;

import crm.libraries.report.InfoMensual;
import crm.libraries.report.InfoMensualCliente;
import crm.services.report.sei.InfoMensualReportSEI;
import crm.services.util.HibernateUtil;

public class InfoMensualReport implements InfoMensualReportSEI, ReportService{
	private static final Log log = LogFactory.getLog(InfoSemanalReport.class);
	
	public InfoMensual findByMonth(int month, int year) throws RemoteException {
		
		//Calendar cal = new GregorianCalendar();
		Calendar cal = Calendar.getInstance();
		
		cal.set(Calendar.YEAR, year);		
		cal.set(Calendar.MONTH, month-1);
		cal.set(Calendar.DATE, 1);
		Date startDate = cal.getTime();
		
		cal.set(Calendar.MONTH, month);
		Date endDate = cal.getTime();
			
		return findByDateRange(startDate,endDate);
	}
	
	private InfoMensual findByDateRange(Date startDate, Date endDate) {
		Session session = HibernateUtil.abrirSession();
		
		List presupuestos = session.createSQLQuery(
				"SELECT t.ppto_nroppto AS nroppto, nc.nc_nuevo AS nuevoCliente, " +
				"cli.nombre_fantasia AS nombreFantasia, cli.cl_codcliente AS codigoCliente, " +
				"rent.facturacion AS facturacion, ea.estado AS estado FROM tx_ppto t "+
				"INNER JOIN mst_cliente cli ON t.ppto_codcliente = cli.cl_codcliente "+
				"INNER JOIN tx_nuevo_cliente nc ON nc.nc_codcliente = cli.cl_codcliente "+
				"INNER JOIN vw_rentabilidad rent ON rent.presupuesto = t.ppto_nroppto "+				
				"INNER JOIN vw_pptos_estados ea ON ea.nroppto = t.ppto_nroppto "+
				"WHERE t.ppto_fecinicio >= ? AND t.ppto_fecfin <= ? "+
				"ORDER BY facturacion, t.ppto_nroppto"
				)
				.addScalar("nroppto", Hibernate.LONG)
				.addScalar("nuevoCliente", Hibernate.STRING)
				.addScalar("nombreFantasia", Hibernate.STRING)
				.addScalar("codigoCliente", Hibernate.LONG)
				.addScalar("facturacion", Hibernate.DOUBLE)				
				.addScalar("estado", Hibernate.STRING)
				.setDate(0, startDate)
				.setDate(1, endDate)
				.list();
		/*if (log.isDebugEnabled()){
			log.info("************************"+startDate);
		}*/		
		
		InfoMensual result = new InfoMensual();
		long contTotal = presupuestos.size();
		long contConfirmados = 0;
		long contNoConfirmdos = 0;
		double totalConfirmados = 0.0;
		double totalNoConfirmados = 0.0;
		int contCNC = 0;
		int contCNNC = 0;
		int contCVC = 0;
		int contCVNC = 0;
		InfoMensualCliente[] clientesNuevosConfirmados = new InfoMensualCliente[presupuestos.size()];
		InfoMensualCliente[] clientesNuevosNoConfirmados = new InfoMensualCliente[presupuestos.size()];
		InfoMensualCliente[] clientesViejosConfirmados = new InfoMensualCliente[presupuestos.size()];
		InfoMensualCliente[] clientesViejosNoConfirmados = new InfoMensualCliente[presupuestos.size()];
		
		/*for (int i=0; i< presupuestos.size();i++){ 
			Object[] row = (Object[]) presupuestos.get(i);			
			if(((String)row[5]).equals("Confirmado") || ((String)row[5]).equals("Orden de servicio") ||
					((String)row[5]).equals("Orden de facturación")){
				contConfirmados++;
				totalConfirmados += ((Double)row[4]).doubleValue();
				if(((String)row[1]).equals("S") && !isClienteSaved(((Long)row[3]).longValue(), clientesNuevosConfirmados)){
					clientesNuevosConfirmados[contCNC] = new InfoMensualCliente();
					clientesNuevosConfirmados[contCNC].setNombre((String)row[2]);
					clientesNuevosConfirmados[contCNC].setCodCliente(((Long)row[3]).longValue());
					contCNC++;
				}
				else if(((String)row[1]).equals("N") && !isClienteSaved(((Long)row[3]).longValue(), clientesViejosConfirmados)){
					clientesViejosConfirmados[contCVC] = new InfoMensualCliente();
					clientesViejosConfirmados[contCVC].setNombre((String)row[2]);
					clientesViejosConfirmados[contCVC].setCodCliente(((Long)row[3]).longValue());
					contCVC++;
				}
			}
			else {
				contNoConfirmdos++;
				totalNoConfirmados+= ((Double)row[4]).doubleValue();
				if(((String)row[1]).equals("S") && !isClienteSaved(((Long)row[3]).longValue(), clientesNuevosNoConfirmados)){
					clientesNuevosNoConfirmados[contCNNC] = new InfoMensualCliente();
					clientesNuevosNoConfirmados[contCNNC].setNombre((String)row[2]);
					clientesNuevosNoConfirmados[contCNNC].setCodCliente(((Long)row[3]).longValue());
					contCNNC++;
				}
				else if(((String)row[1]).equals("N") && !isClienteSaved(((Long)row[3]).longValue(), clientesViejosNoConfirmados)){
					clientesViejosNoConfirmados[contCVNC] = new InfoMensualCliente();
					clientesViejosNoConfirmados[contCVNC].setNombre((String)row[2]);
					clientesViejosNoConfirmados[contCVNC].setCodCliente(((Long)row[3]).longValue());
					contCVNC++;
				}
			}
			
		}*/
		for (int i=0; i< presupuestos.size();i++){ 
			Object[] row = (Object[]) presupuestos.get(i);			
			if(((String)row[5]).equals("Confirmado") || ((String)row[5]).equals("Orden de servicio") ||
					((String)row[5]).equals("Orden de facturación")){
				contConfirmados++;
				totalConfirmados += ((Double)row[4]).doubleValue();
				if(((String)row[1]).equals("S") && !isClienteSaved(((Long)row[3]).longValue(), clientesNuevosConfirmados)){
					clientesNuevosConfirmados[contCNC] = new InfoMensualCliente();
					clientesNuevosConfirmados[contCNC].setNombre((String)row[2]);
					clientesNuevosConfirmados[contCNC].setCodCliente(((Long)row[3]).longValue());
					contCNC++;
				}
				else if(((String)row[1]).equals("N") && !isClienteSaved(((Long)row[3]).longValue(), clientesViejosConfirmados)){
					clientesViejosConfirmados[contCVC] = new InfoMensualCliente();
					clientesViejosConfirmados[contCVC].setNombre((String)row[2]);
					clientesViejosConfirmados[contCVC].setCodCliente(((Long)row[3]).longValue());
					contCVC++;
				}
			}
			else {
				contNoConfirmdos++;
				totalNoConfirmados+= ((Double)row[4]).doubleValue();
				if(((String)row[1]).equals("S") && !isClienteSaved(((Long)row[3]).longValue(), clientesNuevosNoConfirmados)){
					clientesNuevosNoConfirmados[contCNNC] = new InfoMensualCliente();
					clientesNuevosNoConfirmados[contCNNC].setNombre((String)row[2]);
					clientesNuevosNoConfirmados[contCNNC].setCodCliente(((Long)row[3]).longValue());
					contCNNC++;
				}
				else if(((String)row[1]).equals("N") && !isClienteSaved(((Long)row[3]).longValue(), clientesViejosNoConfirmados)){
					clientesViejosNoConfirmados[contCVNC] = new InfoMensualCliente();
					clientesViejosNoConfirmados[contCVNC].setNombre((String)row[2]);
					clientesViejosNoConfirmados[contCVNC].setCodCliente(((Long)row[3]).longValue());
					contCVNC++;
				}
			}
			
		}
		
		result.setCantidadTotal(contTotal);
		result.setCantidadConfirmados(contConfirmados);
		result.setCantidadNoConfirmados(contNoConfirmdos);
		result.setTotalConfirmados(totalConfirmados);
		result.setTotalNoConfirmados(totalNoConfirmados);
		if(clientesNuevosConfirmados != null)
			result.setClientesNuevosConfirmados(clientesNuevosConfirmados);
		if(clientesNuevosNoConfirmados != null)
			result.setClientesNuevosNoConfirmados(clientesNuevosNoConfirmados);
		if(clientesViejosConfirmados != null)
			result.setClientesViejosConfirmados(clientesViejosConfirmados);
		if(clientesViejosNoConfirmados != null)
			result.setClientesViejosNoConfirmados(clientesViejosNoConfirmados);
		
		result.setTop10ClientesFacturado(getTop10ClientesFacturado(startDate, endDate, session));
		result.setTop10ClientesCantEventos(getTop10ClientesEventos(startDate, endDate, session));
		
		HibernateUtil.cerrarSession(session);
		
		return result;
	}
	
	private boolean isClienteSaved(long codCliente, InfoMensualCliente[] clientes){
		boolean encontre = false;
		int cont = 0;
		if(clientes != null){
			while (!encontre && cont < clientes.length){
				if(clientes[cont] != null && clientes[cont].getCodCliente() == codCliente){
					encontre = true;
				}
				cont++;
			}
		}
		return encontre;
	}
	
	private InfoMensualCliente[] getTop10ClientesFacturado(Date startDate, Date endDate, Session session){
		List presupuestos = session.createSQLQuery(
				"SELECT t.ppto_nroppto AS nroppto, cli.nombre_fantasia AS nombreFantasia, "+
				"cli.cl_codcliente AS codigoCliente, sum(rent.facturacion) AS facturacion, " +
				"ea.estact_facturado as estado FROM tx_ppto t "+ 
				"INNER JOIN mst_cliente cli ON t.ppto_codcliente = cli.cl_codcliente "+
				"INNER JOIN vw_rentabilidad rent ON rent.presupuesto = t.ppto_nroppto "+
				"INNER JOIN tx_ppto_est_actual ea ON t.ppto_nroppto = ea.estact_nroppto "+
				"WHERE t.ppto_fecinicio >= ? AND t.ppto_fecfin <= ? "+
				"GROUP BY cli.cl_codcliente HAVING estado = 1 "+
				"ORDER BY facturacion desc, t.ppto_nroppto"
				)
				.addScalar("nroppto", Hibernate.LONG)
				.addScalar("nombreFantasia", Hibernate.STRING)
				.addScalar("codigoCliente", Hibernate.LONG)
				.addScalar("facturacion", Hibernate.DOUBLE)
				.addScalar("estado", Hibernate.INTEGER)
				.setDate(0, startDate)
				.setDate(1, endDate)
				.list();
		
		int longReport = presupuestos.size();
		if(longReport > 10)
			longReport = 10;
		
		InfoMensualCliente[] clientesTop10 = new InfoMensualCliente[longReport];
		for (int i=0; i< longReport;i++){ 
			Object[] row = (Object[]) presupuestos.get(i);
			clientesTop10[i] = new InfoMensualCliente();
			clientesTop10[i].setCodCliente(((Long)row[2]).longValue());
			clientesTop10[i].setNombre((String)row[1]);
			clientesTop10[i].setFacturacion(((Double)row[3]).doubleValue());
		}
		
		return clientesTop10;
	}
	
	private InfoMensualCliente[] getTop10ClientesEventos(Date startDate, Date endDate, Session session){
		/*if (log.isDebugEnabled()){
			log.info("************************"+startDate);
			log.info("************************"+endDate);
		}*/
		List presupuestos = session.createSQLQuery(
				"SELECT t.ppto_nroppto AS nroppto, cli.nombre_fantasia AS nombreFantasia, "+
				"cli.cl_codcliente AS codigoCliente, count(t.ppto_nroppto) AS cantClientes, " +
				"ea.estact_facturado as estado FROM tx_ppto t "+
				"INNER JOIN mst_cliente cli ON t.ppto_codcliente = cli.cl_codcliente "+
				"INNER JOIN tx_ppto_est_actual ea ON t.ppto_nroppto = ea.estact_nroppto "+
				"WHERE t.ppto_fecinicio >= ? AND t.ppto_fecfin <= ? "+
				"GROUP BY cli.nombre_fantasia HAVING estado = 1 "+
				"ORDER BY cantClientes desc, t.ppto_nroppto"
				)
				.addScalar("nroppto", Hibernate.LONG)
				.addScalar("nombreFantasia", Hibernate.STRING)
				.addScalar("codigoCliente", Hibernate.LONG)
				.addScalar("cantClientes", Hibernate.LONG)
				.addScalar("estado", Hibernate.INTEGER)
				.setDate(0, startDate)
				.setDate(1, endDate)
				.list();
		
		int longReport = presupuestos.size();
		if(longReport > 10)
			longReport = 10;
		
		InfoMensualCliente[] clientesTop10 = new InfoMensualCliente[longReport];
		for (int i=0; i< longReport;i++){ 
			Object[] row = (Object[]) presupuestos.get(i);
			clientesTop10[i] = new InfoMensualCliente();
			clientesTop10[i].setCodCliente(((Long)row[2]).longValue());
			clientesTop10[i].setNombre((String)row[1]);
			clientesTop10[i].setCantClientes(((Long)row[3]).longValue());
		}
		
		return clientesTop10;
	}
}
