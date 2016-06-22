package es.cecabank.ealiaapp.logica.logicaimpl;

import java.security.PublicKey;
import java.security.Signature;

import javax.persistence.EntityManager;

import org.apache.axis2.context.MessageContext;
import org.apache.commons.codec.binary.Base64;

import ceca.control.CecaException;
import es.cecabank.ealiaapp.accesobd.dao.EalEntisAplicacionesDao;
import es.cecabank.ealiaapp.accesobd.dao.EalEntisAppsParamDao;
import es.cecabank.ealiaapp.accesobd.dao.EappDispInstalacionesDao;
import es.cecabank.ealiaapp.accesobd.dao.EappSvcsAppsDao;
import es.cecabank.ealiaapp.accesobd.daoimpl.EalEntisAplicacionesDaoImpl;
import es.cecabank.ealiaapp.accesobd.daoimpl.EalEntisAppsParamDaoImpl;
import es.cecabank.ealiaapp.accesobd.daoimpl.EappDispInstalacionesDaoImpl;
import es.cecabank.ealiaapp.accesobd.daoimpl.EappSvcsAppsDaoImpl;
import es.cecabank.ealiaapp.accesobd.model.EalEntisAplicaciones;
import es.cecabank.ealiaapp.accesobd.model.EalEntisAppsParam;
import es.cecabank.ealiaapp.accesobd.model.EalEntisAppsParamId;
import es.cecabank.ealiaapp.accesobd.model.EappDispInstalaciones;
import es.cecabank.ealiaapp.accesobd.model.EappSvcsApps;
import es.cecabank.ealiaapp.accesobd.model.EappSvcsAppsId;
import es.cecabank.ealiacomun.control.ContextoEalia;
import es.cecabank.ealiacomun.control.TrazadorFichero;
import es.cecabank.ealiacomun.util.Constantes;
import es.cecabank.ealiacomun.util.Utils;

public class SecurityService {
	
	
	
	/**
	 * Security validations: permissions and digital signature
	 * @param ctx
	 * @param nombreWSF
	 * @param firma
	 * @param em
	 * @throws CecaException
	 */
	public static void seguridadYControlDeAcceso(ContextoEalia ctx, String nombreWSF, String firma, EntityManager em) throws CecaException {
		String desdeDonde = "seguridadYControlDeAcceso - SecurityService";
		// 1 Obtener datos de la aplicación
		EalEntisAplicacionesDao aplicacionesDao = new EalEntisAplicacionesDaoImpl(em);
		EalEntisAplicaciones aplicaciones = aplicacionesDao.getById(ctx.getDatosMensajes().getCodAplicacion());
		if (aplicaciones == null) {
			throw new CecaException("NSVC", "Código de aplicación no encontrado", desdeDonde);
		}
		
		// 2 Control de acceso a la aplicación
		
		// a Validar bic de la entidad
		if (!aplicaciones.getEalEntisEntidades().getBicEntidad().equals(ctx.getDatosMensajes().getBicEntidad())) {
			throw new CecaException("NSVC", "El BIC no corresponde con el código de aplicación indicado", desdeDonde);
		}
		
		// b Validar WSF habilitado
		EappSvcsAppsDao appsDao = new EappSvcsAppsDaoImpl(em);
		EappSvcsAppsId appsId = new EappSvcsAppsId(ctx.getDatosMensajes().getCodAplicacion(), nombreWSF);
		EappSvcsApps apps = appsDao.getById(appsId);
		if (apps == null) {
			throw new CecaException("NSVC", "El WebService no está habilitado para la aplicación indicada", desdeDonde);
		}
		
		// 3 Descifrado y validación del mensaje
		EalEntisAppsParamDao paramDao = new EalEntisAppsParamDaoImpl(em);
		TrazadorFichero.escribirTrazas(desdeDonde, "Entro en seguridad", "", "", "", TrazadorFichero.NIVEL_INFO);
		EalEntisAppsParam param = paramDao.findParamByCodApp(ctx.getDatosMensajes().getCodAplicacion(),Constantes.PARAM_IND_CIFRADO_WSF);
		if (firma!=null) {
			TrazadorFichero.escribirTrazas(desdeDonde, "la firma(ealiaSignature) es: ", "----", firma, "", TrazadorFichero.NIVEL_INFO);
			TrazadorFichero.escribirTrazas(desdeDonde, "la clave publica es: ", "----", ctx.getDatosMensajes().getClavePublica(), "", TrazadorFichero.NIVEL_INFO);
		}
		if (param.getValorParametro().equals("1") && firma!=null) {
			ctx.getDatosMensajes().setUsaFirma(true);
			try {
				String clavePublica = "";
				if (nombreWSF.equals(Constantes.NOMBRE_SERVICIO_WSF_DISPOSITIVOS_ACTUALIZAR) && !ctx.getDatosMensajes().getClavePublica().isEmpty()) {
					clavePublica = CipherService.getInstance().descifrarClave(ctx.getDatosMensajes().getClavePublica());
				} else { 
					EappDispInstalacionesDao isntalacionesDao = new EappDispInstalacionesDaoImpl(em);
					EappDispInstalaciones instalacion = isntalacionesDao.getById(ctx.getDatosMensajes().getInstId());
					if (instalacion!=null) {
						clavePublica = instalacion.getIdClavePublicaApp();
					}
				}	
				if (!verificarFirma(clavePublica,firma)) {
					throw new CecaException("ERRS", "Error General en el sistema",desdeDonde);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				throw new CecaException("ERRS", "Error General en el sistema",desdeDonde);
				//TODO cuando se produce un error de validación, guardar en la tabla de errores con un código especial para elaborar un inoforme de ataques
			}
		}
	//Si todas las comprobaciones anteriores se han superado correctamente, devolver ACCP.
	}
	
	/**
	 * @param clavePublica
	 * @param hash
	 * @return
	 * @throws Exception
	 */
	public static boolean verificarFirma(String clavePublica, String hash) throws Exception{
		Signature signature = Signature.getInstance("SHA256withRSA");
		PublicKey serverKey = AsimetricCipherService.cargarClavePublica(clavePublica);
		signature.initVerify(serverKey);
		String soapBodyString = MessageContext.getCurrentMessageContext().getEnvelope().getBody().getFirstElement().getFirstElement().toString();
		soapBodyString = Utils.eliminarNamespaceXMLSoap(soapBodyString,true);
		signature.update(soapBodyString.getBytes("UTF-8"));
		return signature.verify(Base64.decodeBase64(hash.getBytes()));
	}
		
}	
	
