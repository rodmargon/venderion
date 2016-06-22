/**
 * 
 */
package es.cecabank.ealiaapp.accesobd.dao;

import java.io.Serializable;

import ceca.control.CecaException;

public interface GenericDao <T, PK extends Serializable>{

	void save(T object) throws CecaException;
	
	void save(T object, boolean closedTransaction) throws CecaException;
	
	T getById(PK id) throws CecaException;
	
	void update(T object) throws CecaException;
	
	public void update(T object, boolean closedTransaction) throws CecaException;
	
	void delete(T object, boolean closedTransaction) throws CecaException;
			
}
