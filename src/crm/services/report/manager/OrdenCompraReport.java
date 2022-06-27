package crm.services.report.manager;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.Session;

import crm.libraries.abm.entities.Usuario;
import crm.libraries.report.OrdenCompra;
import crm.libraries.report.OrdenCompraSalas;
import crm.libraries.report.OrdenCompraServicio;
import crm.services.mail.EmailAddressException;
import crm.services.mail.EmailNameException;
import crm.services.mail.MailMessage;
import crm.services.mail.SendMailException;
import crm.services.mail.SmtpSender;
import crm.services.report.sei.OrdenCompraReportSEI;
import crm.services.transaction.UsuarioManager;
import crm.services.util.HibernateUtil;

public class OrdenCompraReport  implements OrdenCompraReportSEI,ReportService {	
	
	private static final long SYS_SETTINGS_EMAIL_OF = 3;
	
	public OrdenCompra[] findByNroPpto(long nroPpto) throws RemoteException{
		//"SELECT * FROM VW_RPT_WEEK where fechaEvento >= '2006-04-23' and fechaEvento <= '2006-04-25'";
		Session session = HibernateUtil.abrirSession();
		
		List list = session.createSQLQuery
				(
				"select c.empresa as razonSocial, v.nombreEvento as nombreEvento, v.nro_orden as nro_orden, " + 
				"v.lugarEvento as lugarEvento, v.domicilio_lugar as domicilio_lugar, v.telefonos_lugar as telefonos_lugar, " +
				"v.fechaInicialEvento as fechaInicialEvento, v.fechaFinalEvento as fechaFinalEvento, " +
				"v.cantidadSalas as cantidadSalas, c.CUIT as cuit, civa.iv_descripcond as condiva, c.pago_contacto as resp_pago, " + 
				"c.pago_telefono as tel_resp_pago, v.domicilio_pago as domicilio_pago, v.dia_hora_pago as dia_hora_pago, " +
				"v.codprov_crn as codprov_crn, v.un_comercial as un_comercial, v.vendedor as vendedor, v.contacto_cliente as contacto_cliente, " +
				"v.fechaEmision as fechaEmision, v.cpa_descrip_detallada as cpa_descrip_detallada, v.cmp_descrip_detallada as cmp_descrip_detallada, " +
				"v.domicilio_legal as domicilio_legal, v.nombre_fantasia as nombre_fantasia, v.totalPersona as personas, v.domicilio_factura as domicilio_factura " +
				"from VW_RPT_ORDFACTURACION v, MST_CLIENTE c " +
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
				.addScalar("personas", Hibernate.LONG)
				.addScalar("domicilio_factura", Hibernate.STRING)
				.setLong(0,nroPpto)
				.list();
				
		OrdenCompra[] results = new OrdenCompra[list.size()];
		for (int i=0; i< results.length;i++){ 
			results[i] = new OrdenCompra();
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
			//results[i].setUnidad((String)row[16]);
			results[i].setVendedor((String)row[17]);
			results[i].setContactoCliente((String)row[18]);
			results[i].setFechaEmision(((Timestamp)row[19]).toString());
			results[i].setMedioPago((String)row[20]);
			results[i].setCondicionPago((String)row[21]);
			results[i].setDomicilioLegal((String)row[22]);
			results[i].setNombreFantasia((String)row[23]);
			results[i].setCantidadPersonas(((Long)row[24]).longValue());
			results[i].setDomicilioFactura((String)row[25]);
			
			results[i].setSalas(findSalasByPresupuesto(session, nroPpto));
			
			//results[i].setOperadores(findOperadoresbyPresupuesto(session,results[i].getOrdenServicio()));
		}
		
		HibernateUtil.cerrarSession(session);
		
		return results;
	}

	private OrdenCompraSalas[] findSalasByPresupuesto(Session session, long nroPpto) {
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

		OrdenCompraSalas[] results = new OrdenCompraSalas[list.size()];
		for (int i=0; i< results.length;i++){ 
			results[i] = new OrdenCompraSalas();
			Object[] row = (Object[]) list.get(i);
			
			results[i].setSalaId(((Long)row[0]).longValue());
			results[i].setNombreSala((String)row[1]);
			results[i].setFechaInicio(((Timestamp)row[2]).toString());
			results[i].setFechaFin(((Timestamp)row[3]).toString());	
			
			results[i].setServicios(findServiciosBySala(session,results[i].getSalaId(), nroPpto));
		}
		
		return results;
	}
	
	private OrdenCompraServicio[] findServiciosBySala(Session session,long num_sala, long nroPpto) {
		//"SELECT * FROM VW_RPT_WEEK where fechaEvento >= '2006-04-23' and fechaEvento <= '2006-04-25'";

		
		List list = session.createSQLQuery
				(
				"select ss.ppto_ss_servicio as id,ss.ppto_ss_cantidad  as cantidad, servi.si_descripcion_abreviada as servicio, "+
				"ss.ppto_ss_dias as dias , famserv.DESCFAMILIA as familia, ss.ppto_ss_preciodto as importe "+
				"from TX_PPTO_SALAS_SERVICIOS ss "+
				"inner join MST_SERVICIOS serv on ss.ppto_ss_servicio = serv.se_codservicio "+
				"inner join MST_SERVICIOS_IDIOMA servi on serv.se_codservicio = servi.si_codservicio "+
				"inner join VW_FAM_SERVICIOS_IDIOMA famserv on serv.se_codservicio = famserv.SERVICIO "+
				"where ss.ppto_ss_pls = ? " +
				"ORDER BY ss.ppto_ss_orden"
				)
				.addScalar("id",Hibernate.LONG)
				.addScalar("cantidad",Hibernate.LONG)
				.addScalar("servicio",Hibernate.STRING)
				.addScalar("dias",Hibernate.INTEGER)
				.addScalar("familia", Hibernate.STRING)
				.addScalar("importe", Hibernate.DOUBLE)
				.setLong(0,new Long(num_sala))
				.list();

		OrdenCompraServicio[] results = new OrdenCompraServicio[list.size()];
		for (int i=0; i< results.length;i++){ 
			results[i] = new OrdenCompraServicio();
			Object[] row = (Object[]) list.get(i);
			
			results[i].setServicioId(((Long)row[0]).longValue());
			results[i].setCantidad(((Long)row[1]).longValue());
			results[i].setServicio((String)row[2]);
			results[i].setDias(((Integer)row[3]).intValue());
			results[i].setFamilia((String)row[4]);
			results[i].setImporte(((Double)row[5]).doubleValue());
			
			//results[i].setAccesorios(findAccesoriosByServicio(session,results[i].getServicioId()));
		}
		
		return results;
	}
	
	public void sendOFByEmail(long nroPpto, String usuarioId) throws RemoteException{

		try {
			MailMessage mailMessage = new MailMessage();
			Usuario u = UsuarioManager.instance().getUsuarioById(usuarioId);
			mailMessage.setFromAddress(u.getEmail(), u.getApellidoYNombre());					
			mailMessage.setToAddress(new String[]{"saciar@congressrental.com"},new String[]{"FP"});			
			
			mailMessage.setSubject("Orden de compra Nro"+nroPpto);			
			mailMessage.setFilePath(PDF_LOCATION + "ordenCompra_" + nroPpto + ".pdf");
			mailMessage.setFileName("OrdenCompra_" + nroPpto + ".pdf");
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
	
	private String getHTMLBody(long nroPpto){
		String html = new String();
		
		html += "<html>";
		html += "<body>";
		html += "<h1>";
		html += "<FONT SIZE=2>Se adjunta la factura proforma " + nroPpto + "</FONT>";
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
		ReportBuilder.exportOCToPdf(nroPpto, PDF_LOCATION + "ordenCompra_" + nroPpto + ".pdf");		
	}
	
private static OrdenCompraReport instance;
	
	public static synchronized OrdenCompraReport instance() {

			if (instance == null) 
				instance = new OrdenCompraReport();

		return instance;
	}
}
