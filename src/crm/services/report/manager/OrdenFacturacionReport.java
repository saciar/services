package crm.services.report.manager;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRMapArrayDataSource;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.util.JRLoader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.hibernate.Session;

import crm.libraries.abm.entities.UnidadComercial;
import crm.libraries.abm.entities.Usuario;
import crm.libraries.report.OrdenFacturacion;
import crm.libraries.report.OrdenFacturacionSalas;
import crm.libraries.report.OrdenFacturacionServicio;
import crm.services.mail.EmailAddressException;
import crm.services.mail.EmailNameException;
import crm.services.mail.MailMessage;
import crm.services.mail.SendMailException;
import crm.services.mail.SmtpSender;
import crm.services.report.sei.OrdenFacturacionReportSEI;
import crm.services.sei.ModalidadContratManagerSEI;
import crm.services.transaction.UnidadComercialManager;
import crm.services.transaction.UsuarioManager;
import crm.services.util.DateConverter;
import crm.services.util.HibernateUtil;

public class OrdenFacturacionReport implements OrdenFacturacionReportSEI,ReportService {	
	
	private static final long SYS_SETTINGS_EMAIL_OF = 3;
	private static final long SYS_SETTINGS_EMAIL_OF_CHILE = 7;
	private static final long SYS_SETTINGS_EMAIL_OF_MENDOZA = 8;
	private static final long SYS_SETTINGS_EMAIL_OF_MARDELPLATA = 10;
	private static final long SYS_SETTINGS_EMAIL_OF_BSAS = 11;
	private static String CODIGO_UC_CHILE="11";
	private static String CODIGO_UC_MENDOZA="12";
	private static String CODIGO_UC_MARDELPLATA="8";
	private static String CODIGO_UC_FRANQUICIAS="4";

	public OrdenFacturacion[] findByNroPpto(long nroPpto) throws RemoteException{
		//"SELECT * FROM VW_RPT_WEEK where fechaEvento >= '2006-04-23' and fechaEvento <= '2006-04-25'";
		Session session = HibernateUtil.abrirSession();
		
		List list = session.createSQLQuery
				(
						"select c.empresa as razonSocial, v.nombreEvento as nombreEvento, v.nro_orden as nro_orden, " + 
						"v.lugarEvento as lugarEvento, v.domicilio_lugar as domicilio_lugar, v.telefonos_lugar as telefonos_lugar, " +
						"v.fechaInicialEvento as fechaInicialEvento, v.fechaFinalEvento as fechaFinalEvento, " +
						"v.cantidadSalas as cantidadSalas, c.CUIT as cuit, civa.iv_descripcond as condiva, c.pago_contacto as resp_pago, " + 
						"c.pago_telefono as tel_resp_pago, cfac.cf_domic_pago as domicilio_pago, cfac.cf_dia_hora_pago as dia_hora_pago, " +
						"cfac.cf_codprovcrn as codprov_crn, v.un_comercial as un_comercial, v.vendedor as vendedor, v.contacto_cliente as contacto_cliente, " +
						"v.fechaEmision as fechaEmision, v.ppcp_descripcion as cpa_descrip_detallada, v.ppfp_descripcion as cmp_descrip_detallada, " +						
						"v.nombre_fantasia as nombre_fantasia, v.ppto_cliente_obs, " +
						"c.domleg_calle as calle, c.domleg_nro as nro, c.domleg_piso as piso, c.domleg_dpto as depto, LOC.lc_descriplocalidad as loc, "+
						"cfac.cf_calle as calleLeg, cfac.cf_nro as nroLeg, cfac.cf_piso as pisoLeg, cfac.cf_dpto as deptoLeg, LOC2.lc_descriplocalidad as locLeg, v.et_descripcion as tipoEvento "+
						"from VW_RPT_ORDFACTURACION v, MST_CLIENTE_FACT cfac, MST_LOCALIDAD LOC2, MST_CLIENTE c " +
						"inner join MST_CONDIVA civa on c.cl_iva = civa.iv_codcond " +
						"inner join MST_LOCALIDAD LOC on c.domleg_loc = LOC.lc_codlocalidad "+
						"where v.nro_orden = ? AND c.cl_codCliente = v.codClienteFact AND v.codClienteFact = cfac.cf_codcliente AND cfac.cf_loc = LOC2.lc_codlocalidad  "+		
						"order by v.fechaInicialEvento"
				)
				.addScalar("razonSocial",Hibernate.STRING)
				.addScalar("nombreEvento",Hibernate.STRING)
				.addScalar("nro_orden",Hibernate.LONG)
				.addScalar("lugarEvento",Hibernate.STRING)
				.addScalar("domicilio_lugar",Hibernate.STRING)
				.addScalar("telefonos_lugar",Hibernate.STRING)				
				.addScalar("fechaInicialEvento",Hibernate.TIMESTAMP)
				.addScalar("fechaFinalEvento", Hibernate.TIMESTAMP)
				.addScalar("cantidadSalas",Hibernate.LONG)
				.addScalar("cuit",Hibernate.STRING)
				.addScalar("condiva",Hibernate.STRING)
				.addScalar("resp_pago",Hibernate.STRING)
				.addScalar("tel_resp_pago",Hibernate.STRING)
				.addScalar("domicilio_pago",Hibernate.STRING)
				.addScalar("dia_hora_pago",Hibernate.STRING)
				.addScalar("codprov_crn",Hibernate.STRING)		
				.addScalar("un_comercial",Hibernate.STRING)
				.addScalar("vendedor",Hibernate.STRING)
				.addScalar("contacto_cliente",Hibernate.STRING)
				.addScalar("fechaEmision",Hibernate.TIMESTAMP)
				.addScalar("cpa_descrip_detallada",Hibernate.STRING)
				.addScalar("cmp_descrip_detallada",Hibernate.STRING)
				//.addScalar("domicilio_legal",Hibernate.STRING)
				.addScalar("nombre_fantasia",Hibernate.STRING)
				.addScalar("ppto_cliente_obs",Hibernate.STRING)
				.addScalar("calle",Hibernate.STRING)
				.addScalar("nro",Hibernate.STRING)
				.addScalar("piso",Hibernate.STRING)
				.addScalar("depto",Hibernate.STRING)
				.addScalar("loc",Hibernate.STRING)
				.addScalar("calleLeg",Hibernate.STRING)
				.addScalar("nroLeg",Hibernate.STRING)
				.addScalar("pisoLeg",Hibernate.STRING)
				.addScalar("deptoLeg",Hibernate.STRING)
				.addScalar("locLeg",Hibernate.STRING)
				.addScalar("tipoEvento", Hibernate.STRING)
				.setLong(0,nroPpto)
				.list();
				
		OrdenFacturacion[] results = new OrdenFacturacion[list.size()];
		for (int i=0; i< results.length;i++){ 
			results[i] = new OrdenFacturacion();
			Object[] row = (Object[]) list.get(i);
			
			results[i].setRazonSocial((String)row[0]);
			results[i].setNombreEvento((String)row[1]);
			results[i].setOrdenFacturacion((Long)row[2]);
			results[i].setLugarEvento((String)row[3]);
			results[i].setDomicilioLugar((String)row[4]);
			results[i].setTelefonosLugar((String)row[5]);
	        results[i].setFechaInicialEvento(((Timestamp)row[6]).toString());
			results[i].setFechaFinalEvento(((Timestamp)row[7]).toString());
			results[i].setCantidadSalas(((Long)row[8]).longValue());
			results[i].setCuit((String)row[9]);
			results[i].setCondIva((String)row[10]);
			results[i].setResponsablePago((String)row[11]);
			results[i].setTelefonoRespPago((String)row[12]);
			results[i].setDomicilioPago((String)row[13]);
			results[i].setDiaHoraPago((String)row[14]);
			results[i].setCodProvCrn((String)row[15]);			
			results[i].setUnidad((String)row[16]);
			results[i].setVendedor((String)row[17]);
			results[i].setContactoCliente((String)row[18]);
			results[i].setFechaEmision(((Timestamp)row[19]).toString());
			results[i].setMedioPago((String)row[20]);
			results[i].setCondicionPago((String)row[21]);
			results[i].setDomicilioLegal((String)row[29]+(String)row[30]+"   Piso: "+(String)row[31]+"   Depto: "+(String)row[32]+"   Localidad: "+(String)row[33]);
			results[i].setDomicilioFactura((String)row[24]+(String)row[25]+"   Piso: "+(String)row[26]+"   Depto: "+(String)row[27]+"   Localidad: "+(String)row[28]);
			results[i].setNombreFantasia((String)row[22]);
			results[i].setAdelanto(getAdelanto(session, nroPpto));
			results[i].setObservaciones((String)row[23]);
			results[i].setTipoEvento((String)row[34]);
			
			results[i].setSalas(findSalasByPresupuesto(session, nroPpto));
			
			//results[i].setOperadores(findOperadoresbyPresupuesto(session,results[i].getOrdenServicio()));
		}
		
		HibernateUtil.cerrarSession(session);
		
		return results;
	}
	
	private double getAdelanto(Session s, long nroPpto){
		double ad =0d;
		
		Double result = (Double)s.createSQLQuery(
				"select ad_valor as valor "+
				"from TX_PPTO_ADELANTO "+
				"where ad_ppto = ? ")
				.addScalar("valor", Hibernate.DOUBLE)
				.setLong(0,new Long(nroPpto))
				.uniqueResult();
		
		if(result != null){
			ad = result;
		}
		return ad;
	}
	
	private OrdenFacturacionSalas[] findSalasByPresupuesto(Session session, long nroPpto) {
		//"SELECT * FROM VW_RPT_WEEK where fechaEvento >= '2006-04-23' and fechaEvento <= '2006-04-25'";

		
		List list = session.createSQLQuery
				(
				"select s.ppto_s_id as sala_id, sl.els_descripcion as nombre_sala, s.ppto_s_fecinicio as fecha_inicio, "+ 
				"s.ppto_s_fecfin as fecha_fin "+
				"from TX_PPTO_SALAS s "+
				"inner join MST_EVT_LUGAR_SALAS sl on s.ppto_s_codlugsala = sl.els_codlugsala " +				
				"where s.ppto_s_nroppto = ? " +
				"order by s.ppto_s_id "
				)
				.addScalar("sala_id",Hibernate.LONG)
				.addScalar("nombre_sala",Hibernate.STRING)
				.addScalar("fecha_inicio",Hibernate.TIMESTAMP)
				.addScalar("fecha_fin",Hibernate.TIMESTAMP)

				.setLong(0,new Long(nroPpto))
				.list();

		OrdenFacturacionSalas[] results = new OrdenFacturacionSalas[list.size()];
		for (int i=0; i< results.length;i++){ 
			results[i] = new OrdenFacturacionSalas();
			Object[] row = (Object[]) list.get(i);
			
			results[i].setSalaId(((Long)row[0]).longValue());
			results[i].setNombreSala((String)row[1]);
			results[i].setFechaInicio(((Timestamp)row[2]).toString());
			results[i].setFechaFin(((Timestamp)row[3]).toString());	
			
			results[i].setServicios(findServiciosBySala(session,results[i].getSalaId(), nroPpto));
		}
		
		return results;
	}
	
	private OrdenFacturacionServicio[] findServiciosBySala(Session session,long num_sala, long nroPpto) {
		//"SELECT * FROM VW_RPT_WEEK where fechaEvento >= '2006-04-23' and fechaEvento <= '2006-04-25'";

		
		List list = session.createSQLQuery
				(
				"select ss.ppto_ss_servicio as id,ss.ppto_ss_cantidad  as cantidad, servi.si_descripcion_abreviada as servicio, "+
				"ss.ppto_ss_dias as dias , famserv.DESCFAMILIA as familia, ss.ppto_ss_preciodto as importe, ss.ppto_ss_detalle as detalle, ss.ppto_ss_descuento as descuento "+
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
				.addScalar("importe", Hibernate.DOUBLE)
				.addScalar("detalle", Hibernate.STRING)
				.addScalar("descuento", Hibernate.INTEGER)
				.setLong(0,new Long(num_sala))
				.setInteger(1,Integer.parseInt(ModalidadContratManagerSEI.MODALIDAD_INTERNO))
				.setInteger(2,Integer.parseInt(ModalidadContratManagerSEI.MODALIDAD_EXTERNO))
				.list();

		OrdenFacturacionServicio[] results = new OrdenFacturacionServicio[list.size()];
		for (int i=0; i< results.length;i++){ 
			results[i] = new OrdenFacturacionServicio();
			Object[] row = (Object[]) list.get(i);
			
			results[i].setServicioId(((Long)row[0]).longValue());
			results[i].setCantidad(((Long)row[1]).longValue());
			if((Long)row[0]==1)
				results[i].setServicio((String)row[6]);
			else results[i].setServicio((String)row[2]);
			results[i].setDias(((Integer)row[3]).intValue());
			results[i].setFamilia((String)row[4]);
			results[i].setImporte(((Double)row[5]).doubleValue());
			results[i].setDescuento(((Integer)row[7]).intValue());
			
			//results[i].setAccesorios(findAccesoriosByServicio(session,results[i].getServicioId()));
		}
		
		return results;
	}
	
	public boolean sendOFByEmail2(long nroPpto, String usuarioId, String destinatario) throws RemoteException{
		try {
			MailMessage mailMessage = new MailMessage();
			Usuario u = UsuarioManager.instance().getUsuarioById(usuarioId);
			mailMessage.setFromAddress(u.getEmail(), u.getApellidoYNombre());

			mailMessage.setToAddress(new String[]{destinatario},new String[]{"OF"});
			mailMessage.setBccAddress(new String[]{ReportService.EMAIL_CRM_CONTROL}, new String []{"OF"});
			
			mailMessage.setSubject("Orden de facturacion Nro"+nroPpto);			
			mailMessage.setFilePath(PDF_LOCATION + "ordenFact_" + nroPpto + ".pdf");
			mailMessage.setFileName("OrdenFacturacion_" + nroPpto + ".pdf");
			mailMessage.setHtmlBody(getHTMLBody(nroPpto));
			
			SmtpSender.getInstance().sendMail(mailMessage);
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
	
	public boolean sendOFByEmail(long nroPpto, String usuarioId) throws RemoteException{
		
		try {
			MailMessage mailMessage = new MailMessage();
			Usuario u = UsuarioManager.instance().getUsuarioById(usuarioId);
			mailMessage.setFromAddress(u.getEmail(), u.getApellidoYNombre());
			UnidadComercial unidad = UnidadComercialManager.instance().getUCDataByCodigoUsuario(usuarioId);
			
			//if(!unidad.getCodigo().equals(CODIGO_UC_CHILE) && !unidad.getCodigo().equals(CODIGO_UC_MENDOZA))
			if(unidad != null && unidad.getCodigo()!=null){
				if(unidad.getCodigo().equals(CODIGO_UC_CHILE))
					mailMessage.setToAddress(new String[]{getEmailOfSysSettingById(SYS_SETTINGS_EMAIL_OF_CHILE)},new String[]{"OF"});
				else if(unidad.getCodigo().equals(CODIGO_UC_MENDOZA))
					mailMessage.setToAddress(new String[]{getEmailOfSysSettingById(SYS_SETTINGS_EMAIL_OF_MENDOZA)},new String[]{"OF"});
				else if(unidad.getCodigo().equals(CODIGO_UC_MARDELPLATA))
					mailMessage.setToAddress(new String[]{getEmailOfSysSettingById(SYS_SETTINGS_EMAIL_OF_MARDELPLATA), getEmailOfSysSettingById(SYS_SETTINGS_EMAIL_OF)},new String[]{"OF", "OF"});
				else if(unidad.getCodigo().equals(CODIGO_UC_FRANQUICIAS))
					mailMessage.setToAddress(new String[]{getEmailOfSysSettingById(SYS_SETTINGS_EMAIL_OF)},new String[]{"OF"});			
				else
					mailMessage.setToAddress(new String[]{getEmailOfSysSettingById(SYS_SETTINGS_EMAIL_OF_BSAS)},new String[]{"OF"});
			}
			else
				mailMessage.setToAddress(new String[]{getEmailOfSysSettingById(SYS_SETTINGS_EMAIL_OF_BSAS)},new String[]{"OF"});
			mailMessage.setBccAddress(new String[]{ReportService.EMAIL_CRM_CONTROL}, new String []{"OF"});
			
			mailMessage.setSubject("Orden de facturacion Nro"+nroPpto);			
			mailMessage.setFilePath(PDF_LOCATION + "ordenFact_" + nroPpto + ".pdf");
			mailMessage.setFileName("OrdenFacturacion_" + nroPpto + ".pdf");
			mailMessage.setHtmlBody(getHTMLBody(nroPpto));
			
			SmtpSender.getInstance().sendMail(mailMessage);
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
	
	private String getHTMLBody(long nroPpto){
		String html = new String();
		
		html += "<html>";
		html += "<body>";
		html += "<h1>";
		html += "<FONT SIZE=2>Se adjunta la orden de facturacion " + nroPpto + "</FONT>";
		html += "</h1>";
		html += "</body>";
		html += "</html>";
		
		return html;
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
		ReportBuilder.exportOFToPdf(nroPpto, PDF_LOCATION + "ordenFact_" + nroPpto + ".pdf");		
	}
	
	private static OrdenFacturacionReport instance;
	
	public static synchronized OrdenFacturacionReport instance() {

			if (instance == null) 
				instance = new OrdenFacturacionReport();

		return instance;
	}
}