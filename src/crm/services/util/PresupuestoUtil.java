package crm.services.util;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;

import crm.libraries.abm.entities.ABMEntity;
import crm.libraries.abm.entities.Cliente;
import crm.libraries.abm.entities.EstadoEvento;
import crm.libraries.abm.entities.LugarEvento;
import crm.libraries.abm.entities.ModalidadContrat;
import crm.libraries.abm.entities.MonedaExtranjera;
import crm.libraries.abm.entities.PptoCambioEstado;
import crm.libraries.abm.entities.PptoEstadoActual;
import crm.libraries.abm.entities.Ppto_Adelanto;
import crm.libraries.abm.entities.Ppto_Agregado;
import crm.libraries.abm.entities.Ppto_Contacto;
import crm.libraries.abm.entities.Ppto_Contacto_Lugar;
import crm.libraries.abm.entities.Ppto_Facturacion;
import crm.libraries.abm.entities.Ppto_GastoAsistentes;
import crm.libraries.abm.entities.Ppto_GastoHoteleria;
import crm.libraries.abm.entities.Ppto_GastoOperador;
import crm.libraries.abm.entities.Ppto_GastoRepresentacion;
import crm.libraries.abm.entities.Ppto_GastoSC;
import crm.libraries.abm.entities.Ppto_GastoVarios;
import crm.libraries.abm.entities.Ppto_GastoViaticos;
import crm.libraries.abm.entities.Ppto_Pago;
import crm.libraries.abm.entities.Ppto_Rentabilidad;
import crm.libraries.abm.entities.Ppto_Sala;
import crm.libraries.abm.entities.Ppto_Sala_Agregado;
import crm.libraries.abm.entities.Ppto_Sala_Horario;
import crm.libraries.abm.entities.Ppto_Sala_Servicio;
import crm.libraries.abm.entities.Ppto_Sala_Servicio_Desc_Detallada;
import crm.libraries.abm.entities.Presupuesto;
import crm.libraries.abm.entities.PrtPptoCancelacion;
import crm.libraries.abm.entities.PrtPptoCondPago;
import crm.libraries.abm.entities.PrtPptoFPago;
import crm.libraries.abm.entities.PrtPptoHeader;
import crm.libraries.abm.entities.PrtPptoPeriodo;
import crm.libraries.abm.entities.PrtPptoTipoPresupuesto;
import crm.libraries.abm.entities.PrtPptoValidez;
import crm.libraries.abm.entities.SalaLugar;
import crm.libraries.abm.entities.Servicio;
import crm.libraries.abm.entities.TipoArmado;
import crm.libraries.abm.entities.TipoEvento;
import crm.libraries.abm.entities.TipoLugarEvento;
import crm.libraries.abm.entities.TipoUniforme;
import crm.libraries.abm.entities.Usuario;
import crm.libraries.abm.entities.Vendedor;
import crm.libraries.abm.entities.VendedorPpto;
import crm.libraries.abm.helper.AdelantoHelper;
import crm.libraries.abm.helper.AgregadoHelper;
import crm.libraries.abm.helper.AgregadoSalaHelper;
import crm.libraries.abm.helper.ContactoHelper;
import crm.libraries.abm.helper.ContactoLugarHelper;
import crm.libraries.abm.helper.DescDetalladaServicioHelper;
import crm.libraries.abm.helper.EstadoActualHelper;
import crm.libraries.abm.helper.FacturacionHelper;
import crm.libraries.abm.helper.GastoContratHelper;
import crm.libraries.abm.helper.HorariosHelper;
import crm.libraries.abm.helper.PagoHelper;
import crm.libraries.abm.helper.PresupuestoHelper;
import crm.libraries.abm.helper.RentabilidadHelper;
import crm.libraries.abm.helper.SalaHelper;
import crm.libraries.abm.helper.ServicioHelper;
import crm.services.sei.EstadoEventoManagerSEI;

public class PresupuestoUtil {
	 
	private static final Log log = LogFactory.getLog(PresupuestoUtil.class);
	
	private Session session;
	
	private PresupuestoUtil(Session session){
		this.session = session;
	}
	
	public static PresupuestoUtil instance(Session session){
		return new PresupuestoUtil(session);
	}
	
	/**
	 * Convierte un PresupuestoHelper a Presupuesto
	 * 
	 * @param pa el presupuestoHelper que viene del cliente
	 *  
	 */
	public Presupuesto toPresupuesto(PresupuestoHelper pa) {

		// si el presupuesto es null salgo
		if (pa == null) {
			if (log.isErrorEnabled())
				log.error("El PresupuestoHelper no puede ser NULL");
			return null;
		}
		
		if (pa.getCodigoUsuario() == null) {
			if (log.isErrorEnabled())
				log.error("El Id del usuario no puede ser NULL");
			return null;
		}
		
		// traigo el presupuesto
		Presupuesto p=null;
		
		// traigo el presupuesto de la base si es que existe.
		if (pa.getNumeroDePresupuesto()>0){
			try {
				if (log.isDebugEnabled())
					log.debug("Precargando informaci�n del presupuesto..." + pa.getNumeroDePresupuesto());
				
				p = (Presupuesto)session.load(Presupuesto.class, pa.getNumeroDePresupuesto());
			} catch (ObjectNotFoundException e) {}
		}
		
		if (p==null){
			if (log.isDebugEnabled())
				log.debug("Creando un nuevo presupuesto...");
			p = new Presupuesto();
		}

		// Grabar el estado actual y agregar los cambios de estados
		procesarCambiosDeEstado(p,pa);
		
		// Guardar el vendedor que realizo la operacion
		updateVendedor(pa, p);

		// Guardo el cliente
		updateCliente(pa, p);

		if (log.isDebugEnabled())
			log.debug("Cargando datos varios...");

		// otros datos varios
		p.setObservacionesDelCliente(pa.getObservacionesDelCliente());
		p.setFechaDeInicio(pa.getFechaDeInicio());
		p.setFechaDeFinalizacion(pa.getFechaDeFinalizacion());
		p.setFechaDeInstalacion(pa.getFechaDeInstalacion());
		p.setTotalDePersonas(pa.getTotalDePersonas());
		p.setNombreDelEvento(pa.getNombreDelEvento());
		p.setObservacionesDelEvento(pa.getObservacionesDelEvento());
		p.setCodigoReferencia(Integer.parseInt(pa.getCodigoReferencia()));
		p.setResponsableEvento(pa.getResponsableEvento());
		p.setResponsableTel(pa.getResponsableTel());
		p.setResponsableEmail(pa.getResponsableEmail());
		p.setResponsableNextelFlota(pa.getResponsableNextelFlota());
		p.setResponsableNextelId(pa.getResponsableNextelId());
		p.setActivo(pa.getActivo());
		p.setCotizacion(pa.getCotizacion());
		// cargar el lugar del evento
		LugarEvento lugarDelEvento = updateLugarDelEvento(pa, p);

		// grabo el tipo de evento
		if (compareEntities(p.getTipoDeEvento(),pa.getCodigoTipoDeEvento())){
			if (log.isDebugEnabled())
				log.debug("Grabando tipo de evento...");
			
			p.setTipoDeEvento((TipoEvento) session.load(TipoEvento.class, pa.getCodigoTipoDeEvento()));
		}
		
		// grabo el header en el reporte de presupuesto
		if (compareEntities(p.getEncabezadoPpto(),pa.getCodigoEncabezado())){
			if (log.isDebugEnabled())
				log.debug("Grabando encabezado...");
			
			p.setEncabezadoPpto((PrtPptoHeader) session.load(PrtPptoHeader.class, pa.getCodigoEncabezado()));
		}
		
//		 grabo la forma de pago en el reporte de presupuesto
		if (compareEntities(p.getFormaPagoPpto(),pa.getCodigoFormaPago())){
			if (log.isDebugEnabled())
				log.debug("Grabando forma de pago...");
			
			p.setFormaPagoPpto((PrtPptoFPago) session.load(PrtPptoFPago.class, pa.getCodigoFormaPago()));
		}
		
//		 grabo la forma de pago en el reporte de presupuesto
		if (compareEntities(p.getCondPagoPpto(),pa.getCodigoCondPago())){
			if (log.isDebugEnabled())
				log.debug("Grabando condicion de pago...");
			
			p.setCondPagoPpto((PrtPptoCondPago) session.load(PrtPptoCondPago.class, pa.getCodigoCondPago()));
		}
		
//		 grabo la validez en el reporte de presupuesto
		if (compareEntities(p.getValidezPpto(),pa.getCodigoValidez())){
			if (log.isDebugEnabled())
				log.debug("Grabando validez...");
			
			p.setValidezPpto((PrtPptoValidez) session.load(PrtPptoValidez.class, pa.getCodigoValidez()));
		}		

//		 grabo el tipo de presupuesto en el reporte de presupuesto
		if (compareEntities(p.getTipoPpto(),pa.getCodigoTipoPpto())){
			if (log.isDebugEnabled())
				log.debug("Grabando tipo de presupuesto...");
			
			p.setTipoPpto((PrtPptoTipoPresupuesto) session.load(PrtPptoTipoPresupuesto.class, pa.getCodigoTipoPpto()));
		}	
		
//		 grabo la cancelacion de presupuesto en el reporte de presupuesto
		if (compareEntities(p.getCancelacionPpto(),pa.getCodigoCancelacion())){
			if (log.isDebugEnabled())
				log.debug("Grabando cancelacion de presupuesto...");
			
			p.setCancelacionPpto((PrtPptoCancelacion) session.load(PrtPptoCancelacion.class, pa.getCodigoCancelacion()));
		}	
		
//		 grabo el periodo de presupuesto en el reporte de presupuesto
		if (compareEntities(p.getPeriodoPpto(),pa.getCodigoPeriodo())){
			if (log.isDebugEnabled())
				log.debug("Grabando periodo de presupuesto...");
			
			p.setPeriodoPpto((PrtPptoPeriodo) session.load(PrtPptoPeriodo.class, pa.getCodigoPeriodo()));
		}
		
//		 grabo la moneda de presupuesto en el reporte de presupuesto
		if (compareEntities(p.getMoneda(),pa.getCodigoMoneda())){
			if (log.isDebugEnabled())
				log.debug("Grabando moneda de presupuesto...");
			
			p.setMoneda((MonedaExtranjera) session.load(MonedaExtranjera.class, pa.getCodigoMoneda()));
		}	
		
		// graba el tipo del lugar
		if (compareEntities(p.getTipoDeLugarDelEvento(),pa.getCodigoTipoDeLugarDelEvento())){
			if (log.isDebugEnabled())
				log.debug("Grabando tipo de lugar del evento...");
			
			p.setTipoDeLugarDelEvento((TipoLugarEvento) session.load(TipoLugarEvento.class, pa.getCodigoTipoDeLugarDelEvento()));
		}

		if (compareEntities(p.getUniforme(),pa.getCodigoUniforme())){
			if (log.isDebugEnabled())
				log.debug("Grabando tipo de uniforme...");
		
			p.setUniforme((TipoUniforme) session.load(TipoUniforme.class, pa.getCodigoUniforme()));
		}
		
		// elimino las salas anteriores si es que las tiene
		//borrarSalasAnteriores(p);
		
		// creo las salas nuevas
		crearSalas(p, pa, lugarDelEvento); // TODO lo comente para q compile

		// -----------gastos----------
		crearGastosSC(p, pa);
		crearGastosOperador(p, pa);
		crearGastosAsistentes(p, pa);
		crearGastosViaticos(p, pa);
		crearGastosHoteleria(p, pa);
		crearGastosRepresentacion(p, pa);
		crearGastosVarios(p, pa);

		// -----------------------------
		
		updateContacto(p,pa);
		
		updateContactoLugar(p,pa);
		
		updateFacturacion(p,pa);
		
		updateRentabilidad(p,pa);
		
		updatePago(p,pa);

		updateAgregado(p,pa);
		
		updateAdelanto(p,pa);
		
		return p;
	}


	/**
	 * Actualizo el lugar del evento solo si este cambio.
	 * 
	 * @param pa
	 * @param p
	 * @return
	 */
	private LugarEvento updateLugarDelEvento(PresupuestoHelper pa, Presupuesto p) {
		
		LugarEvento lugarDelEvento = null;
		
		if (compareEntities(p.getLugarDelEvento(),pa.getCodigoLugarDelEvento())){
			if (log.isDebugEnabled())
				log.debug("Grabando el lugar del evento..." + pa.getCodigoLugarDelEvento());
			
			lugarDelEvento = (LugarEvento) session.load(LugarEvento.class, pa.getCodigoLugarDelEvento());

			p.setLugarDelEvento(lugarDelEvento);
		}
		return lugarDelEvento;
	}

	/**
	 * Graba el cliente, solo si este cambio
	 * @param pa
	 * @param p
	 */
	private void updateCliente(PresupuestoHelper pa, Presupuesto p) {
		// jamas deberia ser null porque es obligatorio
		if (pa.getCodigoCliente() != null) {
			if (log.isDebugEnabled())
				log.debug("Asignando Cliente..." + pa.getCodigoCliente());

			String id = pa.getCodigoCliente();
			Cliente cliente = p.getCliente();
			
			if (cliente == null || !id.equals(cliente.getCodigo())){
				// Cargar el cliente
				p.setCliente((Cliente) session.load(Cliente.class, id));
			}
		}
	}

	/**
	 * Asigna el vendedor si este cambio.
	 * @param pa
	 * @param p
	 */
	private void updateVendedor(PresupuestoHelper pa, Presupuesto p) {
		if (pa.getCodigoVendedor() != null) {
			if (log.isDebugEnabled())
				log.debug("Asignando Vendedor..." + pa.getCodigoVendedor());

			String id = pa.getCodigoVendedor();
			
			VendedorPpto vppto = p.getVendedor();
			if (vppto == null)
				vppto = new VendedorPpto();
			
			if ((vppto.getVendedor() == null)){ //|| !id.equals(vppto.getVendedor().getCodigo())){
				vppto.setFecha(DateConverter.convertDateToString(new Date(),"yyyy-MM-dd HH:mm:ss"));
				vppto.setVendedor((Vendedor) session.load(Vendedor.class, id));
				p.setVendedor(vppto);
				vppto.setPresupuesto(p);
			}
		}
	}
	
	/**
	 * Guarda los datos de seguimiento
	 * 
	 * @param p
	 * @param pa
	 * @return
	 */
	/*private void updateSeguimiento (Presupuesto p, PresupuestoHelper pa){
		SeguimientoHelper segHelper = pa.getSeguimiento();
		
		Ppto_Seguimiento seguimiento = p.getSeguimiento();
		if (seguimiento == null)
			seguimiento = new Ppto_Seguimiento();
		
		if (!String.valueOf(segHelper.getCodigoUsuario()).equals(seguimiento.getCodigoUsuario()) ||
				!String.valueOf(segHelper.getCodigoSeguimiento()).equals(seguimiento.getCodigoSeguimiento()) ||
				!String.valueOf(segHelper.getCodigoSeguimientoRespuesta()).equals(seguimiento.getCodigoSeguimientoRespuesta())){
			seguimiento.setCodigoUsuario(String.valueOf(segHelper.getCodigoUsuario()));
			seguimiento.setCodigoSeguimiento(String.valueOf(segHelper.getCodigoSeguimiento()));
			seguimiento.setCodigoSeguimientoRespuesta(String.valueOf(segHelper.getCodigoSeguimientoRespuesta()));
			seguimiento.setFechaYHora(String.valueOf(segHelper.getCodigoSeguimientoRespuesta()));
			seguimiento.setObservaciones(String.valueOf(segHelper.getObservaciones()));
			seguimiento.setPresupuesto(p);
			p.setSeguimiento(seguimiento);
		}
	}*/
	
	/**
	 * Guarda los datos de facturacion
	 * 
	 * @param p
	 * @param pa
	 * @return
	 */
	private void updateFacturacion (Presupuesto p, PresupuestoHelper pa){
		FacturacionHelper factHelper = pa.getFacturacion();
		
		Ppto_Facturacion facturacion = p.getFacturacion();
		if (facturacion == null)
			facturacion = new Ppto_Facturacion();
		
		if (!String.valueOf(factHelper.getCodCliente()).equals(facturacion.getCodCliente())){
			facturacion.setCodCliente(String.valueOf(factHelper.getCodCliente()));
			facturacion.setPresupuesto(p);
			p.setFacturacion(facturacion);
		}
	}
	
	/**
	 * Guarda los datos de rentabilidad
	 * 
	 * @param p
	 * @param pa
	 * @return
	 */
	private void updateRentabilidad (Presupuesto p, PresupuestoHelper pa){
		RentabilidadHelper rentHelper = pa.getRentabilidad();
		
		Ppto_Rentabilidad rentabilidad = p.getRentabilidad();
		if (rentabilidad == null)
			rentabilidad = new Ppto_Rentabilidad();
		
		if (rentHelper != null && !String.valueOf(rentHelper.getFacturacionOriginal()).equals(rentabilidad.getFacturacionOriginal()) ||
				!String.valueOf(rentHelper.getFacturacionExtra()).equals(rentabilidad.getFacturacionExtra()) ||
				!String.valueOf(rentHelper.getCostoOperativo()).equals(rentabilidad.getCostoOperativo()) ||
				!String.valueOf(rentHelper.getGastosAsistentes()).equals(rentabilidad.getGastosAsistentes()) ||
				!String.valueOf(rentHelper.getGastosContrataciones()).equals(rentabilidad.getGastosContrataciones()) ||
				!String.valueOf(rentHelper.getGastosOperadores()).equals(rentabilidad.getGastosOperadores()) ||
				!String.valueOf(rentHelper.getGastosOtros()).equals(rentabilidad.getGastosOtros()) ||
				!String.valueOf(rentHelper.getComisionesLugar()).equals(rentabilidad.getComisionesLugar()) ||
				!String.valueOf(rentHelper.getComisionesTerceros()).equals(rentabilidad.getComisionesTerceros())){
			rentabilidad.setFacturacionOriginal(String.valueOf(rentHelper.getFacturacionOriginal()));
			rentabilidad.setFacturacionExtra(String.valueOf(rentHelper.getFacturacionExtra()));
			rentabilidad.setCostoOperativo(String.valueOf(rentHelper.getCostoOperativo()));
			rentabilidad.setGastosAsistentes(String.valueOf(rentHelper.getGastosAsistentes()));
			rentabilidad.setGastosContrataciones(String.valueOf(rentHelper.getGastosContrataciones()));
			rentabilidad.setGastosOperadores(String.valueOf(rentHelper.getGastosOperadores()));
			rentabilidad.setGastosOtros(String.valueOf(rentHelper.getGastosOtros()));
			rentabilidad.setComisionesLugar(String.valueOf(rentHelper.getComisionesLugar()));
			rentabilidad.setComisionesTerceros(String.valueOf(rentHelper.getComisionesTerceros()));
			
			rentabilidad.setPresupuesto(p);
			p.setRentabilidad(rentabilidad);
		}
	}
	
	/**
	 * Guarda los datos agrgados como modo de ingreso de equipo y seguridad del mismo
	 * 
	 * @param p
	 * @param pa
	 * @return
	 */
	private void updateAgregado (Presupuesto p, PresupuestoHelper pa){
		AgregadoHelper agregadoHelper = pa.getAgregado();
		
		Ppto_Agregado agregado = p.getAgregado();
		
		if (agregado == null)
			agregado = new Ppto_Agregado();
		
		if (!String.valueOf(agregadoHelper.getModoIngreso()).equals(agregado.getModoIngreso()) ||
				!String.valueOf(agregadoHelper.getSeguridadIngreso()).equals(agregado.getSeguridadIngreso()) ||
				!String.valueOf(agregadoHelper.getCategoriaEvento()).equals(agregado.getCategoriaEvento())){
			agregado.setModoIngreso(String.valueOf(agregadoHelper.getModoIngreso()));
			agregado.setCategoriaEvento(String.valueOf(agregadoHelper.getCategoriaEvento()));
			agregado.setSeguridadIngreso(String.valueOf(agregadoHelper.getSeguridadIngreso()));
			agregado.setTipoVenta(String.valueOf(agregadoHelper.getTipoVenta()));
			agregado.setPresupuesto(p);
			p.setAgregado(agregado);
		}
		
	}
	
	/**
	 * Guarda los datos de adelanto, valor y porcentaje
	 * 
	 * @param p
	 * @param pa
	 * @return
	 */
	private void updateAdelanto (Presupuesto p, PresupuestoHelper pa){
		AdelantoHelper adelantoHelper = pa.getAdelanto();
		
		Ppto_Adelanto adelanto = p.getAdelanto();
		
		if (adelanto == null)
			adelanto = new Ppto_Adelanto();
		
		if (!String.valueOf(adelantoHelper.getValor()).equals(adelanto.getValor()) ||
				!String.valueOf(adelantoHelper.getPorcentaje()).equals(adelanto.getPorcentaje())){
			adelanto.setValor(String.valueOf(adelantoHelper.getValor()));
			adelanto.setPorcentaje(String.valueOf(adelantoHelper.getPorcentaje()));			
			adelanto.setPresupuesto(p);
			p.setAdelanto(adelanto);
		}
		
	}
	
	/**
	 * Guarda los datos agrgados como modo de ingreso de equipo y seguridad del mismo
	 * 
	 * @param p
	 * @param pa
	 * @return
	 */
	/*private void updateAdelanto(Presupuesto p, PresupuestoHelper pa){
		AdelantoHelper adelantoHelper = pa.getAdelanto();
		
		Ppto_Adelanto adelanto = p.getAdelanto();
		
		if (adelanto == null)
			adelanto = new Ppto_Adelanto();
		
		if (adelantoHelper.getValor() != adelanto.getValor()){
			adelanto.setValor(adelantoHelper.getValor());
			adelanto.setPresupuesto(p);
			p.setAdelanto(adelanto);
		}
		
	}*/
	
	/**
	 * Guarda los datos del contacto
	 * 
	 * @param p
	 * @param pa
	 * @return
	 */
	private void updateContacto (Presupuesto p, PresupuestoHelper pa){
		
		ContactoHelper ch = pa.getContacto();
		
		Ppto_Contacto pc = p.getContacto();
		if (pc == null)
			pc = new Ppto_Contacto();
		
//		(Ppto_Contacto) session.load(Ppto_Contacto.class, pa.getContacto().getCodContacto());
		
		if (!String.valueOf(ch.getCodContacto()).equals(pc.getCodContacto())){
			pc.setCodContacto(String.valueOf(ch.getCodContacto()));
			pc.setPresupuesto(p);
			p.setContacto(pc);
		}
	}
	
	private void updateContactoLugar (Presupuesto p, PresupuestoHelper pa){
		
		ContactoLugarHelper ch = pa.getContactoLugar();
		
		Ppto_Contacto_Lugar pc = p.getContactoLugar();
		if (pc == null)
			pc = new Ppto_Contacto_Lugar();
		
//		(Ppto_Contacto) session.load(Ppto_Contacto.class, pa.getContacto().getCodContacto());
		
		if (!String.valueOf(ch.getCodContacto()).equals(pc.getCodContacto())){
			pc.setCodContacto(String.valueOf(ch.getCodContacto()));
			pc.setPresupuesto(p);
			p.setContactoLugar(pc);
		}
	}
	
	/**
	 * Guarda un Pago
	 * 
	 * @param entity
	 * @param codigo
	 * @return
	 */
	private void updatePago(Presupuesto p, PresupuestoHelper pa){
		PagoHelper pagoHelper = pa.getPago();
		
		Ppto_Pago pago = p.getPago();
		if (pago == null)
			pago = new Ppto_Pago();
		
		if (!String.valueOf(pagoHelper.getCodMedioPago()).equals(pago.getCodMedioPago()) ||
				!String.valueOf(pagoHelper.getCodCondicionPago()).equals(pago.getCodCondicionPago())){
			pago.setCodMedioPago(String.valueOf(pagoHelper.getCodMedioPago()));
			pago.setCodCondicionPago(String.valueOf(pagoHelper.getCodCondicionPago()));
			pago.setPresupuesto(p);
			p.setPago(pago);
		}
	}
	
	/**
	 * Indica si el codigo es igual o no al codigo de la entidad
	 * 
	 * @param entity
	 * @param codigo
	 * @return
	 */
	private boolean compareEntities(ABMEntity entity, String codigo){
		return (codigo != null && (entity == null || !entity.getCodigo().equals(codigo)));
	}
	
	/**
	 * Convierte un EstadoActualHelper a PptoEstadoActual
	 * @param p
	 * @return
	 */
	private PptoEstadoActual toEstadoActual(EstadoActualHelper estado) {
		PptoEstadoActual ea = new PptoEstadoActual();
		
		ea.setActualizado(estado.getActualizado());
		ea.setCancelado(estado.getCancelado());
		ea.setCobrado(estado.getCobrado());
		ea.setConfirmado(estado.getConfirmado());
		ea.setFacturado(estado.getFacturado());
		ea.setNuevo(estado.getNuevo());
		ea.setOf(estado.getOrdenDeFacturacion());
		ea.setOs(estado.getOrdenDeServicio());
		ea.setRechazado(estado.getRechazado());
		ea.setOc(estado.getOrdenDeCompra());
		ea.setAdelantado(estado.getAdelantado());
		ea.setAdelanto(estado.getAdelanto());
		ea.setAcobrar(estado.getAcobrar());
		ea.setAdelantoacobrar(estado.getAdelantoacobrar());
		ea.setAdelantocobrado(estado.getAdelantocobrado());
		ea.setAdicionales(estado.getAdicionales());
		ea.setAdicionalesFacturados(estado.getAdicionalesFacturados());
		
		return ea;
	}
	
	/**
	 * Verifica si hubo un cambio de estado y lo agrega a la lista si es necesario
	 * @param p presupuesto
	 * @param estadoActual estado actual del presupuesto p
	 */
	private void procesarCambiosDeEstado(Presupuesto p, PresupuestoHelper pa){
		if (log.isDebugEnabled())
			log.debug("Verificando estado anterior del presupuesto...");
		
		// obtengo el estado actual
		PptoEstadoActual nuevoEstado = toEstadoActual(pa.getEstado());
		
		// traigo el usuario
		Usuario user = (Usuario) session.load(Usuario.class, pa.getCodigoUsuario());
		
		// si es nuevo agrego el estado nuevo
		if (p.isNew()){
			agregarCambioEstado(p, EstadoEventoManagerSEI.CODIGO_ESTADO_NUEVO, user);
            nuevoEstado.setNuevo(1);
		}
		
		// sino lo hago modificado
		else {
			agregarCambioEstado(p, EstadoEventoManagerSEI.CODIGO_ESTADO_ACTUALIZADO, user);
            nuevoEstado.setActualizado(1);
		}

		// ahora veo que estados cambiaron
		PptoEstadoActual estadoActual = p.getEstadoActual();
		
		// si es null, entonces le doy new para que tenga todo en 0.
		if (estadoActual == null)
			estadoActual = new PptoEstadoActual();
	
		if (estadoActual.getCancelado() == 0 && nuevoEstado.getCancelado() == 1)
			agregarCambioEstado(p, EstadoEventoManagerSEI.CODIGO_ESTADO_CANCELADO, user);
		
		if (estadoActual.getConfirmado() == 0 && nuevoEstado.getConfirmado() == 1)
			agregarCambioEstado(p, EstadoEventoManagerSEI.CODIGO_ESTADO_CONFIRMADO, user);
		
		if (estadoActual.getRechazado() == 0 && nuevoEstado.getRechazado() == 1)
			agregarCambioEstado(p, EstadoEventoManagerSEI.CODIGO_ESTADO_RECHAZADO, user);
		
		if (estadoActual.getOs() == 0 && nuevoEstado.getOs() == 1)
			agregarCambioEstado(p, EstadoEventoManagerSEI.CODIGO_ESTADO_ORDEN_SERVICIO, user);
		
		if (estadoActual.getOf() == 0 && nuevoEstado.getOf() == 1)
			agregarCambioEstado(p, EstadoEventoManagerSEI.CODIGO_ESTADO_ORDEN_FACTURACION, user);
		
		if (estadoActual.getOc() == 0 && nuevoEstado.getOc() == 1)
			agregarCambioEstado(p, EstadoEventoManagerSEI.CODIGO_ESTADO_ORDEN_COMPRA, user);
		
		if (estadoActual.getFacturado() == 0 && nuevoEstado.getFacturado() == 1)
			agregarCambioEstado(p, EstadoEventoManagerSEI.CODIGO_ESTADO_FACTURADO, user);
		
		if (estadoActual.getCobrado() == 0 && nuevoEstado.getCobrado() == 1)
			agregarCambioEstado(p, EstadoEventoManagerSEI.CODIGO_ESTADO_COBRADO, user);
		
		if (estadoActual.getAdelanto() == 0 && nuevoEstado.getAdelanto() == 1)
			agregarCambioEstado(p, EstadoEventoManagerSEI.CODIGO_ESTADO_ADELANTO, user);
		
		if (estadoActual.getAdelantado() == 0 && nuevoEstado.getAdelantado() == 1)
			agregarCambioEstado(p, EstadoEventoManagerSEI.CODIGO_ESTADO_ADELANTADO, user);
		
		if (estadoActual.getAdelantoacobrar() == 0 && nuevoEstado.getAdelantoacobrar() == 1)
			agregarCambioEstado(p, EstadoEventoManagerSEI.CODIGO_ESTADO_ADELANTO_A_COBRAR, user);
		
		if (estadoActual.getAcobrar() == 0 && nuevoEstado.getAcobrar() == 1)
			agregarCambioEstado(p, EstadoEventoManagerSEI.CODIGO_ESTADO_A_COBRAR, user);
		
		if (estadoActual.getAdelantocobrado() == 0 && nuevoEstado.getAdelantocobrado() == 1)
			agregarCambioEstado(p, EstadoEventoManagerSEI.CODIGO_ESTADO_ADELANTO_COBRADO, user);
		
		if (estadoActual.getAdicionales() == 0 && nuevoEstado.getAdicionales() == 1)
			agregarCambioEstado(p, EstadoEventoManagerSEI.CODIGO_ESTADO_ADICIONALES, user);
		
		if (estadoActual.getAdicionalesFacturados() == 0 && nuevoEstado.getAdicionalesFacturados() == 1)
			agregarCambioEstado(p, EstadoEventoManagerSEI.CODIGO_ESTADO_ADICIONALES_FACTURADOS, user);
		
		if (log.isDebugEnabled())
			log.debug("Asignando estado Nuevo al presupuesto...");
		
		// Grabar estado actual
		estadoActual.setEstadoActual(nuevoEstado);
		estadoActual.setPresupuesto(p);
		p.setEstadoActual(estadoActual);
	}
	
	/**
	 * Crea un cambio de estado segun el codigo indicado
	 * @param codigoEstado codigo del cambio de estado, 
	 * @see EstadoEventoManagerSEI para las constantes
	 * @return
	 */
	private void agregarCambioEstado(Presupuesto p, String codigoEstado, Usuario user){
		if (log.isDebugEnabled())
			log.debug("Agregando cambio de estado a "+codigoEstado+" realizado por el usuario "+user.getApellidoYNombre());

		PptoCambioEstado ca = new PptoCambioEstado();
		ca.setEstado((EstadoEvento) session.load(EstadoEvento.class, codigoEstado));
		ca.setFecha(DateConverter.convertDateToString(new Date(),"yyyy-MM-dd HH:mm:ss"));
		ca.setUsuario(user);
		
		// Guardar el cambio de estado
		ca.setPresupuesto(p);
		p.addCambioEstado(ca);
	}
	

	/**
	 * Elimina las salas asociadas con este presupuesto.
	 * 
	 * @param p
	 */
	private void borrarSalasAnteriores(Presupuesto p){		
		Set<Ppto_Sala> salas = p.getSalas();
		if (salas != null && !salas.isEmpty()){
			for (Ppto_Sala sala : salas) {
				Ppto_Sala s = (Ppto_Sala)session.load(Ppto_Sala.class, sala.getId());
				
				if (log.isDebugEnabled())
					log.debug("Borrando sala: "+s.getId()+"  "+s.getSala().getDescripcion());

				session.delete(s);
			}
			
			session.getTransaction().commit();
			session.getTransaction().begin();
		}
		
	}
	
	/**
	 * Crea las salas para asignar a este presupuesto
	 * 
	 * @param p
	 * @param pa
	 * @param lugarDelEvento
	 */
	private void crearSalas(Presupuesto p, PresupuestoHelper pa, LugarEvento lugarDelEvento) {

		if (p.getSalas() == null)
			p.setSalas(new HashSet<Ppto_Sala>());
		else
			p.getSalas().clear();
		
		SalaHelper[] salas2 = pa.getSalas();
		SalaHelper[] salas = CollectionUtil.orderSalasArray(salas2);
		if (salas != null) {
			for (SalaHelper salaAbs : salas) {
				if (log.isDebugEnabled())
					log.debug("Creando nueva sala...");
				
				Ppto_Sala sala = new Ppto_Sala();
				
				sala.setOrden(Integer.toString(salaAbs.getOrden()));

				if (log.isDebugEnabled())
					log.debug("Cargando sala lugar...");

				sala.setSala((SalaLugar) session.load(SalaLugar.class, salaAbs.getCodigoSalaLugar()));

				if (log.isDebugEnabled())
					log.debug("Cargando datos de la sala...");
				
				sala.setFechaDeInicio(salaAbs.getFechaDeInicio());
				sala.setFechaDeFinalizacion(salaAbs.getFechaDeFinalizacion());
				sala.setFechaDeInstalacion(salaAbs.getFechaDeInstalacion());
				sala.setTotalDePersonas(salaAbs.getTotalDePersonas());
				sala.setPresupuesto(p);
				sala.setObservaciones(salaAbs.getObservaciones());
				sala.setNombreSalaUnico(salaAbs.getNombreSalaUnico());
				
				if(salaAbs.getTipoArmado() != null){
					if (compareEntities(sala.getTipoArmado(),salaAbs.getTipoArmado())){
						if (log.isDebugEnabled())
							log.debug("Grabando tipo armado de la sala...");
						
						sala.setTipoArmado((TipoArmado) session.load(TipoArmado.class, salaAbs.getTipoArmado()));
					}
				}			
					
				sala.setFechaDesarme(salaAbs.getFechaDesarme());
				sala.setFechaPrueba(salaAbs.getFechaPrueba());
				
				crearServicios(pa, salaAbs, sala, lugarDelEvento);
				crearHorarios(salaAbs,sala);
				//crearAgregados(salaAbs, sala);
				p.addSala(sala);				
			}
		}
	}
	
	/*
	 * Crea los agregado como son fechas de desarme y pruebas y el tipo de armado de equipos
	 * @param salaAbs
	 * @param sala
	 */
	private void crearHorarios(SalaHelper salaAbs, Ppto_Sala sala){
		HorariosHelper[] agrega = salaAbs.getHorarios();
		if(agrega==null){
			if (log.isDebugEnabled())
				log.debug("LOS HORARIOS SON NULL!!!!!!!!!!!!!");
		}
		if(agrega != null) {
			for(HorariosHelper h : agrega){
			Ppto_Sala_Horario accesorio = new Ppto_Sala_Horario();

			accesorio.setFecha(h.getFecha());
			accesorio.setHoraDesde(h.getHoraDesde());
			accesorio.setHoraHasta(h.getHoraHasta());
			accesorio.setSala(sala);
			sala.addHorario(accesorio);
		}
				
		}
	}
	
	/**
	 * Crea los servicios
	 * 
	 * @param pa
	 * @param salaAbs
	 * @param sala
	 * @param lugarDelEvento
	 */
	private void crearServicios(PresupuestoHelper pa, SalaHelper salaAbs, Ppto_Sala sala, LugarEvento lugarDelEvento) {

		if (log.isDebugEnabled())
			log.debug("Creando Servicios...");

		ServicioHelper[] servicios2 = salaAbs.getServicios();
		ServicioHelper[] servicios = CollectionUtil.orderServiciosArray(servicios2);
		if (servicios != null) {
			for (ServicioHelper servicioAbs : servicios) {				

				Ppto_Sala_Servicio servicio = new Ppto_Sala_Servicio();
				servicio.setOrden(Integer.toString(servicioAbs.getOrden()));
				servicio.setCantidad(Integer.toString(servicioAbs.getCantidad()));
				servicio.setSala(sala);
				

				if (log.isDebugEnabled())
					log.debug("Creando nuevo servicio...");

				Servicio _serv = (Servicio) session.load(Servicio.class, Integer.toString(servicioAbs.getCodigoServicio()));

				servicio.setServicio(_serv);
				servicio.setModalidad((ModalidadContrat) session.load(ModalidadContrat.class, Integer.toString(servicioAbs.getCodigoModalidadContratacion())));
				servicio.setDias(Integer.toString(servicioAbs.getDias()));

				// asigno la fecha de creaci�n
				//servicio.setFechaCreacion(DateConverter.convertDateToString(new Date(),"yyyy-MM-dd HH:mm:ss"));
				
				servicio.setFechaCreacion(servicioAbs.getFechaAlta());
				servicio.setPrecioDescuento(Double.toString(servicioAbs.getPrecioDescuento()));
				servicio.setPrecioDeLista(Double.toString(servicioAbs.getPrecioDeLista()));
				servicio.setDescuento(Integer.toString(servicioAbs.getDescuento())); // preguntar
				servicio.setDetalle(servicioAbs.getDetalle());
				servicio.setModificado(servicioAbs.getModificado());
				/*try {
					servicio.setPrecioDescuento(Double.toString(servicioAbs.getPrecioDeLista() * ((servicioAbs.getDescuento()+100) / 100)));
				} catch (Exception e) {
					System.out.println("Error parseando descuento PresupuestoManager " + e.getMessage());
				}*/

				//crearAccesorios(servicioAbs, servicio);
				crearDescDetallada(servicioAbs, servicio);
				sala.addServicio(servicio);

				// le agrego el servicio al gasto indicado
				Ppto_GastoSC gasto = getGastoSCForServicio(pa, servicioAbs);
				if (gasto != null) {
					gasto.setPpto_Sala_Servicio(servicio);
				}
				
			}
		}
	}
	
	/**
	 * Crea las descripciones detalladas
	 * @param salaAbs
	 * @param sala
	 */
	private void crearDescDetallada(ServicioHelper servicioAbs, Ppto_Sala_Servicio servicio){
		if (servicioAbs.getDescDetallada() != null) {

			for (DescDetalladaServicioHelper descAbs : servicioAbs.getDescDetallada()) {

				if (!descAbs.getDescripcion().equals("")) {

					Ppto_Sala_Servicio_Desc_Detallada descripcion = new Ppto_Sala_Servicio_Desc_Detallada();
					
					descripcion.setDescripcion(descAbs.getDescripcion());
					descripcion.setServicio(servicio);

					servicio.addDescDetallada(descripcion);
				} else {

				}
			}
		}
	}
	
	/**
	 * Crea los gastos por servicio
	 * 
	 * @param ps
	 * @param servicio
	 * @return
	 */
	private Ppto_GastoSC getGastoSCForServicio(PresupuestoHelper ps, ServicioHelper servicio) {
		servicio.getTableItemId();

		GastoContratHelper[] gch = ps.getGastosContratHelper();
		if (gch != null) {		

			for (int i = 0; i < gch.length; i++) {
				if (gch[i].getSalaServicioTableItemId() > 0 && gch[i].getSalaServicioTableItemId() == servicio.getTableItemId()) {
					if (log.isDebugEnabled()){
						log.debug("Buscando gastos para el servicio: " + servicio.getCodigoServicio());
						for(int j = 0; j < 10;j++){
							log.debug("--------------------------------------------------------------------------");
						}
						log.debug("Buscando gastos para el servicio: " + servicio.getCodigoServicio()+ " tableID :"+servicio.getTableItemId());
					}
					return gch[i].getPpto_GastoSC();
				}
				else if(gch[i].getSalaServicioTableItemId()<0){
					
				}
			}
		}
		return null;
	}

	/**
	 * Crea los accesorios de los servicios
	 * 
	 * @param servicioAbs
	 * @param servicio
	 */
	/*private void crearAccesorios(ServicioHelper servicioAbs, Ppto_Sala_Servicio servicio) {

		if (servicioAbs.getAccesorios() != null) {
			if (log.isDebugEnabled())
				log.debug("Creando " + servicioAbs.getAccesorios().length + " Accesorios...");

			for (AccesorioHelper accAbs : servicioAbs.getAccesorios()) {

				if (accAbs.getCantidad() > 0) {
					if (log.isDebugEnabled())
						log.debug("Creando nuevo Accesorio ..." + accAbs);

					Ppto_Sala_Servicio_Accesorio accesorio = new Ppto_Sala_Servicio_Accesorio();

					accesorio.setAccesorio((AccInstalacion) session.load(AccInstalacion.class, Integer.toString(accAbs.getIdAccesorio())));
					accesorio.setCantidad(accAbs.getCantidad());
					accesorio.setServicioDeSala(servicio);

					servicio.addAccesorio(accesorio);
				} else {
					if (log.isDebugEnabled())
						log.debug("El accesorio Accesorio '" + accAbs + "' fue salteado porque tenia cantidad = 0.");
				}
			}
		}

	}*/


	private void crearGastosSC(Presupuesto p, PresupuestoHelper pa) {		
		// inicializo el set
		if (p.getGastosSC() == null){
			p.setGastosSC(new HashSet<Ppto_GastoSC>());
		}else{
			p.getGastosSC().clear();
		}
		GastoContratHelper[] gastos = pa.getGastosContratHelper();

		/*if (log.isDebugEnabled())
			log.debug("Creando " + gastos.length + " gastos subcontratados...");
*/
		if (gastos != null) {
			for (GastoContratHelper gastoHelper : gastos) {
				if (log.isDebugEnabled())
					log.info("GASTO " + gastoHelper.getPpto_GastoSC().getDetalle() + ".................."+gastoHelper.getSalaServicioTableItemId());

				Ppto_GastoSC gastoSC = gastoHelper.getPpto_GastoSC();

				p.addGastoSC(gastoSC);
			}
		}
	}

	/*private void crearGastosSC(Presupuesto p, PresupuestoHelper pa) {		
		// inicializo el set
		if (p.getGastosSC() == null){
			p.setGastosSC(new HashSet<Ppto_GastoSC>());
		}else{
			p.getGastosSC().clear();
		}
		Ppto_GastoSC[] gastos = pa.getGastosSubcontratados();

		if (gastos != null) {
			for (Ppto_GastoSC gastoHelper : gastos) {
				p.addGastoSC(gastoHelper);
			}
		}
	}*/
	
	/**
	 * Crea los gastos del Operador
	 * 
	 * @param p
	 * @param pa
	 */
	private void crearGastosOperador(Presupuesto p, PresupuestoHelper pa) {
		// inicializo el set
		if (p.getGastosOperador() == null){
			p.setGastosOperador(new HashSet<Ppto_GastoOperador>());
		}else{
			p.getGastosOperador().clear();
		}
		if (pa.getGastosOperador() != null) {
			Ppto_GastoOperador[] gastos = pa.getGastosOperador();

			for (int i = 0; i < gastos.length; i++) {

				p.addGastoOperador(gastos[i]);
			}
		}
	}

	/**
	 * Crea los gastos por la representacion
	 * 
	 * @param p
	 * @param pa
	 */
	private void crearGastosRepresentacion(Presupuesto p, PresupuestoHelper pa) {
		// inicializo el set
		if (p.getGastosRepresentacion() == null){
			p.setGastosRepresentacion(new HashSet<Ppto_GastoRepresentacion>());
		}else{
			p.getGastosRepresentacion().clear();
		}
		if (pa.getGastosRepresentacion() != null) {
			Ppto_GastoRepresentacion[] gastos = pa.getGastosRepresentacion();

			if (log.isDebugEnabled())
				log.debug("Creando " + gastos.length + " gastos de representacion...");

			for (int i = 0; i < gastos.length; i++) {
				if (log.isDebugEnabled())
					log.debug("Creando gastos para la representaci�n: " + gastos[i].getDetalle());

				p.addGastoRepresentacion(gastos[i]);
			}
		}
	}

	/**
	 * Creando gastos de asistentes
	 * 
	 * @param p
	 * @param pa
	 */
	private void crearGastosAsistentes(Presupuesto p, PresupuestoHelper pa) {
		// inicializo el set
		if (p.getGastosAsistentes() == null){
			p.setGastosAsistentes(new HashSet<Ppto_GastoAsistentes>());
		}else{
			p.getGastosAsistentes().clear();
		}
		if (pa.getGastosAsistentes() != null) {
			Ppto_GastoAsistentes[] gastos = pa.getGastosAsistentes();

			if (log.isDebugEnabled())
				log.debug("Creando " + gastos.length + " gastos de Asistentes...");

			for (int i = 0; i < gastos.length; i++) {
				if (log.isDebugEnabled())
					log.debug("Creando gastos para el asistente: " + gastos[i].getAsistente());

				p.addGastoAsistentes(gastos[i]);
			}
		}
	}

	/**
	 * Crea los gastos por viaticos
	 * 
	 * @param p
	 * @param pa
	 */
	private void crearGastosViaticos(Presupuesto p, PresupuestoHelper pa) {
		// inicializo el set
		if (p.getGastosViaticos() == null){
			p.setGastosViaticos(new HashSet<Ppto_GastoViaticos>());
		}else{
			p.getGastosViaticos().clear();
		}
		if (pa.getGastosViaticos() != null) {
			Ppto_GastoViaticos[] gastos = pa.getGastosViaticos();

			if (log.isDebugEnabled())
				log.debug("Creando " + gastos.length + " gastos de Viaticos...");

			for (int i = 0; i < gastos.length; i++) {
				if (log.isDebugEnabled())
					log.debug("Creando gastos de viaticos para: " + gastos[i].getDetalle());

				p.addGastoViaticos(gastos[i]);
			}
		}
	}

	/**
	 * Creando gastos de Hoteleria
	 * 
	 * @param p
	 * @param pa
	 */
	private void crearGastosHoteleria(Presupuesto p, PresupuestoHelper pa) {
		// inicializo el set
		if (p.getGastosHoteleria() == null){
			p.setGastosHoteleria(new HashSet<Ppto_GastoHoteleria>());
		}else{
			p.getGastosHoteleria().clear();
		}
		if (pa.getGastosHoteleria() != null) {
			Ppto_GastoHoteleria[] gastos = pa.getGastosHoteleria();

			if (log.isDebugEnabled())
				log.debug("Creando " + gastos.length + " gastos de Hoteleria...");

			for (int i = 0; i < gastos.length; i++) {
				if (log.isDebugEnabled())
					log.debug("Creando gastos de Hoteleria para: " + gastos[i].getDetalle());

				p.addGastoHoteleria(gastos[i]);
			}
		}
	}

	/**
	 * Creando gastos varios
	 * 
	 * @param p
	 * @param pa
	 */
	private void crearGastosVarios(Presupuesto p, PresupuestoHelper pa) {
		// inicializo el set
		if (p.getGastosVarios() == null){
			p.setGastosVarios(new HashSet<Ppto_GastoVarios>());
		}else{
			p.getGastosVarios().clear();
		}
		if (pa.getGastosVarios() != null) {
			Ppto_GastoVarios[] gastos = pa.getGastosVarios();

			if (log.isDebugEnabled())
				log.debug("Creando " + gastos.length + " gastos Varios...");

			for (int i = 0; i < gastos.length; i++) {

				if (log.isDebugEnabled())
					log.debug("Creando gastos para: " + gastos[i].getDetalle());

				p.addGastoVarios(gastos[i]);
			}
		}
	}
	

	/*private void crearAccesoriosEvento(Presupuesto p, PresupuestoHelper pa) {		
		//----- inicializo los set-----------------------
		if (p.getAccesoriosAccesorio() == null){
			p.setAccesoriosAccesorio(new HashSet<Ppto_AccesorioAccesorio>());
		}else{
			p.getAccesoriosAccesorio().clear();
		}		
		if (p.getAccesoriosOperador() == null){
			p.setAccesoriosOperador(new HashSet<Ppto_AccesorioOperador>());
		}else{
			p.getAccesoriosOperador().clear();
		}		
		if (p.getAccesoriosOperadorGeneral() == null){
			p.setAccesoriosOperadorGeneral(new HashSet<Ppto_AccesorioOperadorGeneral>());
		}else{
			p.getAccesoriosOperadorGeneral().clear();
		}	
		if (p.getAccesoriosOperadorAudio() == null){
			p.setAccesoriosOperadorAudio(new HashSet<Ppto_AccesorioOperadorAudio>());
		}else{
			p.getAccesoriosOperadorAudio().clear();
		}	
		if (p.getAccesoriosOperadorVideo() == null){
			p.setAccesoriosOperadorVideo(new HashSet<Ppto_AccesorioOperadorVideo>());
		}else{
			p.getAccesoriosOperadorVideo().clear();
		}			
		if (p.getAccesoriosOperadorComputers() == null){
			p.setAccesoriosOperadorComputers(new HashSet<Ppto_AccesorioOperadorComputers>());
		}else{
			p.getAccesoriosOperadorComputers().clear();
		}			
		if (p.getAccesoriosOperadorLighting() == null){
			p.setAccesoriosOperadorLighting(new HashSet<Ppto_AccesorioOperadorLighting>());
		}else{
			p.getAccesoriosOperadorLighting().clear();
		}	
		//---------------------------------------------------------------------------
		
		

		if (pa.getAccesoriosAccesorio() != null) {
			Ppto_AccesorioAccesorio[] accesorios = pa.getAccesoriosAccesorio();
			for (int i = 0; i < accesorios.length; i++) {
				p.addAccesorioAccesorio(accesorios[i]);
			}
		}
		if (pa.getAccesoriosOperador() != null) {
			Ppto_AccesorioOperador[] accesorios = pa.getAccesoriosOperador();
			for (int i = 0; i < accesorios.length; i++) {
				p.addAccesorioOperador(accesorios[i]);
			}
		}			
		if (pa.getAccesoriosOperadorGeneral() != null) {
			Ppto_AccesorioOperadorGeneral[] accesorios = pa.getAccesoriosOperadorGeneral();
			for (int i = 0; i < accesorios.length; i++) {
				p.addAccesorioOperadorGeneral(accesorios[i]);
			}
		}		
		if (pa.getAccesoriosOperadorAudio() != null) {
			Ppto_AccesorioOperadorAudio[] accesorios = pa.getAccesoriosOperadorAudio();
			for (int i = 0; i < accesorios.length; i++) {
				p.addAccesorioOperadorAudio(accesorios[i]);
			}
		}						
		if (pa.getAccesoriosOperadorVideo() != null) {
			Ppto_AccesorioOperadorVideo[] accesorios = pa.getAccesoriosOperadorVideo();
			for (int i = 0; i < accesorios.length; i++) {
				p.addAccesorioOperadorVideo(accesorios[i]);
			}
		}			
		if (pa.getAccesoriosOperadorComputers() != null) {
			Ppto_AccesorioOperadorComputers[] accesorios = pa.getAccesoriosOperadorComputers();
			for (int i = 0; i < accesorios.length; i++) {
				p.addAccesorioOperadorComputers(accesorios[i]);
			}
		}		
		if (pa.getAccesoriosOperadorLighting() != null) {
			Ppto_AccesorioOperadorLighting[] accesorios = pa.getAccesoriosOperadorLighting();
			for (int i = 0; i < accesorios.length; i++) {
				p.addAccesorioOperadorLighting(accesorios[i]);
			}
		}
	}*/

}
