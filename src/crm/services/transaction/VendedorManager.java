package crm.services.transaction;

import java.rmi.RemoteException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.hql.ast.tree.OrderByClause;

import crm.libraries.abm.entities.ABMEntity;
import crm.libraries.abm.entities.Vendedor;
import crm.libraries.abm.entities.VendedorUsuario;
import crm.services.sei.CategVendedorManagerSEI;
import crm.services.sei.VendedorManagerSEI;
import crm.services.util.CollectionUtil;
import crm.services.util.HibernateUtil;

public class VendedorManager implements VendedorManagerSEI,ManagerService {

	public Vendedor getVendedorById(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();

		Criteria c = session.createCriteria(Vendedor.class);
		c.add(Expression.eq("codigo", codigo));
		
		Vendedor a = (Vendedor) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return a;
	}
	
	public Vendedor getVendedorByApYNom(String apellidoYNombre) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(Vendedor.class);
		c.add(Expression.eq("apellidoYNombre", apellidoYNombre));
		Vendedor a = (Vendedor) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return a;
	}
	
	public Vendedor getVendedorByUserId(String userId) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(Vendedor.class);
		c.add(Expression.eq("codUsuario", userId));
		Vendedor a = (Vendedor) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return a;
	}

	public Vendedor[] getAllVendedores() throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(Vendedor.class);
		c.add(Expression.eq("activo","S"));
		c.add(Restrictions.or(Restrictions.eq("categoria", "1"),Restrictions.eq("categoria", "2")));	
		c.addOrder(Order.asc("apellidoYNombre"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (Vendedor[])list.toArray(new Vendedor[0]);
	}

	public Vendedor[] findByField(String field,String value){
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(Vendedor.class);
		c.add(Expression.like(field,"%" + value + "%"));
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (Vendedor[])list.toArray(new Vendedor[0]);
	}
	
	public Vendedor[] findByCategoryIdAndField(String categoryId,String field,String value){
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(Vendedor.class);
		c.add(Expression.eq("categoria",categoryId));
		c.add(Expression.like(field,"%" + value + "%"));
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (Vendedor[])list.toArray(new Vendedor[0]);
	}
	
	public Object[] getVendedoresSinUnidadComercial(String categoria) throws RemoteException{
   		Session session = HibernateUtil.abrirSession();
   		
   		List result = session.createSQLQuery(
   				"SELECT vd_codvend, apynom FROM mst_vendedores v " +
   				"where not exists" +
   				"(SELECT * FROM mst_unidades_vendedores m where v.vd_codvend = m.ucv_codvendedor) " +
   				"and vd_categoria = :categoria")
   				.addScalar("vd_codvend", Hibernate.STRING)
   				.addScalar("apynom", Hibernate.STRING)
   				.setString("categoria",categoria)
   				.list(); 
   		
   		HibernateUtil.cerrarSession(session);
   		
   		return CollectionUtil.listToObjectArray(result);
   	}
	
	public Object[] getAllVendedoresNotInUnidadesVendedores(String categoria) throws RemoteException {
		Session session = HibernateUtil.abrirSession();

		List list = session.createSQLQuery
				(				
				"SELECT vd_codvend,apynom FROM MST_VENDEDORES " +
				"WHERE activo = 'S' AND vd_categoria = :categoria " +
				"AND vd_codvend NOT IN(SELECT uc_codsupervisor FROM MST_UNIDADES_COMERCIALES where activo='S')"
				)
				.addScalar("vd_codvend",Hibernate.STRING)
				.addScalar("apynom",Hibernate.STRING)
				.setString("categoria",categoria)
				.list();

		HibernateUtil.cerrarSession(session);
		
		return CollectionUtil.listToObjectArray(list);
	}

	public Object[] getAllVendedoresNotInUnidadesVendedoresByVendedores(String categoria,String vendedoresArray) throws RemoteException {
		Session session = HibernateUtil.abrirSession();

		
		String[] vendedores = vendedoresArray.split(",");

		String query = new String();

		query += "SELECT vd_codvend,apynom FROM MST_VENDEDORES " ;
		query += "WHERE activo = 'S' AND vd_categoria = :categoria " ;
		query += "AND vd_codvend NOT IN(" ;
		query += "SELECT ucv_codvendedor FROM MST_UNIDADES_VENDEDORES " ;
		query += "WHERE ucv_codvendedor NOT IN(";		
		for(int i = 0;i < vendedores.length;i++){									
			query += "'" + vendedores[i] + "'";
			if(i < (vendedores.length - 1)){
				query += ",";
			}
		}
		query += "))" ;
		
		List list = session.createSQLQuery
				(query)
				.addScalar("vd_codvend",Hibernate.STRING)
				.addScalar("apynom",Hibernate.STRING)
				.setString("categoria",categoria)
				.list();

		HibernateUtil.cerrarSession(session);
		
		return CollectionUtil.listToObjectArray(list);
	}
	
	
	
	
	

	public Object[] getAllVendedoresNotInUnidadesComerciales(String categoria) throws RemoteException {
		Session session = HibernateUtil.abrirSession();

		List list = session.createSQLQuery
				(				
				"SELECT vd_codvend,apynom FROM MST_VENDEDORES " +
				"WHERE activo = 'S' AND vd_categoria = :categoria " +
				"AND vd_codvend NOT IN(" +
				"SELECT uc_codsupervisor FROM MST_UNIDADES_COMERCIALES)" /*+
				"WHERE activo = 'S')"*/
				)
				.addScalar("vd_codvend",Hibernate.STRING)
				.addScalar("apynom",Hibernate.STRING)
				.setString("categoria",categoria)
				.list();

		HibernateUtil.cerrarSession(session);
		
		return CollectionUtil.listToObjectArray(list);
	}

	public Object[] getAllVendedoresNotInUnidadesComercialesByVendedores(String categoria,String vendedor) throws RemoteException {
		Session session = HibernateUtil.abrirSession();

		String query = new String();

		query += "SELECT vd_codvend,apynom FROM MST_VENDEDORES " ;
		query += "WHERE activo = 'S' AND vd_categoria = :categoria " ;
		query += "AND vd_codvend NOT IN(" ;
		query += "SELECT uc_codsupervisor FROM MST_UNIDADES_COMERCIALES " ;
		query += "WHERE activo = 'S' AND uc_codsupervisor NOT IN(";		
		query += "'" + vendedor + "'";
		query += "))" ;
		
		List list = session.createSQLQuery
				(query)
				.addScalar("vd_codvend",Hibernate.STRING)
				.addScalar("apynom",Hibernate.STRING)
				.setString("categoria",categoria)
				.list();

		HibernateUtil.cerrarSession(session);
		
		return CollectionUtil.listToObjectArray(list);
	}
	
	
	public Vendedor[] getAllVendedoresTranslated(String lang) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public void remove(String codigo) throws RemoteException {
		Vendedor entity = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			
			entity = (Vendedor) session.get(Vendedor.class, codigo);						
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

	public String update(Vendedor vendedor/*,String unidadComercial*/) throws RemoteException {
		Vendedor v = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			if (StringUtils.isBlank(vendedor.getCodigo())) {
				v = new Vendedor();
				v.setFecing(vendedor.getFecing());
				assignID(session,v);
			} else {
				v = (Vendedor) session.get(Vendedor.class, vendedor.getCodigo());				
			}


			
			v.setApellidoYNombre(vendedor.getApellidoYNombre());
			v.setCategoria(vendedor.getCategoria());			
			v.setNextelFlota(vendedor.getNextelFlota());
			v.setIdNextel(vendedor.getIdNextel());
			v.setCodUsuario(vendedor.getCodUsuario());
			v.setCodEquipo(vendedor.getCodEquipo());
			v.setActivo(vendedor.getActivo());
			v.setApellidoYNombre(vendedor.getApellidoYNombre());
		    		
			
			if(vendedor.getCategoria().equals(CategVendedorManagerSEI.CATEGORY_SUPERVISOR)){
				v.setCodEquipo(v.getCodigo());
			}
			else if(vendedor.getCategoria().equals(CategVendedorManagerSEI.CATEGORY_LUGAR_EVENTO)){
				v.setCodEquipo(v.getCodigo());
			}
			
			session.saveOrUpdate(v);

			tx.commit();
			session.flush();
			
			if(v.getCategoria().equals(CategVendedorManagerSEI.CATEGORY_VENDEDOR) || v.getCategoria().equals(CategVendedorManagerSEI.CATEGORY_SUPERVISOR)){
				VendedorUsuarioManager vendedorUsuarioManager = new VendedorUsuarioManager();
				
				vendedorUsuarioManager.removeByUsuario(v.getCodUsuario());
				vendedorUsuarioManager.update(new VendedorUsuario(v.getCodigo(),v.getCodUsuario()));				
			}
		} catch (HibernateException he) {
			if (tx != null && tx.isActive())
				tx.rollback();
			he.printStackTrace(System.err);
		} finally {
			HibernateUtil.cerrarSession(session);
		}
		
		return v.getCodigo();
		
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

	/*public String[] getVendedorReportByCodigoReport(String codigo, String categoria) throws RemoteException {
		Session session = HibernateUtil.abrirSession();

		Object[] results = (Object[])session.createQuery(
				"select nextelFlota,idNextel " + 
				"from Vendedor " +
				"where activo = 'S' and categoria = :cat"
				)
				.setString("cat",categoria)
				.uniqueResult();
		
		HibernateUtil.cerrarSession(session);
		String[] row = null;
		
		if (results != null){
			row = new String[2];
			row[0] = results[0].toString();
			row[1] = results[1].toString();
		}
		
		return row;
	}*/
	
	public Object[] getVendedorReportByCodigoReport(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();

		List list = (List)session.createQuery(
				"select nextelFlota,idNextel " + 
				"from Vendedor " +
				"where activo = 'S' and codigo = :cod"
				)
				.setString("cod", codigo)
				.list();
		
		HibernateUtil.cerrarSession(session);
		
		return CollectionUtil.listToObjectArray(list);
	}

	public Object[] getVendedoresByComercialUnit(String codigoUC) throws RemoteException {		
		Session session = HibernateUtil.abrirSession();
		
		List list = session.createQuery("select codigoVendedor,nombreVendedor" +
				" from VistaUCVendedores where codigoUnidadComercial = '" + codigoUC + "'").list();
		
		System.out.println("El query que se tiro fue: " + "select codigoVendedor,nombreVendedor" +
				" from VistaUCVendedores where codigoUnidadComercial = '" + codigoUC + "'");
		
		HibernateUtil.cerrarSession(session);		
		
		return CollectionUtil.listToObjectArray(list);
	}
	
	public boolean isVendedorById(String codVendedor) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		
		Query query = session
		.createQuery("select count(*) from Vendedor where codigo = :cod and activo = 'S'");
		
		query.setString("cod", codVendedor);
		
		Integer count = (Integer) query.uniqueResult();

		HibernateUtil.cerrarSession(session);

		return count.intValue() > 0;
	}
}
