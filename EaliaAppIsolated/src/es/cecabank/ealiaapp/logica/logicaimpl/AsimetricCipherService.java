package es.cecabank.ealiaapp.logica.logicaimpl;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;

/**
 * Clase encargada de cifrar y descifrar mensajes
 * 
 * @author id0404
 * 
 */

public class AsimetricCipherService {
	
	private static final String ALGORITHM = "RSA";
	private static final int KEY_LENGTH = 1024;

	public static KeyPair generateKey() throws NoSuchAlgorithmException {
		final KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
		keyGen.initialize(KEY_LENGTH);
		KeyPair key = keyGen.generateKeyPair();
		return key;
	}

	/**
	 * Cipher a message
	 * @param mensaje
	 * @param key
	 * @return cadena cifrada en formato base 64
	 * @throws Exception
	 */
	public static String cifrar(String mensaje, PrivateKey key) throws Exception {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] cif = cipher.doFinal(mensaje.getBytes());
		String cifrado = new String(Base64.encodeBase64(cif));
		return cifrado;
	}

	
	/**
	 * Generate private key with RSA algorithm
	 * @param privKeyPEM
	 * @return
	 * @throws Exception
	 */
	public static RSAPrivateKey cargarClavePrivada(String privKeyPEM) throws Exception {
		
		// Base64 decode the data
		byte[] encoded = Base64.decodeBase64(privKeyPEM.getBytes());

		// PKCS8 decode the encoded RSA private key
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		RSAPrivateKey privKey = (RSAPrivateKey) kf.generatePrivate(keySpec);
		
		return privKey;
	}

	/**
	 * Generate public key with RSA algorithm
	 * @param publKeyPEM
	 * @return
	 * @throws Exception
	 */
	public static RSAPublicKey cargarClavePublica(String publKeyPEM) throws Exception {
		
		byte[] encoded = Base64.decodeBase64(publKeyPEM.getBytes());
		
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		RSAPublicKey publKey = (RSAPublicKey) kf.generatePublic(keySpec);
		
		return publKey;
	}

	/**
	 * @param mensaje
	 * @param key
	 * @return cadena descifrada
	 * @throws Exception
	 */
	public static String descifrar(String mensaje, PublicKey key) throws Exception {
		byte[] cif = Base64.decodeBase64(mensaje.getBytes());
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, key);
		byte[] des = cipher.doFinal(cif);
		return new String(des);
	}

	// FIXME ¿tamaño de mensaje esperado? si es más de 117 -> big brown
}
