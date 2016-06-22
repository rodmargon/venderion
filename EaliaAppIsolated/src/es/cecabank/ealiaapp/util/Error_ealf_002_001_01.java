package es.cecabank.ealiaapp.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.axis2.context.MessageContext;

import ceca.control.CecaException;
import es.cecabank.ealiaapp.control.PropertiesEaliaApp;
import es.cecabank.ealiaapp.logica.beans.DatosMensajesBean;
import es.cecabank.ealiaapp.servicios.mensajes.ealf_002_001_01.Document;
import es.cecabank.ealiaapp.servicios.mensajes.ealf_002_001_01.EalfStatusReportV01;
import es.cecabank.ealiaapp.servicios.wsimpl.agenda.wsf_agenda_actualizar.AgendaActualizarResponse;
import es.cecabank.ealiaapp.servicios.wsimpl.agenda.wsf_agenda_actualizar.AgendaActualizarResponseFault;
import es.cecabank.ealiaapp.servicios.wsimpl.dispositivos.wsf_dispositivos_actualizar.DispositivosActualizarResponse;
import es.cecabank.ealiaapp.servicios.wsimpl.dispositivos.wsf_dispositivos_actualizar.DispositivosActualizarResponseFault;
import es.cecabank.ealiaapp.servicios.wsimpl.ficheros.wsf_ficheros_cargar.FicherosCargarResponse;
import es.cecabank.ealiaapp.servicios.wsimpl.ficheros.wsf_ficheros_cargar.FicherosCargarResponseFault;
import es.cecabank.ealiaapp.servicios.wsimpl.operaciones.wsf_operaciones_anular.OperacionesAnularResponse;
import es.cecabank.ealiaapp.servicios.wsimpl.operaciones.wsf_operaciones_anular.OperacionesAnularResponseFault;
import es.cecabank.ealiaapp.servicios.wsimpl.operaciones.wsf_operaciones_cobrar.OperacionesCobrarResponse;
import es.cecabank.ealiaapp.servicios.wsimpl.operaciones.wsf_operaciones_cobrar.OperacionesCobrarResponseFault;
import es.cecabank.ealiaapp.servicios.wsimpl.operaciones.wsf_operaciones_pagar.OperacionesPagarResponse;
import es.cecabank.ealiaapp.servicios.wsimpl.operaciones.wsf_operaciones_pagar.OperacionesPagarResponseFault;
import es.cecabank.ealiaapp.servicios.wsimpl.operaciones.wsf_operaciones_solicitar.OperacionesSolicitarResponse;
import es.cecabank.ealiaapp.servicios.wsimpl.operaciones.wsf_operaciones_solicitar.OperacionesSolicitarResponseFault;
import es.cecabank.ealiaapp.servicios.wsimpl.usuarios.wsf_usuarios_finalizarsesion.UsuariosFinalizarSesionResponse;
import es.cecabank.ealiaapp.servicios.wsimpl.usuarios.wsf_usuarios_finalizarsesion.UsuariosFinalizarSesionResponseFault;
import es.cecabank.ealiaapp.servicios.wsimpl.usuarios.wsf_usuarios_gestionar.UsuariosGestionarResponse;
import es.cecabank.ealiaapp.servicios.wsimpl.usuarios.wsf_usuarios_gestionar.UsuariosGestionarResponseFault;
import es.cecabank.ealiacomun.control.ContextoEalia;
import es.cecabank.ealiacomun.control.CtrlIncidencias;
import es.cecabank.ealiacomun.control.TrazadorFichero;
import es.cecabank.ealiacomun.logica.beans.MensajeBean;
import es.cecabank.ealiacomun.mensajes.elements.complex.ealiaapp.EalfGroupHeader1;
import es.cecabank.ealiacomun.mensajes.elements.complex.ealiaapp.EalfOriginalGroup1;
import es.cecabank.ealiacomun.mensajes.elements.simple.BICFIIdentifier;
import es.cecabank.ealiacomun.mensajes.elements.simple.ISODateTime;
import es.cecabank.ealiacomun.mensajes.elements.simple.Max350Text;
import es.cecabank.ealiacomun.mensajes.elements.simple.Max35Text;
import es.cecabank.ealiacomun.util.Constantes;

public class Error_ealf_002_001_01 {

	
	
	
	/* ***********************************************
	 * **********  OPERACIONES SOLICITAR  ***************
	 ************************************************/
	public OperacionesSolicitarResponse crearRespuestaError_operacionesSolicitar() throws CecaException {
		String desdeDonde = "crearRespuestaError_operacionesSolicitar - es.cecabank.ealiaapp.util.Error_ealf_002_001_01";
		TrazadorFichero.escribirTrazas(desdeDonde, "Entro", "", "", "", TrazadorFichero.NIVEL_DEBUG);
		OperacionesSolicitarResponse res = null;

		try {
			File f = new File(PropertiesEaliaApp.getPropiedad(PropertiesEaliaApp.PATH_XML_ERROR) + Constantes.WSF_XML_ERROR_OPERACIONES_SOLICITAR_EALF_002_001_01);
			InputStream is = new FileInputStream(f);
			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			XMLStreamReader reader = null;
			try {
				reader = inputFactory.createXMLStreamReader(is);
				res = OperacionesSolicitarResponse.Factory.parse(reader);
				res.getOperacionesSolicitarReturn().getStsRpt().setGrpHdr(UtilApp.extraerGroupHeaderDeXML(MessageContext.getCurrentMessageContext().getEnvelope().toString()));
				String desError = (String)MessageContext.getCurrentMessageContext().getProperty("DES_MENSAJE_ERROR");
				if (desError != null && !desError.equals("")){
					res.getOperacionesSolicitarReturn().getStsRpt().getOrgnlGrpInfAndSts().setOrgnlMsgId(Max350Text.Factory.fromString(res.getOperacionesSolicitarReturn().getStsRpt().getGrpHdr().getMsgId().getMax350Text(), ""));
					res.getOperacionesSolicitarReturn().getStsRpt().getOrgnlGrpInfAndSts().setRpsStsCd(Max35Text.Factory.fromString("RJVL", ""));
					int indice = desError.indexOf(":");
					String texto = Constantes.EXCEPCION_PARSEO_ORDEN;
					if (indice == -1){
						indice = desError.indexOf(texto);
						if (indice != -1){
							desError = desError.replaceAll(texto, "No se han completado todos los datos o alguno de los datos es incorrecto");
							indice += ("No se han completado todos los datos o alguno de los datos es incorrecto").length();
						}
					}
					else{
						texto = Constantes.EXCEPCION_PARSEO_FORMATO;
						indice = desError.indexOf(texto);
						if (indice != -1){
							desError = desError.replaceAll(texto, "No se han completado todos los datos o alguno de los datos es incorrecto");
							indice += ("No se han completado todos los datos o alguno de los datos es incorrecto").length();
						}
					}
					if (indice != -1){
						res.getOperacionesSolicitarReturn().getStsRpt().getOrgnlGrpInfAndSts().setRpsStsDes(Max350Text.Factory.fromString(desError.substring(0, indice), "")); 
						res.getOperacionesSolicitarReturn().getStsRpt().getOrgnlGrpInfAndSts().setRpsStsAddInf(Max350Text.Factory.fromString(desError.substring(indice+1, desError.length()).trim(), ""));
					}
				}
			} finally {
				if (reader != null) {
					reader.close();
				}
			}
		} catch (Exception e) {
			ContextoEalia ctx = (ContextoEalia)MessageContext.getCurrentMessageContext().getProperty("CONTEXTO_EALIA");
			MensajeBean error = CtrlIncidencias.consultarMensaje("001", ctx);
			CecaException ce = new CecaException(error.getCodError(), e.getMessage(), error.getDesMensajeVisualizacion(), e, desdeDonde);
			CtrlIncidencias.generarLOG(ce, ctx);
			throw new CecaException("000", "", ""); // Con 000 paramos todo
		}
		return res;
	}

	public OperacionesSolicitarResponse crearRespuestaErrorObjeto_operacionesSolicitar(MensajeBean mensaje, ContextoEalia ctx) throws CecaException {
		String desdeDonde = "crearRespuestaErrorObjeto_operacionesSolicitar - es.cecabank.ealiaapp.util.Error_ealf_002_001_01";
		TrazadorFichero.escribirTrazas(desdeDonde, "Entro", "", "", "", TrazadorFichero.NIVEL_DEBUG);
		OperacionesSolicitarResponse response = null;

		try {
			response = new OperacionesSolicitarResponse();
			Document doc = new Document();
			EalfStatusReportV01 ealfPaymentStsRpt = new EalfStatusReportV01();
			EalfGroupHeader1 grpHdr = new EalfGroupHeader1();
			EalfOriginalGroup1 orgnlGrpInfAndSts = new EalfOriginalGroup1();
			BICFIIdentifier bicfi = new BICFIIdentifier();

			bicfi.setBICFIIdentifier(Constantes.BIC_EALIA);
			grpHdr.setMsgId(Max350Text.Factory.fromString(ctx.getDatosMensajes().getMsgIdResp(), ""));
			grpHdr.setUsrAgtBIC(bicfi);
			grpHdr.setCreDtTm(ISODateTime.Factory.fromString(ctx.getDatosMensajes().getCreDtTmResp(), ""));
			grpHdr.setEalAppId(Max35Text.Factory.fromString(((DatosMensajesBean)ctx.getDatosMensajes()).getCodAplicacion(), ""));
			grpHdr.setInstId(Max350Text.Factory.fromString(ctx.getDatosMensajes().getInstId(), ""));

			orgnlGrpInfAndSts.setOrgnlMsgId(Max350Text.Factory.fromString(ctx.getDatosMensajes().getMsgId(), ""));
			orgnlGrpInfAndSts.setRpsStsCd(Max35Text.Factory.fromString(mensaje.getCodError(), ""));
			orgnlGrpInfAndSts.setRpsStsDes(Max350Text.Factory.fromString(mensaje.getDesError(), ""));
			if (mensaje.getCodError().equals("RJVL")){
				orgnlGrpInfAndSts.setRpsStsAddInf(Max350Text.Factory.fromString(mensaje.getDesMensajeVisualizacion(), ""));
			}

			ealfPaymentStsRpt.setGrpHdr(grpHdr);
			ealfPaymentStsRpt.setOrgnlGrpInfAndSts(orgnlGrpInfAndSts);

			doc.setStsRpt(ealfPaymentStsRpt);
			response.setOperacionesSolicitarReturn(doc);
		} catch (Exception e) {
			MensajeBean error = CtrlIncidencias.consultarMensaje("001", ctx);
			CecaException ce = new CecaException(error.getCodError(), e.getMessage(), error.getDesMensajeVisualizacion(), e, desdeDonde);
			CtrlIncidencias.generarLOG(ce, ctx);
			throw new CecaException("000", "", ""); // Con 000 paramos todo
		}
		return response;
	}

	public OperacionesSolicitarResponseFault crearRespuestaErrorFault_operacionesSolicitar() throws CecaException {
		String desdeDonde = "crearRespuestaErrorFault_operacionesSolicitar - es.cecabank.ealiaapp.util.Error_ealf_002_001_01";
		TrazadorFichero.escribirTrazas(desdeDonde, "Entro", "", "", "", TrazadorFichero.NIVEL_DEBUG);
		OperacionesSolicitarResponseFault res = null;

		try {
			File f = new File(PropertiesEaliaApp.getPropiedad(PropertiesEaliaApp.PATH_XML_ERROR) + Constantes.WSF_XML_ERROR_OPERACIONES_SOLICITAR_EALF_002_001_01);
			InputStream is = new FileInputStream(f);

			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			XMLStreamReader reader = null;
			try {
				reader = inputFactory.createXMLStreamReader(is);
				res = OperacionesSolicitarResponseFault.Factory.parse(reader);
			} finally {
				if (reader != null) {
					reader.close();
				}
			}
		} catch (Exception e) {
			ContextoEalia ctx = (ContextoEalia)MessageContext.getCurrentMessageContext().getProperty("CONTEXTO_EALIA");
			MensajeBean error = CtrlIncidencias.consultarMensaje("001", ctx);
			CecaException ce = new CecaException(error.getCodError(), e.getMessage(), error.getDesMensajeVisualizacion(), e, desdeDonde);
			CtrlIncidencias.generarLOG(ce, ctx);
			throw new CecaException("000", "", ""); // Con 000 paramos todo
		}
		return res;
	}
	
	/* **********************************************
	 * **********  OPERACIONES PAGAR  ***************
	 ***********************************************/
	public OperacionesPagarResponse crearRespuestaError_operacionesPagar() throws CecaException {
		String desdeDonde = "crearRespuestaError_operacionesPagar - es.cecabank.ealiaapp.util.Error_ealf_002_001_01";
		TrazadorFichero.escribirTrazas(desdeDonde, "Entro", "", "", "", TrazadorFichero.NIVEL_DEBUG);
		OperacionesPagarResponse res = null;

		try {
			File f = new File(PropertiesEaliaApp.getPropiedad(PropertiesEaliaApp.PATH_XML_ERROR) + Constantes.WSF_XML_ERROR_OPERACIONES_PAGAR_EALF_002_001_01);
			InputStream is = new FileInputStream(f);
			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			XMLStreamReader reader = null;
			try {
				reader = inputFactory.createXMLStreamReader(is);
				res = OperacionesPagarResponse.Factory.parse(reader);
				res.getOperacionesPagarReturn().getStsRpt().setGrpHdr(UtilApp.extraerGroupHeaderDeXML(MessageContext.getCurrentMessageContext().getEnvelope().toString()));
				String desError = (String)MessageContext.getCurrentMessageContext().getProperty("DES_MENSAJE_ERROR");
				if (desError != null && !desError.equals("")){
					res.getOperacionesPagarReturn().getStsRpt().getOrgnlGrpInfAndSts().setOrgnlMsgId(Max350Text.Factory.fromString(res.getOperacionesPagarReturn().getStsRpt().getGrpHdr().getMsgId().getMax350Text(), ""));
					res.getOperacionesPagarReturn().getStsRpt().getOrgnlGrpInfAndSts().setRpsStsCd(Max35Text.Factory.fromString("RJVL", ""));
					int indice = desError.indexOf(":");
					String texto = Constantes.EXCEPCION_PARSEO_ORDEN;
					if (indice == -1){
						indice = desError.indexOf(texto);
						if (indice != -1){
							desError = desError.replaceAll(texto, "No se han completado todos los datos o alguno de los datos es incorrecto");
							indice += ("No se han completado todos los datos o alguno de los datos es incorrecto").length();
						}
					}
					else{
						texto = Constantes.EXCEPCION_PARSEO_FORMATO;
						indice = desError.indexOf(texto);
						if (indice != -1){
							desError = desError.replaceAll(texto, "No se han completado todos los datos o alguno de los datos es incorrecto");
							indice += ("No se han completado todos los datos o alguno de los datos es incorrecto").length();
						}
					}
					if (indice != -1){
						res.getOperacionesPagarReturn().getStsRpt().getOrgnlGrpInfAndSts().setRpsStsDes(Max350Text.Factory.fromString(desError.substring(0, indice), "")); 
						res.getOperacionesPagarReturn().getStsRpt().getOrgnlGrpInfAndSts().setRpsStsAddInf(Max350Text.Factory.fromString(desError.substring(indice+1, desError.length()).trim(), ""));
					}
				}
			} finally {
				if (reader != null) {
					reader.close();
				}
			}
		} catch (Exception e) {
			ContextoEalia ctx = (ContextoEalia)MessageContext.getCurrentMessageContext().getProperty("CONTEXTO_EALIA");
			MensajeBean error = CtrlIncidencias.consultarMensaje("001", ctx);
			CecaException ce = new CecaException(error.getCodError(), e.getMessage(), error.getDesMensajeVisualizacion(), e, desdeDonde);
			CtrlIncidencias.generarLOG(ce, ctx);
			throw new CecaException("000", "", ""); // Con 000 paramos todo
		}
		return res;
	}

	public OperacionesPagarResponse crearRespuestaErrorObjeto_operacionesPagar(MensajeBean mensaje, ContextoEalia ctx) throws CecaException {
		String desdeDonde = "crearRespuestaErrorObjeto_operacionesPagar - es.cecabank.ealiaapp.util.Error_ealf_002_001_01";
		TrazadorFichero.escribirTrazas(desdeDonde, "Entro", "", "", "", TrazadorFichero.NIVEL_DEBUG);
		OperacionesPagarResponse response = null;

		try {
			response = new OperacionesPagarResponse();
			Document doc = new Document();
			EalfStatusReportV01 ealfPaymentStsRpt = new EalfStatusReportV01();
			EalfGroupHeader1 grpHdr = new EalfGroupHeader1();
			EalfOriginalGroup1 orgnlGrpInfAndSts = new EalfOriginalGroup1();
			BICFIIdentifier bicfi = new BICFIIdentifier();

			bicfi.setBICFIIdentifier(Constantes.BIC_EALIA);
			grpHdr.setMsgId(Max350Text.Factory.fromString(ctx.getDatosMensajes().getMsgIdResp(), ""));
			grpHdr.setUsrAgtBIC(bicfi);
			grpHdr.setCreDtTm(ISODateTime.Factory.fromString(ctx.getDatosMensajes().getCreDtTmResp(), ""));
			grpHdr.setEalAppId(Max35Text.Factory.fromString(((DatosMensajesBean)ctx.getDatosMensajes()).getCodAplicacion(), ""));
			grpHdr.setInstId(Max350Text.Factory.fromString(ctx.getDatosMensajes().getInstId(), ""));

			orgnlGrpInfAndSts.setOrgnlMsgId(Max350Text.Factory.fromString(ctx.getDatosMensajes().getMsgId(), ""));
			orgnlGrpInfAndSts.setRpsStsCd(Max35Text.Factory.fromString(mensaje.getCodError(), ""));
			orgnlGrpInfAndSts.setRpsStsDes(Max350Text.Factory.fromString(mensaje.getDesError(), ""));
			if (mensaje.getCodError().equals("RJVL")){
				orgnlGrpInfAndSts.setRpsStsAddInf(Max350Text.Factory.fromString(mensaje.getDesMensajeVisualizacion(), ""));
			}

			ealfPaymentStsRpt.setGrpHdr(grpHdr);
			ealfPaymentStsRpt.setOrgnlGrpInfAndSts(orgnlGrpInfAndSts);

			doc.setStsRpt(ealfPaymentStsRpt);
			response.setOperacionesPagarReturn(doc);
		} catch (Exception e) {
			MensajeBean error = CtrlIncidencias.consultarMensaje("001", ctx);
			CecaException ce = new CecaException(error.getCodError(), e.getMessage(), error.getDesMensajeVisualizacion(), e, desdeDonde);
			CtrlIncidencias.generarLOG(ce, ctx);
			throw new CecaException("000", "", ""); // Con 000 paramos todo
		}
		return response;
	}

	public OperacionesPagarResponseFault crearRespuestaErrorFault_operacionesPagar() throws CecaException {
		String desdeDonde = "crearRespuestaErrorFault_operacionesPagar - es.cecabank.ealiaapp.util.Error_ealf_002_001_01";
		TrazadorFichero.escribirTrazas(desdeDonde, "Entro", "", "", "", TrazadorFichero.NIVEL_DEBUG);
		OperacionesPagarResponseFault res = null;

		try {
			File f = new File(PropertiesEaliaApp.getPropiedad(PropertiesEaliaApp.PATH_XML_ERROR) + Constantes.WSF_XML_ERROR_OPERACIONES_PAGAR_EALF_002_001_01);
			InputStream is = new FileInputStream(f);

			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			XMLStreamReader reader = null;
			try {
				reader = inputFactory.createXMLStreamReader(is);
				res = OperacionesPagarResponseFault.Factory.parse(reader);
			} finally {
				if (reader != null) {
					reader.close();
				}
			}
		} catch (Exception e) {
			ContextoEalia ctx = (ContextoEalia)MessageContext.getCurrentMessageContext().getProperty("CONTEXTO_EALIA");
			MensajeBean error = CtrlIncidencias.consultarMensaje("001", ctx);
			CecaException ce = new CecaException(error.getCodError(), e.getMessage(), error.getDesMensajeVisualizacion(), e, desdeDonde);
			CtrlIncidencias.generarLOG(ce, ctx);
			throw new CecaException("000", "", ""); // Con 000 paramos todo
		}
		return res;
	}

	
	
	/* ***********************************************
	 * **********  OPERACIONES COBRAR  ***************
	 ************************************************/
	public OperacionesCobrarResponse crearRespuestaError_operacionesCobrar() throws CecaException {
		String desdeDonde = "crearRespuestaError_operacionesCobrar - es.cecabank.ealiaapp.util.Error_ealf_002_001_01";
		TrazadorFichero.escribirTrazas(desdeDonde, "Entro", "", "", "", TrazadorFichero.NIVEL_DEBUG);
		OperacionesCobrarResponse res = null;

		try {
			File f = new File(PropertiesEaliaApp.getPropiedad(PropertiesEaliaApp.PATH_XML_ERROR) + Constantes.WSF_XML_ERROR_OPERACIONES_COBRAR_EALF_002_001_01);
			InputStream is = new FileInputStream(f);
			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			XMLStreamReader reader = null;
			try {
				reader = inputFactory.createXMLStreamReader(is);
				res = OperacionesCobrarResponse.Factory.parse(reader);
				res.getOperacionesCobrarReturn().getStsRpt().setGrpHdr(UtilApp.extraerGroupHeaderDeXML(MessageContext.getCurrentMessageContext().getEnvelope().toString()));
				String desError = (String)MessageContext.getCurrentMessageContext().getProperty("DES_MENSAJE_ERROR");
				if (desError != null && !desError.equals("")){
					res.getOperacionesCobrarReturn().getStsRpt().getOrgnlGrpInfAndSts().setOrgnlMsgId(Max350Text.Factory.fromString(res.getOperacionesCobrarReturn().getStsRpt().getGrpHdr().getMsgId().getMax350Text(), ""));
					res.getOperacionesCobrarReturn().getStsRpt().getOrgnlGrpInfAndSts().setRpsStsCd(Max35Text.Factory.fromString("RJVL", ""));
					int indice = desError.indexOf(":");
					String texto = Constantes.EXCEPCION_PARSEO_ORDEN;
					if (indice == -1){
						indice = desError.indexOf(texto);
						if (indice != -1){
							desError = desError.replaceAll(texto, "No se han completado todos los datos o alguno de los datos es incorrecto");
							indice += ("No se han completado todos los datos o alguno de los datos es incorrecto").length();
						}
					}
					else{
						texto = Constantes.EXCEPCION_PARSEO_FORMATO;
						indice = desError.indexOf(texto);
						if (indice != -1){
							desError = desError.replaceAll(texto, "No se han completado todos los datos o alguno de los datos es incorrecto");
							indice += ("No se han completado todos los datos o alguno de los datos es incorrecto").length();
						}
					}
					if (indice != -1){
						res.getOperacionesCobrarReturn().getStsRpt().getOrgnlGrpInfAndSts().setRpsStsDes(Max350Text.Factory.fromString(desError.substring(0, indice), "")); 
						res.getOperacionesCobrarReturn().getStsRpt().getOrgnlGrpInfAndSts().setRpsStsAddInf(Max350Text.Factory.fromString(desError.substring(indice+1, desError.length()).trim(), ""));
					}
				}
				//res.getOperacionesCobrarReturn().getStsRpt().setSplmtryData(UtilApp.extraerSupplementaryDataDeXML(MessageContext.getCurrentMessageContext().getEnvelope().toString()));
			} finally {
				if (reader != null) {
					reader.close();
				}
			}
		} catch (Exception e) {
			ContextoEalia ctx = (ContextoEalia)MessageContext.getCurrentMessageContext().getProperty("CONTEXTO_EALIA");
			MensajeBean error = CtrlIncidencias.consultarMensaje("001", ctx);
			CecaException ce = new CecaException(error.getCodError(), e.getMessage(), error.getDesMensajeVisualizacion(), e, desdeDonde);
			CtrlIncidencias.generarLOG(ce, ctx);
			throw new CecaException("000", "", ""); // Con 000 paramos todo
		}
		return res;
	}

	public OperacionesCobrarResponse crearRespuestaErrorObjeto_operacionesCobrar(MensajeBean mensaje, ContextoEalia ctx) throws CecaException {
		String desdeDonde = "crearRespuestaErrorObjeto_operacionesCobrar - es.cecabank.ealiaapp.util.Error_ealf_002_001_01";
		TrazadorFichero.escribirTrazas(desdeDonde, "Entro", "", "", "", TrazadorFichero.NIVEL_DEBUG);
		OperacionesCobrarResponse response = null;

		try {
			response = new OperacionesCobrarResponse();
			Document doc = new Document();
			EalfStatusReportV01 ealfPaymentStsRpt = new EalfStatusReportV01();
			EalfGroupHeader1 grpHdr = new EalfGroupHeader1();
			EalfOriginalGroup1 orgnlGrpInfAndSts = new EalfOriginalGroup1();
			BICFIIdentifier bicfi = new BICFIIdentifier();

			bicfi.setBICFIIdentifier(Constantes.BIC_EALIA);
			grpHdr.setMsgId(Max350Text.Factory.fromString(ctx.getDatosMensajes().getMsgIdResp(), ""));
			grpHdr.setUsrAgtBIC(bicfi);
			grpHdr.setCreDtTm(ISODateTime.Factory.fromString(ctx.getDatosMensajes().getCreDtTmResp(), ""));
			grpHdr.setEalAppId(Max35Text.Factory.fromString(((DatosMensajesBean)ctx.getDatosMensajes()).getCodAplicacion(), ""));
			grpHdr.setInstId(Max350Text.Factory.fromString(ctx.getDatosMensajes().getInstId(), ""));

			orgnlGrpInfAndSts.setOrgnlMsgId(Max350Text.Factory.fromString(ctx.getDatosMensajes().getMsgId(), ""));
			orgnlGrpInfAndSts.setRpsStsCd(Max35Text.Factory.fromString(mensaje.getCodError(), ""));
			orgnlGrpInfAndSts.setRpsStsDes(Max350Text.Factory.fromString(mensaje.getDesError(), ""));
			if (mensaje.getCodError().equals("RJVL")){
				orgnlGrpInfAndSts.setRpsStsAddInf(Max350Text.Factory.fromString(mensaje.getDesMensajeVisualizacion(), ""));
			}

			ealfPaymentStsRpt.setGrpHdr(grpHdr);
			ealfPaymentStsRpt.setOrgnlGrpInfAndSts(orgnlGrpInfAndSts);

			doc.setStsRpt(ealfPaymentStsRpt);
			response.setOperacionesCobrarReturn(doc);
		} catch (Exception e) {
			MensajeBean error = CtrlIncidencias.consultarMensaje("001", ctx);
			CecaException ce = new CecaException(error.getCodError(), e.getMessage(), error.getDesMensajeVisualizacion(), e, desdeDonde);
			CtrlIncidencias.generarLOG(ce, ctx);
			throw new CecaException("000", "", ""); // Con 000 paramos todo
		}
		return response;
	}

	public OperacionesCobrarResponseFault crearRespuestaErrorFault_operacionesCobrar() throws CecaException {
		String desdeDonde = "crearRespuestaErrorFault_operacionesCobrar - es.cecabank.ealiaapp.util.Error_ealf_002_001_01";
		TrazadorFichero.escribirTrazas(desdeDonde, "Entro", "", "", "", TrazadorFichero.NIVEL_DEBUG);
		OperacionesCobrarResponseFault res = null;

		try {
			File f = new File(PropertiesEaliaApp.getPropiedad(PropertiesEaliaApp.PATH_XML_ERROR) + Constantes.WSF_XML_ERROR_OPERACIONES_COBRAR_EALF_002_001_01);
			InputStream is = new FileInputStream(f);

			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			XMLStreamReader reader = null;
			try {
				reader = inputFactory.createXMLStreamReader(is);
				res = OperacionesCobrarResponseFault.Factory.parse(reader);
			} finally {
				if (reader != null) {
					reader.close();
				}
			}
		} catch (Exception e) {
			ContextoEalia ctx = (ContextoEalia)MessageContext.getCurrentMessageContext().getProperty("CONTEXTO_EALIA");
			MensajeBean error = CtrlIncidencias.consultarMensaje("001", ctx);
			CecaException ce = new CecaException(error.getCodError(), e.getMessage(), error.getDesMensajeVisualizacion(), e, desdeDonde);
			CtrlIncidencias.generarLOG(ce, ctx);
			throw new CecaException("000", "", ""); // Con 000 paramos todo
		}
		return res;
	}

	
	/* ***********************************************
	 * **********  OPERACIONES ANULAR  ***************
	 ************************************************/
	public OperacionesAnularResponse crearRespuestaError_operacionesAnular() throws CecaException {
		String desdeDonde = "crearRespuestaError_operacionesAnular - es.cecabank.ealiaapp.util.Error_ealf_002_001_01";
		TrazadorFichero.escribirTrazas(desdeDonde, "Entro", "", "", "", TrazadorFichero.NIVEL_DEBUG);
		OperacionesAnularResponse res = null;

		try {
			File f = new File(PropertiesEaliaApp.getPropiedad(PropertiesEaliaApp.PATH_XML_ERROR) + Constantes.WSF_XML_ERROR_OPERACIONES_ANULAR_EALF_002_001_01);
			InputStream is = new FileInputStream(f);
			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			XMLStreamReader reader = null;
			try {
				reader = inputFactory.createXMLStreamReader(is);
				res = OperacionesAnularResponse.Factory.parse(reader);
				res.getOperacionesAnularReturn().getStsRpt().setGrpHdr(UtilApp.extraerGroupHeaderDeXML(MessageContext.getCurrentMessageContext().getEnvelope().toString()));
				String desError = (String)MessageContext.getCurrentMessageContext().getProperty("DES_MENSAJE_ERROR");
				if (desError != null && !desError.equals("")){
					res.getOperacionesAnularReturn().getStsRpt().getOrgnlGrpInfAndSts().setOrgnlMsgId(Max350Text.Factory.fromString(res.getOperacionesAnularReturn().getStsRpt().getGrpHdr().getMsgId().getMax350Text(), ""));
					res.getOperacionesAnularReturn().getStsRpt().getOrgnlGrpInfAndSts().setRpsStsCd(Max35Text.Factory.fromString("RJVL", ""));
					int indice = desError.indexOf(":");
					String texto = Constantes.EXCEPCION_PARSEO_ORDEN;
					if (indice == -1){
						indice = desError.indexOf(texto);
						if (indice != -1){
							desError = desError.replaceAll(texto, "No se han completado todos los datos o alguno de los datos es incorrecto");
							indice += ("No se han completado todos los datos o alguno de los datos es incorrecto").length();
						}
					}
					else{
						texto = Constantes.EXCEPCION_PARSEO_FORMATO;
						indice = desError.indexOf(texto);
						if (indice != -1){
							desError = desError.replaceAll(texto, "No se han completado todos los datos o alguno de los datos es incorrecto");
							indice += ("No se han completado todos los datos o alguno de los datos es incorrecto").length();
						}
					}
					if (indice != -1){
						res.getOperacionesAnularReturn().getStsRpt().getOrgnlGrpInfAndSts().setRpsStsDes(Max350Text.Factory.fromString(desError.substring(0, indice), "")); 
						res.getOperacionesAnularReturn().getStsRpt().getOrgnlGrpInfAndSts().setRpsStsAddInf(Max350Text.Factory.fromString(desError.substring(indice+1, desError.length()).trim(), ""));
					}
				}
				//res.getOperacionesAnularReturn().getStsRpt().setSplmtryData(UtilApp.extraerSupplementaryDataDeXML(MessageContext.getCurrentMessageContext().getEnvelope().toString()));
			} finally {
				if (reader != null) {
					reader.close();
				}
			}
		} catch (Exception e) {
			ContextoEalia ctx = (ContextoEalia)MessageContext.getCurrentMessageContext().getProperty("CONTEXTO_EALIA");
			MensajeBean error = CtrlIncidencias.consultarMensaje("001", ctx);
			CecaException ce = new CecaException(error.getCodError(), e.getMessage(), error.getDesMensajeVisualizacion(), e, desdeDonde);
			CtrlIncidencias.generarLOG(ce, ctx);
			throw new CecaException("000", "", ""); // Con 000 paramos todo
		}
		return res;
	}

	public OperacionesAnularResponseFault crearRespuestaErrorFault_operacionesAnular() throws CecaException {
		String desdeDonde = "crearRespuestaErrorFault_operacionesAnular - es.cecabank.ealiaapp.util.Error_ealf_002_001_01";
		TrazadorFichero.escribirTrazas(desdeDonde, "Entro", "", "", "", TrazadorFichero.NIVEL_DEBUG);
		OperacionesAnularResponseFault res = null;

		try {
			File f = new File(PropertiesEaliaApp.getPropiedad(PropertiesEaliaApp.PATH_XML_ERROR) + Constantes.WSF_XML_ERROR_OPERACIONES_ANULAR_EALF_002_001_01);
			InputStream is = new FileInputStream(f);

			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			XMLStreamReader reader = null;
			try {
				reader = inputFactory.createXMLStreamReader(is);
				res = OperacionesAnularResponseFault.Factory.parse(reader);
			} finally {
				if (reader != null) {
					reader.close();
				}
			}
		} catch (Exception e) {
			ContextoEalia ctx = (ContextoEalia)MessageContext.getCurrentMessageContext().getProperty("CONTEXTO_EALIA");
			MensajeBean error = CtrlIncidencias.consultarMensaje("001", ctx);
			CecaException ce = new CecaException(error.getCodError(), e.getMessage(), error.getDesMensajeVisualizacion(), e, desdeDonde);
			CtrlIncidencias.generarLOG(ce, ctx);
			throw new CecaException("000", "", ""); // Con 000 paramos todo
		}
		return res;
	}

	public OperacionesAnularResponse crearRespuestaErrorObjeto_operacionesAnular(MensajeBean mensaje, ContextoEalia ctx) throws CecaException {

		String desdeDonde = "crearRespuestaErrorObjeto_operacionesAnular - es.cecabank.ealiaapp.util.Error_ealf_002_001_01";
		TrazadorFichero.escribirTrazas(desdeDonde, "Entro", "", "", "", TrazadorFichero.NIVEL_DEBUG);
		OperacionesAnularResponse response = null;

		try {
			response = new OperacionesAnularResponse();
			Document doc = new Document();

			EalfStatusReportV01 ealfPaymentStsRpt = new EalfStatusReportV01();
			EalfGroupHeader1 grpHdr = new EalfGroupHeader1();
			EalfOriginalGroup1 orgnlGrpInfAndSts = new EalfOriginalGroup1();
			BICFIIdentifier bicfi = new BICFIIdentifier();

			bicfi.setBICFIIdentifier(Constantes.BIC_EALIA);
			grpHdr.setMsgId(Max350Text.Factory.fromString(ctx.getDatosMensajes().getMsgIdResp(), ""));
			grpHdr.setUsrAgtBIC(bicfi);
			grpHdr.setCreDtTm(ISODateTime.Factory.fromString(ctx.getDatosMensajes().getCreDtTmResp(), ""));
			grpHdr.setEalAppId(Max35Text.Factory.fromString(((DatosMensajesBean)ctx.getDatosMensajes()).getCodAplicacion(), ""));
			grpHdr.setInstId(Max350Text.Factory.fromString(ctx.getDatosMensajes().getInstId(), ""));

			orgnlGrpInfAndSts.setOrgnlMsgId(Max350Text.Factory.fromString(ctx.getDatosMensajes().getMsgId(), ""));
			orgnlGrpInfAndSts.setRpsStsCd(Max35Text.Factory.fromString(mensaje.getCodError(), ""));
			orgnlGrpInfAndSts.setRpsStsDes(Max350Text.Factory.fromString(mensaje.getDesError(), ""));
			if (mensaje.getCodError().equals("RJVL")){
				orgnlGrpInfAndSts.setRpsStsAddInf(Max350Text.Factory.fromString(mensaje.getDesMensajeVisualizacion(), ""));
			}

			ealfPaymentStsRpt.setGrpHdr(grpHdr);
			ealfPaymentStsRpt.setOrgnlGrpInfAndSts(orgnlGrpInfAndSts);

			doc.setStsRpt(ealfPaymentStsRpt);
			response.setOperacionesAnularReturn(doc);

		} catch (Exception e) {
			MensajeBean error = CtrlIncidencias.consultarMensaje("001", ctx);
			CecaException ce = new CecaException(error.getCodError(), e.getMessage(), error.getDesMensajeVisualizacion(), e, desdeDonde);
			CtrlIncidencias.generarLOG(ce, ctx);
			throw new CecaException("000", "", ""); // Con 000 paramos todo
		}

		return response;
	}

	
	
	/* ***********************************************
	 * **********  DISPOSITIVOS ACTUALIZAR  **********
	 ************************************************/
	public DispositivosActualizarResponse crearRespuestaError_dispositivosActualizar() throws CecaException {
		String desdeDonde = "crearRespuestaError_dispositivosActualizar - es.cecabank.ealiaapp.util.Error_ealf_002_001_01";
		TrazadorFichero.escribirTrazas(desdeDonde, "Entro", "", "", "", TrazadorFichero.NIVEL_DEBUG);
		DispositivosActualizarResponse res = null;

		try {
			File f = new File(PropertiesEaliaApp.getPropiedad(PropertiesEaliaApp.PATH_XML_ERROR) + Constantes.WSF_XML_ERROR_DISPOSITIVOS_ACTUALIZAR_EALF_002_001_01);
			InputStream is = new FileInputStream(f);
			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			XMLStreamReader reader = null;
			try {
				reader = inputFactory.createXMLStreamReader(is);
				res = DispositivosActualizarResponse.Factory.parse(reader);
				res.getDispositivosActualizarReturn().getStsRpt().setGrpHdr(UtilApp.extraerGroupHeaderDeXML(MessageContext.getCurrentMessageContext().getEnvelope().toString()));
				String desError = (String)MessageContext.getCurrentMessageContext().getProperty("DES_MENSAJE_ERROR");
				if (desError != null && !desError.equals("")){
					res.getDispositivosActualizarReturn().getStsRpt().getOrgnlGrpInfAndSts().setOrgnlMsgId(Max350Text.Factory.fromString(res.getDispositivosActualizarReturn().getStsRpt().getGrpHdr().getMsgId().getMax350Text(), ""));
					res.getDispositivosActualizarReturn().getStsRpt().getOrgnlGrpInfAndSts().setRpsStsCd(Max35Text.Factory.fromString("RJVL", ""));
					int indice = desError.indexOf(":");
					String texto = Constantes.EXCEPCION_PARSEO_ORDEN;
					if (indice == -1){
						indice = desError.indexOf(texto);
						if (indice != -1){
							desError = desError.replaceAll(texto, "No se han completado todos los datos o alguno de los datos es incorrecto");
							indice += ("No se han completado todos los datos o alguno de los datos es incorrecto").length();
						}
					}
					else{
						texto = Constantes.EXCEPCION_PARSEO_FORMATO;
						indice = desError.indexOf(texto);
						if (indice != -1){
							desError = desError.replaceAll(texto, "No se han completado todos los datos o alguno de los datos es incorrecto");
							indice += ("No se han completado todos los datos o alguno de los datos es incorrecto").length();
						}
					}
					if (indice != -1){
						res.getDispositivosActualizarReturn().getStsRpt().getOrgnlGrpInfAndSts().setRpsStsDes(Max350Text.Factory.fromString(desError.substring(0, indice), "")); 
						res.getDispositivosActualizarReturn().getStsRpt().getOrgnlGrpInfAndSts().setRpsStsAddInf(Max350Text.Factory.fromString(desError.substring(indice+1, desError.length()).trim(), ""));
					}
				}
				//res.getDispositivosActualizarReturn().getStsRpt().setSplmtryData(UtilApp.extraerSupplementaryDataDeXML(MessageContext.getCurrentMessageContext().getEnvelope().toString()));
			} finally {
				if (reader != null) {
					reader.close();
				}
			}
		} catch (Exception e) {
			ContextoEalia ctx = (ContextoEalia)MessageContext.getCurrentMessageContext().getProperty("CONTEXTO_EALIA");
			MensajeBean error = CtrlIncidencias.consultarMensaje("001", ctx);
			CecaException ce = new CecaException(error.getCodError(), e.getMessage(), error.getDesMensajeVisualizacion(), e, desdeDonde);
			CtrlIncidencias.generarLOG(ce, ctx);
			throw new CecaException("000", "", ""); // Con 000 paramos todo
		}
		return res;
	}

	public DispositivosActualizarResponseFault crearRespuestaErrorFault_dispositivosActualizar() throws CecaException {
		String desdeDonde = "crearRespuestaErrorFault_dispositivosActualizar - es.cecabank.ealiaapp.util.Error_ealf_002_001_01";
		TrazadorFichero.escribirTrazas(desdeDonde, "Entro", "", "", "", TrazadorFichero.NIVEL_DEBUG);
		DispositivosActualizarResponseFault res = null;

		try {
			File f = new File(PropertiesEaliaApp.getPropiedad(PropertiesEaliaApp.PATH_XML_ERROR) + Constantes.WSF_XML_ERROR_DISPOSITIVOS_ACTUALIZAR_EALF_002_001_01);
			InputStream is = new FileInputStream(f);

			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			XMLStreamReader reader = null;
			try {
				reader = inputFactory.createXMLStreamReader(is);
				res = DispositivosActualizarResponseFault.Factory.parse(reader);
			} finally {
				if (reader != null) {
					reader.close();
				}
			}
		} catch (Exception e) {
			ContextoEalia ctx = (ContextoEalia)MessageContext.getCurrentMessageContext().getProperty("CONTEXTO_EALIA");
			MensajeBean error = CtrlIncidencias.consultarMensaje("001", ctx);
			CecaException ce = new CecaException(error.getCodError(), e.getMessage(), error.getDesMensajeVisualizacion(), e, desdeDonde);
			CtrlIncidencias.generarLOG(ce, ctx);
			throw new CecaException("000", "", ""); // Con 000 paramos todo
		}
		return res;
	}

	public DispositivosActualizarResponse crearRespuestaErrorObjeto_dispositivosActualizar(MensajeBean mensaje, ContextoEalia ctx) throws CecaException {
		String desdeDonde = "crearRespuestaErrorObjeto_dispositivosActualizar - es.cecabank.ealiaapp.util.Error_ealf_002_001_01";
		TrazadorFichero.escribirTrazas(desdeDonde, "Entro", "", "", "", TrazadorFichero.NIVEL_DEBUG);
		DispositivosActualizarResponse response = null;

		try {
			response = new DispositivosActualizarResponse();
			Document doc = new Document();
			EalfStatusReportV01 ealfPaymentStsRpt = new EalfStatusReportV01();
			EalfGroupHeader1 grpHdr = new EalfGroupHeader1();
			EalfOriginalGroup1 orgnlGrpInfAndSts = new EalfOriginalGroup1();
			BICFIIdentifier bicfi = new BICFIIdentifier();

			bicfi.setBICFIIdentifier(Constantes.BIC_EALIA);
			grpHdr.setMsgId(Max350Text.Factory.fromString(ctx.getDatosMensajes().getMsgIdResp(), ""));
			grpHdr.setUsrAgtBIC(bicfi);
			grpHdr.setCreDtTm(ISODateTime.Factory.fromString(ctx.getDatosMensajes().getCreDtTmResp(), ""));
			grpHdr.setEalAppId(Max35Text.Factory.fromString(((DatosMensajesBean)ctx.getDatosMensajes()).getCodAplicacion(), ""));
			grpHdr.setInstId(Max350Text.Factory.fromString(ctx.getDatosMensajes().getInstId(), ""));
			
			orgnlGrpInfAndSts.setOrgnlMsgId(Max350Text.Factory.fromString(ctx.getDatosMensajes().getMsgId(), ""));
			orgnlGrpInfAndSts.setRpsStsCd(Max35Text.Factory.fromString(mensaje.getCodError(), ""));
			orgnlGrpInfAndSts.setRpsStsDes(Max350Text.Factory.fromString(mensaje.getDesError(), ""));
			if (mensaje.getCodError().equals("RJVL")){
				orgnlGrpInfAndSts.setRpsStsAddInf(Max350Text.Factory.fromString(mensaje.getDesMensajeVisualizacion(), ""));
			}
			
			ealfPaymentStsRpt.setGrpHdr(grpHdr);
			ealfPaymentStsRpt.setOrgnlGrpInfAndSts(orgnlGrpInfAndSts);

			doc.setStsRpt(ealfPaymentStsRpt);
			response.setDispositivosActualizarReturn(doc);
		} catch (Exception e) {
			MensajeBean error = CtrlIncidencias.consultarMensaje("001", ctx);
			CecaException ce = new CecaException(error.getCodError(), e.getMessage(), error.getDesMensajeVisualizacion(), e, desdeDonde);
			CtrlIncidencias.generarLOG(ce, ctx);
			throw new CecaException("000", "", ""); // Con 000 paramos todo
		}
		return response;
	}

	
	/* **********************************************
	 * **********  AGENDA ACTUALIZAR  ***************
	 ************************************************/
	public AgendaActualizarResponse crearRespuestaError_agendaActualizar() throws CecaException {
		String desdeDonde = "crearRespuestaError_agendaActualizar - es.cecabank.ealiaapp.util.Error_ealf_002_001_01";
		TrazadorFichero.escribirTrazas(desdeDonde, "Entro", "", "", "", TrazadorFichero.NIVEL_DEBUG);
		AgendaActualizarResponse res = null;

		try {
			File f = new File(PropertiesEaliaApp.getPropiedad(PropertiesEaliaApp.PATH_XML_ERROR) + Constantes.WSF_XML_ERROR_AGENDA_ACTUALIZAR_EALF_002_001_01);
			InputStream is = new FileInputStream(f);
			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			XMLStreamReader reader = null;
			try {
				reader = inputFactory.createXMLStreamReader(is);
				res = AgendaActualizarResponse.Factory.parse(reader);
				res.getAgendaActualizarReturn().getStsRpt().setGrpHdr(UtilApp.extraerGroupHeaderDeXML(MessageContext.getCurrentMessageContext().getEnvelope().toString()));
				String desError = (String)MessageContext.getCurrentMessageContext().getProperty("DES_MENSAJE_ERROR");
				if (desError != null && !desError.equals("")){
					res.getAgendaActualizarReturn().getStsRpt().getOrgnlGrpInfAndSts().setOrgnlMsgId(Max350Text.Factory.fromString(res.getAgendaActualizarReturn().getStsRpt().getGrpHdr().getMsgId().getMax350Text(), ""));
					res.getAgendaActualizarReturn().getStsRpt().getOrgnlGrpInfAndSts().setRpsStsCd(Max35Text.Factory.fromString("RJVL", ""));
					int indice = desError.indexOf(":");
					String texto = Constantes.EXCEPCION_PARSEO_ORDEN;
					if (indice == -1){
						indice = desError.indexOf(texto);
						if (indice != -1){
							desError = desError.replaceAll(texto, "No se han completado todos los datos o alguno de los datos es incorrecto");
							indice += ("No se han completado todos los datos o alguno de los datos es incorrecto").length();
						}
					}
					else{
						texto = Constantes.EXCEPCION_PARSEO_FORMATO;
						indice = desError.indexOf(texto);
						if (indice != -1){
							desError = desError.replaceAll(texto, "No se han completado todos los datos o alguno de los datos es incorrecto");
							indice += ("No se han completado todos los datos o alguno de los datos es incorrecto").length();
						}
					}
					if (indice != -1){
						res.getAgendaActualizarReturn().getStsRpt().getOrgnlGrpInfAndSts().setRpsStsDes(Max350Text.Factory.fromString(desError.substring(0, indice), "")); 
						res.getAgendaActualizarReturn().getStsRpt().getOrgnlGrpInfAndSts().setRpsStsAddInf(Max350Text.Factory.fromString(desError.substring(indice+1, desError.length()).trim(), ""));
					}
				}
			} finally {
				if (reader != null) {
					reader.close();
				}
			}
		} catch (Exception e) {
			throw new CecaException("000", "", ""); // Con 000 paramos todo
		}
		return res;
	}

	public AgendaActualizarResponseFault crearRespuestaErrorFault_agendaActualizar() throws CecaException {
		String desdeDonde = "crearRespuestaErrorFault_agendaActualizar - es.cecabank.ealiaapp.util.Error_ealf_002_001_01";
		TrazadorFichero.escribirTrazas(desdeDonde, "Entro", "", "", "", TrazadorFichero.NIVEL_DEBUG);
		AgendaActualizarResponseFault res = null;

		try {
			File f = new File(PropertiesEaliaApp.getPropiedad(PropertiesEaliaApp.PATH_XML_ERROR) + Constantes.WSF_XML_ERROR_AGENDA_ACTUALIZAR_EALF_002_001_01);
			InputStream is = new FileInputStream(f);

			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			XMLStreamReader reader = null;
			try {
				reader = inputFactory.createXMLStreamReader(is);
				res = AgendaActualizarResponseFault.Factory.parse(reader);
			} finally {
				if (reader != null) {
					reader.close();
				}
			}
		} catch (Exception e) {
			throw new CecaException("000", "", ""); // Con 000 paramos todo
		}
		return res;
	}
	
	public AgendaActualizarResponse crearRespuestaErrorObjeto_agendaActualizar(MensajeBean mensaje, ContextoEalia ctx) throws CecaException {
		String desdeDonde = "crearRespuestaErrorObjeto_dispositivosActualizar - es.cecabank.ealiaapp.util.Error_ealf_002_001_01";
		TrazadorFichero.escribirTrazas(desdeDonde, "Entro", "", "", "", TrazadorFichero.NIVEL_DEBUG);
		AgendaActualizarResponse response = null;

		try {
			response = new AgendaActualizarResponse();
			Document doc = new Document();
			EalfStatusReportV01 ealfPaymentStsRpt = new EalfStatusReportV01();
			EalfGroupHeader1 grpHdr = new EalfGroupHeader1();
			EalfOriginalGroup1 orgnlGrpInfAndSts = new EalfOriginalGroup1();
			BICFIIdentifier bicfi = new BICFIIdentifier();

			bicfi.setBICFIIdentifier(Constantes.BIC_EALIA);
			grpHdr.setMsgId(Max350Text.Factory.fromString(ctx.getDatosMensajes().getMsgIdResp(), ""));
			grpHdr.setUsrAgtBIC(bicfi);
			grpHdr.setCreDtTm(ISODateTime.Factory.fromString(ctx.getDatosMensajes().getCreDtTmResp(), ""));
			grpHdr.setEalAppId(Max35Text.Factory.fromString(((DatosMensajesBean)ctx.getDatosMensajes()).getCodAplicacion(), ""));
			grpHdr.setInstId(Max350Text.Factory.fromString(ctx.getDatosMensajes().getInstId(), ""));
			
			orgnlGrpInfAndSts.setOrgnlMsgId(Max350Text.Factory.fromString(ctx.getDatosMensajes().getMsgId(), ""));
			orgnlGrpInfAndSts.setRpsStsCd(Max35Text.Factory.fromString(mensaje.getCodError(), ""));
			orgnlGrpInfAndSts.setRpsStsDes(Max350Text.Factory.fromString(mensaje.getDesError(), ""));
			if (mensaje.getCodError().equals("RJVL")){
				orgnlGrpInfAndSts.setRpsStsAddInf(Max350Text.Factory.fromString(mensaje.getDesMensajeVisualizacion(), ""));
			}
			
			ealfPaymentStsRpt.setGrpHdr(grpHdr);
			ealfPaymentStsRpt.setOrgnlGrpInfAndSts(orgnlGrpInfAndSts);

			doc.setStsRpt(ealfPaymentStsRpt);
			response.setAgendaActualizarReturn(doc);
		} catch (Exception e) {
			MensajeBean error = CtrlIncidencias.consultarMensaje("001", ctx);
			CecaException ce = new CecaException(error.getCodError(), e.getMessage(), error.getDesMensajeVisualizacion(), e, desdeDonde);
			CtrlIncidencias.generarLOG(ce, ctx);
			throw new CecaException("000", "", ""); // Con 000 paramos todo
		}
		return response;
	}
	
	/* **********************************************
	 * **********  FICHEROS CARGAR  *****************
	 ************************************************/
	public FicherosCargarResponse crearRespuestaError_ficherosCargar() throws CecaException{
		String desdeDonde = "crearRespuestaError_ficherosCargar - es.cecabank.ealiaapp.util.Error_ealf_002_001_01";
		TrazadorFichero.escribirTrazas(desdeDonde, "Entro", "", "", "", TrazadorFichero.NIVEL_DEBUG);
		FicherosCargarResponse res = null;

		try{
			File f = new File(PropertiesEaliaApp.getPropiedad(PropertiesEaliaApp.PATH_XML_ERROR) + Constantes.WSF_XML_ERROR_FICHEROS_CARGAR_EALF_002_001_01);
			InputStream is = new FileInputStream(f);
			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			XMLStreamReader reader = null;
            try {
            	reader = inputFactory.createXMLStreamReader(is);
            	res = FicherosCargarResponse.Factory.parse(reader);
            	res.getFicherosCargarReturn().getStsRpt().setGrpHdr(UtilApp.extraerGroupHeaderDeXML(MessageContext.getCurrentMessageContext().getEnvelope().toString()));
                String desError = (String)MessageContext.getCurrentMessageContext().getProperty("DES_MENSAJE_ERROR");
				if (desError != null && !desError.equals("")){
					res.getFicherosCargarReturn().getStsRpt().getOrgnlGrpInfAndSts().setOrgnlMsgId(Max350Text.Factory.fromString(res.getFicherosCargarReturn().getStsRpt().getGrpHdr().getMsgId().getMax350Text(), ""));
					res.getFicherosCargarReturn().getStsRpt().getOrgnlGrpInfAndSts().setRpsStsCd(Max35Text.Factory.fromString("RJVL", ""));
					int indice = desError.indexOf(":");
					String texto = Constantes.EXCEPCION_PARSEO_ORDEN;
					if (indice == -1){
						indice = desError.indexOf(texto);
						if (indice != -1){
							desError = desError.replaceAll(texto, "No se han completado todos los datos o alguno de los datos es incorrecto");
							indice += ("No se han completado todos los datos o alguno de los datos es incorrecto").length();
						}
					}
					else{
						texto = Constantes.EXCEPCION_PARSEO_FORMATO;
						indice = desError.indexOf(texto);
						if (indice != -1){
							desError = desError.replaceAll(texto, "No se han completado todos los datos o alguno de los datos es incorrecto");
							indice += ("No se han completado todos los datos o alguno de los datos es incorrecto").length();
						}
					}
					if (indice != -1){
						res.getFicherosCargarReturn().getStsRpt().getOrgnlGrpInfAndSts().setRpsStsDes(Max350Text.Factory.fromString(desError.substring(0, indice), "")); 
						res.getFicherosCargarReturn().getStsRpt().getOrgnlGrpInfAndSts().setRpsStsAddInf(Max350Text.Factory.fromString(desError.substring(indice+1, desError.length()).trim(), ""));
					}
				}
            } finally {
                if (reader != null) {
                    reader.close();
                }
            }
        } catch(Exception e){
        	ContextoEalia ctx = (ContextoEalia)MessageContext.getCurrentMessageContext().getProperty("CONTEXTO_EALIA");
        	MensajeBean error = CtrlIncidencias.consultarMensaje("001", ctx);
        	CecaException ce = new CecaException(error.getCodError(), e.getMessage(), error.getDesMensajeVisualizacion(), e, desdeDonde);
        	CtrlIncidencias.generarLOG(ce, ctx);
        	throw new CecaException("000", "", ""); // Con 000 paramos todo
    	}
		return res;
    }

	public FicherosCargarResponseFault crearRespuestaErrorFault_ficherosCargar() throws CecaException{
    	String desdeDonde = "crearRespuestaErrorFault_ficherosCargar - es.cecabank.ealiaapp.util.Error_ealf_002_001_01";
    	TrazadorFichero.escribirTrazas(desdeDonde, "Entro", "", "", "", TrazadorFichero.NIVEL_DEBUG);
    	FicherosCargarResponseFault res = null;
    	try{
    		File f = new File(PropertiesEaliaApp.getPropiedad(PropertiesEaliaApp.PATH_XML_ERROR) + Constantes.WSF_XML_ERROR_DISPOSITIVOS_ACTUALIZAR_EALF_002_001_01);
			InputStream is = new FileInputStream(f);

			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			XMLStreamReader reader = null;
			try {
				reader = inputFactory.createXMLStreamReader(is);
				res = FicherosCargarResponseFault.Factory.parse(reader);
			} finally {
				if (reader != null) {
					reader.close();
				}
			}
        } catch(Exception e){
        	ContextoEalia ctx = (ContextoEalia)MessageContext.getCurrentMessageContext().getProperty("CONTEXTO_EALIA");
        	MensajeBean error = CtrlIncidencias.consultarMensaje("001", ctx);
        	CecaException ce = new CecaException(error.getCodError(), e.getMessage(), error.getDesMensajeVisualizacion(), e, desdeDonde);
        	CtrlIncidencias.generarLOG(ce, ctx);
        	throw new CecaException("000", "", ""); // Con 000 paramos todo
    	}
		return res;
    }
	
    public FicherosCargarResponse crearRespuestaErrorObjeto_ficherosCargar(MensajeBean mensaje, ContextoEalia ctx) throws CecaException{
    	String desdeDonde = "crearRespuestaErrorObjeto_ficherosCargar - es.cecabank.ealiaapp.util.Error_ealf_002_001_01";
    	TrazadorFichero.escribirTrazas(desdeDonde, "Entro", "", "", "", TrazadorFichero.NIVEL_DEBUG);
    	FicherosCargarResponse response = null;
    	
    	try {
			response = new FicherosCargarResponse();
			Document doc = new Document();
			EalfStatusReportV01 ealfPaymentStsRpt = new EalfStatusReportV01();
			EalfGroupHeader1 grpHdr = new EalfGroupHeader1();
			EalfOriginalGroup1 orgnlGrpInfAndSts = new EalfOriginalGroup1();
			BICFIIdentifier bicfi = new BICFIIdentifier();

			bicfi.setBICFIIdentifier(Constantes.BIC_EALIA);
			grpHdr.setMsgId(Max350Text.Factory.fromString(ctx.getDatosMensajes().getMsgIdResp(), ""));
			grpHdr.setUsrAgtBIC(bicfi);
			grpHdr.setCreDtTm(ISODateTime.Factory.fromString(ctx.getDatosMensajes().getCreDtTmResp(), ""));
			grpHdr.setEalAppId(Max35Text.Factory.fromString(((DatosMensajesBean)ctx.getDatosMensajes()).getCodAplicacion(), ""));
			grpHdr.setInstId(Max350Text.Factory.fromString(ctx.getDatosMensajes().getInstId(), ""));

			orgnlGrpInfAndSts.setOrgnlMsgId(Max350Text.Factory.fromString(ctx.getDatosMensajes().getMsgId(), ""));
			orgnlGrpInfAndSts.setOrgnlMsgId(Max350Text.Factory.fromString(ctx.getDatosMensajes().getMsgId(), ""));
			orgnlGrpInfAndSts.setRpsStsCd(Max35Text.Factory.fromString(mensaje.getCodError(), ""));
			orgnlGrpInfAndSts.setRpsStsDes(Max350Text.Factory.fromString(mensaje.getDesError(), ""));
			if (mensaje.getCodError().equals("RJVL")){
				orgnlGrpInfAndSts.setRpsStsAddInf(Max350Text.Factory.fromString(mensaje.getDesMensajeVisualizacion(), ""));
			}
			
			ealfPaymentStsRpt.setGrpHdr(grpHdr);
			ealfPaymentStsRpt.setOrgnlGrpInfAndSts(orgnlGrpInfAndSts);

			doc.setStsRpt(ealfPaymentStsRpt);
			response.setFicherosCargarReturn(doc);
		} catch (Exception e) {
			MensajeBean error = CtrlIncidencias.consultarMensaje("001", ctx);
			CecaException ce = new CecaException(error.getCodError(), e.getMessage(), error.getDesMensajeVisualizacion(), e, desdeDonde);
			CtrlIncidencias.generarLOG(ce, ctx);
			throw new CecaException("000", "", ""); // Con 000 paramos todo
		}
		return response;
    }
    
    
    /* **********************************************
	 * **********  USUARIOS FINALIZAR SESION  *******
	 ************************************************/
    public UsuariosFinalizarSesionResponse crearRespuestaError_usuariosFinalizarSesion() throws CecaException{
    	String desdeDonde = "crearRespuestaError_usuariosFinalizarSesion - es.cecabank.ealiaapp.util.Error_ealf_002_001_01";
    	TrazadorFichero.escribirTrazas(desdeDonde, "Entro", "", "", "", TrazadorFichero.NIVEL_DEBUG);
    	UsuariosFinalizarSesionResponse res = null;
    	try{
    		File file = new File(PropertiesEaliaApp.getPropiedad(PropertiesEaliaApp.PATH_XML_ERROR) + Constantes.WSF_XML_ERROR_USUARIOS_FINALIZARSESION_EALF_002_001_01);
        	InputStream is = new FileInputStream(file);
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            XMLStreamReader reader = null;
            try {
                reader = inputFactory.createXMLStreamReader(is);
				res = UsuariosFinalizarSesionResponse.Factory.parse(reader);
				res.getusuariosFinalizarSesionReturn().getStsRpt().setGrpHdr(UtilApp.extraerGroupHeaderDeXML(MessageContext.getCurrentMessageContext().getEnvelope().toString()));
				String desError = (String)MessageContext.getCurrentMessageContext().getProperty("DES_MENSAJE_ERROR");
				if (desError != null && !desError.equals("")){
					res.getusuariosFinalizarSesionReturn().getStsRpt().getOrgnlGrpInfAndSts().setOrgnlMsgId(Max350Text.Factory.fromString(res.getusuariosFinalizarSesionReturn().getStsRpt().getGrpHdr().getMsgId().getMax350Text(), ""));
					res.getusuariosFinalizarSesionReturn().getStsRpt().getOrgnlGrpInfAndSts().setRpsStsCd(Max35Text.Factory.fromString("RJVL", ""));
					int indice = desError.indexOf(":");
					String texto = Constantes.EXCEPCION_PARSEO_ORDEN;
					if (indice == -1){
						indice = desError.indexOf(texto);
						if (indice != -1){
							desError = desError.replaceAll(texto, "No se han completado todos los datos o alguno de los datos es incorrecto");
							indice += ("No se han completado todos los datos o alguno de los datos es incorrecto").length();
						}
					}
					else{
						texto = Constantes.EXCEPCION_PARSEO_FORMATO;
						indice = desError.indexOf(texto);
						if (indice != -1){
							desError = desError.replaceAll(texto, "No se han completado todos los datos o alguno de los datos es incorrecto");
							indice += ("No se han completado todos los datos o alguno de los datos es incorrecto").length();
						}
					}
					if (indice != -1){
						res.getusuariosFinalizarSesionReturn().getStsRpt().getOrgnlGrpInfAndSts().setRpsStsDes(Max350Text.Factory.fromString(desError.substring(0, indice), "")); 
						res.getusuariosFinalizarSesionReturn().getStsRpt().getOrgnlGrpInfAndSts().setRpsStsAddInf(Max350Text.Factory.fromString(desError.substring(indice+1, desError.length()).trim(), ""));
					}
				}
            } finally {
                if (reader != null) {
                    reader.close();
                }
            }
        } catch(Exception e){
        	ContextoEalia ctx = (ContextoEalia) MessageContext.getCurrentMessageContext().getProperty("CONTEXTO_EALIA");
        	MensajeBean error = CtrlIncidencias.consultarMensaje("001", ctx);
        	CecaException ce = new CecaException(error.getCodError(), e.getMessage(), error.getDesMensajeVisualizacion(), e, desdeDonde);
        	CtrlIncidencias.generarLOG(ce, ctx);
        	throw new CecaException("000", "", ""); // Con 000 paramos todo
    	}
		return res;
    }

    public UsuariosFinalizarSesionResponse crearRespuestaErrorObjeto_usuariosFinalizarSesion(MensajeBean mensaje, ContextoEalia ctx) throws CecaException{
    	String desdeDonde = "crearRespuestaErrorObjeto_usuariosFinalizarSesion - es.cecabank.ealiaapp.util.Error_ealf_002_001_01";
    	TrazadorFichero.escribirTrazas(desdeDonde, "Entro", "", "", "", TrazadorFichero.NIVEL_DEBUG);
    	UsuariosFinalizarSesionResponse response = null;
    	try{
    		response = new UsuariosFinalizarSesionResponse();
			Document doc = new Document();
			EalfStatusReportV01 ealfPaymentStsRpt = new EalfStatusReportV01();
			EalfGroupHeader1 grpHdr = new EalfGroupHeader1();
			EalfOriginalGroup1 orgnlGrpInfAndSts = new EalfOriginalGroup1();
			BICFIIdentifier bicfi = new BICFIIdentifier();

			bicfi.setBICFIIdentifier(Constantes.BIC_EALIA);
			grpHdr.setMsgId(Max350Text.Factory.fromString(ctx.getDatosMensajes().getMsgIdResp(), ""));
			grpHdr.setUsrAgtBIC(bicfi);
			grpHdr.setCreDtTm(ISODateTime.Factory.fromString(ctx.getDatosMensajes().getCreDtTmResp(), ""));
			grpHdr.setEalAppId(Max35Text.Factory.fromString(((DatosMensajesBean)ctx.getDatosMensajes()).getCodAplicacion(), ""));
			grpHdr.setInstId(Max350Text.Factory.fromString(ctx.getDatosMensajes().getInstId(), ""));

			orgnlGrpInfAndSts.setOrgnlMsgId(Max350Text.Factory.fromString(ctx.getDatosMensajes().getMsgId(), ""));
			orgnlGrpInfAndSts.setOrgnlMsgId(Max350Text.Factory.fromString(ctx.getDatosMensajes().getMsgId(), ""));
			orgnlGrpInfAndSts.setRpsStsCd(Max35Text.Factory.fromString(mensaje.getCodError(), ""));
			orgnlGrpInfAndSts.setRpsStsDes(Max350Text.Factory.fromString(mensaje.getDesError(), ""));
			if (mensaje.getCodError().equals("RJVL")){
				orgnlGrpInfAndSts.setRpsStsAddInf(Max350Text.Factory.fromString(mensaje.getDesMensajeVisualizacion(), ""));
			}

			ealfPaymentStsRpt.setGrpHdr(grpHdr);
			ealfPaymentStsRpt.setOrgnlGrpInfAndSts(orgnlGrpInfAndSts);

			doc.setStsRpt(ealfPaymentStsRpt);
			response.setUsuariosFinalizarSesionReturn(doc);
        } catch(Exception e){
        	MensajeBean error = CtrlIncidencias.consultarMensaje("001", ctx);
        	CecaException ce = new CecaException(error.getCodError(), e.getMessage(), error.getDesMensajeVisualizacion(), e, desdeDonde);
        	CtrlIncidencias.generarLOG(ce, ctx);
        	throw new CecaException("000", "", ""); // Con 000 paramos todo
    	}
		return response;
    }

    public UsuariosFinalizarSesionResponseFault crearRespuestaErrorFault_usuariosFinalizarSesion() throws CecaException{
    	String desdeDonde = "crearRespuestaErrorFault_usuariosFinalizarSesion - es.cecabank.ealiaapp.util.Error_ealf_002_001_01";
    	TrazadorFichero.escribirTrazas(desdeDonde, "Entro", "", "", "", TrazadorFichero.NIVEL_DEBUG);
    	UsuariosFinalizarSesionResponseFault response = null;
    	try{
    		File file = new File(PropertiesEaliaApp.getPropiedad(PropertiesEaliaApp.PATH_XML_ERROR) + Constantes.WSF_XML_ERROR_USUARIOS_FINALIZARSESION_EALF_002_001_01);
			InputStream inputStream = new FileInputStream(file);
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            XMLStreamReader reader = null;
            try {
                reader = inputFactory.createXMLStreamReader(inputStream);
                response = UsuariosFinalizarSesionResponseFault.Factory.parse(reader);
            } finally {
                if (reader != null) {
                    reader.close();
                }
            }
        } catch(Exception e){
        	ContextoEalia ctx = (ContextoEalia)MessageContext.getCurrentMessageContext().getProperty("CONTEXTO_EALIA");
        	MensajeBean error = CtrlIncidencias.consultarMensaje("001", ctx);
        	CecaException ce = new CecaException(error.getCodError(), e.getMessage(), error.getDesMensajeVisualizacion(), e, desdeDonde);
        	CtrlIncidencias.generarLOG(ce, ctx);
        	throw new CecaException("000", "", ""); // Con 000 paramos todo
    	}
		return response;
    }
    

    
    
    /* **********************************************
	 * **********  USUARIOS GESTIONAR  **************
	 ************************************************/
    public UsuariosGestionarResponse crearRespuestaError_usuariosGestionar() throws CecaException{
    	String desdeDonde = "crearRespuestaError_usuariosGestionar - es.cecabank.ealiaapp.util.Error_ealf_002_001_01";
    	TrazadorFichero.escribirTrazas(desdeDonde, "Entro", "", "", "", TrazadorFichero.NIVEL_DEBUG);
    	UsuariosGestionarResponse res = null;
    	try{
    		File file = new File(PropertiesEaliaApp.getPropiedad(PropertiesEaliaApp.PATH_XML_ERROR) + Constantes.WSF_XML_ERROR_USUARIOS_GESTIONAR_EALF_002_001_01);
        	InputStream is = new FileInputStream(file);
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            XMLStreamReader reader = null;
            try {
            	reader = inputFactory.createXMLStreamReader(is);
				res = UsuariosGestionarResponse.Factory.parse(reader);
				res.getUsuariosGestionarReturn().getStsRpt().setGrpHdr(UtilApp.extraerGroupHeaderDeXML(MessageContext.getCurrentMessageContext().getEnvelope().toString()));
				String desError = (String)MessageContext.getCurrentMessageContext().getProperty("DES_MENSAJE_ERROR");
				if (desError != null && !desError.equals("")){
					res.getUsuariosGestionarReturn().getStsRpt().getOrgnlGrpInfAndSts().setOrgnlMsgId(Max350Text.Factory.fromString(res.getUsuariosGestionarReturn().getStsRpt().getGrpHdr().getMsgId().getMax350Text(), ""));
					res.getUsuariosGestionarReturn().getStsRpt().getOrgnlGrpInfAndSts().setRpsStsCd(Max35Text.Factory.fromString("RJVL", ""));
					int indice = desError.indexOf(":");
					String texto = Constantes.EXCEPCION_PARSEO_ORDEN;
					if (indice == -1){
						indice = desError.indexOf(texto);
						if (indice != -1){
							desError = desError.replaceAll(texto, "No se han completado todos los datos o alguno de los datos es incorrecto");
							indice += ("No se han completado todos los datos o alguno de los datos es incorrecto").length();
						}
					}
					else{
						texto = Constantes.EXCEPCION_PARSEO_FORMATO;
						indice = desError.indexOf(texto);
						if (indice != -1){
							desError = desError.replaceAll(texto, "No se han completado todos los datos o alguno de los datos es incorrecto");
							indice += ("No se han completado todos los datos o alguno de los datos es incorrecto").length();
						}
					}
					if (indice != -1){
						res.getUsuariosGestionarReturn().getStsRpt().getOrgnlGrpInfAndSts().setRpsStsDes(Max350Text.Factory.fromString(desError.substring(0, indice), "")); 
						res.getUsuariosGestionarReturn().getStsRpt().getOrgnlGrpInfAndSts().setRpsStsAddInf(Max350Text.Factory.fromString(desError.substring(indice+1, desError.length()).trim(), ""));
					}
				}
            } finally {
                if (reader != null) {
                    reader.close();
                }
            }
        } catch(Exception e){
        	ContextoEalia ctx = (ContextoEalia) MessageContext.getCurrentMessageContext().getProperty("CONTEXTO_EALIA");
        	MensajeBean error = CtrlIncidencias.consultarMensaje("001", ctx);
        	CecaException ce = new CecaException(error.getCodError(), e.getMessage(), error.getDesMensajeVisualizacion(), e, desdeDonde);
        	CtrlIncidencias.generarLOG(ce, ctx);
        	throw new CecaException("000", "", ""); // Con 000 paramos todo
    	}
		return res;
    }

    public UsuariosGestionarResponse crearRespuestaErrorObjeto_usuariosGestionar(MensajeBean mensaje, ContextoEalia ctx) throws CecaException{
    	String desdeDonde = "crearRespuestaErrorObjeto_usuariosGestionar - es.cecabank.ealiaapp.util.Error_ealf_002_001_01";
    	TrazadorFichero.escribirTrazas(desdeDonde, "Entro", "", "", "", TrazadorFichero.NIVEL_DEBUG);
    	UsuariosGestionarResponse response = null;
    	try{
    		response = new UsuariosGestionarResponse();
			Document doc = new Document();
			EalfStatusReportV01 ealfPaymentStsRpt = new EalfStatusReportV01();
			EalfGroupHeader1 grpHdr = new EalfGroupHeader1();
			EalfOriginalGroup1 orgnlGrpInfAndSts = new EalfOriginalGroup1();
			BICFIIdentifier bicfi = new BICFIIdentifier();

			bicfi.setBICFIIdentifier(Constantes.BIC_EALIA);
			grpHdr.setMsgId(Max350Text.Factory.fromString(ctx.getDatosMensajes().getMsgIdResp(), ""));
			grpHdr.setUsrAgtBIC(bicfi);
			grpHdr.setCreDtTm(ISODateTime.Factory.fromString(ctx.getDatosMensajes().getCreDtTmResp(), ""));
			grpHdr.setEalAppId(Max35Text.Factory.fromString(((DatosMensajesBean)ctx.getDatosMensajes()).getCodAplicacion(), ""));
			grpHdr.setInstId(Max350Text.Factory.fromString(ctx.getDatosMensajes().getInstId(), ""));

			orgnlGrpInfAndSts.setOrgnlMsgId(Max350Text.Factory.fromString(ctx.getDatosMensajes().getMsgId(), ""));
			orgnlGrpInfAndSts.setOrgnlMsgId(Max350Text.Factory.fromString(ctx.getDatosMensajes().getMsgId(), ""));
			orgnlGrpInfAndSts.setRpsStsCd(Max35Text.Factory.fromString(mensaje.getCodError(), ""));
			orgnlGrpInfAndSts.setRpsStsDes(Max350Text.Factory.fromString(mensaje.getDesError(), ""));
			if (mensaje.getCodError().equals("RJVL")){
				orgnlGrpInfAndSts.setRpsStsAddInf(Max350Text.Factory.fromString(mensaje.getDesMensajeVisualizacion(), ""));
			}

			ealfPaymentStsRpt.setGrpHdr(grpHdr);
			ealfPaymentStsRpt.setOrgnlGrpInfAndSts(orgnlGrpInfAndSts);

			doc.setStsRpt(ealfPaymentStsRpt);
			response.setUsuariosGestionarReturn(doc);
        } catch(Exception e){
        	MensajeBean error = CtrlIncidencias.consultarMensaje("001", ctx);
        	CecaException ce = new CecaException(error.getCodError(), e.getMessage(), error.getDesMensajeVisualizacion(), e, desdeDonde);
        	CtrlIncidencias.generarLOG(ce, ctx);
        	throw new CecaException("000", "", ""); // Con 000 paramos todo
    	}
		return response;
    }

    public UsuariosGestionarResponseFault crearRespuestaErrorFault_usuariosGestionar() throws CecaException{
    	String desdeDonde = "crearRespuestaErrorFault_usuariosGestionar - es.cecabank.ealiaapp.util.Error_ealf_002_001_01";
    	TrazadorFichero.escribirTrazas(desdeDonde, "Entro", "", "", "", TrazadorFichero.NIVEL_DEBUG);
    	UsuariosGestionarResponseFault response = null;
    	try{
    		File file = new File(PropertiesEaliaApp.getPropiedad(PropertiesEaliaApp.PATH_XML_ERROR) + Constantes.WSF_XML_ERROR_USUARIOS_GESTIONAR_EALF_002_001_01);
			InputStream inputStream = new FileInputStream(file);
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            XMLStreamReader reader = null;
            try {
                reader = inputFactory.createXMLStreamReader(inputStream);
                response = UsuariosGestionarResponseFault.Factory.parse(reader);
            } finally {
                if (reader != null) {
                    reader.close();
                }
            }
        } catch(Exception e){
        	ContextoEalia ctx = (ContextoEalia)MessageContext.getCurrentMessageContext().getProperty("CONTEXTO_EALIA");
        	MensajeBean error = CtrlIncidencias.consultarMensaje("001", ctx);
        	CecaException ce = new CecaException(error.getCodError(), e.getMessage(), error.getDesMensajeVisualizacion(), e, desdeDonde);
        	CtrlIncidencias.generarLOG(ce, ctx);
        	throw new CecaException("000", "", ""); // Con 000 paramos todo
    	}
		return response;
    }
}
