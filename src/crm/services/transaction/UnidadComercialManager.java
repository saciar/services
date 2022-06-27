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

import crm.libraries.abm.entities.UnidadComercial;
import crm.services.sei.UnidadComercialManagerSEI;
import crm.services.sei.UnidadVendedorManagerSEI;
import crm.services.sei.VendedorUsuarioManagerSEI;
import crm.services.util.CollectionUtil;
import crm.services.util.HibernateUtil;

public class UnidadComercialManager implements UnidadComercialManagerSEI,ManagerService {
	
	public UnidadComercial getUCDataByCodigoUsuario(String codigoUsuario) throws RemoteException{
		UnidadVendedorManagerSEI uvman = new UnidadVendedorManager();
		VendedorUsuarioManagerSEI vuman = new VendedorUsuarioManager();
		
		String codigoVendedor = vuman.getCodigoVendedor(codigoUsuario);
		String codigoUnidad = uvman.getCodigoUnidad(codigoVendedor);
		
		UnidadComercial uc = getUnidadComercialById(codigoUnidad);

		return uc;
	}
	
	public UnidadComercial getUCDataByCodigoVendedor(String codigoVendedor) throws RemoteException{
		UnidadVendedorManagerSEI uvman = new UnidadVendedorManager();
		//VendedorUsuarioManagerSEI vuman = new VendedorUsuarioManager();
		
		//String codigoVendedor = vuman.getCodigoVendedor(codigoUsuario);
		String codigoUnidad = uvman.getCodigoUnidad(codigoVendedor);
		
		UnidadComercial uc = getUnidadComercialById(codigoUnidad);

		return uc;
	}
	
	public UnidadComercial[] getUnidadComercialesBySupervisorOrDescripcion(String codigoSupervisor,String descripcion) throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(UnidadComercial.class);
		c.add(Expression.or(Expression.eq("codigoSupervisor",codigoSupervisor),Expression.eq("descripcion",descripcion)));
		
		List list = c.list();
		HibernateUtil.cerrarSession(session);		

		return (UnidadComercial[]) list.toArray(new UnidadComercial[0]);
	}
	
	
	public UnidadComercial getUCDataByCodigoUnidad(String codigoUnidad){
		Session session = HibernateUtil.abrirSession();

		Query query = 
			session.createQuery("select descripcion,codigoSucursal,objetivoGlobal from UnidadComercial where codigo = :codigo and activo = 's'");
		
		query.setString("codigo", codigoUnidad);
		
		Object[] reg = (Object[])query.uniqueResult();
		
		HibernateUtil.cerrarSession(session);
		
		UnidadComercial uc = null;
		
		if (reg != null){
			uc = new UnidadComercial();
			uc.setDescripcion(reg[0].toString());
			uc.setCodigoSucursal(reg[1].toString());
			uc.setObjetivoGlobal(reg[2].toString());
		}
		
    	return uc;
	}	
	
	public Object[] getPptosOfAllUnidadComercial() throws RemoteException{
		Session session = HibernateUtil.abrirSession();
		
		List presupuestos = session.createSQLQuery(
				"SELECT count(*) as cantidad, ucv_codunidad as unidad, uc_descripcion as unidadNombre FROM tx_vendedor_ppto t "+
				"inner join mst_unidades_vendedores on vp_vendedor=ucv_codvendedor "+
				"inner join mst_unidades_comerciales on ucv_codunidad = uc_codunidad "+
				"inner join tx_ppto_est_actual on estact_nroppto = vp_nroppto "+
				"where estact_confirmado=1 and estact_cancelado=0 "+
		"group by ucv_codunidad")
		.addScalar("cantidad", Hibernate.INTEGER)
		.addScalar("unidad", Hibernate.LONG)
		.addScalar("unidadNombre", Hibernate.STRING)
		.list();
		
		HibernateUtil.cerrarSession(session);
		
		return CollectionUtil.listToObjectArray(presupuestos);
	}	
	
	public UnidadComercial getUnidadComercialById(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();

		Criteria c = session.createCriteria(UnidadComercial.class);
		c.add(Expression.eq("codigo", codigo));
		c.add(Expression.eq("activo","S"));
		UnidadComercial a = (UnidadComercial) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return a;
	}

	public UnidadComercial[] getAllUnidadComerciales() throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(UnidadComercial.class);
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (UnidadComercial[])list.toArray(new UnidadComercial[0]);
	}

	public UnidadComercial[] findByField(String field,String value){
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(UnidadComercial.class);
		c.add(Expression.like(field,"%" + value + "%"));
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		return (UnidadComercial[])list.toArray(new UnidadComercial[0]);
	}
	
	public UnidadComercial[] getAllUnidadComercialesTranslated(String lang) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public void remove(String codigo) throws RemoteException {
		UnidadComercial entity = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			
			entity = (UnidadComercial) session.get(UnidadComercial.class, codigo);						
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

	
	public String update(UnidadComercial unidadComercial) throws RemoteException {
		UnidadComercial uc = null;		
		///UnidadVendedorManager unidadVendedorManager = new UnidadVendedorManager();
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			if (StringUtils.isBlank(unidadComercial.getCodigo())) {
				uc = new UnidadComercial();
				HibernateUtil.assignID(session,uc);
			} else {
				uc = (UnidadComercial) session.get(UnidadComercial.class, unidadComercial.getCodigo());
			}

		
			uc.setCodigoSucursal(unidadComercial.getCodigoSucursal());
			uc.setDescripcion(unidadComercial.getDescripcion());
			uc.setCodigoSupervisor(unidadComercial.getCodigoSupervisor());
			uc.setObjetivoGlobal(unidadComercial.getObjetivoGlobal());
			uc.setActivo(unidadComercial.getActivo());			
		    		
			session.saveOrUpdate(uc);

			tx.commit();
			session.flush();
			
			
			//if(!StringUtils.isBlank(vendedorId)){
				//unidadVendedorManager.removeByVendedor(vendedorId);
				//unidadVendedorManager.update(new UnidadVendedor(uc.getCodigo(),vendedorId,"S"));				
			//}
		} catch (HibernateException he) {
			if (tx != null && tx.isActive())
				tx.rollback();

			he.printStackTrace(System.err);
			// throw new SystemException(he);
		} finally {
			HibernateUtil.cerrarSession(session);
		}
		return uc.getCodigo();
	}
	
	private static UnidadComercialManager instance;
	
	public static synchronized UnidadComercialManager instance() {

			if (instance == null) 
				instance = new UnidadComercialManager();

		return instance;
	}
	
}
