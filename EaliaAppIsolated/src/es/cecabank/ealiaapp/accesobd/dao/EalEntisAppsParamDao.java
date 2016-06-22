package es.cecabank.ealiaapp.accesobd.dao;

import java.util.List;

import es.cecabank.ealiaapp.accesobd.model.EalEntisAppsParam;
import es.cecabank.ealiaapp.accesobd.model.EalEntisAppsParamId;

public interface EalEntisAppsParamDao extends GenericDao<EalEntisAppsParam, EalEntisAppsParamId> {
	
	/**
	 * Returns parameter's configurations list for every applications
	 *  
	 * @param entidad entity identificator
	 * @param parametro parameter identificator to be searched
	 * @return parameter's configurations list for a concret entity
	 */
	List<EalEntisAppsParam> findParamsByEntidad(String entidad, String parametro);
	
	
	/**
	 * Returns selected parameter for an application code. If the application code doesn't exist it returns
	 * default value
	 * @param codapp system's application code
	 * @param parametro parameter to be retrieved
	 * @return EalEntisAppsParam object
	 */
	EalEntisAppsParam findParamByCodApp(String codapp, String parametro);
}
