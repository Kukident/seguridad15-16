package Otros;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

public class VerificarFirma {
	public static boolean Verificar(byte [] file, String path,String pass, byte [] firma, String algoritmo, int longitud_clave, String user) 
			throws KeyStoreException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException, InvalidKeyException, SignatureException{       
	    int    		longbloque;
	    byte   		bloque[]         = new byte[longitud_clave];
	    long   		filesize         = 0;
	    
	    // Variables para el KeyStore

		KeyStore    ks;
		char[]      ks_password  	= pass.toCharArray();
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
		
		//////Conseguimos el alias de la entrada del keystore mediante el idPropietario
		 Enumeration enumeration = ks.aliases();
	        while(enumeration.hasMoreElements()) {
	            String alias = (String)enumeration.nextElement();
	            System.out.println("alias name: " + alias);
	            byte[] certificado = ks.getCertificate(alias).getEncoded();
	            
	            ByteArrayInputStream inStream = null;

				inStream = new ByteArrayInputStream(certificado);
				CertificateFactory cf = CertificateFactory.getInstance("X.509");
				X509Certificate cert1 = (X509Certificate)cf.generateCertificate(inStream);
//			    System.out.println ("Certificado: " +
//						"\n -- Algoritmo Firma  = " + cert1.getSigAlgName() +
//						"\n -- Usuario =" + cert1.getIssuerX500Principal() +
//						"\n -- Parametros Algoritmo =" + cert1.getSigAlgParams() +
//						"\n --  Algoritmo =" + cert1.getPublicKey().getAlgorithm() +
//						"\n --  Codificacion =" + cert1.getPublicKey().getEncoded()					
//		    		);       

				//System.out.println("Longitud_clave: "+cert1.alg);
				if (user.equals(cert1.getSubjectX500Principal().toString())){
					user=alias;
				}
	        }

	    // Obtener la clave publica del keystore
	    PublicKey   publicKey  = ks.getCertificate(user).getPublicKey();

	    System.out.println("*** CLAVE PUBLICA ***");
	    System.out.println(publicKey);
		
	    // Obtener el usuario del Certificado tomado del KeyStrore
	    byte []   certificadoRaw  = ks.getCertificate(user).getEncoded();
	    
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
		inStream.close();
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
