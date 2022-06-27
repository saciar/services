package crm.services.util;


public class SystemConfig {
	/**
	 * TODO: pasar esto al archivo .properties
	 * @return
	 */
    public static final String getSessionFactoryJndi(){
        return "java:/hibernate/SessionFactory";
    }
}
