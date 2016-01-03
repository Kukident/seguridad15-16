package Otros;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class VerificarFirma {
	public static boolean Verificar(byte [] file, String path, byte [] firma, String algoritmo, int longitud_clave) throws Exception{
		String 		provider         = "SunJCE";
	    //String 		algoritmo        =  "SHA1withDSA";
	    String 		algoritmo_base   =  "RSA";    
	    //int    		longitud_clave   =  1024;         
	    int    		longbloque;
	    byte   		bloque[]         = new byte[longitud_clave];
	    long   		filesize         = 0;
	    
	    // Variables para el KeyStore

		KeyStore    ks;
		char[]      ks_password  	= "147258".toCharArray();
		char[]      key_password 	= "147258".toCharArray();
		String		ks_file			= path;	 
		
		
		
		/*******************************************************************
		 *       Verificacion
		 ******************************************************************/
		System.out.println("************************************* ");
		System.out.println("    VERIFICACION                    * ");
		System.out.println("************************************* ");
		
		
		ks = KeyStore.getInstance("JCEKS");

		ks.load(new FileInputStream(ks_file),  ks_password);

		ByteArrayInputStream fmensajeV   = new ByteArrayInputStream(file);        

		// Creamos un objeto para verificar
		Signature verifier=Signature.getInstance(algoritmo);	
		

	    // Obtener la clave publica del keystore
	    PublicKey   publicKey  = ks.getCertificate("cliente").getPublicKey();

	    System.out.println("*** CLAVE PUBLICA ***");
	    System.out.println(publicKey);
		
	    // Obtener el usuario del Certificado tomado del KeyStrore
	    byte []   certificadoRaw  = ks.getCertificate("cliente").getEncoded();
	    
	    ByteArrayInputStream inStream = null;

	    inStream = new ByteArrayInputStream(certificadoRaw);
	    CertificateFactory cf = CertificateFactory.getInstance("X.509");
	    X509Certificate cert = (X509Certificate)cf.generateCertificate(inStream);
	    System.out.println ("Usuario certificado " +
			   						cert.getIssuerX500Principal());       
	       
	    // Inicializamos el objeto para verificar
		
	    verifier.initVerify(publicKey);
	    
	    while ((longbloque = fmensajeV.read(bloque)) > 0) {
	        filesize = filesize + longbloque;    		     
	    	verifier.update(bloque,0,longbloque);
	    }  

		boolean resultado = false;
		
		resultado = verifier.verify(firma);
		
		System.out.println();
		fmensajeV.close();
		if (resultado == true){
		    System.out.print("Firma CORRECTA");
			return true;
		}
		else{
			System.out.print("Firma NO correcta");
			return false;
		}
		
	}
}
