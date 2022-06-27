package crm.services.report.manager;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRMapArrayDataSource;
import net.sf.jasperreports.engine.data.JRXmlDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.JRXmlUtils;

public class InformeCreator implements ReportService{
	private static final String REPORT_INFORME_OS= "jasper/Informe_OS.jasper";
	
	private static InformeCreator instance;
	
	public static synchronized InformeCreator instance() {

		if (instance == null) 
			instance = new InformeCreator();

		return instance;
	}
	
	public JasperPrint createOSReport(long nroPpto) throws RemoteException, JRException {
		
		return createReport( String.valueOf(nroPpto));
	}
	
	private JasperPrint createReport(String nro) throws RemoteException, JRException {

		// 1- cargar los reporte desde los .jasper			
		JasperReport oSReport = (JasperReport)JRLoader.loadObject(getClass().getResourceAsStream(REPORT_INFORME_OS));
		
		
		// 2- create a map of parameters to pass to the report.
		Map parameters = new HashMap();

		parameters.put("REPORT_NRO_OS", nro);
		
		// 3- create JasperPrint using fillReport() method
		JasperPrint jasperPrint = JasperFillManager.fillReport(oSReport, parameters,new JREmptyDataSource(1));

		return jasperPrint;
	}
	

}
