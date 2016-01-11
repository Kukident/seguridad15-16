package ServidorWeb;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.security.AlgorithmParameters;
import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


public class CifradoDescifrado {
	
	public static void cifrar(Fichero fichero) throws Exception{
		String provider         = "SunJCE";
		ByteArrayInputStream ftextoclaro   = new    ByteArrayInputStream(fichero.getDocumento());
		ByteArrayOutputStream ftextocifrado = new ByteArrayOutputStream();
		ByteArrayOutputStream fparametros   = new ByteArrayOutputStream();


		byte   bloqueclaro[]    = new byte[2024];
		byte   bloquecifrado[]  = new byte[2048];
		String algoritmo        = "AES";
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
	
	public static byte [] descifrar(byte [] cifrado, byte [] param) throws Exception{
		String provider         = "SunJCE";
		String algoritmo        = "AES";
		String transformacion   = "/CBC/PKCS5Padding";
		int    longbloque;
		
		KeyStore    ks;
		char[]      ks_password  	= "147258".toCharArray();
		char[]      key_password 	= "147258".toCharArray();
		String		ks_file			= "D:/git/seguridad/src/ServidorWeb/servidor.jce";
		
		ks = KeyStore.getInstance("JCEKS");

		ks.load(new FileInputStream(ks_file),  ks_password); 

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

}
