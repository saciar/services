package crm.services.report.manager;

import java.rmi.RemoteException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.hibernate.Session;

import crm.libraries.abm.entities.VariacionFecha;
import crm.libraries.report.ListaPrecios;
import crm.libraries.report.ListaPreciosFamilias;
import crm.libraries.report.ListaPreciosServicios;
import crm.services.report.sei.ListaPreciosReportSEI;
import crm.services.util.HibernateUtil;

public class ListaPreciosReport implements ListaPreciosReportSEI, ReportService{
	
private static ListaPreciosReport instance;
private static final Log log = LogFactory.getLog(ListaPreciosReport.class);
	
	public static synchronized ListaPreciosReport instance() {

			if (instance == null) 
				instance = new ListaPreciosReport();

		return instance;
	}
	
	public ListaPrecios findByMes(int fecha) throws RemoteException{
		return null;
	}
	
	public ListaPrecios findByFecha(String fecha) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		
		/*Object list = session.createSQLQuery
				(
				"select v.vm_variacion as variacion " +
				"from MST_VARIACION_MES v "+
				"where v.vm_mes = ? and v.vm_activo='S' "
				)
				.addScalar("variacion", Hibernate.DOUBLE)	
				.setLong(0, new Integer(mes))
				.uniqueResult();*/
		//int variacion = crm.services.wsdl2.manager.VariacionFechaManager.instance().getVariacionFecha(fecha);
		VariacionFecha[] variacion = crm.services.wsdl2.manager.VariacionFechaManager.instance().getVariacionesFecha(fecha);
		ListaPrecios results = new ListaPrecios();
		if (log.isDebugEnabled())
			log.info("FECHA PUTAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA: \n" + fecha);
		results.setMes(fecha);
		//results.setDtoTemporada(variacion);
		
		results.setFamilias(findFamilias(session,variacion));
		return results;
	}
	
	private ListaPreciosFamilias[] findFamilias(Session session, VariacionFecha[] variacionXMes){
		
		List list = session.createSQLQuery
		(
		"SELECT familia, f.fs_descripcion as descripcion FROM vw_rpt_lista_precios v" +
		"inner join MST_FAM_SERVICIOS f on f.fs_codfamilia = familia" +
		" group by familia"
		)
		.addScalar("familia", Hibernate.LONG)
		.addScalar("descripcion", Hibernate.STRING)
		.list();
		
		ListaPreciosFamilias[] results = new ListaPreciosFamilias[list.size()];
		for (int i=0; i< results.length;i++){ 
			results[i] = new ListaPreciosFamilias();
			Object[] row = (Object[]) list.get(i);
			
			results[i].setDescripcion((String)row[1]);
			results[i].setServicios(findServicios(session, (Long)row[0], variacionXMes));
			
		}
		return results;				
	}
	
	private ListaPreciosServicios[] findServicios(Session session, long codFamilia, VariacionFecha[] variacionXMes){
		
		List list = session.createSQLQuery
		(
		"select s.nombre as servicio, s.codigo as codigoServicios, s.precio as precio, s.dosDias as dosDias, " +
		"s.tresDias as tresDias, s.cuatroDias as cuatroDias, s.cincoDias as cincoDias "+		
		"from vw_rpt_lista_precios s "+
		"where s.familia = ? "+
		"order by servicio"
		)
		.addScalar("servicio", Hibernate.STRING)
		.addScalar("codigoServicios", Hibernate.LONG)
		.addScalar("precio", Hibernate.DOUBLE)
		.addScalar("dosDias", Hibernate.DOUBLE)
		.addScalar("tresDias", Hibernate.DOUBLE)
		.addScalar("cuatroDias", Hibernate.DOUBLE)
		.addScalar("cincoDias", Hibernate.DOUBLE)		
		.setLong(0, new Long(codFamilia))
		.list();
		
		ListaPreciosServicios[] results = new ListaPreciosServicios[list.size()];
		for (int i=0; i< results.length;i++){ 
			results[i] = new ListaPreciosServicios();
			Object[] row = (Object[]) list.get(i);
			
			results[i].setServicio((String)row[0]);
			results[i].setCodigoServicio(((Long)row[1]).longValue());
			results[i].setUnDia(getPrecioConDtoFechas(((Double)row[2]).doubleValue(), variacionXMes, ((Long)row[1]).longValue()));
			if(row[3]!= null)
				results[i].setDosDias(getPrecioConDtoFechas(((Double)row[3]).doubleValue(), variacionXMes,((Long)row[1]).longValue()));
			else results[i].setDosDias(getPrecioConDtoFechas(((Double)row[2]).doubleValue()*2, variacionXMes,((Long)row[1]).longValue()));
			if(row[4]!= null)
				results[i].setTresDias(getPrecioConDtoFechas(((Double)row[4]).doubleValue(), variacionXMes,((Long)row[1]).longValue()));
			else results[i].setDosDias(getPrecioConDtoFechas(((Double)row[2]).doubleValue()*3, variacionXMes,((Long)row[1]).longValue()));
			if(row[5]!= null)
				results[i].setCuatroDias(getPrecioConDtoFechas(((Double)row[5]).doubleValue(), variacionXMes,((Long)row[1]).longValue()));
			else results[i].setDosDias(getPrecioConDtoFechas(((Double)row[2]).doubleValue()*4, variacionXMes,((Long)row[1]).longValue()));
			if(row[6]!= null)
				results[i].setCincoDias(getPrecioConDtoFechas(((Double)row[6]).doubleValue(), variacionXMes,((Long)row[1]).longValue()));
			else results[i].setDosDias(getPrecioConDtoFechas(((Double)row[2]).doubleValue()*5, variacionXMes,((Long)row[1]).longValue()));
			
		}
		return results;				
	}
	
	private double getPrecioConDtoTemporada(double valor, double porc){
		double n = 0d;
		double v = valor;
		
		n = v + (valor*porc/100); 
		
		return n; 
	}
	
	private double getPrecioConDtoFechas(double valor, VariacionFecha[] variaciones, long cod){
		double descuento = 0d;
		double v = valor;
		
		if(variaciones != null){
			for(int i=0; i< variaciones.length;i++){
				v=valor;
				descuento = (valor * Integer.parseInt(variaciones[i].getVariacion())) / 100;
				valor = valor + descuento;
				if(cod==152){
				if (log.isDebugEnabled())
					log.info("VALORES "+ i +" (%"+variaciones[i].getVariacion()+" de "+v+")= \n" + valor);
				}
			}
		}
		
		return valor; 
	}
}
