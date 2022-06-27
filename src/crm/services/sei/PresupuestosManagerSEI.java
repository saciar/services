package crm.services.sei;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;

import crm.libraries.abm.entities.Presupuesto;
import crm.libraries.abm.helper.PresupuestoHelper;

public interface PresupuestosManagerSEI extends Remote {

	public long nuevoPresupuesto(PresupuestoHelper pa)
			throws RemoteException;

	public long actualizarPresupuesto(PresupuestoHelper p) throws RemoteException;

	//public Object[] buscarPorEstado(String codEstado) throws RemoteException;

	//public Object[] buscarPorCliente(long codCliente)
	//		throws RemoteException;
	
	//public Object[] buscarPorClienteFac(long codCliente)
	//		throws RemoteException;
	
	//public Object[] buscarPorClienteYFecha(long codCliente, String fecha)
	//		throws RemoteException;

	//public Object[] buscarPorLugar(long codigoLugar)
	//		throws RemoteException;
	
	//public Object[] buscarPorLugarYFecha(long codLugar, String fecha)
	//		throws RemoteException;
	
	//public Object[] buscarPorVendedor(long codVendedor)
	//		throws RemoteException;

	//public Object[] buscarPorFechaInicio(String fechaInicio)
	//		throws RemoteException;

	public Object[] buscarPorNumero(long nro) 
			throws RemoteException;

	public Presupuesto buscarPresupuesto(long numPpto) throws RemoteException;

	public Object[] buscarPendientesPorVendedor(String codigoVendedor) throws RemoteException;

	public Object[] buscarDeHoyPorVendedor(String codigoVendedor) throws RemoteException;

	public Object[] buscarConfirmadosPorVendedor(String codigoVendedor)throws RemoteException;

	public void setAsCobrado(String numeroPresupuesto,String usuario) throws RemoteException;
	
	public Object[] findOFNoFacturados() throws RemoteException;

	//public void setAsFacturado(String numeroPresupuesto,String usuario) throws RemoteException;
	public void setAsFacturado(String numeroPresupuesto, String usuarioId, String factura) throws RemoteException;
	
	public Object[] getPresupuestoByFactura(String nrofactura) throws RemoteException;
	
	public String getFacturaByNroPpto(long nroppto) throws RemoteException;
	
	public Object[] getFacturasByNroPpto(long nroppto) throws RemoteException;
	
	public Object[] buscarAVencerPorVendedor(String codigoVendedor)throws RemoteException;

	public Object[] buscarPendientesPorUC(String codigoVendedor)throws RemoteException;

	public Object[] buscarAVencerPorUC(String codigoVendedor)throws RemoteException;

	public Object[] buscarDeHoyPorUC(String codigoVendedor)throws RemoteException;

	public Object[] buscarConfirmadosPorUC(String codigoVendedor)throws RemoteException;

	public double getFacturacionByPPto(long nroPpto)throws RemoteException;	
	
	public String getFechaByNroPptoAndStateAndUser(long nroPpto, int codEstado)throws RemoteException;
	
	public boolean canEdit(long nroPpto,String loggedUser)throws RemoteException;

	public boolean isPptoActualizado(long nroPpto) throws RemoteException;
	
	public boolean isPptoCancelado(long nroPpto) throws RemoteException;

	public String getMaxFechaByNroPptoAndState(long nroPpto, int codEstado)throws RemoteException;
	
	//public void modificarActivo(long nroPpto, String activo, int codUsuario) throws RemoteException;
	public void modificarActivo(long nroPpto, String activo, int codUsuario, String ip, String mac) throws RemoteException;

	public int getCantPresupuestosByClientes(long codCliente) throws RemoteException;	
	
	public void setAsAdelantado(String numeroPresupuesto, String usuarioId, String nrofactura) throws RemoteException;
	
	public Object[] buscarEstadoActual(long nroPpto) throws RemoteException;
	
	//public Object[] buscarPorTipoEvt(long nro) throws RemoteException;
	
	public Object[] findOFNoFacturadosByUnidadAdm(String codUnidad) throws RemoteException;
	
	public Object[] findFacturadosNoCobradosByUnidadAdm(String codUnidad) throws RemoteException;
	
	public Object[] buscarPresupuestosAbiertosVendedor(long codVendedor) throws RemoteException;
	
	public void setAsCobradoAConfirmar(String numeroPresupuesto, String usuarioId)
	throws RemoteException;
	
	public void setAnticipoAConfirmar(String numeroPresupuesto, String usuarioId)
	throws RemoteException;
	
	public void setAnticipoCobrado(String numeroPresupuesto, String usuarioId)
	throws RemoteException;
	
	public Object[] findFacturadosNoCobradosByUnidadAdmNroPpto(String codUnidad, long nroppto) throws RemoteException;
	
	public void setAsAdicionalesFacturados(String numeroPresupuesto, String usuarioId, String nrofactura) throws RemoteException;
	
	public Object[] findCobradosByUnidadAdmNroPpto(String codUnidad, long nroppto) throws RemoteException;
	
	public Object[] buscar(String clienteEvt, String clienteFact, String fechaDesde, String fechaHasta, String lugar,
			String vendedor, String uc, String estado, String tipoEvt, String nombreEvt) throws RemoteException;
	
	public boolean setUnidadAdministrativaByNroPpto(String nroppto, String codUnidad) throws RemoteException;
	
	public Object[] findOFNoFacturadosByUnidadAdm2(String codUnidad) throws RemoteException;
	
	public Object[] buscarParaReportes(String clienteEvt, String clienteFact, String fechaDesde, String fechaHasta, String lugar,
			String uc, String estado, String tipoEvt, String nombreEvt, String codFacturacion) throws RemoteException;
	
	public Object[] buscarParaReportesDeServicios(String clienteEvt,String fechaDesde, String fechaHasta, String lugar,
			String condpago, String estado, boolean subcontratado, String codServ, String nombreSub) throws RemoteException;
	
	public Object[] buscarParaReportesFacturacion(long nroPpto, String nroFactura, String codUnidad) throws RemoteException;
	
	public void setFactura(long id, String nroFactura) throws RemoteException ;

	public Object[] buscarParaReportesRentabilidad(String clienteEvt, String clienteFact, String fechaDesde, String fechaHasta, String lugar,
			String condpago, String estado, String tipoEvt, String nombreEvt,String vendedores) throws RemoteException;
	
	public void setDesConfirmado(String numeroPresupuesto, String usuarioId) throws RemoteException;
	
	public Object[] buscarParaReportesRentabilidadCostos(String nroppto) throws RemoteException;
	
	public Object[] buscarParaReportesDeFamServicios(String clienteEvt,String fechaDesde, String fechaHasta, String lugar,
			String condpago, String estado, boolean subcontratado, String codFam) throws RemoteException;
	public Object[] buscarHorariosCostos(String codSala) throws RemoteException;

	public Object[] buscarParaReportesDeServiciosSubc(String fechaDesde, String fechaHasta, int codEstado) throws RemoteException;
	public Object[] buscarParaReportesComercial(String clienteEvt, String fechaDesde, String fechaHasta, String lugar,
			String estado, String vendedor) throws RemoteException;
	
	public Object[] buscarParaReportesGerencia(String clienteEvt,String fechaDesde, String fechaHasta, String lugar,
			String estado, String tipoEvt, String vend) throws RemoteException;
	
	public Object[] buscarParaReportesGerenciaVendedores(String clienteEvt,String fechaDesde, String fechaHasta, String lugar,
			String estado, String tipoEvt, String vend) throws RemoteException;
	
	public Object[] buscarComparacionesGerenciaAnio(String vendedores, String anios, String estado, String tipos, int mes) throws RemoteException;
	
	public Object[] buscarComparacionesGerenciaMeses(String vendedores, String meses, String estado, String tipos, int anio) throws RemoteException;
	
	public Object[] buscarPptosParaOperadores(String fechaDesde, String fechaHasta) throws RemoteException;
	
	public Object[] buscarOperadoresPorPpto(int nroPpto, String fecha) throws RemoteException;
}
