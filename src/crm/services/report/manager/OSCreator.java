package crm.services.report.manager;

import java.rmi.RemoteException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRMapArrayDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import crm.libraries.report.EventoAccesorio;
import crm.libraries.report.EventoOperador;
import crm.libraries.report.EventoProveedor;
import crm.libraries.report.OrdenServicio;
import crm.libraries.report.OrdenServicioSala;
import crm.libraries.report.OrdenServicioServicio;
import crm.services.transaction.EstadoEventoManager;
import crm.services.transaction.PresupuestosManager;
import crm.services.util.DateConverter;

public class OSCreator implements ReportService{	
	private static final String REPORT_OS_NAME = "jasper/os.jasper";
	private static final String REPORT_OS_SALAS_NAME = "jasper/os_salas.jasper";
	private static final String REPORT_OS_SERVICIOS_NAME = "jasper/os_servicios.jasper";
	private static final String REPORT_OS_OPERADORES_NAME = "jasper/eventos_operadores.jasper";
	private static final String REPORT_OS_PROVEEDORES_NAME = "jasper/eventos_proveedores.jasper";
	
	private static OSCreator instance;
	private Date fechaEmision;
	
	public static synchronized OSCreator instance() {

		if (instance == null) 
			instance = new OSCreator();

		return instance;
	}
	
	/**
	 * Crea un reporte por week.
	 * 
	 * @param week
	 * @param year
	 * @return
	 * @throws RemoteException
	 * @throws JRException
	 */
	public JasperPrint createOSReport(long nroPpto) throws RemoteException, JRException {

		OrdenServicio[] eventos = OrdenServicioReport.instance().findByNroPpto(nroPpto);
		String subTitle = createOSTitle(nroPpto);
		String title = getEstadoOS(nroPpto);
		
		return createReport(eventos, title, subTitle);
	}
	
	private String getEstadoOS (long nroPpto){
		try{
			String fechaOSSt = PresupuestosManager.instance().getFechaByNroPptoAndStateAndUser(nroPpto, 
					(Integer.valueOf(EstadoEventoManager.CODIGO_ESTADO_ORDEN_SERVICIO)).intValue());
			Date fechaOS = OSReportDSBuilder.getDatefromString(fechaOSSt);
			if (PresupuestosManager.instance().isPptoCancelado(nroPpto)){
				String fechaCancSt = PresupuestosManager.instance().getFechaByNroPptoAndStateAndUser(nroPpto, 
						(Integer.valueOf(EstadoEventoManager.CODIGO_ESTADO_CANCELADO)).intValue());
				Date fechaCanc = OSReportDSBuilder.getDatefromString(fechaCancSt);
				fechaEmision = fechaCanc; 
				OrdenServicioReport.instance().setEstadoOS("Cancelada");
				return "Cancelada";
			}			
			else if(PresupuestosManager.instance().isPptoActualizado(nroPpto)){				
				Date fechaAct = getMaximaFechaActualizacion(nroPpto, 
						Integer.valueOf(EstadoEventoManager.CODIGO_ESTADO_ACTUALIZADO).intValue());
				if(fechaOS.before(fechaAct)){
					fechaEmision = fechaAct;
					OrdenServicioReport.instance().setEstadoOS("Actualizada");
					return "Actualizada";
				}
			} 
			fechaEmision = fechaOS;
			OrdenServicioReport.instance().setEstadoOS("Original");
			return "Original";
		}
		catch (RemoteException e){
			e.printStackTrace();
			return null;
		}
		
	}
	
	private Date getMaximaFechaActualizacion(long nroPpto, int estado) throws RemoteException{
		String fecha = PresupuestosManager.instance().getMaxFechaByNroPptoAndState(nroPpto, estado);
		return OSReportDSBuilder.getDatefromString(fecha);
	}
	
	/**
	 * Crea un reporte de eventos
	 * 
	 * @param eventos el listado de eventos que se iterará
	 * @param subTitle el titulo que recibirá el reporte, puede ser week o day
	 * @param year el año del reporte
	 * @return
	 * @throws RemoteException
	 * @throws JRException
	 */
	@SuppressWarnings("unchecked")
	private JasperPrint createReport(OrdenServicio[] eventos, String title, String subTitle) throws RemoteException, JRException {

		// 1- cargar los reporte desde los .jasper			
		JasperReport oSReport = (JasperReport)JRLoader.loadObject(getClass().getResourceAsStream(REPORT_OS_NAME));
		JasperReport oSsalasReport = (JasperReport)JRLoader.loadObject(getClass().getResourceAsStream(REPORT_OS_SALAS_NAME));
		JasperReport oSserviciosReport = (JasperReport)JRLoader.loadObject(getClass().getResourceAsStream(REPORT_OS_SERVICIOS_NAME));
		JasperReport oSoperadoresReport = (JasperReport)JRLoader.loadObject(getClass().getResourceAsStream(REPORT_OS_OPERADORES_NAME));
		JasperReport oSproveedoresReport = (JasperReport)JRLoader.loadObject(getClass().getResourceAsStream(REPORT_OS_PROVEEDORES_NAME));
		
		
		// 2- create a map of parameters to pass to the report.
		Map parameters = new HashMap();
		parameters.put("SUBREPORT_SALAS",oSsalasReport);
		parameters.put("SUBREPORT_SERVICIOS",oSserviciosReport);
		parameters.put("SUBREPORT_OPERADORES", oSoperadoresReport);
		parameters.put("SUBREPORT_PROVEEDORES", oSproveedoresReport);
		
		parameters.put("REPORT_TITLE",title);
		parameters.put("REPORT_SUBTITLE", subTitle);
		parameters.put("REPORT_UPDATE",new Date());
		parameters.put("REPORT_IMAGE_URL",getClass().getResource("imagenes/logo-crn_blancoPpto.png").toString());
		parameters.put("REPORT_DATE", fechaEmision);
		
		// 3- create JasperPrint using fillReport() method
		JasperPrint jasperPrint = JasperFillManager.fillReport(oSReport, parameters, OSReportDSBuilder.toJRMap(eventos));
		
		return jasperPrint;
	}
	
	/**
	 * Crea el titulo para un reporte por week
	 * 
	 * @param week
	 * @param year
	 * @return
	 */
	public String createOSTitle(long nroPpto){
				
		StringBuffer sb = new StringBuffer();
		
		sb.append("ORDEN DE SERVICIO N°: ");
		sb.append(nroPpto);
		
		return sb.toString();
	}
	
	public Date getFechaEmision(){
		return fechaEmision;
	}
}

/**
 * Prepara el data source a partir de un array de eventos
 * 
 * @author hernux
 */
class OSReportDSBuilder {
		
		/**
		 * Crea el datasource para el reporte de eventos
		 * 
		 * @param ordenes
		 * @return
		 */
		@SuppressWarnings("unchecked")
		public static JRMapArrayDataSource toJRMap(OrdenServicio[] ordenes){
			Object[] data = new Object[ordenes.length];
			
			for (int i=0;i<ordenes.length;i++) {
				OrdenServicio ordenServicio = ordenes[i];
				Map map = new HashMap();
				
				map.put("nombreFantasia",ordenServicio.getNombreFantasia());
				map.put("nombreEvento",ordenServicio.getNombreEvento());
				map.put("numeroOrden",ordenServicio.getOrdenServicio());
				map.put("lugarEvento",ordenServicio.getLugarEvento());
				map.put("tipoEvento",ordenServicio.getTipoEvento());
				map.put("fechaInicialEvento",getDatefromString((ordenServicio.getFechaInicialEvento())));
				map.put("cantidadSalas",ordenServicio.getCantidadSalas());
				map.put("fechaInstalacion",getDatefromString((ordenServicio.getFechaInstalacion())));
				map.put("unidad",ordenServicio.getUnidad());
				map.put("vendedor",ordenServicio.getVendedor());
				map.put("domicilio", ordenServicio.getDomicilio());			
				map.put("fechaFinalEvento", getDatefromString((ordenServicio.getFechaFinalEvento())));
				map.put("ingresoEquipos", ordenServicio.getIngresoEquipos());
				if(ordenServicio.getObservaciones() == null || ordenServicio.getObservaciones().equals(""))
					map.put("observacionesEvento", "Sin observaciones.");
				else map.put("observacionesEvento", ordenServicio.getObservaciones());
				map.put("responsableEvento", ordenServicio.getResponsableEvento());
				map.put("responsableLugar", ordenServicio.getResponsableLugar());
				map.put("seguridadEquipos", ordenServicio.getSeguridadEquipos());
				map.put("telefonosLugar", ordenServicio.getTelefonosLugar());
				map.put("telefonosRespEvento", ordenServicio.getTelefonosRespEvento());
				map.put("telefonosRespLugar", ordenServicio.getTelefonosRespLugar());
				map.put("tipoLugar", ordenServicio.getTipoLugar());
				map.put("tipoUniforme", ordenServicio.getTipoUniforme());
				map.put("totalPersonas", ordenServicio.getTotalPersonas());
				map.put("nroWeek", calcularWeek(getDatefromString((ordenServicio.getFechaInicialEvento()))));
				map.put("categoria", ordenServicio.getCategoria());
				
				//map.put("data_source_proveedores", toJRMap(ordenServicio.getProveedores()));
				map.put("data_source_salas",toJRMap(ordenServicio.getSalas(),i+1));
				//map.put("data_source_operadores", toJRMap(ordenServicio.getOperadores()));
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
		private static JRMapArrayDataSource toJRMap(OrdenServicioSala[] salas, int numeroSala){
			Object[] data = new Object[salas.length];
			
			for (int i=0;i<salas.length;i++) {
				OrdenServicioSala sala = salas[i];
				Map map = new HashMap();
				
				map.put("salaId",sala.getSalaId());
				map.put("nombreSala",sala.getNombreSala());
				map.put("fechaInicio",getDatefromString((sala.getFechaInicio())));
				map.put("fechaFin",getDatefromString((sala.getFechaFin())));
				map.put("totalPersonas",sala.getTotalPersonas());
				map.put("fechaDesarme", getDatefromString((sala.getFechaDesarme())));
				map.put("fechaPruebas", getDatefromString((sala.getFechaPruebas())));
				map.put("tipoArmado", sala.getTipoArmado());
				map.put("alto", sala.getAlto());
				map.put("ancho", sala.getAncho());
				map.put("largo", sala.getLargo());
				map.put("capacidad", sala.getCapacidad());
				map.put("totalPersonas",sala.getTotalPersonas());
				map.put("observacionesSala", sala.getObservaciones());
				map.put("numeroSala",numeroSala);
				
				map.put("data_source_servicios", toJRMap(sala.getServicios()));
				
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
		private static JRMapArrayDataSource toJRMap(OrdenServicioServicio[] servicios){
			Object[] data = new Object[servicios.length];
			
			for (int i=0;i<servicios.length;i++) {
				OrdenServicioServicio servicio = servicios[i];
				Map map = new HashMap();
				
				map.put("servicioId",servicio.getServicioId());
				map.put("cantidad",servicio.getCantidad());
				map.put("servicio",servicio.getServicio());
				map.put("dias",servicio.getDias());
				map.put("familia",servicio.getFamilia());
				if(servicio.isSubcontratado()){
					map.put("subcontratado", "Si");
				}
				else map.put("subcontratado", "No"); 
				StringBuffer accesorios = new StringBuffer();
				
				for (int j=0;j < servicio.getAccesorios().length;j++){
					EventoAccesorio accesorio = servicio.getAccesorios()[j];
					accesorios.append(accesorio.getCantidad());
					accesorios.append(" ");
					accesorios.append(accesorio.getAccesorio());
					
					if (j+1 < servicio.getAccesorios().length)
						accesorios.append(", ");
				}
				
				map.put("accesorios", accesorios.toString());
				
				data[i] = map;
			}
			
			return new JRMapArrayDataSource(data);
		}
		
		@SuppressWarnings("unchecked")
		private static JRMapArrayDataSource toJRMap(EventoOperador[] operadores){
			Object[] data = new Object[operadores.length];
			
			for (int i=0;i<operadores.length;i++) {
				EventoOperador operador = operadores[i];
				Map map = new HashMap();
				
				map.put("nombreyApellido",operador.getNombreyApellido());
				map.put("puesto",operador.getPuesto());
				
				data[i] = map;
			}
			
			return new JRMapArrayDataSource(data);
		}
		
		@SuppressWarnings("unchecked")
		private static JRMapArrayDataSource toJRMap(EventoProveedor[] proveedores){
			Object[] data = new Object[proveedores.length];
			
			for (int i=0;i<proveedores.length;i++) {
				EventoProveedor proveedor = proveedores[i];
				Map map = new HashMap();
				
				map.put("nombre",proveedor.getProveedor());
				map.put("telefonos",proveedor.getTelefonos());
				map.put("domicilio",proveedor.getDomicilio());		
				
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
		
		private static long calcularWeek (Date fechaInicio){
			 Calendar cal = Calendar.getInstance();
		        
		        cal.setTime(fechaInicio);
		        cal.setFirstDayOfWeek(Calendar.SUNDAY);
		        cal.setMinimalDaysInFirstWeek(1);
		        return cal.get(Calendar.WEEK_OF_YEAR);
		}
}
