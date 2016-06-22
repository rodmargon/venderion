package es.cecabank.ealiaapp.accesobd.daoimpl;

import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import es.cecabank.ealiaapp.accesobd.dao.EalEntisAppsParamDao;
import es.cecabank.ealiaapp.accesobd.model.EalEntisAppsParam;
import es.cecabank.ealiaapp.accesobd.model.EalEntisAppsParamId;
import es.cecabank.ealiacomun.util.Constantes;

public class EalEntisAppsParamDaoImpl extends GenericDaoImpl<EalEntisAppsParam, EalEntisAppsParamId> implements EalEntisAppsParamDao {
	
	public EalEntisAppsParamDaoImpl(EntityManager em) {
		super(EalEntisAppsParam.class, em);
	}

	@SuppressWarnings("unchecked")
	public List<EalEntisAppsParam> findParamsByEntidad(String entidad, String parametro) {
		String sql = "select e from EalEntisAppsParam e " +
				"where e.ealEntisAplicaciones.ealEntisEntidades.bicEntidad IN ( :entidades ) " +
				"and e.id.idParametro = :parametro";
		List<String> entidades = Arrays.asList(new String[]{entidad, Constantes.BIC_EALIA});
		
		Query q = em.createQuery(sql).setParameter("entidades", entidades).setParameter("parametro", parametro);
		
		return q.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public EalEntisAppsParam findParamByCodApp(String codapp, String parametro){
		
		String sql = "select e from EalEntisAppsParam e " +
				"where e.id.codAplicacion = :codapp " +
				"and e.id.idParametro = :parametro";
		
		List<EalEntisAppsParam> resultado = em.createQuery(sql).setParameter("codapp", codapp).setParameter("parametro", parametro).getResultList();
		
		if (resultado.size() == 0){
			sql = "select e from EalEntisAppsParam e " +
					"where e.id.codAplicacion = 'XXX' " +
					"and e.id.idParametro = :parametro";
			
			resultado = em.createQuery(sql).setParameter("parametro", parametro).getResultList();
			
			return resultado.size()>0?resultado.get(0):null;
		}else{
			return resultado.get(0);
		}
	}
	
}
