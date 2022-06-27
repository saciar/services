package crm.services.report.manager;

import java.rmi.RemoteException;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.JRTextExporter;
import net.sf.jasperreports.engine.export.JRTextExporterParameter;

public class ReportBuilder {
	
	public static final int REPORT_TYPE_WEEK = 1;
	public static final int REPORT_TYPE_DAILY = 2;
	
	public static final int EXPORT_TYPE_PDF = 1;
	public static final int EXPORT_TYPE_HTML = 2;
	public static final int EXPORT_TYPE_RTF = 3;
	public static final int EXPORT_TYPE_TXT = 4;
	
	private static ReportBuilder instance;
	
	public static synchronized ReportBuilder instance() {

			if (instance == null) 
				instance = new ReportBuilder();

		return instance;
	}
	
	/**
	 * Exporta este presupuesto a pdf
	 * 
	 * @param nroPpto numero de presupuesto de la orden de servicio
	 * @param output ruta del archivo de destino
	 */
	public static void exportPresupuestoToPdf(long nroPpto, long idCancelacion, long idHeader, long idFooter, long idInstalacion,
			long idValidez, long idFormaPago, long idCondPago, long idFirma, long idSeguridad, long idPersonal, long idCondReserva, long idTipoPresupuesto, 
			long idPeriodo, long idMoneda, double cotizacion, String output){
		exportPresupuestoToFile(nroPpto, idCancelacion, idHeader, idFooter, idInstalacion,
				idValidez, idFormaPago, idCondPago, idFirma, idSeguridad, idPersonal, idCondReserva, idTipoPresupuesto, idPeriodo, idMoneda, cotizacion, output, EXPORT_TYPE_PDF);
	}
	
	/**
	 * Exporta esta orden de servicio a html
	 * 
	 * @param nroPpto numero de presupuesto de la orden de servicio
	 * @param output ruta del archivo de destino
	 */
	public static void exportPresupuestoToHtml(long nroPpto, long idCancelacion, long idHeader, long idFooter, long idInstalacion,
			long idValidez, long idFormaPago, long idCondPago, long idFirma, long idSeguridad, long idPersonal, long idCondReserva, long idTipoPresupuesto, 
			long idPeriodo, long idMoneda, double cotizacion, String output){
		exportPresupuestoToFile(nroPpto, idCancelacion, idHeader, idFooter, idInstalacion,
				idValidez, idFormaPago, idCondPago, idFirma, idSeguridad, idPersonal, idCondReserva, idTipoPresupuesto, idPeriodo, idMoneda, cotizacion, output, EXPORT_TYPE_HTML);
	}
	
	/**
	 * Exporta esta orden de servicio a  rtf
	 * 
	 * @param nroPpto numero de presupuesto de la orden de servicio
	 * @param output ruta del archivo de destino
	 */
	public static void exportPresupuestoToRtf(long nroPpto, long idCancelacion, long idHeader, long idFooter, long idInstalacion,
			long idValidez, long idFormaPago, long idCondPago, long idFirma, long idSeguridad, long idPersonal, long idCondReserva, long idTipoPresupuesto, 
			long idPeriodo, long idMoneda, double cotizacion, String output){
		exportPresupuestoToFile(nroPpto, idCancelacion, idHeader, idFooter, idInstalacion,
				idValidez, idFormaPago, idCondPago, idFirma, idSeguridad, idPersonal, idCondReserva, idTipoPresupuesto, idPeriodo, idMoneda, cotizacion, output, EXPORT_TYPE_TXT);
	}
	
	/**
	 * Exporta un presupuesto al tipo de archivo indicado
	 * 
	 * @param nroPpto numero de presupuesto de la orden de servicio
	 * @param output ruta del archivo de destino
	 * @param type tipo de archivo de destino, puede ser pdf, html o rtf
	 */
	public static void exportPresupuestoToFile(long nroPpto, long idCancelacion, long idHeader, long idFooter, long idInstalacion,
			long idValidez, long idFormaPago, long idCondPago, long idFirma, long idSeguridad, long idPersonal, long idCondReserva, long idTipoPresupuesto, 
			long idPeriodo, long idMoneda, double cotizacion, String output, int type){
		JasperPrint jp = createPresupuestoReport(nroPpto, idCancelacion, idHeader, idFooter, idInstalacion,
				idValidez, idFormaPago, idCondPago, idFirma, idSeguridad, idPersonal, idCondReserva, idTipoPresupuesto, idPeriodo, idMoneda, cotizacion);
		exportReportToFile(jp, output, type);
	}
	
	/**
	 * Exporta esta orden de servicio a pdf
	 * 
	 * @param nroPpto numero de presupuesto de la orden de servicio
	 * @param output ruta del archivo de destino
	 */
	public static void exportOSDesconfirmadaToPdf(long nroPpto, String output){
		exportOSDesconfirmadaToFile(nroPpto, output, EXPORT_TYPE_PDF);
	}
	
	/**
	 * Exporta esta orden de servicio a pdf
	 * 
	 * @param nroPpto numero de presupuesto de la orden de servicio
	 * @param output ruta del archivo de destino
	 */
	public static void exportOSToPdf(long nroPpto, String output){
		exportOSToFile(nroPpto, output, EXPORT_TYPE_PDF);
	}
	
	/**
	 * Exporta esta orden de servicio a html
	 * 
	 * @param nroPpto numero de presupuesto de la orden de servicio
	 * @param output ruta del archivo de destino
	 */
	public static void exportOSToHtml(long nroPpto, String output){
		exportOSToFile(nroPpto, output, EXPORT_TYPE_HTML);
	}
	
	/**
	 * Exporta esta orden de servicio a  rtf
	 * 
	 * @param nroPpto numero de presupuesto de la orden de servicio
	 * @param output ruta del archivo de destino
	 */
	public static void exportOSToRtf(long nroPpto, String output){
		exportOSToFile(nroPpto, output, EXPORT_TYPE_RTF);
	}
	
	/**
	 * Exporta una orden de servicio al tipo de archivo indicado
	 * 
	 * @param nroPpto numero de presupuesto de la orden de servicio
	 * @param output ruta del archivo de destino
	 * @param type tipo de archivo de destino, puede ser pdf, html o rtf
	 */
	public static void exportOSToFile(long nroPpto, String output, int type){
		JasperPrint jp = createOSReport(nroPpto);
		exportReportToFile(jp, output, type);
	}
	
	public static void exportInformeOSToPdf(long nroPpto, String output){
		JasperPrint jp = createInformeReport(nroPpto);
		exportReportToFile(jp, output, EXPORT_TYPE_PDF);
	}
	
	/**
	 * Exporta una orden de servicio al tipo de archivo indicado
	 * 
	 * @param nroPpto numero de presupuesto de la orden de servicio
	 * @param output ruta del archivo de destino
	 * @param type tipo de archivo de destino, puede ser pdf, html o rtf
	 */
	public static void exportOSDesconfirmadaToFile(long nroPpto, String output, int type){
		JasperPrint jp = createOSDesconfirmadaReport(nroPpto);
		exportReportToFile(jp, output, type);
	}
	
	/**
	 * Exporta este diario a pdf
	 * 
	 * @param day dia del mes para el reporte
	 * @param month mes del a�o
	 * @param year a�o del reporte
	 * @param output ruta del archivo de destino
	 */
	public static void exportDailyToPdf(int day, int month, int year, String output){
		exportDailyToFile(day, month,year, output, EXPORT_TYPE_PDF);
	}
	
	/**
	 * Exporta este diario a html
	 * 
	 * @param day dia del mes para el reporte
	 * @param month mes del a�o
	 * @param year a�o del reporte
	 * @param output ruta del archivo de destino
	 */
	public static void exportDailyToHtml(int day, int month, int year, String output){
		exportDailyToFile(day, month,year, output, EXPORT_TYPE_HTML);
	}
	
	/**
	 * Exporta este diario a  rtf
	 * 
	 * @param day dia del mes para el reporte
	 * @param month mes del a�o
	 * @param year a�o del reporte
	 * @param output ruta del archivo de destino
	 */
	public static void exportDailyToRtf(int day, int month, int year, String output){
		exportDailyToFile(day, month,year, output, EXPORT_TYPE_RTF);
	}
	
	/**
	 * Exporta un reporte diario al tipo de archivo indicado
	 * 
	 * @param day dia del mes para el reporte
	 * @param month mes del a�o
	 * @param year a�o del reporte
	 * @param output ruta del archivo de destino
	 * @param type tipo de archivo de destino, puede ser pdf, html o rtf
	 */
	public static void exportDailyToFile(int day, int month, int year, String output, int type){
		JasperPrint jp = createDailyReport(day, month, year);
		exportReportToFile(jp, output, type);
	}
	
	/**
	 * Exporta este week a pdf
	 * 
	 * @param week semana del a�o de este reporte
	 * @param year a�o del reporte
	 * @param output ruta del archivo de destino
	 */
	public static void exportWeekToPdf(int week, int year, String output){
		exportWeekToFile(week,year, output, EXPORT_TYPE_PDF);
	}
	
	/**
	 * Exporta este week a html
	 * 
	 * @param week semana del a�o de este reporte
	 * @param year a�o del reporte
	 * @param output ruta del archivo de destino
	 */
	public static void exportWeekToHtml(int week, int year, String output){
		exportWeekToFile(week,year, output, EXPORT_TYPE_HTML);
	}
	
	/**
	 * Exporta este week a rtf
	 * 
	 * @param week semana del a�o de este reporte
	 * @param year a�o del reporte
	 * @param output ruta del archivo de destino
	 */
	public static void exportWeekToRtf(int week, int year, String output){
		exportWeekToFile(week,year, output, EXPORT_TYPE_RTF);
	}
	
	/**
	 * Exporta un reporte semanal al tipo de archivo indicado
	 * 
	 * @param week semana del a�o de este reporte
	 * @param year a�o del reporte
	 * @param output ruta del archivo de destino
	 * @param type tipo de archivo de destino, puede ser pdf, html o rtf
	 */
	public static void exportWeekToFile(int week, int year, String output, int type){
		JasperPrint jp = createWeekReport(week, year);
		exportReportToFile(jp, output, type);
	}
	
	/**
	 * Exporta esta orden de facturacion a pdf
	 * 
	 * @param nroPpto numero de presupuesto de la orden de facturacion
	 * @param output ruta del archivo de destino
	 */
	public static void exportOCToPdf(long nroPpto, String output){
		exportOCToFile(nroPpto, output, EXPORT_TYPE_PDF);
	}
	
	/**
	 * Exporta esta orden de facturacion a html
	 * 
	 * @param nroPpto numero de presupuesto de la orden de facturacion
	 * @param output ruta del archivo de destino
	 */
	public static void exportOCToHtml(long nroPpto, String output){
		exportOCToFile(nroPpto, output, EXPORT_TYPE_HTML);
	}
	
	/**
	 * Exporta esta orden de facturacion a  rtf
	 * 
	 * @param nroPpto numero de presupuesto de la orden de facturacion
	 * @param output ruta del archivo de destino
	 */
	public static void exportOCToRtf(long nroPpto, String output){
		exportOCToFile(nroPpto, output, EXPORT_TYPE_RTF);
	}
	
	/**
	 * Exporta esta orden de facturacion a pdf
	 * 
	 * @param nroPpto numero de presupuesto de la orden de facturacion
	 * @param output ruta del archivo de destino
	 */	
	public static void exportOCToFile(long nroPpto, String output, int type){
		JasperPrint jp = createOCReport(nroPpto);
		exportReportToFile(jp, output, type);
	}
	
	/**
	 * Exporta esta orden de facturacion a pdf
	 * 
	 * @param nroPpto numero de presupuesto de la orden de facturacion
	 * @param output ruta del archivo de destino
	 */
	public static void exportOFToPdf(long nroPpto, String output){
		exportOFToFile(nroPpto, output, EXPORT_TYPE_PDF);
	}
	
	/**
	 * Exporta esta orden de facturacion a html
	 * 
	 * @param nroPpto numero de presupuesto de la orden de facturacion
	 * @param output ruta del archivo de destino
	 */
	public static void exportOFToHtml(long nroPpto, String output){
		exportOFToFile(nroPpto, output, EXPORT_TYPE_HTML);
	}
	
	/**
	 * Exporta esta orden de facturacion a  rtf
	 * 
	 * @param nroPpto numero de presupuesto de la orden de facturacion
	 * @param output ruta del archivo de destino
	 */
	public static void exportOFToRtf(long nroPpto, String output){
		exportOFToFile(nroPpto, output, EXPORT_TYPE_RTF);
	}
	
	/**
	 * Exporta esta orden de facturacion a pdf
	 * 
	 * @param nroPpto numero de presupuesto de la orden de facturacion
	 * @param output ruta del archivo de destino
	 */	
	public static void exportOFToFile(long nroPpto, String output, int type){
		JasperPrint jp = createOFReport(nroPpto);
		exportReportToFile(jp, output, type);
	}
	
	/**
	 * Exporta esta orden de facturacion a pdf
	 * 
	 * @param nroPpto numero de presupuesto de la orden de facturacion
	 * @param output ruta del archivo de destino
	 */
	public static void exportOFAdelantoToPdf(long nroPpto, String output){
		exportOFAdelantoToFile(nroPpto, output, EXPORT_TYPE_PDF);
	}
	
	/**
	 * Exporta esta orden de facturacion a html
	 * 
	 * @param nroPpto numero de presupuesto de la orden de facturacion
	 * @param output ruta del archivo de destino
	 */
	public static void exportOFAdelantoToHtml(long nroPpto, String output){
		exportOFAdelantoToFile(nroPpto, output, EXPORT_TYPE_HTML);
	}
	
	/**
	 * Exporta esta orden de facturacion a  rtf
	 * 
	 * @param nroPpto numero de presupuesto de la orden de facturacion
	 * @param output ruta del archivo de destino
	 */
	public static void exportOFAdelantoToRtf(long nroPpto, String output){
		exportOFAdelantoToFile(nroPpto, output, EXPORT_TYPE_RTF);
	}
	
	/**
	 * Exporta esta orden de facturacion a pdf
	 * 
	 * @param nroPpto numero de presupuesto de la orden de facturacion
	 * @param output ruta del archivo de destino
	 */	
	public static void exportOFAdelantoToFile(long nroPpto, String output, int type){
		JasperPrint jp = createOFAdelantoReport(nroPpto);
		exportReportToFile(jp, output, type);
	}
	
	/**
	 * Crea un reporte por una lista de precios de servicios
	 * 
	 * @param codlugar codigo del lugar para ver si otroga descuento
	 * @return JasperPrint con el reporte creado
	 */
	public static JasperPrint createListaReport(int mes){
		try {			
			return ListaPreciosCreator.instance().createListaPreciosReport(mes);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (JRException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Crea un reporte por una orden de facturacion
	 * 
	 * @param nroPpto numero de presupuesto de la orden de facturacion
	 * @return JasperPrint con el reporte creado
	 */
	public static JasperPrint createOCReport(long nroPpto){
		try {
			return OCCreator.instance().createOCReport(nroPpto);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (JRException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Crea un reporte por una orden de facturacion
	 * 
	 * @param nroPpto numero de presupuesto de la orden de facturacion
	 * @return JasperPrint con el reporte creado
	 */
	public static JasperPrint createOFReport(long nroPpto){
		try {
			return OFCreator.instance().createOFReport(nroPpto);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (JRException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Crea un reporte por una orden de facturacion dela delanto
	 * 
	 * @param nroPpto numero de presupuesto de la orden de facturacion del adelanto
	 * @return JasperPrint con el reporte creado
	 */
	public static JasperPrint createOFAdelantoReport(long nroPpto){
		try {
			return AdelantoCreator.instance().createOFReport(nroPpto);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (JRException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Crea un reporte por una orden de servicio
	 * 
	 * @param nroPpto numero de presupuesto de la oreden de servicio
	 * @return JasperPrint con el reporte creado
	 */
	public static JasperPrint createOSReport(long nroPpto){
		try {			
			return OSCreator.instance().createOSReport(nroPpto);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (JRException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static JasperPrint createInformeReport (long nroPpto){
		try{
			return InformeCreator.instance().createOSReport(nroPpto);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (JRException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Crea un reporte por una orden de servicio
	 * 
	 * @param nroPpto numero de presupuesto de la oreden de servicio
	 * @return JasperPrint con el reporte creado
	 */
	public static JasperPrint createOSDesconfirmadaReport(long nroPpto){
		try {			
			return OsDesconfirmadaCreator.instance().createOSReport(nroPpto);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (JRException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Crea un reporte por un presupuesto
	 * 
	 * @param idCancelacion codigo de cancelacion del presupuesto
	 * @param idHeader codigo de header del reporte
	 * @param idFooter codigo de footer del reporte
	 * @param idInstalacion codigo de instalacion
	 * @param idValidez codigo de validez del presupuesto
	 * @param idFormaPago codigo de forma de pago
	 * @param nroPpto numero de presupuesto de la orden de facturacion
	 * @return JasperPrint con el reporte creado
	 */
	public static JasperPrint createPresupuestoReport(long nroPpto, long idCancelacion, long idHeader, long idFooter, long idInstalacion,
			long idValidez, long idFormaPago, long idCondPago, long idFirma, long idSeguridad, long idPersonal, long idCondReserva, long idTipoPresupuesto, 
			long idPeriodo, long idMoneda, double cotizacion){
		try {
			return PresupuestoCreator.instance().createPresupuestoReport(nroPpto, idCancelacion, idHeader, idFooter, idInstalacion,
					idValidez, idFormaPago, idCondPago, idFirma, idSeguridad, idPersonal, idCondReserva, idTipoPresupuesto, idPeriodo, idMoneda, cotizacion);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (JRException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Crea un reporte por week.
	 * 
	 * @param week semana del a�o de este reporte
	 * @param year a�o del reporte
	 * @return JasperPrint con el reporte creado
	 */
	public static JasperPrint createWeekReport(int week, int year) {
		/*try {
			return EventReport.instance().createWeekReport(week, year);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (JRException e) {
			e.printStackTrace();
		}
		*/
		return null;
	}
	
	/**
	 * Crea un reporte diario
	 * 
	 * @param day dia del mes para el reporte
	 * @param month mes del a�o
	 * @param year a�o del reporte
	 * @return JasperPrint con el reporte creado
	 * @throws RemoteException
	 * @throws JRException
	 */
	public static JasperPrint createDailyReport(int day, int month, int year) {
		/*try {
			return EventReport.instance().createDailyReport(day, month, year);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (JRException e) {
			e.printStackTrace();
		}*/
		
		return null;
	}
	
	/**
	 * Exporta un reporte a archivo utilizando el tipo indicado por el usuario.
	 * 
	 * @param jp el reporte que se va a exportar
	 * @param output ruta del archivo de destino
	 * @param type tipo de archivo de destino, puede ser pdf, html o rtf
	 */
	public static void exportReportToFile(JasperPrint jp, String output, int type){
		
		try {
			switch(type){
			case EXPORT_TYPE_PDF:
				JasperExportManager.exportReportToPdfFile(jp, output);
				break;
			case EXPORT_TYPE_HTML:
				JasperExportManager.exportReportToHtmlFile(jp, output);
				break;
			case EXPORT_TYPE_RTF:
				exportReportToRtfFile(jp, output);
				break;
			case EXPORT_TYPE_TXT:
				exportReportToTxtFile(jp, output);
				break;
			default:
				throw new RuntimeException("Tipo de export desconocido, no se que hacer");
			}
		} 
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Exporta a RTF, utilizando el mismo sistema que el Facade JasperExportManager
	 * 
	 * @param jasperPrint el reporte que se va a exportar
	 * @param destFileName ruta del archivo de destino
	 * @throws JRException  
	 */
	private static void exportReportToTxtFile(JasperPrint jasperPrint, String destFileName) throws JRException {
		JRTextExporter exporter = new JRTextExporter();
		
		exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
		exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, destFileName);
		/*exporter.setParameter(JRTextExporterParameter.PAGE_WIDTH, new Integer(80));
		exporter.setParameter(JRTextExporterParameter.PAGE_HEIGHT, new Integer(792));
        exporter.setParameter(JRTextExporterParameter.CHARACTER_WIDTH, new Integer(3));
        exporter.setParameter(JRTextExporterParameter.CHARACTER_HEIGHT, new Integer(10));*/
		exporter.setParameter(JRTextExporterParameter.CHARACTER_WIDTH, new Integer(5));
		exporter.setParameter(JRTextExporterParameter.CHARACTER_HEIGHT, new Integer(10));
		exporter.exportReport();
	}

	
	/**
	 * Exporta a RTF, utilizando el mismo sistema que el Facade JasperExportManager
	 * 
	 * @param jasperPrint el reporte que se va a exportar
	 * @param destFileName ruta del archivo de destino
	 * @throws JRException  
	 */
	private static void exportReportToRtfFile(JasperPrint jasperPrint, String destFileName) throws JRException {
		JRRtfExporter exporter = new JRRtfExporter();
		
		exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
		exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, destFileName);
		
		exporter.exportReport();
	}

}
