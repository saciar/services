package crm.services.transaction;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import crm.libraries.abm.entities.Acceso;
import crm.libraries.abm.entities.Administrador;
import crm.libraries.abm.entities.Usuario;
import crm.libraries.abm.entities.Vendedor;
import crm.services.mail.EmailAddressException;
import crm.services.mail.EmailNameException;
import crm.services.mail.MailMessage;
import crm.services.mail.SendMailException;
import crm.services.mail.SmtpSender;
import crm.services.sei.CategVendedorManagerSEI;
import crm.services.sei.PerfilManagerSEI;
import crm.services.sei.UsuarioManagerSEI;
import crm.services.util.DateConverter;
import crm.services.util.HibernateUtil;
import crm.services.wsdl2.manager.AdministradorManager;

public class UsuarioManager implements UsuarioManagerSEI,ManagerService {

	private static final Log log = LogFactory.getLog(UsuarioManager.class);
	
	public Usuario getUsuarioById(String codigo) throws RemoteException {
		Session session = HibernateUtil.abrirSession();

		Criteria c = session.createCriteria(Usuario.class);
		c.add(Expression.eq("codigo", codigo));
		c.add(Expression.eq("activo","S"));
		Usuario u = (Usuario) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		/*if(u != null){
		loadAccesosString(u);
		}*/
		return u;
	}

	public Usuario getUsuarioById2(String name, String pass) throws RemoteException {
		Session session = HibernateUtil.abrirSession();
		// Servicio s = (Servicio)session.get(Servicio.class,codigo);
		Criteria c = session.createCriteria(Usuario.class);
		c.add(Expression.eq("loginName", name));
		c.add(Expression.eq("password", pass));
		c.add(Expression.eq("activo","S"));
		Usuario u = (Usuario) c.uniqueResult();
		HibernateUtil.cerrarSession(session);
		return u;
	}
	
	public Usuario[] findByField(String field, String value) {
		Session session = HibernateUtil.abrirSession();
		Criteria c = session.createCriteria(Usuario.class);
		c.add(Expression.like(field,"%" + value + "%"));
		c.add(Expression.eq("activo","S"));
		List list = c.list();
		HibernateUtil.cerrarSession(session);

		//loadAccesosString(list);
		return (Usuario[]) list.toArray(new Usuario[0]);
	}

	public Usuario[] getAllUsuariosTranslated(String lang)
			throws RemoteException {
		return null;
	}

	public void remove(String codigo) throws RemoteException {
		Usuario entity = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			
			entity = (Usuario) session.get(Usuario.class, codigo);						
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

	public void update(Usuario usuario) throws RemoteException {
		Usuario u = null;
		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateUtil.abrirSession();
			tx = session.beginTransaction();

			if (StringUtils.isBlank(usuario.getCodigo())) {
				u = new Usuario();
				HibernateUtil.assignID(session, u);
			} else {
				u = (Usuario) session.get(Usuario.class, usuario.getCodigo());
			}

			u.setLoginName(usuario.getLoginName());
			u.setPassword(usuario.getPassword());
			u.setEmail(usuario.getEmail());
			u.setApellidoYNombre(usuario.getApellidoYNombre());
			u.setPerfil(usuario.getPerfil());
			u.setActivo(usuario.getActivo());
			u.setLimite_descuento(usuario.getLimite_descuento());

			session.saveOrUpdate(u);

			tx.commit();
			session.flush();
			
			//si es vendedor o supervisor.. lo doy de alta como tal.
			if(u.getPerfil().equals(PerfilManagerSEI.PERFIL_VENDEDOR) || u.getPerfil().equals(PerfilManagerSEI.PERFIL_SUPERVISOR)){
				VendedorManager vendedorManager = new VendedorManager();
				Vendedor vendedor = vendedorManager.getVendedorByUserId(u.getCodigo());
				if(vendedor == null){
					vendedor = new Vendedor();
					vendedor.setCodUsuario(u.getCodigo());
					vendedor.setCodEquipo(u.getCodigo());
					vendedor.setFecing(DateConverter.convertDateToString(new Date(),"yyyy/MM/dd H:mm:ss"));
					vendedor.setActivo("S");
				}
				vendedor.setApellidoYNombre(u.getApellidoYNombre());																
				if(u.getPerfil().equals(PerfilManagerSEI.PERFIL_VENDEDOR)){
					vendedor.setCategoria(CategVendedorManagerSEI.CATEGORY_VENDEDOR);
				}else if(u.getPerfil().equals(PerfilManagerSEI.PERFIL_SUPERVISOR)){
					vendedor.setCategoria(CategVendedorManagerSEI.CATEGORY_SUPERVISOR);
				}
				vendedorManager.update(vendedor);
			}
			else if(u.getPerfil().equals(PerfilManagerSEI.PERFIL_COBRANZAS) || u.getPerfil().equals(PerfilManagerSEI.PERFIL_FACTURACION)
						|| u.getPerfil().equals(PerfilManagerSEI.PERFIL_GERENCIA_ADMINISTRATIVA)){
				AdministradorManager adminManager = new AdministradorManager();
				Administrador ad = adminManager.getAdministradorByUserId(u.getCodigo());
				if(ad == null){
					ad = new Administrador();
					ad.setCodUsuario(u.getCodigo());
					ad.setFecing(DateConverter.convertDateToString(new Date(),"yyyy/MM/dd H:mm:ss"));
					ad.setActivo("S");					
				}
				ad.setApellidoYNombre(u.getApellidoYNombre());
				if(u.getPerfil().equals(PerfilManagerSEI.PERFIL_COBRANZAS))
					ad.setCategoria(CategVendedorManagerSEI.CATEGORY_COBRANZA);
				else if(u.getPerfil().equals(PerfilManagerSEI.PERFIL_FACTURACION))
					ad.setCategoria(CategVendedorManagerSEI.CATEGORY_FACTURACION);
				else if(u.getPerfil().equals(PerfilManagerSEI.PERFIL_GERENCIA_ADMINISTRATIVA))
					ad.setCategoria(CategVendedorManagerSEI.CATEGORY_GERENTE_ADMINISTRATIVO);
				adminManager.update(ad);
			}
		} catch (HibernateException he) {
			if (tx != null && tx.isActive())
				tx.rollback();

			he.printStackTrace(System.err);
			// throw new SystemException(he);
		} finally {
			HibernateUtil.cerrarSession(session);
		}
		//if(!StringUtils.isBlank(usuario.getAccesosString())){
			//setAccesos(u.getCodigo(), usuario.getAccesosString().split(","));
		//}
	}

	public String getUserCodeByUsername(String username, String password) {
		Session session = HibernateUtil.abrirSession();
		
		Query query = null;
		
		if(password != null){
			query = session
				.createQuery("select codigo from Usuario where loginName = :username and password = :password and activo = 'S'");
			query.setString("username", username);
			query.setString("password", password);
		}
		else {
			query = session
				.createQuery("select codigo from Usuario where loginName = :username and activo = 'S'");
			query.setString("username", username);			
		}		
		String codigo = (String) query.uniqueResult();

		HibernateUtil.cerrarSession(session);

		return codigo;
	}
	
	public String getNameByCode(String code){
		Session session = HibernateUtil.abrirSession();

		Query query = session
				.createQuery("select apellidoYNombre from Usuario where codigo = :userCode and activo = 'S'");

		query.setString("userCode", code);

		String apellidoYNombre = (String) query.uniqueResult();

		HibernateUtil.cerrarSession(session);

		return apellidoYNombre;
	}

	
	/*public void setAccesos(String pk, String accesoIds[])throws RemoteException {		
		if(accesoIds != null){
		Session session = null;
		try {			
			session = HibernateUtil.abrirSession();
			Set accesos = new HashSet();
			for (int i = 0; (accesoIds != null) && (i < accesoIds.length); i++) {
				Acceso acceso = (Acceso) session.get(Acceso.class, accesoIds[i]);
				accesos.add(acceso);
			}			
			Usuario usuario = (Usuario) session.get(Usuario.class, pk);
			usuario.setAccesos(accesos);
			
			session.flush();						
		} catch (HibernateException he) {
			he.printStackTrace();
		} finally {
			HibernateUtil.cerrarSession(session);
		}
		}
	}

	private void loadAccesosString(List usuarios) {
		Iterator it = usuarios.iterator();
		while (it.hasNext()) {
			Usuario usuario = (Usuario) it.next();
			loadAccesosString(usuario);
		}
	}

	private void loadAccesosString(Usuario usuario) {
		String accesoIds = new String();
		int i = 0;
		Iterator it = usuario.getAccesos().iterator();
		while (it.hasNext()) {
			Acceso acceso = (Acceso) it.next();
			if (i > 0) {
				accesoIds += ",";
			}
			accesoIds += acceso.getCodigo();
			i++;
		}
		usuario.setAccesosString(accesoIds);
		usuario.setAccesos(null);
	}*/


	public boolean getAccessTo(long codigoUsuario, int something) throws RemoteException {
		boolean result = false;
		
		Session session = HibernateUtil.abrirSession();
		List l = session.createQuery("select codigoAcceso from AccesoUsuario " +
				"where codigoUsuario = '" + codigoUsuario + "'").list();
		
		if ( l != null ) {
			
			Iterator it = l.iterator();
			
			while ( it.hasNext() && !result ) {
				
				int codigoAcceso = Integer.parseInt((String)it.next());
				result = codigoAcceso == something;
				
			}
			
		}
		
		HibernateUtil.cerrarSession(session);

		
		
		return result;
	}

	public boolean hasPerfil(String userId,String perfilId) throws RemoteException{
		Usuario user = getUsuarioById(userId);
		if(user != null && user.getPerfil().equals(perfilId)){
			return true;
		}		
		return false;
	}
	
	public void sendPasswordByEmail (String codUsuario,String pass, String userName, String name) throws RemoteException{

			try {				
				MailMessage mailMessage = new MailMessage();
				mailMessage.setFromAddress("crm@congressrental.com.ar","CRM Congress Rental");					
				//mailMessage.setToAddress(new String[]{user.getEmail().substring(0, user.getEmail().length()-3)},new String[]{user.getApellidoYNombre()});
				//mailMessage.setToAddress(new String[]{user.getEmail()},new String[]{user.getApellidoYNombre()});
				
				mailMessage.setFilePath("");
				mailMessage.setFileName("");	
				mailMessage.setToAddress(new String[]{codUsuario.substring(0, codUsuario.length()-3)},new String[]{"Usuario"});
				mailMessage.setSubject("Generacion de clave");

				mailMessage.setHtmlBody(getHTMLBody(pass, userName, name));
				//mailMessage.setHtmlBody("Prueba");
				SmtpSender.getInstance().sendMail(mailMessage);
			} catch (EmailAddressException e) {
				e.printStackTrace();
			} catch (EmailNameException e) {
				e.printStackTrace();
			} catch (SendMailException e) {
				e.printStackTrace();
			}

	}
	
	private String getHTMLBody(String pass, String userName, String name){
		String html = new String();
		
		html += "<html>";
		html += "<body>";		
		html += "<h1>";
		html += "<FONT SIZE='2' FACE='VERDANA'> Hola "+name+ "!. Estos son tus datos de identificacion en la aplicacion (Guarda bien estos datos):<br>";
		html += "<br>";
		html += "Usuario: " + userName + "<br>";		
		html += "Password: " + pass + "<br>";		
		html += "<br>";
		html += "Si no tiene instalada la aplicacion ingresa a <a href='http://crm.congressrental.com:8888'>http://crm.congressrental.com:8888</a><br>";
		html += "<br>";
		html += "Muchas gracias </FONT>";
		html += "</h1>";
		html += "</body>";
		html += "</html>";
		
		return html;
	}
	
	private static UsuarioManager instance;
	
	public static synchronized UsuarioManager instance() {

			if (instance == null) 
				instance = new UsuarioManager();

		return instance;
	}
}
