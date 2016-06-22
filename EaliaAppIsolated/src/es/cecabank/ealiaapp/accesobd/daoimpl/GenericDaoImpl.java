/**
 * 
 */
package es.cecabank.ealiaapp.accesobd.daoimpl;

import java.io.Serializable;

import javax.annotation.Resource;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import ceca.control.CecaException;
import es.cecabank.ealiaapp.accesobd.dao.GenericDao;
import es.cecabank.ealiaapp.control.CtrlErroresPersistence;
import es.cecabank.ealiaapp.logica.beans.ErrorPersistenceBean;
import es.cecabank.ealiacomun.control.TrazadorFichero;

public class GenericDaoImpl<T, PK extends Serializable> implements GenericDao<T, PK> {
	
	@PersistenceContext
	protected EntityManager em;
	
	@Resource
	protected UserTransaction userTx;
	
	private Class<T> persistentClass;
	
	public GenericDaoImpl(final Class<T> persistentClass, EntityManager em) {
		this.em = em;
		this.persistentClass = persistentClass;
	}

	
	/**
	 * Initialization of UserTransaction depending of the scope
	 * @throws NamingException
	 */
	protected void initUserTx() throws NamingException{
		String desdeDonde = "GenericDaoImpl-initUserTx";
		try {
			this.userTx = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
		} catch (NamingException e) {
			try {
				// Por si no lo encuentra como java:comp (esto no es muy fiable...pero en todo caso está el siguiente catch)
				this.userTx = (UserTransaction) new InitialContext().lookup("UserTransaction");
			} catch (NamingException e1) {
				TrazadorFichero.escribirTrazas(desdeDonde, "No se inicializa el objeto UserTransaction.", "", "", "", e1, TrazadorFichero.NIVEL_FATAL);
				throw e1;
			}
		}
	}
	
	/**
	 * get Method. It doen't close the connection and neither the transaction
	 * 
	 */
	public T getById(PK id) throws CecaException {
		String desdeDonde = "GenericDaoImpl-getById";
		try {
			T entity = (T) em.find(this.persistentClass, id);
			return entity;
		} catch (NoResultException e) {
			return null;
		} catch (java.lang.Exception e) {
			TrazadorFichero.escribirTrazas(desdeDonde, "falla la recuperación por id.", "", "", "", e, TrazadorFichero.NIVEL_FATAL);
			throw new CecaException("008", "Error al obetener entidad" + " " + e.getMessage(), e,
					"EaliaPagos - accesobd.daoimpl." + getClass().toString());
		}
	}	

	
	/**
	 * Object persistence method
	 * @param object
	 * @param closedTransaction
	 * @throws CecaException
	 */
	public void save(T object, boolean closedTransaction)throws CecaException {
		String desdeDonde = "GenericDaoImpl-save";
		if (closedTransaction){
			this.save(object);
		}
		else{
			try {
				em.joinTransaction();
				em.persist(object);
			} catch (Exception e) {
				TrazadorFichero.escribirTrazas(desdeDonde, "falla la persistencia.", "", "", "", e, TrazadorFichero.NIVEL_FATAL);
				ErrorPersistenceBean error = CtrlErroresPersistence.tratarError(e);
				throw new CecaException("007", "Error al insertar: ", e.getMessage(), error.getSqlException(),
						"EaliaPagos - accesobd.daoimpl." + getClass().toString());
			}
		}
	}
	
	/**
	 * Method for persisting objects creating its own User transaction
	 * 
	 */
	public void save(T object) throws CecaException {
		String desdeDonde = "GenericDaoImpl-save";
		try {
			initUserTx();
			userTx.begin();
			em.joinTransaction();
			em.persist(object);
			userTx.commit();
		} catch (Exception e) {
			try {
				userTx.rollback();
			} catch (IllegalStateException e1) {
			} catch (SecurityException e1) {
			} catch (SystemException e1) {
			}
			TrazadorFichero.escribirTrazas(desdeDonde, "falla la persistencia", "", "", "", e, TrazadorFichero.NIVEL_FATAL);
			// Llamamos a CtrlErroresPersistence para tratar el error.
			// Si es un error que tenemos controlado, se lanzará una excepción en este método.
			ErrorPersistenceBean error = CtrlErroresPersistence.tratarError(e);
			// Si no es un error controlado, lanzamos esta
			throw new CecaException("007", "Error al insertar: ", e.getMessage(), error.getSqlException(),
					"EaliaPagos - accesobd.daoimpl." + getClass().toString());
		} 
	}
	
	/**
	 * Updating objects
	 * @param object
	 * @param closedTransaction
	 * @throws CecaException
	 */
	public void update(T object, boolean closedTransaction)throws CecaException {
		String desdeDonde = "GenericDaoImpl-update";
		if (closedTransaction){
			this.update(object);
		}
		else{
			try {
				em.joinTransaction();
				em.merge(object);
			} catch (Exception e) {
				TrazadorFichero.escribirTrazas(desdeDonde, "falla la actualización.", "", "", "", e, TrazadorFichero.NIVEL_FATAL);
				ErrorPersistenceBean error = CtrlErroresPersistence.tratarError(e);
				// TODO Falta asignar un nuevo número a este error.
				throw new CecaException("007", "Error al actualizar: ", e.getMessage(), error.getSqlException(),
						"EaliaPagos - accesobd.daoimpl." + getClass().toString());
			}
		}
	}
	
	/**
	 * Updating objects creating its own User transaction
	 * El EntityManager se va a manejar desde fuera en todo caso.
	 */
	public void update(T object) throws CecaException {		
		String desdeDonde = "GenericDaoImpl-update";
		try {
			initUserTx();
			userTx.begin();
			em.joinTransaction();
			em.merge(object);
			userTx.commit();
		} catch (Exception e) {
			try {
				userTx.rollback();
			} catch (IllegalStateException e1) {
			} catch (SecurityException e1) {
			} catch (SystemException e1) {
			}
			TrazadorFichero.escribirTrazas(desdeDonde, "falla la actualización.", "", "", "", e, TrazadorFichero.NIVEL_FATAL);
			ErrorPersistenceBean error = CtrlErroresPersistence.tratarError(e);
			// TODO Falta asignar un nuevo número a este error.
			throw new CecaException("009", "Error al actualizar", e.getMessage(), error.getSqlException(),
					"EaliaPagos - accesobd.daoimpl." + getClass().toString());
		} 
	}
	
	/**
	 * Deleting objects method
	 * @param object
	 * @param closedTransaction
	 * @throws CecaException
	 */
	public void delete(T object, boolean closedTransaction)throws CecaException {
		String desdeDonde = "GenericDaoImpl-delete";
		if (closedTransaction){
			this.delete(object);
		}
		else{
			try {
				em.joinTransaction();
				em.remove(object);
			} catch (Exception e) {
				TrazadorFichero.escribirTrazas(desdeDonde, "falla el borrado", "", "", "", e, TrazadorFichero.NIVEL_FATAL);
				ErrorPersistenceBean error = CtrlErroresPersistence.tratarError(e);
				// TODO Falta asignar un nuevo número a este error.
				throw new CecaException("007", "Error al borrar: ", e.getMessage(), error.getSqlException(),
						"EaliaPagos - accesobd.daoimpl." + getClass().toString());
			}
		}
	}
	
	/**
	 * Deleting objects method creating its own User Transaction
	 * El EntityManager se va a manejar desde fuera en todo caso.
	 */
	public void delete(T object) throws CecaException {
		String desdeDonde = "GenericDaoImpl-delete";
		try {
			initUserTx();
			userTx.begin();
			em.joinTransaction();
			em.remove(object);
			userTx.commit();
		} catch (Exception e) {
			try {
				userTx.rollback();
			} catch (IllegalStateException e1) {
			} catch (SecurityException e1) {
			} catch (SystemException e1) {
			}
			TrazadorFichero.escribirTrazas(desdeDonde, "falla el borrado", "", "", "", e, TrazadorFichero.NIVEL_FATAL);
			ErrorPersistenceBean error = CtrlErroresPersistence.tratarError(e);
			// TODO Falta asignar un nuevo número a este error.
			throw new CecaException("007", "Error al borrar: ", e.getMessage(), error.getSqlException(),
					"EaliaPagos - accesobd.daoimpl." + getClass().toString());
		} 
	}
	

	/**
	 * Commiting changes method
	 * @param transaction
	 * @throws CecaException
	 */
	public static void saveDB(UserTransaction transaction) throws CecaException{
		String desdeDonde = "GenericDaoImpl-saveDB";
		try{
			transaction.commit();
		}catch (java.lang.Exception e){
			TrazadorFichero.escribirTrazas(desdeDonde, "falla la operación de confirmación (commit).", "", "", "", e, TrazadorFichero.NIVEL_FATAL);
			ErrorPersistenceBean error = CtrlErroresPersistence.tratarError(e);
			throw new CecaException("013", "Error al hacer commit: "+error.getObjeto(), error.getSqlException(),  "EaliaPagos - accesobd.daoimpl.GenericDao");
		}
		
	}
		
}
