package es.cecabank.ealiaapp.servicios.wsimpl;

import javax.servlet.http.HttpServletRequest;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.databinding.ADBBean;
import org.apache.axis2.databinding.ADBException;

import ceca.control.CecaException;
import es.cecabank.ealiaapp.accesobd.dao.EappDispInstalacionesDao;
import es.cecabank.ealiaapp.accesobd.dao.EappSvcsInstruccionesTiposDao;
import es.cecabank.ealiaapp.accesobd.dao.EappSvcsServiciosRespuestasDao;
import es.cecabank.ealiaapp.accesobd.daoimpl.EappDispInstalacionesDaoImpl;
import es.cecabank.ealiaapp.accesobd.daoimpl.EappSvcsInstruccionesTiposDaoImpl;
import es.cecabank.ealiaapp.accesobd.daoimpl.EappSvcsServiciosRespuestasDaoImpl;
import es.cecabank.ealiaapp.accesobd.model.EappDispInstalaciones;
import es.cecabank.ealiaapp.accesobd.model.EappSvcsServiciosRespuestas;
import es.cecabank.ealiaapp.logica.beans.DatosMensajesBean;
import es.cecabank.ealiaapp.logica.logicaimpl.EaliaSkeleton;
import es.cecabank.ealiaapp.logica.logicaimpl.MessagesService;
import es.cecabank.ealiaapp.logica.logicaimpl.SecurityService;
import es.cecabank.ealiaapp.logica.logicaimpl.SesionService;
import es.cecabank.ealiacomun.control.CtrlIncidencias;
import es.cecabank.ealiacomun.control.CtrlTrazas;
import es.cecabank.ealiacomun.control.EaliaException;
import es.cecabank.ealiacomun.control.TrazadorFichero;
import es.cecabank.ealiacomun.logica.beans.MensajeBean;
import es.cecabank.ealiacomun.mensajes.elements.complex.ealiaapp.EalfGroupHeader1;
import es.cecabank.ealiacomun.mensajes.elements.complex.ealiaapp.EalfOriginalGroup1;
import es.cecabank.ealiacomun.mensajes.elements.simple.BICFIIdentifier;
import es.cecabank.ealiacomun.mensajes.elements.simple.ISODateTime;
import es.cecabank.ealiacomun.mensajes.elements.simple.Max350Text;
import es.cecabank.ealiacomun.mensajes.elements.simple.Max35Text;
import es.cecabank.ealiacomun.util.Constantes;

/**
 * Web services common functionality
 * 
  
 * @param <I> Web service input class.
 * @param <O> Web service output class.
 * @see #validarSesion() If any web service does not use session Validation or uses a different one from 
 * 		default one
 * @see #validarInstructionType() If any web service does not validate
 *      instruction type field
 * @see #controlarAcceso() If any web service does not control service
 *      access
 */
public abstract class AbstractSkeleton<I extends ADBBean, O extends ADBBean> extends EaliaSkeleton {
	/**
	 * input message
	 */
	protected I input;
	/**
	 * Webservice data bean
	 */
	protected DatosMensajesBean datos;
	
	// //////////////////////////////////////////
	// //////////////////////////////////////////
	// FUNCIONALIDAD COMÚN DE TODOS LOS WSF
	// //////////////////////////////////////////
	// //////////////////////////////////////////

	/**
	 * main web service logic
	 * @param input input message
	 * @return output message
	 * @throws CecaException
	 * @throws Exception
	 */
	public O ejecutar(I input) throws CecaException, Exception {
		TrazadorFichero.escribirTrazas(getDesdeDonde(), "Entro", "", "", "", TrazadorFichero.NIVEL_DEBUG);

		/******************************************************/
		/** 1. SETTING MESSAGE DATO INTO CONTEXT **/
		/******************************************************/
		this.input = input;
		this.datos = new DatosMensajesBean();

		O response = null;

		try {
			setDatos();
			ctx.setDatosMensajes(datos);

			initEntityManager();

			/******************************************************/
			/** 2. VALIDATIONS *****************************/
			/******************************************************/
			MessagesService.grabarMensajeEntrada(datos, getMensajeEntrada(), getNombreWebService(), em);
			controlarAcceso();
			validarInstructionType();
			validarEntrada();
			validarSesion();

			/******************************************************/
			/** 3. WEB SERVICE SPECIFIC CODE ***********************/
			/******************************************************/
			ejecutarLogica();
			response = generateResponse();
			validarSalida(response);
			
			return response;
			/******************************************************/
			/** 4. EXCEPTION MANAGEMENT **************************/
			/******************************************************/
			
		} catch (Exception e) {
			response = tratarExcepcion(e, datos.getInstrTp(), getNombreWebService(), getDesdeDonde());
			return response;
		} finally {
			try {
				setStatusCode(response);
				MessagesService.grabarMensajeSalida(datos, getMensajeSalida(), getNombreWebService(), em, ctx);
			} catch (CecaException e) {
				// No devolvemos error en este caso
			}
			
			// Getting the signature
			EappDispInstalacionesDao daoInstalaciones = new EappDispInstalacionesDaoImpl(em);
			EappDispInstalaciones instalacion = daoInstalaciones.getById(ctx.getDatosMensajes().getInstId());
			if (instalacion != null) {
				String firma = instalacion.getIdClavePrivadaEal();
				ctx.setFirma(firma);
			}
			
			// Se cierra el gestor de entidades
			closeEntityManager();

			/******************************************************/
			/** 5. GRABAR TRAZAS EN BBDD **************************/
			/******************************************************/
			CtrlTrazas.grabarTraza(datos, getNombreOperacion(), excepcion, getEaliaCtx());
			TrazadorFichero.escribirTrazas(getDesdeDonde(), "Salgo", "", "", "", TrazadorFichero.NIVEL_INFO);
		}
	}

	/**
	 * Instruction Type Validation. 
	 * @throws CecaException
	 * @throws Exception
	 */
	protected void validarInstructionType() throws CecaException, Exception {
		String codTipoInstruccion = null;
		try {
			codTipoInstruccion = datos.getInstrTp();
		} catch (NullPointerException e) {
		}
		EappSvcsInstruccionesTiposDao daoInstTp = new EappSvcsInstruccionesTiposDaoImpl(em);
		daoInstTp.validaIntructionType(getNombreWebService(), codTipoInstruccion);
	}

	/**
	 * Session validation
	 * @throws CecaException
	 */
	protected void validarSesion() throws CecaException {
		validarSesion("");
	}

	/**
	 * Session validation
	 * @param tipoControl  type of validation ""/"LGIN"/"ALTA"/...
	 * Default value is ""
	 * @throws CecaException If there is any validation error
	 */
	protected void validarSesion(String tipoControl) throws CecaException {
		int codSesion = SesionService.validarSesion(datos, tipoControl, em);
		if (codSesion == SesionService.VALIDAR_SESION_NOK) {
			throw new CecaException("NSSN", "No hay sesion.", getDesdeDonde());
		} else if (codSesion == SesionService.VALIDAR_SESION_DUPL) {
			throw new CecaException("DUPL", "Sesión duplicada.", getDesdeDonde());
		}
	}

	/**
	 * AccesControl
	 * @throws CecaException
	 */
	protected void controlarAcceso() throws CecaException {
		// Validación de seguridad y control de acceso al servicio
		SecurityService.seguridadYControlDeAcceso(ctx, getNombreWebService(), getHashParameter(),em);
	}

	/**
	 * Output object validation
	 * @param res Output message
	 * @throws Exception
	 */
	private void validarSalida(O res) throws Exception {
		String desdeDonde = new StringBuffer("validarSalida ").append(getDesdeDonde()).toString();
		TrazadorFichero.escribirTrazas(desdeDonde, "Entro", "", "", "", TrazadorFichero.NIVEL_DEBUG);
		SOAPFactory soapFactory;
		if (MessageContext.getCurrentMessageContext().isSOAP11()) {
			soapFactory = OMAbstractFactory.getSOAP11Factory();
		} else {
			soapFactory = OMAbstractFactory.getSOAP12Factory();
		}
		SOAPEnvelope envelope = toEnvelope(soapFactory, res);
		validarSalida(envelope.getBody().getFirstElement().getXMLStreamReaderWithoutCaching());
		TrazadorFichero.escribirTrazas(desdeDonde, "Salgo", "", "", "", TrazadorFichero.NIVEL_INFO);
	}

	/**
	 * Parse input object to SOAPEnvelope response 
	 * @param factory Factory soap
	 * @param param Response Object
	 * @return Envelope response
	 * @throws AxisFault
	 */
	private SOAPEnvelope toEnvelope(SOAPFactory factory, ADBBean param) throws AxisFault {
		try {
			SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
			emptyEnvelope.getBody().addChild(param.getOMElement(getResponseQName(), factory));
			return emptyEnvelope;
		} catch (ADBException e) {
			throw AxisFault.makeFault(e);
		} catch (Exception ex) {
			throw AxisFault.makeFault(ex);
		}
	}

	/**
	 * Exception management
	 * 
	 * @param e input exception
	 * @param instrtp instrction type
	 * @param nombreServicio Service name
	 * @param desdeDonde trace text for loggin
	 * @return Error output message
	 * @throws CecaException
	 */
	private O tratarExcepcion(Exception e, String instrtp, String nombreServicio, String desdeDonde)
			throws CecaException {
		TrazadorFichero.escribirTrazas(desdeDonde, "Entro", "", "", "", TrazadorFichero.NIVEL_DEBUG);
		CecaException exception = null;
		MensajeBean mensaje = new MensajeBean(Constantes.COD_APLICACION_EALIAAPP);
		String descripcionRespuesta = "";
		if (e.getClass() == (CecaException.class)) {
			exception = (CecaException) e;
			EappSvcsServiciosRespuestasDao eappSvcsServiciosRespuestasDao = new EappSvcsServiciosRespuestasDaoImpl(em);
			
			
			//Solución ÑAPA para el instruction type de wsf_usuarios_gestionar al buscar el error cuando no hay instruction type
			/*if (nombreServicio.equals(Constantes.NOMBRE_SERVICIO_WSF_USUARIOS_GESTIONAR) && instrtp.equals("")) {
				instrtp = Constantes.COD_TIPO_INSTRUCCION_ALTA;
			}*/
			
			
			descripcionRespuesta = eappSvcsServiciosRespuestasDao.buscarDescripcionMensajeRespuesta(
					exception.getCodError(), instrtp, nombreServicio, ctx.getCodIdioma());
			if (descripcionRespuesta == null) {
				descripcionRespuesta = eappSvcsServiciosRespuestasDao.buscarDescripcionMensajeRespuesta("ETMT",
						instrtp, nombreServicio, ctx.getCodIdioma());
				exception = new CecaException("ETMT", descripcionRespuesta, descripcionRespuesta, exception.getE(),
						desdeDonde);
			}
			if (exception.getDescripcionTecnica() != null && !exception.getDescripcionTecnica().equals("")) {
				mensaje.setDesMensajeVisualizacion(exception.getDescripcionTecnica());
			}
		} else if (e.getClass() == (EaliaException.class)) {
			exception = new CecaException("RJVL", e.getMessage(), e, desdeDonde);
			EappSvcsServiciosRespuestasDao eappSvcsServiciosRespuestasDao = new EappSvcsServiciosRespuestasDaoImpl(em);
			descripcionRespuesta = eappSvcsServiciosRespuestasDao.buscarDescripcionMensajeRespuesta("RJVL", instrtp,
					nombreServicio, ctx.getCodIdioma());
			if (descripcionRespuesta == null) {
				if (exception.getE() != null) {
					exception = new CecaException("RJVL",
							"No se han completado todos los datos o alguno de los datos es incorrecto",
							"No se han completado todos los datos o alguno de los datos es incorrecto",
							exception.getE(), desdeDonde);
				} else {
					exception = new CecaException("RJVL",
							"No se han completado todos los datos o alguno de los datos es incorrecto",
							"No se han completado todos los datos o alguno de los datos es incorrecto", e, desdeDonde);
				}
				descripcionRespuesta = exception.getDescripcionTecnica();
			}
			// El campo descripción técina lo devolveremos en la respuesta si es
			// una RJVL
			if (exception.getDescripcionTecnica() != null && !exception.getDescripcionTecnica().equals("")) {
				mensaje.setDesMensajeVisualizacion(exception.getDescripcionTecnica());
			}
		} else {
			mensaje = CtrlIncidencias.consultarMensaje("001", ctx);
			descripcionRespuesta = "Error general en el sistema.";
			exception = new CecaException("ERRS", descripcionRespuesta, mensaje.getDesMensajeVisualizacion(), e,
					desdeDonde);
		}
		ctx.setStatus(Constantes.COD_ESTADO_WS_ERROR);
		CtrlIncidencias.generarLOG(exception, ctx);
		mensaje.setCodError(exception.getCodError());
		mensaje.setDesError(descripcionRespuesta);
		excepcion = exception;
		O res = generarRespuestaError(mensaje);
		TrazadorFichero.escribirTrazas(desdeDonde, "Salgo", "", "", "", TrazadorFichero.NIVEL_INFO);
		return res;
	}

	
	/**
	 * input validations
	 */
	protected abstract void validarEntrada() throws CecaException;

	/**
	 * Main method logic execution
	 */
	protected abstract void ejecutarLogica() throws CecaException, Exception;

	/**
	 * @return Output message
	 * @throws Exception
	 */
	protected abstract O generateResponse() throws Exception;

	/**
	 * XML Output validation
	 * @param reader
	 *           
	 */
	protected abstract void validarSalida(XMLStreamReader reader) throws Exception;

	/**
	 * Generate output error message object
	 * @param mensaje Web service storaged data
	 * @return Output Error Message
	 */
	protected abstract O generarRespuestaError(MensajeBean mensaje) throws CecaException;

	/**
	 * 
	 * @return Execution point description String for logger
	 */
	protected abstract String getDesdeDonde();

	/**
	 * @return Web service name
	 */
	protected abstract String getNombreWebService();

	/**
	 * @return MY_QNAME response parametter value
	 */
	protected abstract QName getResponseQName();

	/**
	 * Setter for web service data bean
	 */
	protected abstract void setDatos() throws CecaException;

	/**
	 * @return web service output message name
	 */
	protected abstract String getMensajeSalida();

	/**
	 * @return web service input message name
	 */
	protected abstract String getMensajeEntrada();

	/**
	 * Get Operation name
	 * @return Operation name
	 */
	protected abstract String getNombreOperacion();

	/**
	 * 
	 * @param response output message
	 *          
	 */
	protected abstract void setStatusCode(O response);

	
	/**
	 * GroupHeader output
	 * @return Filled GroupHeader
	 * @throws CecaException
	 */
	protected EalfGroupHeader1 generateGroupHeader() throws CecaException {
		String desdeDonde = "generateGroupHeader - " + getNombreWebService();
		TrazadorFichero.escribirTrazas(desdeDonde, "Entro", "", "", "", TrazadorFichero.NIVEL_DEBUG);
		EalfGroupHeader1 ealfGroupHeader1 = new EalfGroupHeader1();
		ealfGroupHeader1.setMsgId(Max350Text.Factory.fromString(ctx.getDatosMensajes().getMsgIdResp(), ""));
		ealfGroupHeader1.setCreDtTm(ISODateTime.Factory.fromString(ctx.getDatosMensajes().getCreDtTmResp(), ""));
		if (ctx.getDatosMensajes().getInstrTp() != null && !ctx.getDatosMensajes().getInstrTp().isEmpty()) {
			ealfGroupHeader1.setInstrTp(Max35Text.Factory.fromString(ctx.getDatosMensajes().getInstrTp(), ""));
		}
		ealfGroupHeader1.setUsrAgtBIC(BICFIIdentifier.Factory.fromString(ctx.getDatosMensajes().getBicEntidad(), ""));
		// XXX La comparación con la cadena "----" viene de la inicialización
		// del atributo en DatosMensajes. REFACTORIZABLE in the future!!!
		if (ctx.getDatosMensajes().getIdUsuario() != null && !ctx.getDatosMensajes().getIdUsuario().isEmpty()
				&& !"----".equals(ctx.getDatosMensajes().getIdUsuario())) {
			ealfGroupHeader1.setUsrId(Max35Text.Factory.fromString(ctx.getDatosMensajes().getIdUsuario(), ""));
		}
		if (ctx.getDatosMensajes().getSessionId() != null && !ctx.getDatosMensajes().getSessionId().isEmpty()) {
			ealfGroupHeader1.setSsnId(Max350Text.Factory.fromString(ctx.getDatosMensajes().getSessionId(), ""));
		}
		ealfGroupHeader1.setEalAppId(Max35Text.Factory.fromString(ctx.getDatosMensajes().getCodAplicacion(), ""));
		ealfGroupHeader1.setInstId(Max350Text.Factory.fromString(ctx.getDatosMensajes().getInstId(), ""));
		TrazadorFichero.escribirTrazas(desdeDonde, "Salgo", "", "", "", TrazadorFichero.NIVEL_INFO);
		return ealfGroupHeader1;
	}

	/**
	 * Generate OriginalGroup output
	 * @return Filled OriginalGroup
	 * @throws CecaException
	 */
	protected EalfOriginalGroup1 generateOriginalGroup() throws CecaException {
		String desdeDonde = "generateOriginalGroup - " + getNombreWebService();
		TrazadorFichero.escribirTrazas(desdeDonde, "Entro", "", "", "", TrazadorFichero.NIVEL_DEBUG);

		EalfOriginalGroup1 ealfOriginalGroup1 = new EalfOriginalGroup1();
		ealfOriginalGroup1.setOrgnlMsgId(Max350Text.Factory.fromString(ctx.getDatosMensajes().getMsgId(), ""));
		ealfOriginalGroup1.setRpsStsCd(Max35Text.Factory.fromString(Constantes.COD_ESTADO_WS_RESULT_ACEPTADO, ""));

		EappSvcsServiciosRespuestasDao eappSvcsServiciosRespuestasDao = new EappSvcsServiciosRespuestasDaoImpl(em);
		EappSvcsServiciosRespuestas eappSvcsServiciosRespuestas = eappSvcsServiciosRespuestasDao
				.getDesServicioRespuestaById(Constantes.COD_ESTADO_WS_RESULT_ACEPTADO, ctx.getDatosMensajes()
						.getInstrTp(), getNombreWebService(), Constantes.COD_IDIOMA_ES);
		ealfOriginalGroup1
				.setRpsStsDes(Max350Text.Factory.fromString(eappSvcsServiciosRespuestas.getDesRespuesta(), ""));
		TrazadorFichero.escribirTrazas(desdeDonde, "Salgo", "", "", "", TrazadorFichero.NIVEL_INFO);
		return ealfOriginalGroup1;
	}
	
	/**
	 * Get digital sign (input Object Hash) included within HHTP Header
	 * @return
	 */
	private String getHashParameter() {
		HttpServletRequest req =(HttpServletRequest)MessageContext.getCurrentMessageContext().getProperty("transport.http.servletRequest");
		String hashParameterValue = req.getHeader("ealiaSignature");
		return hashParameterValue;
	}
				
}
