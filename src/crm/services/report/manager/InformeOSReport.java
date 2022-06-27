package crm.services.report.manager;

import java.rmi.RemoteException;

import crm.services.report.sei.InformeOSReportSEI;

public class InformeOSReport implements InformeOSReportSEI,ReportService{
	
	private static InformeOSReport instance;
	
	public static synchronized InformeOSReport instance(){
		if (instance == null) 
			instance = new InformeOSReport();

		return instance;
	}

	public void createInformeOS(long nroPpto) throws RemoteException{
		ReportBuilder.exportInformeOSToPdf(nroPpto, PDF_LOCATION + "Informe_" + nroPpto + ".pdf");
	}
	
}
