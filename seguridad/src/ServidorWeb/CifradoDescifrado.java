package ServidorWeb;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.SecretKeySpec;


public class CifradoDescifrado {

	public static void cifrar(Fichero fichero, String algoritmo) 
			throws KeyStoreException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException, UnrecoverableEntryException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, ShortBufferException, IllegalBlockSizeException, BadPaddingException{
		String provider         = "SunJCE";
		ByteArrayInputStream ftextoclaro   = new    ByteArrayInputStream(fichero.getDocumento());
		ByteArrayOutputStream ftextocifrado = new ByteArrayOutputStream();
		ByteArrayOutputStream fparametros   = new ByteArrayOutputStream();


		byte   bloqueclaro[]    = new byte[2024];
		byte   bloquecifrado[]  = new byte[2048];
		//String algoritmo        = "AES";
		String transformacion   = "/CBC/PKCS5Padding";
		int    longclave        = 128;
		int    longbloque;
		double lf;              // longitud del fichero

		KeyStore    ks;
		char[]      ks_password  	= "147258".toCharArray();
		char[]      key_password 	= "147258".toCharArray();
		String		ks_file			= "D:/git/seguridad/src/ServidorWeb/servidor.jce";	  


		ks = KeyStore.getInstance("JCEKS");

		ks.load(new FileInputStream(ks_file),  ks_password); 

		if (algoritmo.equals("arcfour")){
			System.out.println("Metodo de cifrado RC4");
			KeyStore.SecretKeyEntry pkEntry = (KeyStore.SecretKeyEntry)
					ks.getEntry("rc4", new KeyStore.PasswordProtection(key_password));

			byte[]  kreg_raw = pkEntry.getSecretKey().getEncoded();
			SecretKeySpec kreg = new SecretKeySpec(kreg_raw, "RC4");

			Cipher cifrador = Cipher.getInstance("RC4", "SunJCE");

			cifrador.init(Cipher.ENCRYPT_MODE, kreg);

			byte[] ciphertext = new byte[cifrador.getOutputSize(fichero.getDocumento().length)];

			int outputLenUpdate = cifrador.update(fichero.getDocumento(), 0, fichero.getDocumento().length, ciphertext, 0);

			int outputLenFinal = cifrador.doFinal(ciphertext, outputLenUpdate);

			
			fichero.setPrivado(true);
			fichero.setDocumento(ciphertext);
		}



		if (algoritmo.equals("aes")){
			System.out.println("Metodo de cifrado AES");

			KeyStore.SecretKeyEntry pkEntry = (KeyStore.SecretKeyEntry)
					ks.getEntry("aes", new KeyStore.PasswordProtection(key_password));

			byte[]  kreg_raw = pkEntry.getSecretKey().getEncoded();
			SecretKeySpec kreg = new SecretKeySpec(kreg_raw, "AES");



			/************************************************************
		CIFRAR
			 ************************************************************/
			System.out.println("*** INICIO CIFRADO " + algoritmo + "-" + longclave + " ************");

			Cipher cifrador = Cipher.getInstance(algoritmo + transformacion);

			// Se cifra con la modalidad opaca de la clave

			cifrador.init(Cipher.ENCRYPT_MODE, kreg);

			lf  = 0;

			while ((longbloque = ftextoclaro.read(bloqueclaro)) > 0) {
				lf = lf + longbloque;
				bloquecifrado = cifrador.update(bloqueclaro,0,longbloque);
				ftextocifrado.write(bloquecifrado);
			}     

			// Hacer dofinal y medir su tiempo
			bloquecifrado = cifrador.doFinal();
			ftextocifrado.write(bloquecifrado);

			// Escribir resultados
			fichero.setDocumento(ftextocifrado.toByteArray());
			fichero.setPrivado(true);

			//System.out.println("Long. ultimo bloque" + bloquecifrado.length );
			System.out.println("*** FIN CIFRADO " + algoritmo + "-" + longclave + " Provider: " + provider);
			System.out.println("Bytes  cifrados = " + (int)lf );

			// Cerrar ficheros
			ftextocifrado.close();
			ftextoclaro.close();


			/*******************************************************************
			 *  Obtener parametros del algoritmo y archivarlos
			 *  
			 *  NOTA: Para los cifradores en flujo no se ejecuta el lazo de  
			 *        parametros porque no se necesitan. Ejemplo: RC4
			 *******************************************************************/
			// System.out.println("Leer los parametros(IV,...) usados por el cifrador ..." );

			//AlgorithmParameters  paramxx =  cifrador.getParameters();


			AlgorithmParameters param = AlgorithmParameters.getInstance(algoritmo);        
			param =  cifrador.getParameters();

			System.out.println("Parametros del cifrado ..." + param.toString());

			byte[]  paramSerializados = param.getEncoded();
			fichero.setParamCifrado(paramSerializados);
			fparametros.close();
		}
	}

	public static byte [] descifrar(byte [] cifrado, byte [] param, String algoritmo) 
			throws KeyStoreException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException, UnrecoverableEntryException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, ShortBufferException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException{
		String provider         = "SunJCE";
		//String algoritmo        = "AES";
		String transformacion   = "/CBC/PKCS5Padding";
		int    longbloque;

		KeyStore    ks;
		char[]      ks_password  	= "147258".toCharArray();
		char[]      key_password 	= "147258".toCharArray();
		String		ks_file			= "D:/git/seguridad/src/ServidorWeb/servidor.jce";

		ks = KeyStore.getInstance("JCEKS");

		ks.load(new FileInputStream(ks_file),  ks_password); 

		if (algoritmo.equals("arcfour")){
			KeyStore.SecretKeyEntry pkEntry = (KeyStore.SecretKeyEntry)
					ks.getEntry("rc4", new KeyStore.PasswordProtection(key_password));

			byte[]  kreg_raw = pkEntry.getSecretKey().getEncoded();
			SecretKeySpec kreg = new SecretKeySpec(kreg_raw, "RC4");
			System.out.println("Descifrando...");
			// Get a cipher object for RC4 decryption.
			Cipher descifrador = Cipher.getInstance("RC4", "SunJCE");

			// Initialize the decryption object.  This call will
			// set up rc4Decrypter to perform decryption using RC4.
			descifrador.init(Cipher.DECRYPT_MODE, kreg);

			// Create some space to hold the results of
			// decrypting the ciphertext.  A call to getOutputSize() will
			// return the number of bytes necessary to hold the decrypted data.
			// This number may be larger than the number that is
			// actually needed.
			byte[] recoveredPlaintext = new byte[descifrador.getOutputSize(cifrado.length)];

			// Decrypt the data.  The call to update() will
			// decrypt as much data as is possible.  If not enough
			// bytes are passed in to decrypt a whole block, the
			// extra bytes will be kept for the next call to update(),
			// or a call to doFinal().
			int outputLenUpdate = descifrador.update(
					cifrado, 0, cifrado.length, recoveredPlaintext, 0);

			// After passing in all of the data to decrypt,
			// call doFinal() which will remove any
			// padding on the input data, and finish decryption.
			int outputLenFinal = descifrador.doFinal(
					recoveredPlaintext, outputLenUpdate);
			int outputLenTotal = outputLenUpdate + outputLenFinal;
			return recoveredPlaintext;
		}

		if(algoritmo.equals("aes")){
			KeyStore.SecretKeyEntry pkEntry = (KeyStore.SecretKeyEntry)
					ks.getEntry("aes", new KeyStore.PasswordProtection(key_password));

			byte[]  kreg_raw = pkEntry.getSecretKey().getEncoded();
			SecretKeySpec kreg = new SecretKeySpec(kreg_raw, "AES");

			//*****************************************************************************
			//					DESCIFRAR
			//*****************************************************************************
			ByteArrayInputStream  ftextocifrado2 = new ByteArrayInputStream (cifrado);
			ByteArrayOutputStream ftextoclaro2   = new ByteArrayOutputStream();
			ByteArrayInputStream  fparametros_in = new ByteArrayInputStream (param);

			byte bloquecifrado2[]  = new byte[1024];
			byte bloqueclaro2[]    = new byte[1048];		


			System.out.println("*************** INICIO DESCIFRADO *****************" );

			Cipher descifrador = Cipher.getInstance(algoritmo + transformacion, provider);

			// Leer los parametros si el algoritmo soporta parametros

			AlgorithmParameters params = AlgorithmParameters.getInstance(algoritmo,provider);        
			byte[] paramSerializados1 = new byte[fparametros_in.available()];

			fparametros_in.read(paramSerializados1);         
			params.init(paramSerializados1);

			System.out.println("Parametros del descifrado ... = " + params.toString());

			descifrador.init(Cipher.DECRYPT_MODE, kreg, params);

			while ((longbloque = ftextocifrado2.read(bloquecifrado2)) > 0) {

				bloqueclaro2 = descifrador.update(bloquecifrado2,0,longbloque);
				ftextoclaro2.write(bloqueclaro2);
			}

			bloqueclaro2 = descifrador.doFinal();
			ftextoclaro2.write(bloqueclaro2);

			ftextocifrado2.close();
			ftextoclaro2.close();

			System.out.println("*************** FIN DESCIFRADO *****************" );
			return ftextoclaro2.toByteArray();
		}
		return null;
	}
}
