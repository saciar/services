package crm.services.report.manager;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.Session;

import crm.libraries.abm.entities.ClienteContacto;
import crm.libraries.abm.entities.PrtPptoPeriodo;
import crm.libraries.abm.entities.Usuario;
import crm.libraries.report.OrdenFacturacionSalas;
import crm.libraries.report.OrdenFacturacionServicio;
import crm.libraries.report.OrdenServicio;
import crm.libraries.report.Presupuesto;
import crm.libraries.report.PresupuestoOpcional;
import crm.libraries.report.PresupuestoSala;
import crm.libraries.report.PresupuestoServicio;
import crm.services.mail.EmailAddressException;
import crm.services.mail.EmailNameException;
import crm.services.mail.MailMessage;
import crm.services.mail.SendMailException;
import crm.services.mail.SmtpSender;
import crm.services.report.sei.PresupuestoReportSEI;
import crm.services.sei.ModalidadContratManagerSEI;
import crm.services.transaction.ClienteContactoManager;
import crm.services.transaction.LugarEventoManager;
import crm.services.transaction.ModalidadContratManager;
import crm.services.transaction.UsuarioManager;
import crm.services.transaction.VendedorManager;
import crm.services.util.DateConverter;
import crm.services.util.HibernateUtil;

public class PresupuestoReport implements PresupuestoReportSEI, ReportService{
	
	private static final long PERIODO_FECHA_REAL = 1;
	public Presupuesto[] findByNroPpto(long nroPpto, long idCancelacion, long idHeader, 
			long idFooter, long idInstalacion, long idValidez, long idFormaPago, long idCondPago, long idFirma,
			long idSeguridad, long idPersonal, long idCondReserva, long idTipoPresupuesto, long idPeriodo, 
			long idmoneda, double cotizacion) throws RemoteException{
		
		Session session = HibernateUtil.abrirSession();
		
		List list = session.createSQLQuery(
		"select v.nroPpto as nroppto, v.contactoCliente as contactoCliente, v.titulo as titulo, " +
		"v.vendedor as vendedor, v.cliente as cliente, v.telefono1 as telefono1, ppto.ppto_fecinicio as fechaInicio, " +
		"ppto.ppto_fecfin as fechaFinal, ppto.ppto_rpt_moneda as moneda, ppto.ppto_rpt_cotizacion as cotizacion, v.telefono2 as telefono2 " +
		"from VW_RPT_PRESUPUESTO v " +
		"inner join TX_PPTO ppto on v.nroPpto = ppto.ppto_nroppto "+
		"where v.nroPpto = ? " +
		"order by v.contactoCliente"
		)
		.addScalar("nroPpto", Hibernate.LONG)
		.addScalar("contactoCliente", Hibernate.STRING)
		.addScalar("titulo", Hibernate.STRING)
		.addScalar("vendedor", Hibernate.STRING)
		.addScalar("cliente", Hibernate.STRING)
		.addScalar("telefono1", Hibernate.STRING)		
		.addScalar("fechaInicio", Hibernate.TIMESTAMP)
		.addScalar("fechaFinal", Hibernate.TIMESTAMP)
		.addScalar("moneda", Hibernate.STRING)
		.addScalar("cotizacion", Hibernate.DOUBLE)
		.addScalar("telefono2", Hibernate.STRING)
		.setLong(0,nroPpto)
		.list();
		
		Presupuesto[] results = new Presupuesto[list.size()];
		for (int i=0; i< results.length;i++){ 
			results[i] = new Presupuesto();
			Object[] row = (Object[]) list.get(i);
			
			results[i].setNroPpto(((Long)row[0]).longValue());
			results[i].setCancelacion(getCancelacionById(idCancelacion, session));
			results[i].setContactoCliente((String)row[1]);
			results[i].setFooter(getFooterById(idFooter, session));
			results[i].setFormaPago(getFormaPago(idFormaPago, session));
			results[i].setCondPago(getCondPago(idCondPago, session));
			results[i].setHeader(getHeaderById(idHeader, session));
			results[i].setInstalacion(getInstalacionById(idInstalacion, session));
			results[i].setTitulo((String)row[2]);
			results[i].setValidez(getValidezById(idValidez, session));
			results[i].setVendedor((String)row[3]);
			results[i].setCliente((String)row[4]);
			results[i].setFirma(getFirmaById(idFirma, session));
			results[i].setSeguridad(getSeguridadById(idSeguridad, session));
			results[i].setPersonal(getPersonalById(idPersonal, session));
			results[i].setCondReserva(getCondReservaById(idCondReserva, session));
			if((String)row[10] != null)
				results[i].setTelContacto((String)row[5]+" / "+(String)row[10]);
			else
				results[i].setTelContacto((String)row[5]+" / -");
			results[i].setTipoPresupuesto(getTipoPresupuestoById(idTipoPresupuesto, session));
			results[i].setSimboloMoneda(getSimboloMonedaById(idmoneda, session));
			results[i].setCotizacion(cotizacion);
			if(idPeriodo == PERIODO_FECHA_REAL)
				if(((Timestamp)row[6]).toString().equals(((Timestamp)row[7]).toString()))
					results[i].setPeriodo("el "+ getStringfromDate((getDatefromString(((Timestamp)row[6]).toString()))));
				else results[i].setPeriodo("del "+ getStringfromDate((getDatefromString(((Timestamp)row[6]).toString()))) +"al "+ getStringfromDate((getDatefromString(((Timestamp)row[7]).toString()))));
			else				
				results[i].setPeriodo(getPeriodoById(idPeriodo, session));
			
			results[i].setSalas(findSalasByPresupuesto(session, nroPpto, idPeriodo));
		}
		
		HibernateUtil.cerrarSession(session);
		
		return results;
	}
	
	private String getCancelacionById(long idCancelacion, Session session){
		
		String result = (String)session.createSQLQuery(
				"select ppc_descripcion " +
				"from MST_PRT_PPTO_CANCELACION " +
				"where ppc_id = ? and activo = 'S'"
				)
				.addScalar("ppc_descripcion", Hibernate.STRING)
				.setLong(0, idCancelacion)
				.uniqueResult();
		
		return result;
	}
	
	private String getFooterById(long idFooter, Session session){
		String result = (String)session.createSQLQuery(
				"select ppf_descripcion " +
				"from MST_PRT_PPTO_FOOTER " +
				"where ppf_id = ? and activo = 'S'"
				)
				.addScalar("ppf_descripcion", Hibernate.STRING)
				.setLong(0, idFooter)
				.uniqueResult();
				
		return result;
	}
	
	private String getFormaPago(long idFormaPago, Session session){
		String result = (String)session.createSQLQuery(
				"select ppfp_descripcion " +
				"from MST_PRT_PPTO_FPAGO " +
				"where ppfp_id = ? and activo = 'S'"
				)
				.addScalar("ppfp_descripcion", Hibernate.STRING)
				.setLong(0, idFormaPago)
				.uniqueResult();
				
		return result;
	}
	
	private String getCondPago(long idCondPago, Session session){
		String result = (String)session.createSQLQuery(
				"select ppcp_descripcion " +
				"from MST_PRT_PPTO_COND_PAGO " +
				"where ppcp_id = ? and ppcp_activo = 'S'"
				)
				.addScalar("ppcp_descripcion", Hibernate.STRING)
				.setLong(0, idCondPago)
				.uniqueResult();
				
		return result;
	}
	
	private String getHeaderById(long idHeader, Session session){
		String result = (String)session.createSQLQuery(
				"select pph_descripcion " +
				"from MST_PRT_PPTO_HEADER " +
				"where pph_id = ? and activo = 'S'"
				)
				.addScalar("pph_descripcion", Hibernate.STRING)
				.setLong(0, idHeader)
				.uniqueResult();
				
		return result;
	}
	
	private String getInstalacionById(long idInstalacion, Session session){
		String result = (String)session.createSQLQuery(
				"select ppi_descripcion " +
				"from MST_PRT_PPTO_INSTALACION  " +
				"where ppi_id = ? and activo = 'S'"
				)
				.addScalar("ppi_descripcion", Hibernate.STRING)
				.setLong(0, idInstalacion)
				.uniqueResult();
				
		return result;
	}
	
	private String getSeguridadById(long idSeguridad, Session session){
		String result = (String)session.createSQLQuery(
				"select ppse_descripcion " +
				"from MST_PRT_PPTO_SEGURIDAD " +
				"where ppse_id = ? and activo = 'S'"
				)
				.addScalar("ppse_descripcion", Hibernate.STRING)
				.setLong(0, idSeguridad)
				.uniqueResult();
		
		return result;
	}
	
	private String getPersonalById(long idPersonal, Session session){
		String result = (String)session.createSQLQuery(
				"select ppp_descripcion " +
				"from MST_PRT_PPTO_PERSONAL " +
				"where ppp_id = ? and activo = 'S'"
				)
				.addScalar("ppp_descripcion", Hibernate.STRING)
				.setLong(0, idPersonal)
				.uniqueResult();
		
		return result;
	}
	
	private String getCondReservaById(long idCondReserva, Session session){
		String result = (String)session.createSQLQuery(
				"select ppcr_descripcion " +
				"from MST_PRT_PPTO_COND_RESERVA " +
				"where ppcr_id = ? and activo = 'S'"
				)
				.addScalar("ppcr_descripcion", Hibernate.STRING)
				.setLong(0, idCondReserva)
				.uniqueResult();
		
		return result;
	}
	
	private String getValidezById(long idValidez, Session session){
		String result = (String)session.createSQLQuery(
				"select ppv_descripcion " +
				"from MST_PRT_PPTO_VALIDEZ " +
				"where ppv_id = ? and activo = 'S'"
				)
				.addScalar("ppv_descripcion", Hibernate.STRING)
				.setLong(0, idValidez)
				.uniqueResult();
				
		return result;
	}
	
	private String getFirmaById(long idFirma, Session session){
		String result = (String)session.createSQLQuery(
				"select pps_descripcion " +
				"from MST_PRT_PPTO_SIGNATURE " +
				"where pps_id = ? and activo = 'S'"
				)
				.addScalar("pps_descripcion", Hibernate.STRING)
				.setLong(0, idFirma)
				.uniqueResult();
				
		return result;
	}
	
	private String getTipoPresupuestoById(long idTipoPresupuesto, Session session){
		String result = (String)session.createSQLQuery(
				"select pptp_descripcion " +
				"from MST_PRT_PPTO_TIPO_PRESUPUESTO " +
				"where pptp_id = ? and activo = 'S'"
				)
				.addScalar("pptp_descripcion", Hibernate.STRING)
				.setLong(0, idTipoPresupuesto)
				.uniqueResult();
				
		return result;
	}
	
	private String getPeriodoById(long idPeriodo, Session session){
		String result = (String)session.createSQLQuery(
				"select pppe_descripcion " +
				"from MST_PRT_PPTO_PERIODO " +
				"where pppe_id = ? and activo = 'S'"
				)
				.addScalar("pppe_descripcion", Hibernate.STRING)
				.setLong(0, idPeriodo)
				.uniqueResult();
				
		return result;
	}
	
	private String getSimboloMonedaById(long idMoneda, Session session){
		String result = (String)session.createSQLQuery(
				"select me_simbolo " +
				"from MST_MONEDA_EXTRANJERA " +
				"where me_codigo = ? and me_activo = 'S'"
				)
				.addScalar("me_simbolo", Hibernate.STRING)
				.setLong(0, idMoneda)
				.uniqueResult();
				
		return result;
	}
	
	private PresupuestoSala[] findSalasByPresupuesto(Session session, long nroPpto, long idPeriodo) {
		//"SELECT * FROM VW_RPT_WEEK where fechaEvento >= '2006-04-23' and fechaEvento <= '2006-04-25'";

		
		List list = session.createSQLQuery
				(
				"select s.ppto_s_id as sala_id, sl.els_descripcion as nombre_sala, s.ppto_s_fecinicio as fecha_inicio, "+ 
				"s.ppto_s_fecfin as fecha_fin "+
				"from TX_PPTO_SALAS s "+
				"inner join MST_EVT_LUGAR_SALAS sl on s.ppto_s_codlugsala = sl.els_codlugsala " +				
				"where s.ppto_s_nroppto = ? " +
				"order by s.ppto_s_orden "
				)
				.addScalar("sala_id",Hibernate.LONG)
				.addScalar("nombre_sala",Hibernate.STRING)
				.addScalar("fecha_inicio",Hibernate.TIMESTAMP)
				.addScalar("fecha_fin",Hibernate.TIMESTAMP)

				.setLong(0,new Long(nroPpto))
				.list();

		PresupuestoSala[] results = new PresupuestoSala[list.size()];
		for (int i=0; i< results.length;i++){ 
			results[i] = new PresupuestoSala();
			Object[] row = (Object[]) list.get(i);
			
			results[i].setSalaId(((Long)row[0]).longValue());
			results[i].setNombreSala((String)row[1]);
			results[i].setFechaInicio(((Timestamp)row[2]).toString());
			//results[i].setFechaFin(((Timestamp)row[3]).toString());			
			
			if(idPeriodo == PERIODO_FECHA_REAL)
				if(((Timestamp)row[2]).toString().equals(((Timestamp)row[3]).toString()))
					results[i].setFechaFin("el "+ getStringfromDate((getDatefromString(((Timestamp)row[2]).toString()))));
				else results[i].setFechaFin("del "+ getStringfromDate((getDatefromString(((Timestamp)row[2]).toString()))) +"al "+ getStringfromDate((getDatefromString(((Timestamp)row[3]).toString()))));
			else				
				results[i].setFechaFin(getPeriodoById(idPeriodo, session));			
			
			results[i].setServicios(findServiciosBySala(session,results[i].getSalaId(), nroPpto));
			//results[i].setOpcionales(findOpcionalesBySala(session,results[i].getSalaId(), nroPpto));
		}
		
		return results;
		}
	
	/*private PresupuestoOpcional[] findOpcionalesBySala(Session session,long num_sala, long nroPpto) {
		
		List list = session.createSQLQuery
				(
				"select ss.ppto_ss_servicio as id,ss.ppto_ss_cantidad  as cantidad, servi.si_descripcion as servicio, "+
				"ss.ppto_ss_dias as dias , famserv.fs_descripcion as familia, ss.ppto_ss_preciodto as importe, ss.ppto_ss_detalle as detalle, ss.ppto_ss_id as salaServicioId "+
				"from TX_PPTO_SALAS_SERVICIOS ss "+
				"inner join MST_SERVICIOS serv on ss.ppto_ss_servicio = serv.se_codservicio "+
				"inner join MST_SERVICIOS_IDIOMA servi on serv.se_codservicio = servi.si_codservicio "+
				"inner join MST_FAM_SERVICIOS famserv on serv.se_familia = famserv.fs_codfamilia "+
				"where ss.ppto_ss_pls = ? and servi.si_codidioma = ?  and (ss.ppto_ss_modalidad = ? or ss.ppto_ss_modalidad = ?) " +
				"ORDER BY ss.ppto_ss_orden"
				)
				.addScalar("id",Hibernate.LONG)
				.addScalar("cantidad",Hibernate.LONG)
				.addScalar("servicio",Hibernate.STRING)
				.addScalar("dias",Hibernate.INTEGER)
				.addScalar("familia", Hibernate.STRING)
				.addScalar("importe", Hibernate.DOUBLE)
				.addScalar("detalle", Hibernate.STRING)
				.addScalar("salaServicioId", Hibernate.LONG)
				.setLong(0,new Long(num_sala))
				.setInteger(1, CODIGO_CASTELLANO)
				.setInteger(2,Integer.parseInt(ModalidadContratManagerSEI.MODALIDAD_INTERNO_OPCIONAL))
				.setInteger(3,Integer.parseInt(ModalidadContratManagerSEI.MODALIDAD_EXTERNO_OPCIONAL))
				.list();

		PresupuestoOpcional[] results = new PresupuestoOpcional[list.size()];
		for (int i=0; i< results.length;i++){ 
			results[i] = new PresupuestoOpcional();
			Object[] row = (Object[]) list.get(i);
									
			results[i].setServicioId(((Long)row[0]).longValue());
			results[i].setCantidad(((Long)row[1]).longValue());
			if(((Long)row[0]).longValue() == 1){
				results[i].setServicio((String)row[6]);
				results[i].setCaracteristicas(findCaracteristicasBySubcontratado(session,((Long)row[7]).longValue()));
			}
			else {
				results[i].setServicio((String)row[2]);
				results[i].setCaracteristicas(findCaracteristicasByServicio(session,results[i].getServicioId()));
			}
			results[i].setDias(((Integer)row[3]).intValue());
			results[i].setFamilia((String)row[4]);
			results[i].setImporte(((Double)row[5]).doubleValue());	
			
		}
		
		return results;
	}
	
	private PresupuestoServicio[] findServiciosBySala(Session session,long num_sala, long nroPpto) {
		
		List list = session.createSQLQuery
				(
				"select ss.ppto_ss_servicio as id,ss.ppto_ss_cantidad  as cantidad, servi.si_descripcion as servicio, "+
				"ss.ppto_ss_dias as dias , famserv.fs_descripcion as familia, ss.ppto_ss_preciodto as importe, ss.ppto_ss_detalle as detalle, ss.ppto_ss_id as salaServicioId "+
				"from TX_PPTO_SALAS_SERVICIOS ss "+
				"inner join MST_SERVICIOS serv on ss.ppto_ss_servicio = serv.se_codservicio "+
				"inner join MST_SERVICIOS_IDIOMA servi on serv.se_codservicio = servi.si_codservicio "+
				"inner join MST_FAM_SERVICIOS famserv on serv.se_familia = famserv.fs_codfamilia "+
				"where ss.ppto_ss_pls = ? and servi.si_codidioma = ?  and (ss.ppto_ss_modalidad = ? or ss.ppto_ss_modalidad = ?) " +
				"ORDER BY ss.ppto_ss_orden"
				)
				.addScalar("id",Hibernate.LONG)
				.addScalar("cantidad",Hibernate.LONG)
				.addScalar("servicio",Hibernate.STRING)
				.addScalar("dias",Hibernate.INTEGER)
				.addScalar("familia", Hibernate.STRING)
				.addScalar("importe", Hibernate.DOUBLE)
				.addScalar("detalle", Hibernate.STRING)
				.addScalar("salaServicioId", Hibernate.LONG)
				.setLong(0,new Long(num_sala))
				.setInteger(1, CODIGO_CASTELLANO)
				.setInteger(2,Integer.parseInt(ModalidadContratManagerSEI.MODALIDAD_INTERNO))
				.setInteger(3,Integer.parseInt(ModalidadContratManagerSEI.MODALIDAD_EXTERNO))
				.list();

		PresupuestoServicio[] results = new PresupuestoServicio[list.size()];
		for (int i=0; i< results.length;i++){ 
			results[i] = new PresupuestoServicio();
			Object[] row = (Object[]) list.get(i);
									
			results[i].setServicioId(((Long)row[0]).longValue());
			results[i].setCantidad(((Long)row[1]).longValue());
			if(((Long)row[0]).longValue() == 1){
				results[i].setServicio((String)row[6]);
				results[i].setCaracteristicas(findCaracteristicasBySubcontratado(session,((Long)row[7]).longValue()));
			}
			else {
				results[i].setServicio((String)row[2]);
				results[i].setCaracteristicas(findCaracteristicasByServicio(session,results[i].getServicioId()));
			}
			results[i].setDias(((Integer)row[3]).intValue());
			results[i].setFamilia((String)row[4]);
			results[i].setImporte(((Double)row[5]).doubleValue());	
		}
		
		return results;
	}*/
	
	private PresupuestoServicio[] findServiciosBySala(Session session,long num_sala, long nroPpto) {
		
		List list = session.createSQLQuery
				(
				"select ss.ppto_ss_servicio as id,ss.ppto_ss_cantidad  as cantidad, servi.si_descripcion as servicio, "+
				"ss.ppto_ss_dias as dias , famserv.fs_descripcion as familia, ss.ppto_ss_preciodto as importe, ss.ppto_ss_detalle as detalle,"+
				"ss.ppto_ss_id as salaServicioId, ss.ppto_ss_modalidad as modo "+
				"from TX_PPTO_SALAS_SERVICIOS ss "+
				"inner join MST_SERVICIOS serv on ss.ppto_ss_servicio = serv.se_codservicio "+
				"inner join MST_SERVICIOS_IDIOMA servi on serv.se_codservicio = servi.si_codservicio "+
				"inner join MST_FAM_SERVICIOS famserv on serv.se_familia = famserv.fs_codfamilia "+
				"where ss.ppto_ss_pls = ? and servi.si_codidioma = ? " +
				"ORDER BY ss.ppto_ss_orden"
				)
				.addScalar("id",Hibernate.LONG)
				.addScalar("cantidad",Hibernate.LONG)
				.addScalar("servicio",Hibernate.STRING)
				.addScalar("dias",Hibernate.INTEGER)
				.addScalar("familia", Hibernate.STRING)
				.addScalar("importe", Hibernate.DOUBLE)
				.addScalar("detalle", Hibernate.STRING)
				.addScalar("salaServicioId", Hibernate.LONG)
				.addScalar("modo", Hibernate.LONG)
				.setLong(0,new Long(num_sala))
				.setInteger(1, CODIGO_CASTELLANO)
				//.setInteger(2,Integer.parseInt(ModalidadContratManagerSEI.MODALIDAD_INTERNO))
				//.setInteger(3,Integer.parseInt(ModalidadContratManagerSEI.MODALIDAD_EXTERNO))
				.list();

		PresupuestoServicio[] results = new PresupuestoServicio[list.size()];
		for (int i=0; i< results.length;i++){ 
			results[i] = new PresupuestoServicio();
			Object[] row = (Object[]) list.get(i);
									
			results[i].setServicioId(((Long)row[0]).longValue());
			results[i].setCantidad(((Long)row[1]).longValue());
			if(((Long)row[0]).longValue() == 1){
				results[i].setServicio((String)row[6]);
				results[i].setCaracteristicas(findCaracteristicasBySubcontratado(session,((Long)row[7]).longValue()));
			}
			else {
				results[i].setServicio((String)row[2]);
				results[i].setCaracteristicas(findCaracteristicasByServicio(session,results[i].getServicioId()));
			}
			results[i].setDias(((Integer)row[3]).intValue());
			results[i].setFamilia((String)row[4]);
			results[i].setImporte(((Double)row[5]).doubleValue());			
			results[i].setModalidad(((Long)row[8]).longValue());
			
		}
		
		return results;
	}
	
	private String[] findCaracteristicasByServicio(Session session, long codServicio){
		List list = session.createSQLQuery(
				"select ssd_descripcion " +
				"from MST_SERV_DESCRIP_DETALLADA " +
				"where sdd_codserv = ? and sdd_codidioma = ? " +
				"order by sdd_id"
				)
				.addScalar("ssd_descripcion", Hibernate.STRING)
				.setLong(0, new Long(codServicio))
				.setInteger(1, CODIGO_CASTELLANO)
				.list();
		
		String[] results = new String[list.size()];
		for (int i=0; i< results.length;i++){ 
			results[i] = (String)list.get(i);
		}
		
		return results;
	}
	
	private String[] findCaracteristicasBySubcontratado(Session session, long codServicio){
		List list = session.createSQLQuery(
				"select psdd_descripcion " +
				"from TX_PPTO_SERVICIO_DESC_DETALLADA " +
				"where psdd_servicio_id = ? " +
				"order by psdd_id"
				)
				.addScalar("psdd_descripcion", Hibernate.STRING)
				.setLong(0, new Long(codServicio))
				.list();
		
		String[] results = new String[list.size()];
		for (int i=0; i< results.length;i++){ 
			results[i] = (String)list.get(i);
		}
		
		return results;
	}
	
	public void sendPresupByEmail(long nroPpto, String usuarioId, String codContactoCliente) throws RemoteException{

		try {
			MailMessage mailMessage = new MailMessage();

			Usuario u = UsuarioManager.instance().getUsuarioById(usuarioId);			
			mailMessage.setFromAddress(u.getEmail(), u.getApellidoYNombre());
			
			if(!codContactoCliente.equals("")){
				ClienteContacto contacto = ClienteContactoManager.instance().getClienteContactoById(codContactoCliente);		
				mailMessage.setToAddress(new String[]{contacto.getEmail(),u.getEmail()},new String[]{contacto.getApellidoYNombre(),u.getApellidoYNombre()});
			}
			else{ 
				mailMessage.setToAddress(new String[]{u.getEmail()},new String[]{u.getApellidoYNombre()});
				mailMessage.setBccAddress(new String[]{ReportService.EMAIL_CRM_CONTROL}, new String []{u.getApellidoYNombre()});
			}
			mailMessage.setSubject("Preupuesto Nro"+nroPpto);
			mailMessage.setFilePath(PDF_LOCATION + "presupuesto_" + nroPpto + ".pdf");
			mailMessage.setFileName("Presupuesto_" + nroPpto + ".pdf");
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
	
	public void sendEmail(String usuarioId, String codContactoCliente) throws RemoteException{

		try {
			MailMessage mailMessage = new MailMessage();

			//Usuario u = UsuarioManager.instance().getUsuarioById(usuarioId);			
			mailMessage.setFromAddress("administrator@congressrental.com", "Administrador Congress Rental");

			mailMessage.setToAddress(new String[]{usuarioId},new String[]{codContactoCliente});			

			mailMessage.setSubject("Congress Rental AntiSpam");		
			mailMessage.setFilePath(PDF_LOCATION + "image001.gif");
			mailMessage.setFileName("image001.gif");
			mailMessage.setHtmlBody(getHTMLBodyPrueba());
			SmtpSender.getInstance().sendMail(mailMessage);
		} catch (EmailAddressException e) {
			e.printStackTrace();
		} catch (EmailNameException e) {
			e.printStackTrace();
		} catch (SendMailException e) {
			e.printStackTrace();
		}
	}
	
	private String getHTMLBodyPrueba(){
		String html = new String();
		
		html += "<html>";
		html += "<body>";
		html += "<h1>";
		html += "<FONT SIZE=3>Congress Rental Argentina S.A. le comunica que ud. forma parte de la lista negra de spam de nuestro servidor. " +
				"Por favor si ud. desea salir de esta lista comuniquese con el Departamento de Sistemas de nuestra compania al 4006-6529. Muchas gracias.</FONT>";
		html += "</h1>";
		html += "</body>";
		html += "</html>";
		
		return html;
	}
	
	private String getHTMLBody(long nroPpto){
		String html = new String();
		
		html += "<html>";
		html += "<body>";
		html += "<h1>";
		html += "<FONT SIZE=2>Se adjunta el presupuesto " + nroPpto + "</FONT>";
		html += "</h1>";
		html += "</body>";
		html += "</html>";
		
		return html;
	}
	
	/*private String getEmailOfSysSettingById(long id){
		Session session = HibernateUtil.abrirSession();
		
		String result = (String)session.createSQLQuery
		("select ss_valor from SYS_SETTINGS where ss_id = ?")
		.addScalar("ss_valor", Hibernate.STRING)
		.setLong(0,id)
		.uniqueResult();
		
		HibernateUtil.cerrarSession(session);
		
		return result;
	}*/
	
	public void savePdfFile(long nroPpto, long idCancelacion, long idHeader, long idFooter, long idInstalacion,
			long idValidez, long idFormaPago, long idCondPago, long idFirma, long idSeguridad, long idPersonal, 
			long idCondReserva, long idTipoPresupuesto, long idPeriodo, long idMoneda, double cotizacion) throws RemoteException{		
		ReportBuilder.exportPresupuestoToPdf(nroPpto, idCancelacion, idHeader, idFooter, idInstalacion,
				idValidez, idFormaPago, idCondPago, idFirma, idSeguridad, idPersonal, idCondReserva, idTipoPresupuesto, idPeriodo, idMoneda, cotizacion, PDF_LOCATION + "presupuesto_" + nroPpto + ".pdf");		
	}
	
	public void saveTxtFile(long nroPpto, long idCancelacion, long idHeader, long idFooter, long idInstalacion,
			long idValidez, long idFormaPago, long idCondPago, long idFirma, long idSeguridad, long idPersonal, 
			long idCondReserva, long idTipoPresupuesto, long idPeriodo, long idMoneda, double cotizacion) throws RemoteException{		
		ReportBuilder.exportPresupuestoToRtf(nroPpto, idCancelacion, idHeader, idFooter, idInstalacion,
				idValidez, idFormaPago, idCondPago, idFirma, idSeguridad, idPersonal, idCondReserva, idTipoPresupuesto, idPeriodo, idMoneda, cotizacion, PDF_LOCATION + "presupuesto_" + nroPpto + ".doc");		
	}
	
	private static PresupuestoReport instance;
	
	public static synchronized PresupuestoReport instance() {

			if (instance == null) 
				instance = new PresupuestoReport();

		return instance;
	}

	public static String getStringfromDate(Date date){
		String d = null;

		d = DateConverter.convertDateToString(date, "dd-MM-yyyy HH:mm:ss");

		return d.substring(0,10);
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
}
