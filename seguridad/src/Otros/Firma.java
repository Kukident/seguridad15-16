package Otros;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

public class Firma {

	public static byte [] Firmar(byte [] file, String path, String pass, String ksentry, String entrypass, String algoritmo, int longitud_clave) throws Exception{
		ByteArrayInputStream fmensaje   = new    ByteArrayInputStream(file);      

		//String 		provider         = "SunJCE";
		//String 		algoritmo        =  "SHA1withDSA";
		//String 		algoritmo_base   =  "RSA";    
		//int    		longitud_clave   =  1024;         
		int    		longbloque;
		byte   		bloque[]         = new byte[longitud_clave];
		long   		filesize         = 0;

		// Variables para el KeyStore

		KeyStore    ks;
		char[]      ks_password  	= pass.toCharArray();
		char[]      key_password 	= entrypass.toCharArray();
		String		ks_file			= path;	    


		// Obtener la clave privada del keystore

		ks = KeyStore.getInstance("JCEKS");

		ks.load(new FileInputStream(ks_file),  ks_password);
		


		KeyStore.PrivateKeyEntry pkEntry = (KeyStore.PrivateKeyEntry)
				ks.getEntry(ksentry,
						new KeyStore.PasswordProtection(key_password));

		PrivateKey privateKey = pkEntry.getPrivateKey();

		/*System.out.println("************************************* ");
		System.out.println("***             FIRMA             *** ");
		System.out.println("************************************* ");

		// Visualizar clave privada
		System.out.println("*** CLAVE PRIVADA ***");
		System.out.println(privateKey);*/

		// Creamos un objeto para firmar/verificar

		Signature signer = Signature.getInstance(algoritmo);

		// Inicializamos el objeto para firmar
		signer.initSign(privateKey);

		// Para firmar primero pasamos el hash al mensaje (metodo "update")
		// y despues firmamos el hash (metodo sign).

		byte[] firma = null;

		while ((longbloque = fmensaje.read(bloque)) > 0) {
			filesize = filesize + longbloque;    		     
			signer.update(bloque,0,longbloque);
		}  

		firma = signer.sign();

		/*double  v = firma.length;
		System.out.println("*** FIRMA: ****");
		for (int i=0; i<firma.length; i++)
		{
			System.out.print(firma[i] + " ");
		}*/

		fmensaje.close();
		return firma;
	}
}
