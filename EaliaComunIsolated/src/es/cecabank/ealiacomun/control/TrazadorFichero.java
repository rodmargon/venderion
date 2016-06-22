package es.cecabank.ealiacomun.control;

/**
 * Metodo utilizado para trazar
 * Fecha de creación: (21/11/01 14:00:33)
 * @author: 
 */

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.axis2.context.MessageContext;
import org.apache.log4j.Logger;

import ceca.accesobd.BDComun;
import ceca.control.CecaException;
import ceca.vistas.DatosTrazaBean;
import es.cecabank.ealiacomun.util.Constantes;

public class TrazadorFichero {
	public static final int NIVEL_DEBUG = 5; 
	public static final int NIVEL_INFO = 4;  
	public static final int NIVEL_WARN = 3;
	public static final int NIVEL_ERROR = 2;
	public static final int NIVEL_FATAL = 1;
	
	static Logger logger = Logger.getLogger(TrazadorFichero.class);
	/*
	static{
		System.out.println("TRAZA TRAZADOR");
		//Configuracion de log4j, anteriormente en la clase TrazadorFichero
		URL url = Loader.getResource("log4j.properties");
		PropertyConfigurator.configure(url);
	}
	*/
/**
 * Escribe en trazas los datos que recibe.
 * Además escribe el printStackTrace si le llega la excepcion
 * @param desde String : Nombre del metodo y clase desde donte trazamos
 * @param usuario String : Usuario si lo hay.
 * @param web String : Web si la hay
 * @param a String : Primera linea de la traza
 * @param b String : Segunda linea de la traza
 * @param c String : Tercera linea de la traza
 * @param d String : Cuarta linea de la traza
 * @param nivelTraza int : Nivel de la traza
 * @param e Exception : Escribe el printStackTrace si le llega la excepcion
 */
        
	protected TrazadorFichero() { 
	// Default constructor
	}
	/*
	public TrazadorFichero(String idEjecucion) { 
		this.idEjecucion = idEjecucion;
	}
	*/
	public static void trazarError(CecaException ce, ContextoEalia ctx){
		String desdeDonde="trazarError - ceca.control.ContextoCeca";
		escribirTrazas(desdeDonde, "Entro", "", "", "", NIVEL_DEBUG);
		
		String a, b, c;
		a = "@# ERROR: Codigo / Descrip. Interna: " + ce.getCodError() +" / "+ ce.getDescripcionTecnica();

		// Trazo los datos del cliente
		b = "URL("+ctx.getUrlPagina()+")";
		c = "IP(" + ctx.getIpOrigen()+")";
		//puerto
		c += " Puerto("+ctx.getPuerto()+")";
		//tipo_peticion
		c += " Metodo("+ce.getDesdeDonde()+")";
		//nombre del servidor.
		c += " Servidor("+BDComun.HOST_NAME+")";
		//User_Agent:

		if (ce.getTextoExterno() != null && !ce.getTextoExterno().equals("") || ce.getCodigoExterno() != null && !ce.getCodigoExterno().equals("") ){
			b = "Codigo / Descrip. externa: "+ ce.getCodigoExterno() +"/" + ce.getTextoExterno();
		}
			
		if (ce.getE() != null) {
			if (ce.getE() instanceof java.sql.SQLException){
				// Tenemos excepcion la convertimos a excepcion de SQL y la mostramos
				java.sql.SQLException eSQL= (java.sql.SQLException) ce.getE();
				c += "SQL Excepcion Error: " + Integer.toString(eSQL.getErrorCode()) + " - " + ce.getE().getMessage();
			}else{

				// Mostar La descripción de la excepción
				c += "Excepcion("+ ce.getE().getClass().getName() +") Error: " + ce.getE().getMessage();
			}
		    java.io.StringWriter sw = new java.io.StringWriter();
		    java.io.PrintWriter pw = new java.io.PrintWriter(sw);
			ce.getE().printStackTrace(pw);
			c += "\n"+ sw.toString();
		}
		
		// Escribimos con nivel de traza 1
//		if (ce.getHayDatos()){
//			int nivelDatosIncidencias=1;
	//		ParametrosComunBean paramC = CtlCeca.getParam(this);
		//	if (paramC!=null && paramC.getParNivelDatosIncidencias() !=null){
			//	nivelDatosIncidencias = paramC.getNivelDatosIncidencias();
//			}
	//		escribirTrazasDatos(ce.getDesdeDonde(), a, b, c, ce.getDatosT(), nivelDatosIncidencias);
		//}else{
			escribirTrazas(ce.getDesdeDonde(), a, b, c, "", NIVEL_FATAL);
		//}
	}

private static void escribirTrazas(String desde, String idUsuario, String codWeb, String a, String b, String c, String d, DatosTrazaBean datos, int nivelTraza, Exception e, String idEjecucion, String idPerfil, int nivelTrazaAplicacion) {
	// Por defecto el nivel de traza es 0, si no lo puedo obtener se utiliza este
	//int nivelTrazaAplicacion = NIVEL_DEBUG;
	
	//Sólo escribo si el nivelTraza que tengo en aplicación es mayor o igual que el que me pasan por parámetro
	if (nivelTrazaAplicacion >=nivelTraza){
		if (MessageContext.getCurrentMessageContext() != null){
			ContextoEalia ctx = (ContextoEalia) MessageContext.getCurrentMessageContext().getProperty("CONTEXTO_EALIA");
			if (ctx.getCodAplicacion().equals(Constantes.COD_APLICACION_EALIAAPP)){
				logger=Logger.getLogger("es.cecabank.ealiaapp.logica.logicaimpl.MessageReceiverInOut");			
			}
			else if(ctx.getCodAplicacion().equals(Constantes.COD_APLICACION_EALIAPAGOS)){
				logger=Logger.getLogger("es.cecabank.ealiapagos.logica.logicaimpl.MessageReceiverInOut");			
			}else{
				logger=Logger.getLogger("es.cecabank.ealiappii.logica.logicaimpl.MessageReceiverInOut");	
			}
		}
		else{
			logger=Logger.getLogger("es.cecabank.ealiapagos.logica.logicaimpl.MessageReceiverInOut");
		}
		
		writeLog(nivelTraza, "");
		writeLog(nivelTraza, idEjecucion + " ******************************");	
		writeLog(nivelTraza, idEjecucion + " * "+ idPerfil +"DESDE: "+ desde);
		
		if (a!=null && !a.equals("") ) writeLog(nivelTraza, idEjecucion + " * "+a);
		if (b!=null && !b.equals("") )  writeLog(nivelTraza, idEjecucion + " * "+b);
		if (c!=null && !c.equals("") )  writeLog(nivelTraza, idEjecucion + " * "+c);
		if (d!=null && !d.equals("") )  writeLog(nivelTraza, idEjecucion + " * "+d);
		if (e!=null) {
			writeLog(nivelTraza, idEjecucion + " * printStackTrace -->");
			writeLog(nivelTraza, getStackTrace(e));
		}
	}
}

private static void writeLog(int nivelTraza, String msg) {
	switch(nivelTraza){
		case NIVEL_DEBUG: 
			logger.debug(msg);
			break;
		case NIVEL_INFO: 
			logger.info(msg);
			break;
		case NIVEL_WARN: 
			logger.warn(msg);
			break;
		case NIVEL_ERROR: 
			logger.error(msg);
			break;
		case NIVEL_FATAL: 
			logger.fatal(msg);
			break;
	}
}

public static String getStackTrace(Throwable aThrowable) {
    final Writer result = new StringWriter();
    final PrintWriter printWriter = new PrintWriter(result);
    aThrowable.printStackTrace(printWriter);
    return result.toString();
}

public static void escribirTrazas(String desde, String usuario, String web, String a, String b, String c, String d, int nivelTraza, String idEjecucion, String idPerfil, int nivelTrazaAplicacion) {
	escribirTrazas(desde, usuario, web, a, b, c, d, null, nivelTraza, null, idEjecucion, idPerfil, nivelTrazaAplicacion);

}
public static void escribirTrazas(String desde, String usuario, String web, String a, String b, String c, String d, DatosTrazaBean datos, int nivelTraza, String idEjecucion, String idPerfil, int nivelTrazaAplicacion) {
	escribirTrazas(desde, usuario, web, a, b, c, datos.getDatoParaTrazar(nivelTraza, idEjecucion), null, nivelTraza, null, idEjecucion, idPerfil,nivelTrazaAplicacion);

}

public static void escribirTrazas(String par_desde, String a, String b, String c, String d, int nivelTrazas) {
	String idTraza = getIdTraza();
	int nivelTrazaAplicacion = getNivelTrazaAplicacion();
	escribirTrazas(par_desde, "-", "-", a, b, c, d, nivelTrazas, idTraza, "", nivelTrazaAplicacion);

}
public static void escribirTrazas(String par_desde, String a, String b, String c, String d, DatosTrazaBean datos, int nivelTrazas) {
	String idTraza = getIdTraza();
	int nivelTrazaAplicacion = getNivelTrazaAplicacion();
	escribirTrazas(par_desde, "-", "-", a, b, c, d, datos, nivelTrazas, idTraza, "", nivelTrazaAplicacion);

}
public static void escribirTrazas(String par_desde, String a, String b, String c, String d, Exception e, int nivelTrazas) {
	String idTraza = getIdTraza();
	int nivelTrazaAplicacion = getNivelTrazaAplicacion();
	escribirTrazas(par_desde, "-", "-", a, b, c, d, null, nivelTrazas, e, idTraza, "", nivelTrazaAplicacion);
}

private static String getIdTraza() {
	String idTraza;
	if (MessageContext.getCurrentMessageContext() != null){
		idTraza = (String)(MessageContext.getCurrentMessageContext().getProperty("ID_TRAZA"));
	}
	else{
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		idTraza = format.format(new Date());
	}
	
	return idTraza;
}
private static int getNivelTrazaAplicacion() {
	int nivelTrazaAplicacion = NIVEL_DEBUG;
	if (MessageContext.getCurrentMessageContext() != null){
		try {
			nivelTrazaAplicacion = Integer.parseInt(String.valueOf((MessageContext.getCurrentMessageContext().getProperty("NIVEL_LOG"))));
		} catch (Exception e1) {
		}
	}
	
	return nivelTrazaAplicacion;
}
}
