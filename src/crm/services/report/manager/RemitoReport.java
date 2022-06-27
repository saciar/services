package crm.services.report.manager;

import java.rmi.RemoteException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.hibernate.Session;

import crm.libraries.report.Remito;
import crm.libraries.report.RemitoEquipos;
import crm.services.report.sei.RemitoReportSEI;
import crm.services.util.HibernateUtil;

public class RemitoReport implements RemitoReportSEI, ReportService{
	
	private static RemitoReport instance;
	private static final Log log = LogFactory.getLog(RemitoReport.class);
		
		public static synchronized RemitoReport instance() {

				if (instance == null) 
					instance = new RemitoReport();

			return instance;
		}
			
		public Remito[] findByNroPpto(long nroppto) throws RemoteException{
			Session session = HibernateUtil.abrirSession();
			
			List list = session.createSQLQuery
			("SELECT nombre_fantasia, iv_descripcond, CUIT, el_calle, el_nro, el_piso, el_dpto,lc_descriplocalidad FROM tx_ppto t "+
					"inner join mst_cliente on ppto_codcliente = cl_codcliente "+
					"left join mst_condiva on cl_iva=iv_codcond "+
					"inner join mst_evt_lugar on ppto_evt_lugar=el_codlugar "+
					"left join mst_localidad on el_loc = lc_id "+
					"where ppto_nroppto = ?"			
			)		
			.addScalar("nombre_fantasia", Hibernate.STRING)
			.addScalar("iv_descripcond", Hibernate.STRING)
			.addScalar("CUIT", Hibernate.STRING)
			.addScalar("el_calle", Hibernate.STRING)
			.addScalar("el_nro", Hibernate.STRING)
			.addScalar("el_piso", Hibernate.STRING)
			.addScalar("el_dpto", Hibernate.STRING)
			.addScalar("lc_descriplocalidad", Hibernate.STRING)
			.setLong(0, new Long(nroppto))
			.list();
			
			Remito[] results = new Remito[list.size()];
			for (int i=0; i< results.length;i++){ 
				results[i] = new Remito();
				Object[] row = (Object[]) list.get(i);
				
				results[i].setEmpresa((String)row[0]);
				results[i].setConIVA((String)row[1]);
				results[i].setCuit((String)row[2]);
				results[i].setCalle((String)row[3]);
				results[i].setNumero((String)row[4]);
				results[i].setPiso((String)row[5]);
				results[i].setLocalidad((String)row[7]);
				results[i].setEquipos(findRemitoEquipos(session,nroppto));
			}
			return results;
		}
		
		private RemitoEquipos[] findRemitoEquipos(Session session, long nroPpto){
			
			List list = session.createSQLQuery
			(
			"SELECT os, codEquipo, eq_descripcion, eq_nroserie FROM tx_egresos_equipos t "+
			"inner join inv_equipos on codEquipo=eq_idequipo "+
			"where os = ?"		
			)
			.addScalar("os", Hibernate.LONG)
			.addScalar("codEquipo", Hibernate.STRING)
			.addScalar("eq_descripcion", Hibernate.STRING)
			.addScalar("eq_nroserie", Hibernate.STRING)
			.setLong(0, new Long(nroPpto))
			.list();
			
			RemitoEquipos[] results = new RemitoEquipos[list.size()];
			for (int i=0; i< results.length;i++){ 
				results[i] = new RemitoEquipos();
				Object[] row = (Object[]) list.get(i);
				
				results[i].setDescripcion((String)row[2]);
				results[i].setCodigo((String)row[1]);
				results[i].setNroSerie((String)row[3]);
			}
			return results;				
		}

}
