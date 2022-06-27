package crm.services.report.manager;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.hibernate.Session;

import crm.libraries.abm.entities.UnidadComercial;
import crm.libraries.abm.entities.Usuario;
import crm.libraries.report.Adelanto;
import crm.libraries.report.OrdenFacturacion;
import crm.services.mail.EmailAddressException;
import crm.services.mail.EmailNameException;
import crm.services.mail.MailMessage;
import crm.services.mail.SendMailException;
import crm.services.mail.SmtpSender;
import crm.services.report.sei.AdelantoReportSEI;
import crm.services.transaction.UnidadComercialManager;
import crm.services.transaction.UsuarioManager;
import crm.services.util.HibernateUtil;

public class AdelantoReport implements AdelantoReportSEI,ReportService {	
	
	private static final Log log = LogFactory.getLog(AdelantoReport.class);
	
	private static final long SYS_SETTINGS_EMAIL_OF = 3;
	private static final long SYS_SETTINGS_EMAIL_OF_CHILE = 7;
	private static final long SYS_SETTINGS_EMAIL_OF_MENDOZA = 8;
	private static final long SYS_SETTINGS_EMAIL_OF_BSAS = 11;
	private static String CODIGO_UC_CHILE="11";
	private static String CODIGO_UC_MENDOZA="12";
	
	public Adelanto[] findByNroPpto(long nroPpto) throws RemoteException{		
		/*Session session = HibernateUtil.abrirSession();
		
		List list = session.createSQLQuery
				(
				"select c.empresa as razonSocial, v.nombreEvento as nombreEvento, v.nro_orden as nro_orden, " + 
				"v.lugarEvento as lugarEvento, v.domicilio_lugar as domicilio_lugar, v.telefonos_lugar as telefonos_lugar, " +
				"v.fechaInicialEvento as fechaInicialEvento, v.fechaFinalEvento as fechaFinalEvento, " +
				"v.cantidadSalas as cantidadSalas, c.CUIT as cuit, civa.iv_descripcond as condiva, c.pago_contacto as resp_pago, " + 
				"c.pago_telefono as tel_resp_pago, v.domicilio_pago as domicilio_pago, v.dia_hora_pago as dia_hora_pago, " +
				"v.codprov_crn as codprov_crn, v.un_comercial as un_comercial, v.vendedor as vendedor, v.contacto_cliente as contacto_cliente, " +
				"v.fechaEmision as fechaEmision, v.ppcp_descripcion as cpa_descrip_detallada, v.ppfp_descripcion as cmp_descrip_detallada, " +
				"v.domicilio_legal as domicilio_legal, v.nombre_fantasia as nombre_fantasia " +
				"from VW_RPT_ORDFACTURACION_AD v, MST_CLIENTE c " +
				"inner join MST_CONDIVA civa on c.cl_iva = civa.iv_codcond " +
				"where v.nro_orden = ? AND c.cl_codCliente = v.codClienteFact " +				
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
				.addScalar("domicilio_legal",Hibernate.STRING)
				.addScalar("nombre_fantasia",Hibernate.STRING)
				.setLong(0,nroPpto)
				.list();
				
		Adelanto[] results = new Adelanto[list.size()];
		for (int i=0; i< results.length;i++){ 
			results[i] = new Adelanto();
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
			results[i].setDomicilioLegal((String)row[22]);
			results[i].setNombreFantasia((String)row[23]);
			results[i].setAdelanto(getAdelanto(session, nroPpto));	
			
		}
		HibernateUtil.cerrarSession(session);
		
		return results;*/
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
						"cfac.cf_calle as calleLeg, cfac.cf_nro as nroLeg, cfac.cf_piso as pisoLeg, cfac.cf_dpto as deptoLeg, LOC2.lc_descriplocalidad as locLeg "+
						"from VW_RPT_ORDFACTURACION_AD v, MST_CLIENTE_FACT cfac, MST_LOCALIDAD LOC2, MST_CLIENTE c " +
						"inner join MST_CONDIVA civa on c.cl_iva = civa.iv_codcond " +
						"inner join MST_LOCALIDAD LOC on c.domleg_loc = LOC.lc_codlocalidad "+
						"where v.nro_orden = ? AND c.cl_codCliente = v.codClienteFact AND v.codClienteFact = cfac.cf_codcliente AND cfac.cf_loc = LOC2.lc_codlocalidad  "+		
						"order by v.fechaInicialEvento"
				)
				.addScalar("razonSocial",Hibernate.STRING)//1
				.addScalar("nombreEvento",Hibernate.STRING)//2
				.addScalar("nro_orden",Hibernate.LONG)//3
				.addScalar("lugarEvento",Hibernate.STRING)//4
				.addScalar("domicilio_lugar",Hibernate.STRING)//5
				.addScalar("telefonos_lugar",Hibernate.STRING)//6				
				.addScalar("fechaInicialEvento",Hibernate.TIMESTAMP)//7
				.addScalar("fechaFinalEvento", Hibernate.TIMESTAMP)//8
				.addScalar("cantidadSalas",Hibernate.LONG)//9
				.addScalar("cuit",Hibernate.STRING)//10
				.addScalar("condiva",Hibernate.STRING)//11
				.addScalar("resp_pago",Hibernate.STRING)//12
				.addScalar("tel_resp_pago",Hibernate.STRING)//13
				.addScalar("domicilio_pago",Hibernate.STRING)//14
				.addScalar("dia_hora_pago",Hibernate.STRING)//15
				.addScalar("codprov_crn",Hibernate.STRING)//16		
				.addScalar("un_comercial",Hibernate.STRING)//17
				.addScalar("vendedor",Hibernate.STRING)//18
				.addScalar("contacto_cliente",Hibernate.STRING)//19
				.addScalar("fechaEmision",Hibernate.TIMESTAMP)//20
				.addScalar("cpa_descrip_detallada",Hibernate.STRING)//21
				.addScalar("cmp_descrip_detallada",Hibernate.STRING)//22
				.addScalar("nombre_fantasia",Hibernate.STRING)//23
				.addScalar("ppto_cliente_obs",Hibernate.STRING)//24
				.addScalar("calle",Hibernate.STRING)//25
				.addScalar("nro",Hibernate.STRING)//26
				.addScalar("piso",Hibernate.STRING)//27
				.addScalar("depto",Hibernate.STRING)//28
				.addScalar("loc",Hibernate.STRING)//29
				.addScalar("calleLeg",Hibernate.STRING)//30
				.addScalar("nroLeg",Hibernate.STRING)//31
				.addScalar("pisoLeg",Hibernate.STRING)//32
				.addScalar("deptoLeg",Hibernate.STRING)//33
				.addScalar("locLeg",Hibernate.STRING)//34
				.setLong(0,nroPpto)
				.list();
				
		Adelanto[] results = new Adelanto[list.size()];
		for (int i=0; i< results.length;i++){ 
			results[i] = new Adelanto();
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
			results[i].setAdelanto(getAdelanto(session, nroPpto));	
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
	
	public void sendOFByEmail(long nroPpto, String usuarioId) throws RemoteException{

		try {
			MailMessage mailMessage = new MailMessage();
			Usuario u = UsuarioManager.instance().getUsuarioById(usuarioId);
			UnidadComercial unidad = UnidadComercialManager.instance().getUCDataByCodigoUsuario(usuarioId);
			mailMessage.setFromAddress(u.getEmail(), u.getApellidoYNombre());	
			
			if(!unidad.getCodigo().equals(CODIGO_UC_CHILE) && !unidad.getCodigo().equals(CODIGO_UC_MENDOZA))
				mailMessage.setToAddress(new String[]{getEmailOfSysSettingById(SYS_SETTINGS_EMAIL_OF)},new String[]{"OF"});
			else if(unidad.getCodigo().equals(CODIGO_UC_CHILE))
				mailMessage.setToAddress(new String[]{getEmailOfSysSettingById(SYS_SETTINGS_EMAIL_OF_CHILE)},new String[]{"OF"});
			else if(unidad.getCodigo().equals(CODIGO_UC_MENDOZA))
				mailMessage.setToAddress(new String[]{getEmailOfSysSettingById(SYS_SETTINGS_EMAIL_OF_MENDOZA)},new String[]{"OF"});		
			
			mailMessage.setBccAddress(new String[]{ReportService.EMAIL_CRM_CONTROL}, new String []{"OF Adelanto"});
			
			mailMessage.setSubject("Orden de facturacion de adelanto Nro"+nroPpto);			
			mailMessage.setFilePath(PDF_LOCATION + "ordenFactAdelanto_" + nroPpto + ".pdf");
			mailMessage.setFileName("OrdenFacturacionAdelanto_" + nroPpto + ".pdf");
			mailMessage.setHtmlBody(getHTMLBody(nroPpto));
			SmtpSender.getInstance().sendMail(mailMessage);
		} catch (EmailAddressException e) {
			e.printStackTrace();
		} catch (EmailNameException e) {
			e.printStackTrace();
		} catch (SendMailException e) {
			e.printStackTrace();
		}
	}
	
	public boolean sendOFByEmail2(long nroPpto, String usuarioId, String destinatario) throws RemoteException{
		try {
			MailMessage mailMessage = new MailMessage();
			Usuario u = UsuarioManager.instance().getUsuarioById(usuarioId);
			mailMessage.setFromAddress(u.getEmail(), u.getApellidoYNombre());

			mailMessage.setToAddress(new String[]{destinatario,getEmailOfSysSettingById(SYS_SETTINGS_EMAIL_OF_BSAS)},new String[]{"OF","OF"});
			mailMessage.setBccAddress(new String[]{ReportService.EMAIL_CRM_CONTROL}, new String []{"OF Adelanto"});
			
			mailMessage.setSubject("Orden de facturacion de adelanto Nro"+nroPpto);			
			mailMessage.setFilePath(PDF_LOCATION + "ordenFactAdelanto_" + nroPpto + ".pdf");
			mailMessage.setFileName("OrdenFacturacionAdelanto_" + nroPpto + ".pdf");
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
		html += "<FONT SIZE=2>Se adjunta la orden de facturacion del adelanto " + nroPpto + "</FONT>";
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
		ReportBuilder.exportOFAdelantoToPdf(nroPpto, PDF_LOCATION + "ordenFactAdelanto_" + nroPpto + ".pdf");		
	}
	
	private static AdelantoReport instance;
	
	public static synchronized AdelantoReport instance() {

			if (instance == null) 
				instance = new AdelantoReport();

		return instance;
	}

}
