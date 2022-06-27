package crm.services.report.manager;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.hibernate.Session;

import crm.libraries.abm.entities.Usuario;
import crm.libraries.report.EventoAccesorio;
import crm.libraries.report.OrdenServiciosDesconfirmada;
import crm.libraries.report.OrdenServiciosDesconfirmadaSala;
import crm.libraries.report.OrdenServiciosDesconfirmadaServicios;
import crm.services.mail.EmailAddressException;
import crm.services.mail.EmailNameException;
import crm.services.mail.MailMessage;
import crm.services.mail.SendMailException;
import crm.services.mail.SmtpSender;
import crm.services.report.sei.OsDesconfirmadaReportSEI;
import crm.services.sei.ModalidadContratManagerSEI;
import crm.services.transaction.LugarEventoManager;
import crm.services.transaction.PresupuestosManager;
import crm.services.transaction.UsuarioManager;
import crm.services.util.HibernateUtil;
import crm.services.util.StringConverter;

public class OsDesconfirmadaReport implements OsDesconfirmadaReportSEI,ReportService {

	private static final long SYS_SETTINGS_EMAIL_GERENCIA = 9;

	private static OsDesconfirmadaReport instance;
	private static final Log log = LogFactory.getLog(OsDesconfirmadaReport.class);
	
	private static String estado;
	
	public static synchronized OsDesconfirmadaReport instance() {

			if (instance == null) 
				instance = new OsDesconfirmadaReport();

		return instance;
	}
	
	public OrdenServiciosDesconfirmada[] findByNroPpto(long nroPpto) throws RemoteException{
		//"SELECT * FROM VW_RPT_WEEK where fechaEvento >= '2006-04-23' and fechaEvento <= '2006-04-25'";
		Session session = HibernateUtil.abrirSession();
		
		List list = session.createSQLQuery
				(
				"select nombre_fantasia, nombreEvento, numeroPresupuesto, lugarEvento, domicilio, telefonos, responsableLugar, telefonosResposable, " +
				"tipoEvento, fechaInicialEvento, fechaFinalEvento, cantidadSalas, " +
				"fechaInstalacion, un_comercial, vendedor, responsableEvento, responsableEventoTel, modoIngresoEquipos, seguridadEquipos, " +
				"tipoUniforme, tipoLugar, observacionesEvento, totalPersonas, fechaEmision, codigoSala " +
				"from VW_RPT_ORDSERVICIO_GENERAL_DES " +
				"where numeroPresupuesto = ? " +
				"order by fechaInicialEvento"
				)
				.addScalar("nombre_fantasia",Hibernate.STRING)
				.addScalar("nombreEvento",Hibernate.STRING)
				.addScalar("numeroPresupuesto",Hibernate.LONG)
				.addScalar("lugarEvento",Hibernate.STRING)
				.addScalar("domicilio",Hibernate.STRING)
				.addScalar("telefonos",Hibernate.STRING)
				.addScalar("responsableLugar",Hibernate.STRING)
				.addScalar("telefonosResposable",Hibernate.STRING)
				.addScalar("tipoEvento",Hibernate.STRING)
				.addScalar("fechaInicialEvento",Hibernate.TIMESTAMP)
				.addScalar("fechaFinalEvento", Hibernate.TIMESTAMP)
				.addScalar("cantidadSalas",Hibernate.LONG)
				.addScalar("fechaInstalacion",Hibernate.TIMESTAMP)
				.addScalar("un_comercial",Hibernate.STRING)
				.addScalar("vendedor",Hibernate.STRING)
				.addScalar("responsableEvento", Hibernate.STRING)
				.addScalar("responsableEventoTel", Hibernate.STRING)
				.addScalar("modoIngresoEquipos", Hibernate.STRING)
				.addScalar("seguridadEquipos", Hibernate.STRING)
				.addScalar("tipoUniforme", Hibernate.STRING)
				.addScalar("tipoLugar", Hibernate.STRING)
				.addScalar("observacionesEvento", Hibernate.STRING)
				.addScalar("totalPersonas", Hibernate.LONG)	
				.addScalar("fechaEmision",Hibernate.TIMESTAMP)
				.addScalar("codigoSala", Hibernate.LONG)
				.setLong(0,nroPpto)
				.list();
				
		OrdenServiciosDesconfirmada[] results = new OrdenServiciosDesconfirmada[list.size()];
		for (int i=0; i< results.length;i++){ 
			results[i] = new OrdenServiciosDesconfirmada();
			Object[] row = (Object[]) list.get(i);
			
			results[i].setNombreFantasia((String)row[0]);
			results[i].setNombreEvento((String)row[1]);
			results[i].setOrdenServicio((Long)row[2]);
			results[i].setLugarEvento((String)row[3]);
			results[i].setDomicilio((String)row[4]);
			results[i].setTelefonosLugar((String)row[5]);
			results[i].setResponsableLugar((String)row[6]);
			results[i].setTelefonosRespLugar((String)row[7]);			
			results[i].setTipoEvento((String)row[8]);
	        results[i].setFechaInicialEvento(((Timestamp)row[9]).toString());
			results[i].setFechaFinalEvento(((Timestamp)row[10]).toString());
			results[i].setCantidadSalas(((Long)row[11]).longValue());

			results[i].setFechaInstalacion(((Timestamp)row[12]).toString());
			results[i].setUnidad((String)row[13]);
			results[i].setVendedor((String)row[14]);
			results[i].setResponsableEvento((String)row[15]);
			results[i].setTelefonosRespEvento((String)row[16]);
			results[i].setIngresoEquipos((String)row[17]);
			results[i].setSeguridadEquipos((String)row[18]);
			results[i].setTipoUniforme((String)row[19]);
			results[i].setTipoLugar((String)row[20]);
			results[i].setObservaciones((String)row[21]);
			results[i].setTotalPersonas(((Long)row[22]).longValue());
			results[i].setFechaEmision(((Timestamp)row[23]).toString());
			
			//results[i].setProveedores(getProveedoresByNroPpto(session,nroPpto));
			
			results[i].setSalas(findSalasByPresupuesto(session,((Long)row[24]).longValue(), nroPpto));
			
			//results[i].setOperadores(findOperadoresbyPresupuesto(session,results[i].getOrdenServicio()));
		}
		
		HibernateUtil.cerrarSession(session);
		
		return results;
	}

	private OrdenServiciosDesconfirmadaSala[] findSalasByPresupuesto(Session session,long codigoSala, long nroPpto) {
		//"SELECT * FROM VW_RPT_WEEK where fechaEvento >= '2006-04-23' and fechaEvento <= '2006-04-25'";

		
		List list = session.createSQLQuery
				(
				/*"select s.ppto_s_id as sala_id, sl.els_descripcion as nombre_sala, s.ppto_s_fecinicio as fecha_inicio, "+ 
				"s.ppto_s_fecfin as fecha_fin, s.ppto_s_totpersonas as total_personas, "+
				"psa.pas_fechora_pruebas as fecha_pruebas, psa.pas_fechora_desarme as fecha_desarme, ta.ta_descripcion as tipo_armado, " +
				"s.ppto_s_observaciones as observaciones_sala, sl.els_largo as largo, sl.els_ancho as ancho, sl.els_altura as alto, " +
				"sl.els_capacidad as capacidad " + 
				"from TX_PPTO_SALAS s "+
				"inner join MST_EVT_LUGAR_SALAS sl on s.ppto_s_codlugsala = sl.els_codlugsala " +
				"inner join TX_PPTO_SALAS_AGREGADOS psa on s.ppto_s_id = psa.pas_sala_id " +
				"inner join MST_TIPO_ARMADO ta on psa.pas_tipo_armado = ta.ta_id " +				
				"where s.ppto_s_codlugsala = ? and s.ppto_s_nroppto = ? " +
				"order by s.ppto_s_id "*/
				"select s.ppto_s_id as sala_id, sl.els_descripcion as nombre_sala, s.ppto_s_fecinicio as fecha_inicio, "+ 
				"s.ppto_s_fecfin as fecha_fin, s.ppto_s_totpersonas as total_personas, "+
				"ppto_s_fechora_prueba as fecha_pruebas, ppto_s_fechora_desarme as fecha_desarme, ta.ta_descripcion as tipo_armado, " +
				"s.ppto_s_observaciones as observaciones_sala, sl.els_largo as largo, sl.els_ancho as ancho, sl.els_altura as alto, " +
				"sl.els_capacidad as capacidad " + 
				"from TX_PPTO_SALAS s "+
				"inner join MST_EVT_LUGAR_SALAS sl on s.ppto_s_codlugsala = sl.els_codlugsala " +
				"inner join MST_TIPO_ARMADO ta on ppto_s_tipo_armado = ta.ta_id " +				
				"where s.ppto_s_codlugsala = ? and s.ppto_s_nroppto = ? " +
				"order by s.ppto_s_id "
				)
				.addScalar("sala_id",Hibernate.LONG)
				.addScalar("nombre_sala",Hibernate.STRING)
				.addScalar("fecha_inicio",Hibernate.TIMESTAMP)
				.addScalar("fecha_fin",Hibernate.TIMESTAMP)
				.addScalar("total_personas",Hibernate.LONG)
				.addScalar("fecha_pruebas", Hibernate.TIMESTAMP)
				.addScalar("fecha_desarme", Hibernate.TIMESTAMP)
				.addScalar("tipo_armado", Hibernate.STRING)
				.addScalar("observaciones_sala", Hibernate.STRING)
				.addScalar("largo", Hibernate.DOUBLE)
				.addScalar("ancho", Hibernate.DOUBLE)
				.addScalar("alto", Hibernate.DOUBLE)
				.addScalar("capacidad", Hibernate.LONG)				
				.setLong(0,new Long(codigoSala))
				.setLong(1,new Long(nroPpto))
				.list();

		OrdenServiciosDesconfirmadaSala[] results = new OrdenServiciosDesconfirmadaSala[list.size()];
		for (int i=0; i< results.length;i++){ 
			results[i] = new OrdenServiciosDesconfirmadaSala();
			Object[] row = (Object[]) list.get(i);
			
			results[i].setSalaId(((Long)row[0]).longValue());
			results[i].setNombreSala((String)row[1]);
			results[i].setFechaInicio(((Timestamp)row[2]).toString());
			results[i].setFechaFin(((Timestamp)row[3]).toString());
			results[i].setTotalPersonas(((Long)row[4]).longValue());
			results[i].setFechaPruebas(((Timestamp)row[5]).toString());
			results[i].setFechaDesarme(((Timestamp)row[6]).toString());
			results[i].setTipoArmado((String)row[7]);
			results[i].setObservaciones((String)row[8]);
			if(row[9] != null)
				results[i].setLargo(((Double)row[9]).doubleValue());
			else 
				results[i].setLargo(0d);
			if(row[10] != null)
				results[i].setAncho(((Double)row[10]).doubleValue());
			else 
				results[i].setAncho(0d);
			if(row[11] != null)
				results[i].setAlto(((Double)row[11]).doubleValue());
			else 
				results[i].setAlto(0d);
			if(row[12] != null)
				results[i].setCapacidad(((Long)row[12]).longValue());		
			else
				results[i].setCapacidad(0);
			
			results[i].setServicios(findServiciosBySala(session,results[i].getSalaId(), nroPpto));
		}
		
		return results;
	}
	
	private OrdenServiciosDesconfirmadaServicios[] findServiciosBySala(Session session,long num_sala, long nroPpto) {
		//"SELECT * FROM VW_RPT_WEEK where fechaEvento >= '2006-04-23' and fechaEvento <= '2006-04-25'";

		
		List list = session.createSQLQuery
				(
				"select ss.ppto_ss_servicio as id,ss.ppto_ss_cantidad  as cantidad, servi.si_descripcion_abreviada as servicio, "+
				"ss.ppto_ss_dias as dias , famserv.DESCFAMILIA as familia, ss.ppto_ss_detalle as detalle "+
				"from TX_PPTO_SALAS_SERVICIOS ss "+
				"inner join MST_SERVICIOS serv on ss.ppto_ss_servicio = serv.se_codservicio "+
				"inner join MST_SERVICIOS_IDIOMA servi on serv.se_codservicio = servi.si_codservicio "+
				"inner join VW_FAM_SERVICIOS_IDIOMA famserv on serv.se_codservicio = famserv.SERVICIO "+
				"where ss.ppto_ss_pls = ? and (ss.ppto_ss_modalidad = ? or ss.ppto_ss_modalidad = ?) " +
				"ORDER BY ss.ppto_ss_orden"
				)
				.addScalar("id",Hibernate.LONG)
				.addScalar("cantidad",Hibernate.LONG)
				.addScalar("servicio",Hibernate.STRING)
				.addScalar("dias",Hibernate.INTEGER)
				.addScalar("familia", Hibernate.STRING)
				.addScalar("detalle", Hibernate.STRING)
				.setLong(0,new Long(num_sala))
				.setInteger(1,Integer.parseInt(ModalidadContratManagerSEI.MODALIDAD_INTERNO))
				.setInteger(2,Integer.parseInt(ModalidadContratManagerSEI.MODALIDAD_EXTERNO))
				.list();

		OrdenServiciosDesconfirmadaServicios[] results = new OrdenServiciosDesconfirmadaServicios[list.size()];
		for (int i=0; i< results.length;i++){ 
			results[i] = new OrdenServiciosDesconfirmadaServicios();
			Object[] row = (Object[]) list.get(i);
			
			results[i].setServicioId(((Long)row[0]).longValue());
			results[i].setCantidad(((Long)row[1]).longValue());
			if((Long)row[0]==1)
				results[i].setServicio((String)row[5]);
			else results[i].setServicio((String)row[2]);
			results[i].setDias(((Integer)row[3]).intValue());
			results[i].setFamilia((String)row[4]);
			if(((Long)row[0]).longValue() == 1){
				results[i].setSubcontratado(true);
			}
			else {
				results[i].setSubcontratado(false);
			}
			
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

	public boolean sendOSByEmail2(long nroPpto, String fechaInicial, String fechaFinal, String evento, String usuarioId, String codLugar, String emailDestino) throws RemoteException{
		try {
			MailMessage mailMessage = new MailMessage();
			MailMessage mailMessageGerencia = new MailMessage();
			Usuario u = UsuarioManager.instance().getUsuarioById(usuarioId);
			String lugarNombre = LugarEventoManager.instance().getNombreLugarOSById(codLugar);
			String lugarMail = LugarEventoManager.instance().getEmailOSById(codLugar);
			mailMessage.setFromAddress(u.getEmail(), u.getApellidoYNombre());
			mailMessageGerencia.setFromAddress(u.getEmail(), u.getApellidoYNombre());
			//UnidadComercial unidad = UnidadComercialManager.instance().getUCDataByCodigoUsuario(usuarioId);
			
			if(lugarMail!= null && !lugarMail.equals("")){	
				mailMessage.setToAddress(new String[]{emailDestino,lugarMail},new String[]{"Cold",lugarNombre});
			}
			else
				mailMessage.setToAddress(new String[]{emailDestino},new String[]{"Cold"});
			mailMessageGerencia.setToAddress(new String[]{getEmailOfSysSettingById(SYS_SETTINGS_EMAIL_GERENCIA)}, new String []{"Cold"});			
			mailMessage.setBccAddress(new String[]{ReportService.EMAIL_CRM_CONTROL}, new String []{"Cold"});
			
			mailMessage.setSubject("Orden de servicio Desconfirmada Nro"+nroPpto+" "+estado);
			mailMessage.setFilePath(PDF_LOCATION + "ordenServDesconfirmada_" + nroPpto + ".pdf");
			mailMessage.setFileName("ordenServDesconfirmada_" + nroPpto + ".pdf");			
			mailMessage.setHtmlBody(getHTMLBody(nroPpto, fechaInicial, fechaFinal, evento, lugarNombre));
			
			mailMessageGerencia.setSubject("Orden de servicio Desconfirmada Nro"+nroPpto+" "+estado);
			mailMessageGerencia.setFilePath(PDF_LOCATION + "ordenServDesconfirmada_" + nroPpto + ".pdf");
			mailMessageGerencia.setFileName("ordenServDesconfirmada_" + nroPpto + ".pdf");			
			mailMessageGerencia.setHtmlBody(getHTMLBodyGerencia(nroPpto, fechaInicial, fechaFinal, evento, lugarNombre));
			
			SmtpSender.getInstance().sendMail(mailMessage);
			SmtpSender.getInstance().sendMail(mailMessageGerencia);
			
			return true;
		} catch (EmailAddressException e) {
			e.printStackTrace();
			return false;
		} catch (EmailNameException e) {
			e.printStackTrace();
			return false;
		} catch (SendMailException e) {
			e.printStackTrace();
			return false;
		}
	}	
	private String getHTMLBody(long nroPpto, String fechaInicial, String fechaFinal, String evento, String lugar){
		String html = new String();
		String e = StringConverter.convertStringToHTMLText(evento);
		String l = StringConverter.convertStringToHTMLText(lugar);
		html += "<html>";
		html += "<body>";
		html += "<h1>";		
		html += "<FONT SIZE=2>ORDEN DE SERVICIO DESCONFIRMADA NRO: " + nroPpto + ".</FONT><BR><BR>";
		if(e != null)
			html += "<FONT SIZE=2>NOMBRE DE EVENTO: "+ e+".</FONT><BR>";
		else html += "<FONT SIZE=2>NOMBRE DE EVENTO: "+ evento+".</FONT><BR>";
		html += "<FONT SIZE=2>A REALIZARSE EN: "+l+".</FONT><BR>";
		html += "<FONT SIZE=2>FECHA DE INICIO: "+ fechaInicial+" Y FECHA DE FIN: "+ fechaFinal+".</FONT>";		
		html += "</h1>";
		html += "</body>";
		html += "</html>";
		
		return html;
	}
	
	private String getHTMLBodyGerencia(long nroPpto, String fechaInicial, String fechaFinal, String evento, String lugar){
		String html = new String();
		String e = StringConverter.convertStringToHTMLText(evento);
		String l = StringConverter.convertStringToHTMLText(lugar);
		html += "<html>";
		html += "<body>";
		html += "<h1>";		
		html += "<FONT SIZE=2>ORDEN DE SERVICIO DESCONFIRMaDA NRO: " + nroPpto + ".</FONT><BR><BR>";
		if(e != null)
			html += "<FONT SIZE=2>NOMBRE DE EVENTO: "+ e+".</FONT><BR>";
		else html += "<FONT SIZE=2>NOMBRE DE EVENTO: "+ evento+".</FONT><BR>";
		html += "<FONT SIZE=2>A REALIZARSE EN: "+l+".</FONT><BR>";
		html += "<FONT SIZE=2>FECHA DE INICIO: "+ fechaInicial+" Y FECHA DE FIN: "+ fechaFinal+".</FONT><BR>";		
		if(getMonto(nroPpto) != null)
			html += "<FONT SIZE=2>MONTO: $ "+ getMonto(nroPpto)+".</FONT>";
		else html += "<FONT SIZE=2>MONTO: $ No especifica.</FONT>";
		html += "</h1>";
		html += "</body>";
		html += "</html>";
		
		return html;
	}
	
	private Double getMonto(long nroPpto){
		try {
			return PresupuestosManager.instance().getFacturacionByPPto(nroPpto);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	private String getEmailOfSysSettingById(long id){
		Session session = HibernateUtil.abrirSession();
		
		String result = (String)session.createSQLQuery
		("select ss_valor from SYS_SETTINGS where ss_id = ?")
		.addScalar("ss_valor", Hibernate.STRING)
		.setLong(0,id)
		.uniqueResult();
		
		HibernateUtil.cerrarSession(session);
		
		return result;
	}
	
	public void savePdfFile(long nroPpto) throws RemoteException{		
		ReportBuilder.exportOSDesconfirmadaToPdf(nroPpto, PDF_LOCATION + "ordenServDesconfirmada_" + nroPpto + ".pdf");		
	}
	
	public void setEstadoOS(String estado){
		this.estado = estado;
	}
}
