package crm.services.report.manager;

import java.rmi.RemoteException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRMapArrayDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import crm.libraries.report.OrdenFacturacion;
import crm.libraries.report.OrdenFacturacionSalas;
import crm.libraries.report.OrdenFacturacionServicio;
import crm.services.transaction.EstadoEventoManager;
import crm.services.transaction.PresupuestosManager;
import crm.services.util.DateConverter;

public class OFCreator implements ReportService{
	
	private static final Log log = LogFactory.getLog(OFCreator.class);
	
	private static final String REPORT_OF_NAME = "jasper/of.jasper";
	private static final String REPORT_OF_SALAS_NAME = "jasper/of_salas.jasper";
	private static final String REPORT_OF_SERVICIOS_NAME = "jasper/of_servicios.jasper";

	private Date fechaEmision;
	
	private static OFCreator instance;
	
	public static synchronized OFCreator instance() {

		if (instance == null) 
			instance = new OFCreator();

		return instance;
	}
	
	/**
	 * Crea un reporte de orden de facturación.
	 * 
	 * @param nroPpto
	 * @return
	 * @throws RemoteException
	 * @throws JRException
	 */
	public JasperPrint createOFReport(long nroPpto) throws RemoteException, JRException {

		OrdenFacturacion[] ordenesFacturacion = OrdenFacturacionReport.instance().findByNroPpto(nroPpto);
		String subTitle = createOFTitle(nroPpto);
		String title = getEstadoOS(nroPpto);
		
		return createReport(ordenesFacturacion, title, subTitle);
	}
	
	public String getEstadoOS (long nroPpto){
		try{			
			String fechaOSSt = PresupuestosManager.instance().getFechaByNroPptoAndStateAndUser(nroPpto, 
					(Integer.valueOf(EstadoEventoManager.CODIGO_ESTADO_ORDEN_FACTURACION)).intValue());
			Date fechaOS = OFReportDSBuilder.getDatefromString(fechaOSSt);
			if (PresupuestosManager.instance().isPptoCancelado(nroPpto)){
				String fechaCancSt = PresupuestosManager.instance().getFechaByNroPptoAndStateAndUser(nroPpto, 
						(Integer.valueOf(EstadoEventoManager.CODIGO_ESTADO_CANCELADO)).intValue());
				Date fechaCanc = OFReportDSBuilder.getDatefromString(fechaCancSt);
				fechaEmision = fechaCanc; 
				return "Cancelada";
			}			
			else if(PresupuestosManager.instance().isPptoActualizado(nroPpto)){				
				Date fechaAct = getMaximaFechaActualizacion(nroPpto, 
						Integer.valueOf(EstadoEventoManager.CODIGO_ESTADO_ACTUALIZADO).intValue());
				if(fechaOS.before(fechaAct)){
					fechaEmision = fechaAct;
					return "Actualizada";
				}
			} 
			fechaEmision = fechaOS;
			return "Original";
		}
		catch (RemoteException e){
			e.printStackTrace();
			return null;
		}
		
	}
	
	public Date getMaximaFechaActualizacion(long nroPpto, int estado) throws RemoteException{
		String fecha = PresupuestosManager.instance().getMaxFechaByNroPptoAndState(nroPpto, estado);
		return OFReportDSBuilder.getDatefromString(fecha);
	}
	
	/**
	 * Crea un reporte de eventos
	 * 
	 * @param ordenesFacturacion el listado de eventos que se iterará
	 * @param subTitle el titulo que recibirá el reporte, puede ser week o day
	 * @param year el año del reporte
	 * @return
	 * @throws RemoteException
	 * @throws JRException
	 */
	@SuppressWarnings("unchecked")
	public JasperPrint createReport(OrdenFacturacion[] ordenesFacturacion, String title, String subTitle) throws RemoteException, JRException {
		
		// 1- cargar los reporte desde los .jasper			
		JasperReport ofReport = (JasperReport)JRLoader.loadObject(getClass().getResourceAsStream(REPORT_OF_NAME));
		JasperReport ofSalasReport = (JasperReport)JRLoader.loadObject(getClass().getResourceAsStream(REPORT_OF_SALAS_NAME));
		JasperReport ofServiciosReport = (JasperReport)JRLoader.loadObject(getClass().getResourceAsStream(REPORT_OF_SERVICIOS_NAME));		
		
		// 2- create a map of parameters to pass to the report.
		Map parameters = new HashMap();
		parameters.put("SUBREPORT_SALAS",ofSalasReport);
		parameters.put("SUBREPORT_SERVICIOS",ofServiciosReport);
		
		parameters.put("REPORT_TITLE",title);
		parameters.put("REPORT_SUBTITLE", subTitle);
		parameters.put("REPORT_UPDATE",new Date());
		parameters.put("REPORT_IMAGE_URL",getClass().getResource("imagenes/logo-crn_blancoPpto.png").toString());
		parameters.put("REPORT_DATE", fechaEmision);
		
		if (log.isDebugEnabled())
			log.info(OFReportDSBuilder.calcularWeek(new Date()));
		
		// 3- create JasperPrint using fillReport() method
		JasperPrint jasperPrint = JasperFillManager.fillReport(ofReport, parameters, OFReportDSBuilder.toJRMap(ordenesFacturacion));
		
		return jasperPrint;
	}
	
	/**
	 * Crea el titulo para un reporte por week
	 * 
	 * @param week
	 * @param year
	 * @return
	 */
	public String createOFTitle(long nroPpto){
				
		StringBuffer sb = new StringBuffer();
		
		sb.append("ORDEN DE FACTURACION N°: ");
		sb.append(nroPpto);
		
		return sb.toString();
	}
	
	public Date getFechaEmision(){
		return fechaEmision;
	}
	
	public String getStFechaEmision(){
		return DateConverter.convertDateToString(fechaEmision, "yyyy-MM-dd HH:mm:ss");
	}
	
}
/**
 * Prepara el data source a partir de un array de eventos
 * 
 * @author hernux
 */
class OFReportDSBuilder {
	
	private static double totalFacturado;
	
	/**
	 * Crea el datasource para el reporte de eventos
	 * 
	 * @param ordenes
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static JRMapArrayDataSource toJRMap(OrdenFacturacion[] ordenes){
		Object[] data = new Object[ordenes.length];		
		
		for (int i=0;i<ordenes.length;i++) {
			OrdenFacturacion ordenFacturacion = ordenes[i];
			Map map = new HashMap();
			
			map.put("razonSocial",ordenFacturacion.getRazonSocial());
			map.put("nombreEvento",ordenFacturacion.getNombreEvento());
			map.put("numeroOrden",ordenFacturacion.getOrdenFacturacion());
			map.put("lugarEvento",ordenFacturacion.getLugarEvento());
			map.put("fechaInicialEvento",getDatefromString((ordenFacturacion.getFechaInicialEvento())));
			map.put("cantidadSalas",ordenFacturacion.getCantidadSalas());
			map.put("unidad",ordenFacturacion.getUnidad());
			map.put("vendedor",ordenFacturacion.getVendedor());
			map.put("domicilioLugar", ordenFacturacion.getDomicilioLugar());
			map.put("fechaFinalEvento", getDatefromString((ordenFacturacion.getFechaFinalEvento())));
			map.put("telefonosLugar", ordenFacturacion.getTelefonosLugar());
			map.put("contactoCliente", ordenFacturacion.getContactoCliente());
			map.put("nroWeek", calcularWeek(getDatefromString((ordenFacturacion.getFechaInicialEvento()))));
			map.put("razonSocial", ordenFacturacion.getRazonSocial());
			map.put("cuit", ordenFacturacion.getCuit());
			map.put("condIva", ordenFacturacion.getCondIva());
			map.put("domicilioLegal", ordenFacturacion.getDomicilioLegal());
			map.put("domicilioFactura", ordenFacturacion.getDomicilioFactura());
			map.put("condicionPago", ordenFacturacion.getCondicionPago());
			map.put("modoPago", ordenFacturacion.getMedioPago());
			map.put("responsablePago", ordenFacturacion.getResponsablePago());
			map.put("telResponsablePago", ordenFacturacion.getTelefonoRespPago());
			map.put("domicilioPago", ordenFacturacion.getDomicilioPago());
			map.put("diaHoraPago", ordenFacturacion.getDiaHoraPago());
			map.put("codigoProvCrn", ordenFacturacion.getCodProvCrn());
			map.put("nombreFantasia", ordenFacturacion.getNombreFantasia());
			map.put("adelanto", ordenFacturacion.getAdelanto());
			map.put("observaciones", ordenFacturacion.getObservaciones());
			map.put("tipoEvento", ordenFacturacion.getTipoEvento());
			map.put("data_source_salas",toJRMap(ordenFacturacion.getSalas()));
			
			map.put("totalFacturado", totalFacturado);
			
			map.put("subtotalFacturado", totalFacturado-ordenFacturacion.getAdelanto());
			
			totalFacturado = 0.0;
			
			data[i] = map;
		}
		
		return new JRMapArrayDataSource(data);
	}
	
	/**
	 * Crea el datasource para el reporte de salas
	 * 
	 * @param salas
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static JRMapArrayDataSource toJRMap(OrdenFacturacionSalas[] salas){
		Object[] data = new Object[salas.length];
		
		for (int i=0;i<salas.length;i++) {
			OrdenFacturacionSalas sala = salas[i];
			Map map = new HashMap();
			
			map.put("salaId",sala.getSalaId());
			map.put("nombreSala",sala.getNombreSala());
			map.put("fechaInicio",getDatefromString((sala.getFechaInicio())));
			map.put("fechaFin",getDatefromString((sala.getFechaFin())));			
			
			map.put("data_source_servicios", toJRMap(sala.getServicios()));
			
			double totalSala = 0d; 
			for (int j=0;j<sala.getServicios().length;j++) {
				OrdenFacturacionServicio servicio = sala.getServicios()[j];
				totalSala += servicio.getImporte() - (servicio.getImporte()*servicio.getDescuento()/100);
				totalFacturado += servicio.getImporte();
			}
			map.put("totalSala",totalSala);
			data[i] = map;
		}
		
		return new JRMapArrayDataSource(data);
	}
	
	/**
	 * Crea el datasource para el reporte de servicios
	 * 
	 * @param servicios
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static JRMapArrayDataSource toJRMap(OrdenFacturacionServicio[] servicios){
		Object[] data = new Object[servicios.length];
		
		for (int i=0;i<servicios.length;i++) {
			OrdenFacturacionServicio servicio = servicios[i];
			Map map = new HashMap();
			
			map.put("servicioId",servicio.getServicioId());
			map.put("cantidad",servicio.getCantidad());
			map.put("servicio",servicio.getServicio());
			map.put("dias",servicio.getDias());
			map.put("familia",servicio.getFamilia());
			map.put("importe", servicio.getImporte());
			map.put("descuento",servicio.getDescuento());
			data[i] = map;
		}		
		
		return new JRMapArrayDataSource(data);
	}
	
	public static Date getDatefromString(String date){
		Date d = null;
		try {
			d = DateConverter.convertStringToDate(date, "yyyy-MM-dd HH:mm:ss");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return d;
	}
	
	public static long calcularWeek (Date fechaInicio){
		 Calendar cal = Calendar.getInstance();
	        
	        cal.setTime(fechaInicio);
	        cal.setFirstDayOfWeek(Calendar.SUNDAY);
	        cal.setMinimalDaysInFirstWeek(1);
	        return cal.get(Calendar.WEEK_OF_YEAR);
	}
}
