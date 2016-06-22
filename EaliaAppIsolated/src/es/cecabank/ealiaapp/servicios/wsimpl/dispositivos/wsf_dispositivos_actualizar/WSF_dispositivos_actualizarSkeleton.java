package es.cecabank.ealiaapp.servicios.wsimpl.dispositivos.wsf_dispositivos_actualizar;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

import ceca.control.CecaException;
import es.cecabank.ealiaapp.logica.logicaimpl.DevicesService;
import es.cecabank.ealiaapp.servicios.mensajes.ealf_002_001_01.EalfStatusReportV01;
import es.cecabank.ealiaapp.servicios.wsimpl.AbstractSkeleton;
import es.cecabank.ealiaapp.util.Error_ealf_002_001_01;
import es.cecabank.ealiacomun.logica.beans.MensajeBean;
import es.cecabank.ealiacomun.mensajes.elements.complex.ealiaapp.EalfOriginalGroup1;
import es.cecabank.ealiacomun.mensajes.elements.simple.Max350Text;
import es.cecabank.ealiacomun.util.Constantes;

/**
 * WSF_dispositivos_actualizarSkeleton java skeleton for the axisService
 */
public class WSF_dispositivos_actualizarSkeleton extends AbstractSkeleton<DispositivosActualizar, DispositivosActualizarResponse> {

	public DispositivosActualizarResponse dispositivosActualizar(DispositivosActualizar input) throws Exception {
		return ejecutar(input);
	}
	
	
	@Override
	protected void validarSesion() throws CecaException {
	}
	
	@Override
	protected void validarEntrada() throws CecaException {
	}

	@Override
	protected void ejecutarLogica() throws CecaException, Exception {
		String idSesionEalia = DevicesService.registrarSesionActualizandoDispositivoEInstalacion(em, ctx);
		ctx.getDatosMensajes().setSessionId(idSesionEalia);
	}

	@Override
	protected DispositivosActualizarResponse generateResponse() throws Exception {
		DispositivosActualizarResponse response = new DispositivosActualizarResponse();
		es.cecabank.ealiaapp.servicios.mensajes.ealf_002_001_01.Document document = new es.cecabank.ealiaapp.servicios.mensajes.ealf_002_001_01.Document();

		EalfStatusReportV01 pmtStsRpt = new EalfStatusReportV01();
		pmtStsRpt.setGrpHdr(generateGroupHeader());
		EalfOriginalGroup1 originalGroup = generateOriginalGroup();
		if (ctx.getDatosMensajes().isUsaFirma()) {
			originalGroup.setRpsStsAddInf(getInstalationPublicKey());
		}	
		pmtStsRpt.setOrgnlGrpInfAndSts(originalGroup);
				
		document.setStsRpt(pmtStsRpt);
		response.setDispositivosActualizarReturn(document);
		return response;
	}
	
	private Max350Text getInstalationPublicKey(){
		return Max350Text.Factory.fromString(ctx.getDatosMensajes().getClavePublica(),"");
	}

	@Override
	protected void validarSalida(XMLStreamReader reader) throws Exception {
		DispositivosActualizarResponse.Factory.parse(reader);
	}

	@Override
	protected DispositivosActualizarResponse generarRespuestaError(MensajeBean mensaje) throws CecaException {
		DispositivosActualizarResponse res = null;
		Error_ealf_002_001_01 error = new Error_ealf_002_001_01();
		res = error.crearRespuestaErrorObjeto_dispositivosActualizar(mensaje, ctx);
		return res;	
	}

	@Override
	protected String getDesdeDonde() {
		 return "dispositivosActualizar - es.cecabank.ealiaapp.servicios.wsimpl.dispositivos.ws_dispositivos_actualizar." + "WSF_dispositivos_actualizarSkeleton";
	}

	@Override
	protected String getNombreWebService() {
		return Constantes.NOMBRE_SERVICIO_WSF_DISPOSITIVOS_ACTUALIZAR;
	}

	@Override
	protected QName getResponseQName() {
		return DispositivosActualizarResponse.MY_QNAME;
	}

	@Override
	protected void setDatos() throws CecaException {
		datos.crearDatos(input.getEntrada());		
	}

	@Override
	protected String getMensajeSalida() {
		return Constantes.MENSAJE_SALIDA_WSF_DISPOSITIVOS_ACTUALIZAR;
	}

	@Override
	protected String getMensajeEntrada() {
		return Constantes.MENSAJE_ENTRADA_WSF_DISPOSITIVOS_ACTUALIZAR;
	}

	@Override
	protected String getNombreOperacion() {
		return "dispositivosActualizar";
	}

	@Override
	protected void setStatusCode(DispositivosActualizarResponse response) {
		datos.setStatusCode(response.getDispositivosActualizarReturn());		
	}
}
