package crm.services.transaction;

import java.rmi.RemoteException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import crm.libraries.abm.entities.Cliente;
import crm.services.sei.ClienteManagerSEI;
import crm.services.util.DateConverter;
import crm.services.util.HibernateUtil;

public class ClienteManager implements ClienteManagerSEI,ManagerService {
	private static final Log log = LogFactory.getLog(ClienteManager.class);
	
	public Cliente getClienteById(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();

		Criteria c = session.createCriteria(Cliente.class);
		c.add(Expression.eq("codigo", codigo));
		c.add(Expression.eq("activo", 'S'));
		Cliente a = (Cliente) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		
		return a;
	}

	public Cliente[] getAllClientes() throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(Cliente.class);
		c.add(Expression.eq("activo",'S'));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (Cliente[])list.toArray(new Cliente[0]);
	}

	public Cliente[] findByField(String field,String value){
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(Cliente.class);
		c.add(Expression.like(field,"%" + value + "%"));
		c.add(Expression.eq("activo",'S'));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (Cliente[])list.toArray(new Cliente[0]);
	}
	
	public Object[] obtenerCodigoYNombreFantasia(String nombre) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		List list = session.createQuery
				("select c.codigo, c.nombreFantasia, c.empresa " +
				"from Cliente c " +
				"where " +
				"(c.nombreFantasia like :fantasia or c.empresa like :emp) and c.activo = 'S' ")
				.setString("fantasia", nombre+"%")
				.setString("emp", nombre+"%")
				.list();
		
		Object[] results = new Object[list.size()];
		
		for (int i=0; i< results.length;i++){ 
		    results[i] = (Object[]) list.get(i);
		}
		
		HibernateUtil.cerrarSession(session);
		
		return results;
		//return list.toArray();
	}
	
	public Object[] buscarPorNombreFantasiaOEmpresa(String nombre) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		List list = session.createQuery
				("select c.codigo, c.nombreFantasia, c.empresa " +
				"from Cliente c " +
				"where " +
				"(c.nombreFantasia like :fantasia or c.empresa like :emp) and c.activo = 'S' ")
				.setString("fantasia", "%"+nombre+"%")
				.setString("emp", "%"+nombre+"%")
				.list();
		
		Object[] results = new Object[list.size()];
		
		for (int i=0; i< results.length;i++){ 
		    results[i] = (Object[]) list.get(i);
		}
		
		HibernateUtil.cerrarSession(session);
		
		return results;
		//return list.toArray();
	}
	
	public void remove(String codigo) throws RemoteException {
		Cliente entity = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			
			entity = (Cliente) session.get(Cliente.class, codigo);						
			entity.setActivo('N');
		    
			session.saveOrUpdate(entity);

			tx.commit();
			session.flush();
		} catch (HibernateException he) {
			if (tx != null && tx.isActive())
				tx.rollback();

			he.printStackTrace(System.err);
		} finally {
			HibernateUtil.cerrarSession(session);
		}
	}

	/**
	 * Modifica un cliente
	 */
	public String update(Cliente cliente) throws RemoteException {
		Cliente c = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			if (StringUtils.isBlank(cliente.getCodigo())) {
				c = new Cliente();
				// TODO: asignar ID
				HibernateUtil.assignID(session,c);
				
				//a.setCodigo(null);
			} else {
				c = (Cliente) session.get(Cliente.class, cliente.getCodigo());
			}

			
			c.setCodigoBejerman(cliente.getCodigoBejerman());
			c.setEmpresa(cliente.getEmpresa());
			c.setCalle(cliente.getCalle());
			c.setNumero(cliente.getNumero());
			c.setPiso(cliente.getPiso());
			c.setDepartamento(cliente.getDepartamento());
			c.setCodigoPostal(cliente.getCodigoPostal());
			c.setLocalidad(cliente.getLocalidad());
			c.setPartido(cliente.getPartido());
			c.setProvincia(cliente.getProvincia());
			c.setPais(cliente.getPais());
			c.setCuit(cliente.getCuit());
			c.setIva(cliente.getIva());
			c.setPagoContacto(cliente.getPagoContacto());
			c.setPagoTelefono(cliente.getPagoTelefono());		    
			c.setNombreFantasia(cliente.getNombreFantasia());
			c.setFechaModificacion(cliente.getFechaModificacion());
			c.setActivo(cliente.getActivo());
			
		    
			session.saveOrUpdate(c);

			tx.commit();
			session.flush();
		} catch (HibernateException he) {
			if (tx != null && tx.isActive())
				tx.rollback();

			he.printStackTrace(System.err);
			// throw new SystemException(he);
		} finally {
			HibernateUtil.cerrarSession(session);
		}
		return c.getCodigo();
	}

	public Cliente[] getAllClientesTranslated(String lang) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public int getCantidadClientes() throws RemoteException {
		int q = 0;
		
		Session session = HibernateUtil.abrirSession();
		
		q = ((Integer)
				session.createQuery(
		        "select count(*) " +
		        "from Cliente c " +
		        "where c.activo = 'S'"
		        )
		        .uniqueResult())
		        .intValue();
		
		HibernateUtil.cerrarSession(session);
		
		return q;
	}
	
	/**
	 * Trae los nombres de todos los clientes de la base de datos
	 */
	public Object[] getClientesReport() throws RemoteException {
		return getClientesReportLimited(0,0);
	}
	
	public Object[] getClientesReportLimited(int firstResult, int maxResults) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		//Criteria c = session.createCriteria(Cliente.class);
		//List list = c.list();
		long time = 0;
		if (log.isDebugEnabled()){
			time = System.currentTimeMillis();
			log.debug("buscando lista de clientes");
		}
		
		// TODO: no se porque pero esto tira out of memory.. investigar,
		// ya que esta opcion deberia ser mas rapida que la que esta funcionando..
		/*List list = session.createQuery(
				"select new Cliente( c.codigo, c.empresa ) " +
				"from Cliente c " +
				"order by c.empresa"
				)
				.list();*/
		Query q = session.createQuery(
		        "select c.codigo, c.empresa, c.nombreFantasia " +
		        "from Cliente c " +
		        "where c.activo = 'S' " + 
		        "order by c.empresa"
		        );
		
		if (firstResult > 0)
		     q.setFirstResult(firstResult);
		if (maxResults > 0)
			q.setMaxResults(maxResults);		        

		List list = q.list();

		if (log.isDebugEnabled()){
			time = System.currentTimeMillis()-time;
			log.debug("resultados obtenidos en " + time + "ms. "+list.size()+" Clientes encontrados.");
			time = System.currentTimeMillis();
		}
		
		/*
		Cliente[] results = new Cliente[list.size()];
		for (int i=0; i< results.length;i++){ 
		    Object[] row = (Object[]) list.get(i);
		    results[i] = new Cliente((String) row[0],(String) row[1]);
		}*/
		
		Object[] results = new Object[list.size()];
		for (int i=0; i< results.length;i++){
			Object[] row = (Object[]) list.get(i);
			String[] sr = new String[row.length];
			for (int j=0;j<row.length;j++){
				sr[j] = row[j].toString();
			}
		    results[i] = sr;
		}

		HibernateUtil.cerrarSession(session);
		
		if (log.isDebugEnabled()){
			time = System.currentTimeMillis()-time;
			log.debug("Resultados procesados en "+time+"ms. Enviando al cliente...");
		}
		
		return results;
		//return (Cliente[])list.toArray(new Cliente[0]);
	}
	
	/**
	 * Trae la lista de Clientes que fueron modificados despues de una fecha indicada.
	 * Este m�todo se utilizar� en el cliente del webservice, para mantener sincronizada 
	 * la lista de Clientes y evitar hacer consultas constantes.
	 *  
	 * *** IMPORTANTE ***: DEBE TRAER TAMBIEN LOS INACTIVOS.
	 *  
	 * @param fecha
	 * @return
	 * @throws RemoteException
	 */
	public Object[] getClientesModificadosReport(long time) throws RemoteException {
		
		Date fecha = new Date(time);
		
		Session session = HibernateUtil.abrirSession();
		//Criteria c = session.createCriteria(Cliente.class);
		//List list = c.list();
		
		List list = session.createQuery(
				"select c.codigo, c.empresa, c.nombreFantasia, c.activo " +
				"from Cliente c " +
				"where fechaModificacion >= :fecha " +
				"order by c.empresa"
				)
				.setDate("fecha", fecha)
				.list();
		
		Object[] results = new Object[list.size()];
		for (int i=0; i< results.length;i++){ 
		    results[i] = (Object[]) list.get(i);
		}
		
		HibernateUtil.cerrarSession(session);
		
		return results;
		//return (Cliente[])list.toArray(new Cliente[0]);
	}

	/**
	 * Obtiene los datos basicos del Cliente
	 */
	public Cliente getClienteInfo(String codigo) throws RemoteException {
		
		Session session = HibernateUtil.abrirSession();
		//Criteria c = session.createCriteria(Cliente.class);
		//List list = c.list();
		
		Cliente c = (Cliente)session.createQuery(
				"select new Cliente( c.codigo, c.cuit, c.pagoContacto, c.pagoTelefono, c.iva  ) " +
				"from Cliente c " +
				"where codigo = :codigo and activo = 'S'"
				)
				.setString("codigo", codigo)
				.uniqueResult();
		
		HibernateUtil.cerrarSession(session);

		return c;
	}
	
	public void testStringArrayParam(String[] test){
		log.debug("recibi una consulta de array de strings: ");
		for (String string : test) {
			log.debug("string: "+string);
		}
	}
	
	public void testClientArrayParam(Cliente[] test){
		log.debug("recibi una consulta de array de clientes: ");
		for (Cliente c : test) {
			log.debug("cliente: "+c.getNombreFantasia());
		}
	}
	
	public void testClientParam(Cliente test){
		log.debug("recibi una consulta de cliente: ");
		log.debug("cliente: "+test.getNombreFantasia());
	}
	
	public Object[] getClienteNoCobrado(String codigo) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		
		List list = session.createSQLQuery(
				"select nroppto, creador, evento, inicio, factura_1, factura_2, importe from vw_cobranzas "+
				"where codigocliente = ?")
				.addScalar("nroppto",Hibernate.LONG)
				.addScalar("creador", Hibernate.STRING)
				.addScalar("evento", Hibernate.STRING)
				.addScalar("inicio", Hibernate.STRING)
				.addScalar("factura_1", Hibernate.STRING)
				.addScalar("factura_2", Hibernate.STRING)
				.addScalar("importe", Hibernate.STRING)
				.setString(0,codigo)
				.list();
				
				Object[] results = new Object[list.size()];
				for (int i=0; i< results.length;i++){ 
				    results[i] = (Object[]) list.get(i);
				}
				
				HibernateUtil.cerrarSession(session);
				
				return results;
	}
	
	public Object[] getClienteNoUsados(String date) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		
		Date d= null;
		try {
			d = DateConverter.convertStringToDate(date, "yyyy-MM-dd");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		List list = session.createSQLQuery(
				"SELECT cl_codcliente, nombre_fantasia, empresa, (select max(ppto_fecinicio) from tx_ppto where cl_codcliente = ppto_codcliente) as ultFechaUs FROM mst_cliente m "+
				"where cl_codcliente not in (SELECT ppto_codcliente FROM tx_ppto t) or "+
				"(cl_codcliente not in (SELECT ppto_codcliente FROM tx_ppto t WHERE t.ppto_fecinicio >= ?)) and activo='S'")
				.addScalar("cl_codcliente",Hibernate.LONG)
				.addScalar("nombre_fantasia", Hibernate.STRING)
				.addScalar("empresa", Hibernate.STRING)
				.addScalar("ultFechaUs", Hibernate.STRING)
				.setDate(0,d)
				.list();
		
		Object[] results = new Object[list.size()];
		for (int i=0; i< results.length;i++){ 
		    results[i] = (Object[]) list.get(i);
		}
		
		HibernateUtil.cerrarSession(session);
		
		return results;
	}
}
