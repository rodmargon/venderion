package es.cecabank.ealiaapp.logica.logicaimpl;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.persistence.EntityManager;

import org.apache.commons.codec.binary.Base64;

import es.cecabank.ealiaapp.control.HibernateUtil;
import es.cecabank.ealiacomun.control.TrazadorFichero;
import es.cecabank.ealiacomun.util.Constantes;

/**
 * Clase encargada de cifrar y descifrar mensajes
 * 
 * @author id0404
 * 
 */

public class CipherService {
	
	private static final String[] CIPHER_LIST = new String[] { Constantes.COD_CONV_DATOS_EAL,
			Constantes.COD_CONV_APP_EAL };
	private static final String CIPHER_ALGORITHM = "DESede/CBC/PKCS5Padding";
	private static final String KEYSPEC_ALGORITHM = "DESede";
	private static final int IV_LENGTH = 8;
	private static final int KEYSPEC_LENGTH = 24;
	private static final String DIGEST_ALGORITHM = "SHA-384";
	private static final String KEY_CHARSET = "UTF-8";
	// Singleton
	private static CipherService instance;

	private static Map<String, Cipher> encrypt;
	private static Map<String, Cipher> decrypt;

	public CipherService() {
	}

	public static CipherService getInstance() {
		if (instance == null) {
			instance = new CipherService();
			try {
				init();
			} catch (Exception e) {
				TrazadorFichero.escribirTrazas("CipherService - init", "", "", "", "", e, TrazadorFichero.NIVEL_FATAL);
			}
		}
		return instance;
	}

	/**
	 * Init cipher and decipher classes
	 * 
	 * @throws Exception
	 */
	@PostConstruct
	private static void init() throws Exception {
		encrypt = new HashMap<String, Cipher>();
		decrypt = new HashMap<String, Cipher>();

		final IvParameterSpec iv = new IvParameterSpec(getIV());
		for (String elemento : CIPHER_LIST) {
			// Inicia la clave
			byte[] key = getKey(elemento);
			final SecretKeySpec secretKey = new SecretKeySpec(key, KEYSPEC_ALGORITHM);

			// Inicia el cifrador
			Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
			encrypt.put(elemento, cipher);

			// Inicia el descifrador
			Cipher decipher = Cipher.getInstance(CIPHER_ALGORITHM);
			decipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
			decrypt.put(elemento, decipher);
		}

	}

	/**
	 * Get Initialization Vector
	 * 
	 * @return
	 * @throws Exception
	 */
	private static byte[] getIV() throws Exception {
		return getByteArray(Constantes.COD_CONV_VECTOR_INICIALIZACION, IV_LENGTH);
	}

	/**
	 * Get cipher and decipher key
	 * 
	 * @param elemento
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private static byte[] getKey(String elemento) throws Exception {
		return getByteArray(elemento, KEYSPEC_LENGTH);
	}

	/**
	 * Get bit array from element parameter
	 *
	 * @param elemento
	 *            Element which to apply digest funtion
	 * @param length
	 *            Final length
	 * @return bit array after digest
	 * @throws Exception
	 */
	private static byte[] getByteArray(String elemento, int length) throws Exception {
		String keyString = getKeyFromDatabase(elemento);
		byte[] key = MessageDigest.getInstance(DIGEST_ALGORITHM).digest(keyString.getBytes(KEY_CHARSET));
		key = Arrays.copyOf(key, length);
		return key;
	}

	/**
	 * Get cipher key from database
	 * 
	 * @param elemento
	 * @return
	 * @throws Exception
	 */
	private static String getKeyFromDatabase(String elemento) throws Exception {
		EntityManager em = HibernateUtil.getEntityManagerFactory().createEntityManager();

		String value = AplicacionService.getAplicacionParam(Constantes.COD_APLICACION_GENERICA, elemento, em);
		return value;
	}

	/**
	 * Ciphers input string and returns it base 64 formatted
	 * 
	 * @param input
	 *            input string
	 * @param conversor
	 *            Converter to be used. For instance,
	 *            COD_CONV_APP_EAL, COD_CONV_DATOS_EAL, etc
	 * @return String ciphered base 64 format
	 */
	public String cifrar(String input, String conversor) {
		try {
			byte[] encryptedBytes = encrypt.get(conversor).doFinal(input.getBytes(KEY_CHARSET));
			byte[] base64Bytes = Base64.encodeBase64(encryptedBytes);
			return new String(base64Bytes);
		} catch (Exception e) {
			return input;
		}
	}

	/**
	 * Deciphers input string base 64 formatted
	 * 
	 * @param input
	 *            Ciphered string base 64 formatted
	 * @param conversor
	 *            Converter to be used. For instance,
	 *            COD_CONV_APP_EAL, COD_CONV_DATOS_EAL, etc
	 * @return Decipher String
	 */
	public String descifrar(String input, String conversor) {
		try {
			byte[] base64Bytes = Base64.decodeBase64(input.getBytes(KEY_CHARSET));
			byte[] decryptedBytes = decrypt.get(conversor).doFinal(base64Bytes);
			return new String(decryptedBytes);
		} catch (Exception e) {
			return input;
		}
	}

	/**
	 * Cipher input  String (UserId)
	 * 
	 * @param input
	 *            User Id to be ciphered
	 * @return user id ciphered base 64 formatted
	 */
	public String cifrarIdPersona(String input) {
		return input;
		//return cifrar(input, Constantes.COD_CONV_DATOS_EAL);
	}

	/**
	 * Decipher input  String (UserId)
	 * 
	 * @param input
	 *            Ciphered User Id
	 * @return Deciphered user Id
	 */
	public String descifrarIdPersona(String input) {
		return input;
		//return descifrar(input, Constantes.COD_CONV_DATOS_EAL);
	}

	/**
	 * Cipher input string(key)
	 * 
	 * @param input
	 *            Key to be ciphered
	 * @return Ciphered key base 64 formatted
	 */
	public String cifrarClave(String input) {
		return cifrar(input, Constantes.COD_CONV_APP_EAL);
	}

	/**
	 * Decipher input string(key)
	 * 
	 * @param input
	 *            Ciphered key base 64 formatted
	 * @return Deciphered key
	 */
	public String descifrarClave(String input) {
		return descifrar(input, Constantes.COD_CONV_APP_EAL);
	}
}
