package crm.services.wsdl2.manager;

import java.rmi.RemoteException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import crm.libraries.abm.entities.ABMEntity;
import crm.libraries.abm.entities.Administrador;
import crm.services.sei.CategVendedorManagerSEI;
import crm.services.util.CollectionUtil;
import crm.services.util.HibernateUtil;
import crm.services.wsdl2.sei.AdministradorManagerSEI;

public class AdministradorManager implements AdministradorManagerSEI, WSDL2Service{
	
	public Administrador[] findByField(String field,String value) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(Administrador.class);
		c.add(Expression.like(field,"%" + value + "%"));
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (Administrador[])list.toArray(new Administrador[0]);
	}
	
	public Administrador getAdministradorByUserId(String codigo) throws RemoteException{
		Session session = HibernateUtil.abrirSession();

		Criteria c = session.createCriteria(Administrador.class);
		c.add(Expression.eq("codigo", codigo));
		c.add(Expression.eq("activo","S"));
		Administrador a = (Administrador) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return a;
	}
	
	public String getCodAdministradorByCodUsuario(String codUsuario) throws RemoteException{
		Session session =HibernateUtil.abrirSession();
		
		String result = (String)session.createSQLQuery(
				"Select ad_codadmin from mst_administradores m " +
				"where ad_codusuario = :user and activo='S'")
				.addScalar("ad_codadmin", Hibernate.STRING)
				.setString("user", codUsuario)
				.uniqueResult();
		
		HibernateUtil.cerrarSession(session);
		
		return result;
	}
	
	public Object[] getAdministradoresSinUnidadAdministrativa() throws RemoteException{
   		Session session = HibernateUtil.abrirSession();
   		
   		List result = session.createSQLQuery(
   				"SELECT ad_codadmin, apynom FROM mst_administradores v " +
   				"where not exists" +
   				"(SELECT * FROM mst_unidades_administradores m where v.ad_codadmin = m.ucu_codadmin and m.activo='S') " +
   				"and (ad_categoria = :categoria1 or ad_categoria = :categoria2 or ad_categoria = :categoria3)")
   				.addScalar("ad_codadmin", Hibernate.STRING)
   				.addScalar("apynom", Hibernate.STRING)
   				.setString("categoria1",CategVendedorManagerSEI.CATEGORY_COBRANZA)
   				.setString("categoria2",CategVendedorManagerSEI.CATEGORY_FACTURACION)
   				.setString("categoria3",CategVendedorManagerSEI.CATEGORY_GERENTE_ADMINISTRATIVO)
   				.list(); 
   		
   		HibernateUtil.cerrarSession(session);
   		
   		return CollectionUtil.listToObjectArray(result);
   	}
	
	public Object[] getAdministradoresSinUnidadAdministrativaPorUnidad(String codUnidad) throws RemoteException{
   		Session session = HibernateUtil.abrirSession();
   		
   		List result = session.createSQLQuery(
   				
   				"(SELECT ad_codadmin, apynom FROM mst_administradores a "+
   				"inner join mst_unidades_administradores m on a.ad_codadmin = m.ucu_codadmin "+
   				"where m.ucu_codunidad = :codUnidad) "+
   				"union all "+   				
   				"(SELECT ad_codadmin, apynom FROM mst_administradores v " +
   				"where not exists" +
   				"(SELECT * FROM mst_unidades_administradores m where v.ad_codadmin = m.ucu_codadmin and m.activo='S') " +
   				"and (ad_categoria = :categoria1 or ad_categoria = :categoria2 or ad_categoria = :categoria3))")
   				.addScalar("ad_codadmin", Hibernate.STRING)
   				.addScalar("apynom", Hibernate.STRING)
   				.setString("codUnidad",codUnidad)
   				.setString("categoria1",CategVendedorManagerSEI.CATEGORY_COBRANZA)
   				.setString("categoria2",CategVendedorManagerSEI.CATEGORY_FACTURACION)
   				.setString("categoria3",CategVendedorManagerSEI.CATEGORY_GERENTE_ADMINISTRATIVO)
   				.list(); 
   		
   		HibernateUtil.cerrarSession(session);
   		
   		return CollectionUtil.listToObjectArray(result);
   	}
	
	public void remove(String codigo) throws RemoteException {
		Administrador entity = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			
			entity = (Administrador) session.get(Administrador.class, codigo);						
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

	public String update(Administrador administrador) throws RemoteException {
		Administrador adm = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			if (StringUtils.isBlank(administrador.getCodigo())) {
				adm = new Administrador();
				adm.setFecing(administrador.getFecing());
				assignID(session,adm);
			} else {
				adm = (Administrador) session.get(Administrador.class, administrador.getCodigo());				
			}


			
			adm.setApellidoYNombre(administrador.getApellidoYNombre());
			adm.setCategoria(administrador.getCategoria());			
			adm.setNextelFlota(administrador.getNextelFlota());
			adm.setIdNextel(administrador.getIdNextel());
			adm.setCodUsuario(administrador.getCodUsuario());
			adm.setActivo(administrador.getActivo());
			adm.setApellidoYNombre(administrador.getApellidoYNombre());
			
			session.saveOrUpdate(adm);

			tx.commit();
			session.flush();
		} catch (HibernateException he) {
			if (tx != null && tx.isActive())
				tx.rollback();
			he.printStackTrace(System.err);
		} finally {
			HibernateUtil.cerrarSession(session);
		}
		
		return adm.getCodigo();
		
	}

	
	
	private void assignID(Session session,ABMEntity e) {

		String className = e.getClass().toString();
		String entityType = className.substring(className.lastIndexOf(".") + 1,
				className.length());
		String entityAlias = entityType.toLowerCase();

		// ID = MAX_ID_UNTIL_NOW + 1
		String query = "select max(" + entityAlias + ".codigo) + 1 from " 
		+ entityType + " " + entityAlias;
		
		Object result = session.createQuery(query)		
						.uniqueResult();
		
		String codigo = result != null ? result.toString():"1";

		e.setCodigo(codigo);

	}

}
