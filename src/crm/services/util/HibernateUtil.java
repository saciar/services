package crm.services.util;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import crm.libraries.abm.entities.ABMEntity;
import crm.libraries.abm.entities.Localidad;
import crm.libraries.abm.entities.Partido;
import crm.libraries.abm.entities.Provincia;
import crm.libraries.abm.entities.UnidadBonus;


public class HibernateUtil {
    private static Log log = LogFactory.getLog(HibernateUtil.class);
    private static HibernateUtil instance;
    private SessionFactory sessionFactory;
    //private int sessionCount;
    
    /**
     * Abre una session hibernate
     * @return
     */
    public static Session abrirSession() {
        return _getInstance()._abrirSession();
    }
    
    /**
     * Cierra una session hibernate
     * 
     * @param session
     */
    public static void cerrarSession(Session session) {
        _getInstance()._cerrarSession(session);
    }

    /**
     * Devuelve una instancia del singleton
     * 
     * @return
     */
    private static HibernateUtil _getInstance() {
        if (instance == null) {
            synchronized (HibernateUtil.class) {
                if (instance == null) {
                    instance = new HibernateUtil();
                }
            }
        }

        return instance;
    }

    /**
     * Constructor privado del hibernateutil
     *
     */
    private HibernateUtil() {
        // sessionCount = 0;
        try {
            InitialContext ctx = new InitialContext();
            sessionFactory  = (SessionFactory) ctx.lookup(SystemConfig.getSessionFactoryJndi());
        } catch (NamingException e) {
            if (log.isErrorEnabled()){
                log.error("No pude iniciar el Session Factory: " + e.getMessage());
            }
            e.printStackTrace();
        }    
    }

    /**
     * abre la session
     * 
     * @return
     */
    private Session _abrirSession() {
        // sessionCount++;
        return sessionFactory.openSession();
    }
    
    /**
     * Cierra una session
     * 
     * @param session
     */
    public void _cerrarSession(Session session) {
        // sessionCount--;
        try {
            if (session != null) {
                session.close();
            }
        }
        catch (Exception e) {
            if (log.isErrorEnabled()){
                log.error("No pude cerrar la session: " + e.getMessage());
            }
            e.printStackTrace();
        }
    }

    
	public static void assignID(Session session,ABMEntity e) {
		/*
		String className = e.getClass().toString();
		String entityType = className.substring(className.lastIndexOf(".") + 1,
				className.length());
		*/
		String entityName = e.getClass().getSimpleName();
		String entityAlias = entityName.toLowerCase();

		String query = "select max(" + entityAlias + ".codigo) + 1 from " 
		+ entityName + " " + entityAlias;
		
		Object result = session.createQuery(query)		
						.uniqueResult();
		
		String codigo = result != null ? result.toString():"1";

		e.setCodigo(codigo);
	}
	
	public static void assignIDForPartido(Session session,Partido e) {
		/*
		String className = e.getClass().toString();
		String entityType = className.substring(className.lastIndexOf(".") + 1,
				className.length());
		*/
		String entityName = e.getClass().getSimpleName();
		String entityAlias = entityName.toLowerCase();

		String query = "select max(" + entityAlias + ".codigoPartido) + 1 from " 
		+ entityName + " " + entityAlias;
		
		Object result = session.createQuery(query)		
						.uniqueResult();
		
		String codigo = result != null ? result.toString():"1";

		e.setCodigoPartido(codigo);
	}
	
	public static void assignIDForProvincia(Session session,Provincia e) {
		/*
		String className = e.getClass().toString();
		String entityType = className.substring(className.lastIndexOf(".") + 1,
				className.length());
		*/
		String entityName = e.getClass().getSimpleName();
		String entityAlias = entityName.toLowerCase();

		String query = "select max(" + entityAlias + ".codigoProvincia) + 1 from " 
		+ entityName + " " + entityAlias;
		
		Object result = session.createQuery(query)		
						.uniqueResult();
		
		String codigo = result != null ? result.toString():"1";

		e.setCodigoProvincia(codigo);
	}
	
	public static void assignIDForLocalidad(Session session,Localidad e) {
		/*
		String className = e.getClass().toString();
		String entityType = className.substring(className.lastIndexOf(".") + 1,
				className.length());
		*/
		String entityName = e.getClass().getSimpleName();
		String entityAlias = entityName.toLowerCase();

		String query = "select max(" + entityAlias + ".codigoLocalidad) + 1 from " 
		+ entityName + " " + entityAlias;
		
		Object result = session.createQuery(query)		
						.uniqueResult();
		
		String codigo = result != null ? result.toString():"1";

		e.setCodigoLocalidad(codigo);
	}
}
