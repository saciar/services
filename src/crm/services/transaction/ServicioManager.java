package crm.services.transaction;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.rmi.RemoteException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import crm.libraries.abm.entities.Servicio;
import crm.services.sei.ServicioManagerSEI;
import crm.services.util.CollectionUtil;
import crm.services.util.HibernateUtil;

public class ServicioManager implements ServicioManagerSEI,ManagerService {
	private static final Log log = LogFactory.getLog(ServicioManager.class);
	
	public Servicio getServicioById(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		// Servicio s = (Servicio)session.get(Servicio.class,codigo);
		Criteria c = session.createCriteria(Servicio.class);
		c.add(Expression.eq("codigo", codigo));
		c.add(Expression.eq("activo","S"));
		Servicio s = (Servicio) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return s;
	}
	
	public Servicio[] getAllServicios() throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(Servicio.class);
		c.add(Expression.eq("activo","S"));		
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (Servicio[])list.toArray(new Servicio[0]);
	}

	public Servicio[] findByField(String field,String value) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(Servicio.class);
		c.add(Expression.like(field,"%" + value + "%"));
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (Servicio[])list.toArray(new Servicio[0]);
	}
	
	public Servicio[] findByFieldExactly(String field,String value) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(Servicio.class);
		c.add(Expression.like(field,value));
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (Servicio[])list.toArray(new Servicio[0]);
	}
	
	public Servicio[] getAllServiciosTranslated(String lang) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public void remove(String codigo) throws RemoteException {
		if(codigo.equals(SERVICIO_SUBCONTRATADO)){
			throw new RemoteException("No se permite borrar este servicio");
		}
		
		Servicio entity = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			
			entity = (Servicio) session.get(Servicio.class, codigo);						
			entity.setActivo("N");
		    
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
	
	public String update(Servicio servicio, String descDetEspaniol,String descDetIngles) throws RemoteException {
		if(servicio.getFamilia().equals(SERVICIO_SUBCONTRATADO)){
			throw new RemoteException("No se permite editar este servicio");
		}
		
		Servicio s = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();
			
			/* se graban los html con las descripciones detalladas en el servidor */
			/*
			//directorio donde deben guardarse los html
			String filePrefix = null;
						
			filePrefix = (String) session.createSQLQuery("select ss_valor from " +
					"SYS_SETTINGS where ss_variable = 'html_file_prefix'")
					.addScalar("ss_valor",Hibernate.STRING).uniqueResult();
			
			String nextServiceCode =null;
			
			if (servicio.getCodigo() == null){
				String sql_query = "select MAX(se_codservicio)+1 from MST_SERVICIOS";
				String hql_query = "select MAX(codigo) from Servicio";

				nextServiceCode = (String) session.createQuery(hql_query).uniqueResult();
			}
			else
				nextServiceCode = servicio.getCodigo();
			
			//	se obtiene de la base el codigo del idioma espa�ol e ingles
			String spanishCode = (String) session.createSQLQuery(" select id_codidioma from " +
					"MST_IDIOMAS where id_descripcion = 'Espaniol' and activo='S'")
					.addScalar("id_codidioma",Hibernate.STRING).uniqueResult();
			
			String englishCode = (String) session.createSQLQuery(" select id_codidioma from " +
			"MST_IDIOMAS where id_descripcion = 'Ingles' and activo='S'")
			.addScalar("id_codidioma",Hibernate.STRING).uniqueResult();
			
			System.out.println("Guardando en disco la descripcion detallada en espa�ol.");
			System.out.println("FilePrefix: " + filePrefix); // ok
			System.out.println("SpanishCode: " + spanishCode); // wrong
			System.out.println("Path completo: " + filePrefix + nextServiceCode + "_" + spanishCode + ".html");
			
			System.out.println("Guardando en disco la descripcion detallada en ingles.");
			System.out.println("FilePrefix: " + filePrefix);
			System.out.println("EnglishCode: " + englishCode); // 2
			System.out.println("Path completo: " + filePrefix + nextServiceCode + "_" + englishCode + ".html");
			
			if (filePrefix != null && spanishCode != null && englishCode != null && nextServiceCode != null) {
			
				try {
					
					// se graba en un archivo en el servidor el html con la descripcion detallada en 
					// espa�ol
					FileWriter fw = new FileWriter(filePrefix + nextServiceCode + "_" + spanishCode + ".html");
					fw.write(descDetEspaniol);
					fw.flush();
					fw.close();
					
					// se graba en un archivo en el servidor el html con la descripcion detallada en 
					// ingl�s
					fw = new FileWriter(filePrefix + nextServiceCode + "_" + englishCode + ".html");
					fw.write(descDetIngles);
					fw.flush();
					fw.close();
					
				} catch (IOException e) {					
					e.printStackTrace();
				}
				*/
				/* se da de alta (o se modifica) el servicio */
			
				if (StringUtils.isBlank(servicio.getCodigo())) {
					s = new Servicio();
					HibernateUtil.assignID(session,s);
				} else {
					s = (Servicio) session.get(Servicio.class, servicio.getCodigo());
				}

				s.setFamilia(servicio.getFamilia());
				s.setPrecioVenta(servicio.getPrecioVenta());
				s.setPrecioVentaMinimo(servicio.getPrecioVentaMinimo());
				s.setAdmiteDescuento(servicio.getAdmiteDescuento());
				s.setAdmiteSinCargo(servicio.getAdmiteSinCargo());
				s.setPeso(servicio.getPeso());
				s.setLargo(servicio.getLargo());
				s.setAltura(servicio.getAltura());
				s.setAncho(servicio.getAncho());
				s.setAccesorio(servicio.getAccesorio());
				s.setActivo(servicio.getActivo());
				s.setUnidadNegocio(servicio.getUnidadNegocio());
	
				session.saveOrUpdate(s);
	
				tx.commit();
				session.flush();
			
			//}
			
		} catch (HibernateException he) {
			if (tx != null && tx.isActive())
				tx.rollback();
	
			he.printStackTrace(System.err);
			// throw new SystemException(he);			
		} finally {
			HibernateUtil.cerrarSession(session);
			
		}//try superior
		return s.getCodigo();
	}
	
	
	private String getFileName(String servicioId,String idiomaId){
		Session session = HibernateUtil.abrirSession();		
		String filePrefixQuery = new String();
		filePrefixQuery += "select ss_valor from SYS_SETTINGS ";
		filePrefixQuery += "where ss_variable = 'html_file_prefix' ";			
		String filePrefix = (String)session.createSQLQuery(filePrefixQuery).addScalar("ss_valor",Hibernate.STRING).uniqueResult();	
		System.out.println("-------------------------------------------------------------------------");
		System.out.println(filePrefix);
		System.out.println("-------------------------------------------------------------------------");
		return (filePrefix + servicioId + "_" + idiomaId + ".html");
		
	}
	
	public void setDescripcion(String servicioId,String idiomaId,String descripcion)throws RemoteException{
		try {
			// se graba en un archivo en el servidor el html con la descripcion detallada
			FileWriter fw = new FileWriter(getFileName(servicioId,idiomaId));
			fw.write(descripcion);
			fw.flush();
			fw.close();
			
		} catch (IOException e) {					
			e.printStackTrace();
		}
	}
	
	public String getDescripcion(String servicioId,String idiomaId)throws RemoteException{
		try {			
			FileReader reader = new FileReader(getFileName(servicioId,idiomaId));
			StringWriter sw = new StringWriter();
			
			while (reader.ready()){
				sw.write(reader.read());
			}
			
			return sw.toString();
			
		} catch (IOException e) {					
			e.printStackTrace();
		}
		return null;
	}
	
	public Object[] getAllServiciosReport() throws RemoteException {
		Session session = HibernateUtil.abrirSession();

		List list = session.createSQLQuery
				(
				"select se_codservicio,si_descripcion_abreviada " +
				"from VW_SERVICIOS_IDIOMA " +
				"order by si_descripcion_abreviada"
				)
				.addScalar("se_codservicio",Hibernate.STRING)
				.addScalar("si_descripcion_abreviada",Hibernate.STRING)
				.list();

		HibernateUtil.cerrarSession(session);
		
		return CollectionUtil.listToObjectArray(list);
	}
	
	public double getPrecioVtaById(String cod) throws RemoteException {
		Session session = HibernateUtil.abrirSession();

		if (log.isDebugEnabled())
			log.debug("Buscando precio de lista para el servicio " + cod);
	
		double precio = 
			Double.parseDouble((String)
				session.createQuery (
				"select precioVenta " +
				"from Servicio " +
				"where codigo = :code and activo = 'S'" 
				)
				.setString("code", cod)
				.uniqueResult());
		
		HibernateUtil.cerrarSession(session);
		
		return precio;
		
	}
	
	public String admiteAccesorioSegunCodServicio(String codServicio)throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		String st = (String)session.createQuery
			(
			"select accesorio from Servicio " +
			"where codigo = :code and activo = 'S'"
			)
			.setString("code", codServicio)
			.uniqueResult();
		
		HibernateUtil.cerrarSession(session);
		return st;
		
	}
	
	public String admiteDescuentoSegunCodServicio(String codServicio)throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		String st = (String)session.createQuery
			(
			"select admiteDescuento from Servicio " +
			"where codigo = :code and activo = 'S'"
			)
			.setString("code", codServicio)
			.uniqueResult();
		
		HibernateUtil.cerrarSession(session);
		return st;
		
	}
	
	public Object[] getServiciosByFamiliaAndPlaceReport(String familia) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
	
		List list = session.createSQLQuery
				(
				"select s.se_codservicio as codigoServicio, si.si_descripcion_abreviada as descripcion " +
				"from MST_SERVICIOS s " +
				"inner join MST_SERVICIOS_IDIOMA si on s.se_codservicio = si.si_codservicio "+
				"where s.se_familia = ? and s.se_activo = 'S' "+
				"order by si.si_descripcion_abreviada"
				)
				.addScalar("codigoServicio", Hibernate.LONG)
				.addScalar("descripcion", Hibernate.STRING)
				.setString(0, familia)
				.list();
		
		Object[] results = new Object[list.size()];
		
		for (int i=0; i< results.length;i++){ 
		    results[i] = (Object[]) list.get(i);
		}
		
		HibernateUtil.cerrarSession(session);
		
		return results;
		
		//HibernateUtil.cerrarSession(session);
		
		//return CollectionUtil.listToObjectArray(list);
	}
	
	public double getDescuentoByServicioAndTechoDias(String codServ, int cantDias) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		
		double s = Integer.valueOf(codServ);
		
		Double st = (Double)session.createSQLQuery
			(
			"SELECT PORC_DESCUENTO FROM " +
			"VW_PRECIOS_SERVICIOS_DIAS " +
			"WHERE TECHODIAS = (SELECT MAX(TECHODIAS) FROM " + 
			"VW_PRECIOS_SERVICIOS_DIAS WHERE TECHODIAS <= " + cantDias + 
			" AND CODSERVICIO = " + s + ") AND CODSERVICIO = " + s 
			)
		.addScalar("PORC_DESCUENTO",Hibernate.DOUBLE)
		.uniqueResult();
		
		double max = 0;
		
		if (st != null)
			max = st;
		
		HibernateUtil.cerrarSession(session);
		
		return max;
	}

	public String buscarDescripcionEspaniol(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		
		String spanishCode = (String) session.createSQLQuery(" select id_codidioma from " +
		"MST_IDIOMAS where id_descripcion = 'Espaniol' and activo='S'")
		.addScalar("id_codidioma",Hibernate.STRING).uniqueResult();
		
		String filePrefix = (String) session.createSQLQuery("select ss_valor from " +
		"SYS_SETTINGS where ss_variable = 'html_file_prefix'")
		.addScalar("ss_valor",Hibernate.STRING).uniqueResult();
		
		String html = null;
		
		if (spanishCode != null && filePrefix != null){
			String fullPath =  filePrefix + codigo + "_" + spanishCode + ".html";
		
			try {
				
				FileReader reader = new FileReader(fullPath);
				StringWriter sw = new StringWriter();
				
				while (reader.ready()){
					sw.write(reader.read());
				}
				
				html = sw.toString();
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		HibernateUtil.cerrarSession(session);
		
		System.out.println("Returning html: " + html);
		return html;
	}

	public String buscarDescripcionIngles(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		
		String englishCode = (String) session.createSQLQuery(" select id_codidioma from " +
		"MST_IDIOMAS where id_descripcion = 'Ingles' and activo='S'")
		.addScalar("id_codidioma",Hibernate.STRING).uniqueResult();
		
		String filePrefix = (String) session.createSQLQuery("select ss_valor from " +
		"SYS_SETTINGS where ss_variable = 'html_file_prefix'")
		.addScalar("ss_valor",Hibernate.STRING).uniqueResult();
		
		String html = null;
		
		if (englishCode != null && filePrefix != null){
			String fullPath =  filePrefix + codigo + "_" + englishCode + ".html";
		
			try {
				
				FileReader reader = new FileReader(fullPath);
				StringWriter sw = new StringWriter();
				
				while (reader.ready()){
					sw.write(reader.read());
				}
				
				html = sw.toString();
				
				reader.close();
				sw.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		HibernateUtil.cerrarSession(session);
		
		System.out.println("Returning html: " + html);
		return html;
	}

	public String buscarDescripcion(String codServicio, String idioma) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		
		String languageCode = (String) session.createSQLQuery(" select id_codidioma from " +
		"MST_IDIOMAS where id_descripcion = '" + idioma + "' and activo='S'")
		.addScalar("id_codidioma",Hibernate.STRING).uniqueResult();
		
		String filePrefix = (String) session.createSQLQuery("select ss_valor from " +
		"SYS_SETTINGS where ss_variable = 'html_file_prefix'")
		.addScalar("ss_valor",Hibernate.STRING).uniqueResult();
		
		String html = null;
		
		if (languageCode != null && filePrefix != null){
			String fullPath =  filePrefix + codServicio + "_" + languageCode + ".html";
		
			try {
				
				FileReader reader = new FileReader(fullPath);
				StringWriter sw = new StringWriter();
				
				while (reader.ready()){
					sw.write(reader.read());
				}
				
				html = sw.toString();
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		HibernateUtil.cerrarSession(session);
		
		System.out.println("Returning html: " + html);
		return html;
	}
	
	public int getUnidadDeNegocio(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
	
		int precio = Integer.parseInt((String)
				session.createQuery (
				"select unidadNegocio " +
				"from Servicio " +
				"where codigo = :code and activo = 'S'" 
				)
				.setString("code", codigo)
				.uniqueResult());
		
		HibernateUtil.cerrarSession(session);
		
		return precio;
	}

}
