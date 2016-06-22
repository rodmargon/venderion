package es.cecabank.ealiaapp.control;


import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;
import javax.transaction.NotSupportedException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import es.cecabank.ealiacomun.util.Constantes;



public class HibernateUtil {
	
	@PersistenceUnit
	private static EntityManagerFactory emFactory; 
	
	private static void buildEntityManagerFactory() {
		try {
			emFactory = Persistence.createEntityManagerFactory(Constantes.PERSISTENCE_EALIAAPP);
		}
		catch (Throwable ex) {
			System.err.println("Error creando una factoria de session." + ex);
			throw new ExceptionInInitializerError(ex);
		}
	}
	/**
	 * @return EntityManagerFactory
	 */
	public static EntityManagerFactory getEntityManagerFactory() {
		if(emFactory == null || !emFactory.isOpen()){
			buildEntityManagerFactory();
		}
		return emFactory;
	}	
	
	/**
	 * @param transaction
	 */
	public static void rollback(UserTransaction transaction){
    	try{
    		if(transaction!=null && transaction.getStatus() != Status.STATUS_NO_TRANSACTION){
    			transaction.rollback();
    		}	
		}catch(java.lang.Exception ex){
			ex.printStackTrace();
		}
	}
	
	public static void joinTransaccion(UserTransaction transaction, EntityManager factory) throws SystemException, NotSupportedException{
		if (transaction.getStatus() == Status.STATUS_NO_TRANSACTION){ 
			transaction.begin();
			factory.joinTransaction();
		}
	}
}