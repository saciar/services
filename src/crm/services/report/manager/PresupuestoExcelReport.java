package crm.services.report.manager;

import java.rmi.RemoteException;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.hibernate.Hibernate;
import org.hibernate.Session;

import crm.libraries.report.Presupuesto;
import crm.libraries.report.PresupuestoSala;
import crm.libraries.report.PresupuestoServicio;
import crm.services.report.sei.PresupuestoExcelReportSEI;
import crm.services.util.DateConverter;
import crm.services.util.HibernateUtil;

public class PresupuestoExcelReport implements PresupuestoExcelReportSEI, ReportService{
	public Presupuesto[] findByNroPpto(long nroPpto) throws RemoteException{
		Session session = HibernateUtil.abrirSession();

		List list = session.createSQLQuery(
				"select v.nroPpto as nroppto, v.contactoCliente as contactoCliente, v.titulo as titulo, " +
						"v.vendedor as vendedor, v.cliente as cliente, v.telefono1 as telefono1, ppto.ppto_fecinicio as fechaInicio, " +
						"ppto.ppto_fecfin as fechaFinal, v.telefono2 as telefono2, v.evento as evento " +
						"from VW_RPT_PRESUPUESTO v " +
						"inner join TX_PPTO ppto on v.nroPpto = ppto.ppto_nroppto "+
						"where v.nroPpto = ? " +
						"order by v.contactoCliente"
				)
				.addScalar("nroPpto", Hibernate.LONG) //0
				.addScalar("contactoCliente", Hibernate.STRING)	//1
				.addScalar("titulo", Hibernate.STRING)	//2
				.addScalar("vendedor", Hibernate.STRING)	//3
				.addScalar("cliente", Hibernate.STRING)	//4
				.addScalar("telefono1", Hibernate.STRING)	//5	
				.addScalar("fechaInicio", Hibernate.DATE)	//6
				.addScalar("fechaFinal", Hibernate.DATE)	//7
				.addScalar("telefono2", Hibernate.STRING)	//8
				.addScalar("evento", Hibernate.STRING)	//9
				.setLong(0,nroPpto)
				.list();

		Presupuesto[] results = new Presupuesto[list.size()];
		for (int i=0; i< results.length;i++){ 
			results[i] = new Presupuesto();
			Object[] row = (Object[]) list.get(i);			
			results[i].setNroPpto(((Long)row[0]).longValue());
			results[i].setContactoCliente((String)row[1]);
			results[i].setTitulo((String)row[2]);
			results[i].setVendedor((String)row[3]);
			results[i].setCliente((String)row[4]);
			results[i].setEvento((String)row[9]);
			if((String)row[8] != null)
				results[i].setTelContacto((String)row[5]+" / "+(String)row[8]);
			else
				results[i].setTelContacto((String)row[5]+" / -");
			
			results[i].setPeriodo(getFechaFormateada((Date)row[6]));	
			if(!((Date)row[6]).before((Date)row[7])){				
				results[i].setPeriodo(getFechaFormateada((Date)row[6]));			
			}else
				results[i].setPeriodo("Del "+getFechaFormateada((Date)row[6])+ " al "+getFechaFormateada((Date)row[7]));

			results[i].setSalas(findSalasByPresupuesto(session, nroPpto));
			
		}

		HibernateUtil.cerrarSession(session);

		return results;
	}
	
	private String getFechaFormateada(Date fecha){
		String[] dias = {"","Lunes", "Martes","Miercoles", "Jueves","Viernes", "Sabado","Domingo"};
		String[] meses = {"Enero","Febrero","Marzo","Abril","Mayo","Junio","Julio","Agosto","Septiembre","Octubre", "Noviembre","Diciembre"};
		
		Locale l = Locale.getDefault();
		Calendar inic = Calendar.getInstance(l);
		inic.setTime(fecha);
		StringBuffer fechainicial= new StringBuffer();
		fechainicial.append(dias[inic.get(Calendar.DAY_OF_WEEK)]+ " " +inic.get( Calendar.DAY_OF_MONTH)+" de "+meses[inic.get(Calendar.MONTH)]+" del "+inic.get(Calendar.YEAR));
		return fechainicial.toString();
	}

	private PresupuestoSala[] findSalasByPresupuesto(Session session, long nroPpto) {

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

			results[i].setServicios(findServiciosBySala(session,results[i].getSalaId(), nroPpto));

		}

		return results;
	}

	private PresupuestoServicio[] findServiciosBySala(Session session,long num_sala, long nroPpto) {

		List list = session.createSQLQuery
				(
						"select ss.ppto_ss_servicio as id,ss.ppto_ss_cantidad  as cantidad, servi.si_descripcion as servicio, "+
								"ss.ppto_ss_dias as dias , famserv.fs_descripcion as familia, ss.ppto_ss_preciodto as importe, ss.ppto_ss_detalle as detalle,"+
								"ss.ppto_ss_id as salaServicioId, ss.ppto_ss_modalidad as modo, sf.descripcion as super_familia "+
								"from TX_PPTO_SALAS_SERVICIOS ss "+
								"inner join MST_SERVICIOS serv on ss.ppto_ss_servicio = serv.se_codservicio "+
								"inner join MST_SERVICIOS_IDIOMA servi on serv.se_codservicio = servi.si_codservicio "+
								"inner join MST_FAM_SERVICIOS famserv on serv.se_familia = famserv.fs_codfamilia "+
								"inner join mst_familias_superfamilias super on famserv.fs_codfamilia = id_familia "+
								"inner join mst_super_familia sf on idt_super_familia = super.id_super_familia "+
								"where ss.ppto_ss_pls = ? and servi.si_codidioma = ? " +
								"ORDER BY sf.idt_super_familia"
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
				.addScalar("super_familia", Hibernate.STRING)
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
			results[i].setSuperFamilia((String)row[9]);
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
}
