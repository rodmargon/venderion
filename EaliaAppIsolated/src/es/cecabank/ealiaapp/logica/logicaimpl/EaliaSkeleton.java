/**
 * GestionUsuariosServiceSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:33:49 IST)
 */
package es.cecabank.ealiaapp.logica.logicaimpl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.wsdl.WSDLConstants;

import ceca.control.CecaException;
import es.cecabank.ealiaapp.control.HibernateUtil;
import es.cecabank.ealiacomun.control.ContextoEalia;
import es.cecabank.ealiacomun.logica.beans.MensajeBean;

public abstract class EaliaSkeleton {
	
	@PersistenceContext
	protected EntityManager em;

	protected ContextoEalia ctx;

	protected CecaException excepcion = null;

	protected MensajeBean mensajeError;
	
	public EaliaSkeleton(){
		// Permitir el uso de llamadas de dominio cruzado "CROSS DOMAIN"  a los servicios de front"
		// -----------------------------  habilitar CROSS DOMAIN --------------------------------------------------
		List<org.apache.commons.httpclient.Header> headers = new ArrayList<org.apache.commons.httpclient.Header>();
		headers.add(new org.apache.commons.httpclient.Header("Access-Control-Allow-Origin", "*"));
		headers.add(new org.apache.commons.httpclient.Header("Access-Control-Allow-Methods", "POST, GET, PUT, DELETE, OPTIONS"));
		headers.add(new org.apache.commons.httpclient.Header("Access-Control-Allow-Headers", "X-Requested-With, content-type"));
		try {
			MessageContext contextoSalida = MessageContext.getCurrentMessageContext().getOperationContext()
					.getMessageContext(WSDLConstants.MESSAGE_LABEL_OUT_VALUE);
			contextoSalida.setProperty(HTTPConstants.HTTP_HEADERS, headers);
			
		} catch (AxisFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// ----------------------fin código habilitar CROSS DOMAIN --------------------------------------------------
	}
	
	protected void initEntityManager(){
		// Inicializamos el EntityManager
		this.em = HibernateUtil.getEntityManagerFactory().createEntityManager();
	}

	public ContextoEalia getCtx() {
		return ctx;
	}
	
	/**
	 * Este método es para ahorrar en refactorizar código antiguo.
	 * @return
	 */
	public ContextoEalia getContexto() {
		return ctx;
		
	}

	public MensajeBean getMensajeError() {
		return this.mensajeError;
	}

	public CecaException getExcepcion() {
		return excepcion;
	}
	
	protected EntityManager getEm(){		
		return this.em;
	}
	
	protected void closeEntityManager(){
		this.em.close();
	}

	/**
	 * Recupera el objeto ContextoEalia introducido desde el MessageReceiverInOut.
	 * @return
	 */
	protected ContextoEalia getEaliaCtx() {
		//return (ContextoEalia)MessageContext.getCurrentMessageContext().getProperty("CONTEXTO_EALIA");
		return this.ctx;
	}
	
	public void setEaliaCtx(ContextoEalia ce){
		this.ctx = ce;
	}
}
