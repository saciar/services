package crm.services.wsdl2.manager;

import java.rmi.RemoteException;
import java.sql.Date;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.Session;

import crm.services.util.CollectionUtil;
import crm.services.util.HibernateUtil;
import crm.services.wsdl2.sei.SmsManagerSEI;

public class SmsManager implements SmsManagerSEI, WSDL2Service {
	public Object[] buscarSmsParaLiqOperador(String fechaInicial, String fechaFinal, long codOp) throws RemoteException {
		Session session = HibernateUtil.abrirSession();

		List list = session.createSQLQuery(
				"select convert((group_concat(DISTINCT sms_nroppto ORDER BY sms_nroppto ASC SEPARATOR '-')) using utf8) as nro, min(sms_fecha_envio), sms_descripcion, " +
				"sms_telefono, sms_modo, " +
				"sms_operador, apynom, max(sms_fecha_hasta) " +
				"from TX_SMS_2 " +
				"inner join mst_operadores on op_codoper = sms_operador "+
				"where sms_fecha_envio <= ? and sms_fecha_envio >= ? and sms_operador = ? " +
				"group by dayofyear(sms_fecha_envio) order by sms_fecha_envio"
				)
				.addScalar("nro",Hibernate.STRING)
				.addScalar("min(sms_fecha_envio)",Hibernate.TIMESTAMP)
				.addScalar("sms_descripcion",Hibernate.STRING)			
				.addScalar("sms_telefono",Hibernate.STRING)
				.addScalar("sms_modo", Hibernate.STRING)
				.addScalar("apynom", Hibernate.STRING)
				.addScalar("max(sms_fecha_hasta)",Hibernate.TIMESTAMP)
				.setString(0,fechaFinal)
				.setString(1,fechaInicial)
				.setLong(2, codOp)
				.list();
		
		HibernateUtil.cerrarSession(session);
		return CollectionUtil.listToObjectArray(list);
	}
	
	public Object[] buscarSmsParaLiqTodos(String fechaInicial, String fechaFinal) throws RemoteException {
		Session session = HibernateUtil.abrirSession();

		List list = session.createSQLQuery(
				"select convert((group_concat(DISTINCT sms_nroppto ORDER BY sms_nroppto ASC SEPARATOR '-')) using utf8) as nro, min(sms_fecha_envio), sms_descripcion, " +
				"sms_telefono, sms_modo, " +
				"sms_operador, apynom, max(sms_fecha_hasta) " +
				"from TX_SMS_2 " +
				"inner join mst_operadores on op_codoper = sms_operador "+
				"where sms_fecha_envio <= ? and sms_fecha_envio >= ? " +
				"group by sms_operador,dayofyear(sms_fecha_envio) order by sms_fecha_envio, apynom"
				)
				.addScalar("nro",Hibernate.STRING)
				.addScalar("min(sms_fecha_envio)",Hibernate.TIMESTAMP)
				.addScalar("sms_descripcion",Hibernate.STRING)			
				.addScalar("sms_telefono",Hibernate.STRING)
				.addScalar("sms_modo", Hibernate.STRING)
				.addScalar("apynom", Hibernate.STRING)
				.addScalar("max(sms_fecha_hasta)",Hibernate.TIMESTAMP)
				.setString(0,fechaFinal)
				.setString(1,fechaInicial)
				.list();
		
		HibernateUtil.cerrarSession(session);
		return CollectionUtil.listToObjectArray(list);
	}
	
	public Object[] buscarSmsDetalleTodos(String fechaInicial, String fechaFinal) throws RemoteException {
		Session session = HibernateUtil.abrirSession();

		List list = session.createSQLQuery(
				"select sms_nroppto, sms_fecha_envio, sms_descripcion, " +
				"sms_telefono, sms_modo, " +
				"sms_operador, apynom, sms_fecha_hasta " +
				"from TX_SMS_2 " +
				"inner join mst_operadores on op_codoper = sms_operador "+
				"where sms_fecha_envio <= ? and sms_fecha_envio >= ? " +
				"order by sms_fecha_envio, apynom"
				)
				.addScalar("sms_nroppto",Hibernate.LONG)
				.addScalar("sms_fecha_envio",Hibernate.TIMESTAMP)
				.addScalar("sms_descripcion",Hibernate.STRING)			
				.addScalar("sms_telefono",Hibernate.STRING)
				.addScalar("sms_modo", Hibernate.STRING)
				.addScalar("apynom", Hibernate.STRING)
				.addScalar("sms_fecha_hasta",Hibernate.TIMESTAMP)
				.setString(0,fechaFinal)
				.setString(1,fechaInicial)
				.list();
		
		HibernateUtil.cerrarSession(session);
		return CollectionUtil.listToObjectArray(list);
	}
	
	public Object[] buscarSmsDetalleOperador(String fechaInicial, String fechaFinal, long codOp) throws RemoteException {
		Session session = HibernateUtil.abrirSession();

		List list = session.createSQLQuery(
				"select sms_nroppto, sms_fecha_envio, sms_descripcion, " +
				"sms_telefono, sms_modo, " +
				"sms_operador, apynom, sms_fecha_hasta " +
				"from TX_SMS_2 " +
				"inner join mst_operadores on op_codoper = sms_operador "+
				"where sms_fecha_envio <= ? and sms_fecha_envio >= ? and sms_operador = ? " +
				"order by sms_fecha_envio"
				)
				.addScalar("sms_nroppto",Hibernate.LONG)
				.addScalar("sms_fecha_envio",Hibernate.TIMESTAMP)
				.addScalar("sms_descripcion",Hibernate.STRING)			
				.addScalar("sms_telefono",Hibernate.STRING)
				.addScalar("sms_modo", Hibernate.STRING)
				.addScalar("apynom", Hibernate.STRING)
				.addScalar("sms_fecha_hasta",Hibernate.TIMESTAMP)
				.setString(0,fechaFinal)
				.setString(1,fechaInicial)
				.setLong(2, codOp)
				.list();
		
		HibernateUtil.cerrarSession(session);
		return CollectionUtil.listToObjectArray(list);
	}
	
	public Object[] buscarSmsPorNroPptoDetalleOperador(long nroppto, long codOp) throws RemoteException {
		Session session = HibernateUtil.abrirSession();

		List list = session.createSQLQuery(
				"select sms_nroppto, sms_fecha_envio, sms_descripcion, " +
				"sms_telefono, sms_modo, " +
				"sms_operador, apynom, sms_fecha_hasta " +
				"from TX_SMS_2 " +
				"inner join mst_operadores on op_codoper = sms_operador "+
				"where sms_nroppto = ? and sms_operador = ? "+
				"order by sms_fecha_envio"
				)
				.addScalar("sms_nroppto",Hibernate.LONG)
				.addScalar("sms_fecha_envio",Hibernate.TIMESTAMP)
				.addScalar("sms_descripcion",Hibernate.STRING)			
				.addScalar("sms_telefono",Hibernate.STRING)
				.addScalar("sms_modo", Hibernate.STRING)
				.addScalar("apynom", Hibernate.STRING)
				.addScalar("sms_fecha_hasta",Hibernate.TIMESTAMP)
				.setLong(0, nroppto)
				.setLong(1, codOp)
				.list();
		
		HibernateUtil.cerrarSession(session);
		return CollectionUtil.listToObjectArray(list);
	}
	
	public Object[] buscarSmsPorNroPptoDetalleTodos(long nroppto) throws RemoteException {
		Session session = HibernateUtil.abrirSession();

		List list = session.createSQLQuery(
				"select sms_nroppto, sms_fecha_envio, sms_descripcion, " +
				"sms_telefono, sms_modo, " +
				"sms_operador, apynom, sms_fecha_hasta " +
				"from TX_SMS_2 " +
				"inner join mst_operadores on op_codoper = sms_operador "+
				"where sms_nroppto = ? "+
				"order by sms_fecha_envio, apynom"
				)
				.addScalar("sms_nroppto",Hibernate.LONG)
				.addScalar("sms_fecha_envio",Hibernate.TIMESTAMP)
				.addScalar("sms_descripcion",Hibernate.STRING)			
				.addScalar("sms_telefono",Hibernate.STRING)
				.addScalar("sms_modo", Hibernate.STRING)
				.addScalar("apynom", Hibernate.STRING)
				.addScalar("sms_fecha_hasta",Hibernate.TIMESTAMP)
				.setLong(0, nroppto)
				.list();
		
		HibernateUtil.cerrarSession(session);
		return CollectionUtil.listToObjectArray(list);
	}

	public Object[] buscarSmsPorNroPptoLiqTodos(long nroppto) throws RemoteException {
		Session session = HibernateUtil.abrirSession();

		List list = session.createSQLQuery(
				"select sms_nroppto, min(sms_fecha_envio), sms_descripcion, " +
				"sms_telefono, sms_modo, " +
				"sms_operador, apynom, max(sms_fecha_hasta) " +
				"from TX_SMS_2 " +
				"inner join mst_operadores on op_codoper = sms_operador "+
				"where sms_nroppto = ? "+
				"group by sms_operador,dayofyear(sms_fecha_envio) order by sms_fecha_envio, apynom"
				)
				.addScalar("sms_nroppto",Hibernate.LONG)
				.addScalar("min(sms_fecha_envio)",Hibernate.TIMESTAMP)
				.addScalar("sms_descripcion",Hibernate.STRING)			
				.addScalar("sms_telefono",Hibernate.STRING)
				.addScalar("sms_modo", Hibernate.STRING)
				.addScalar("apynom", Hibernate.STRING)
				.addScalar("max(sms_fecha_hasta)",Hibernate.TIMESTAMP)
				.setLong(0, nroppto)
				.list();
		
		HibernateUtil.cerrarSession(session);
		return CollectionUtil.listToObjectArray(list);
	}
	
	public Object[] buscarSmsPorNroPptoLiqOperador(long nroppto, long codOp) throws RemoteException {
		Session session = HibernateUtil.abrirSession();

		List list = session.createSQLQuery(
				"select sms_nroppto, min(sms_fecha_envio), sms_descripcion, " +
				"sms_telefono, sms_modo, " +
				"sms_operador, apynom, max(sms_fecha_hasta) " +
				"from TX_SMS_2 " +
				"inner join mst_operadores on op_codoper = sms_operador "+
				"where sms_nroppto = ? and sms_operador = ? "+
				"group by sms_operador,dayofyear(sms_fecha_envio) order by sms_fecha_envio, apynom"
				)
				.addScalar("sms_nroppto",Hibernate.LONG)
				.addScalar("min(sms_fecha_envio)",Hibernate.TIMESTAMP)
				.addScalar("sms_descripcion",Hibernate.STRING)			
				.addScalar("sms_telefono",Hibernate.STRING)
				.addScalar("sms_modo", Hibernate.STRING)
				.addScalar("apynom", Hibernate.STRING)
				.addScalar("max(sms_fecha_hasta)",Hibernate.TIMESTAMP)
				.setLong(0, nroppto)
				.setLong(1, codOp)
				.list();
		
		HibernateUtil.cerrarSession(session);
		return CollectionUtil.listToObjectArray(list);
	}
}
