package ServidorWeb;

/**********************************************************************
	Nombre:
		Firmar_Verificar_Asimetrico_Keystore_v2.0

	Descripcion:
		Codigo JAVA para Firmar y verificar un fichero con el algoritmo RSA,
            tomando las claves privada y publica de un KeyStore.
		Al verificar se obtiene el usuario del Certificado tomado del 			KeyStore.
	Notas de uso:
		Hay que definir una clase Config con el atributo directorioRaiz.
	Fecha:
		17/11/2015
	Autor:
	      Francisco J. Fernandez Masaguer
		ETSI TELECOMUNACION VIGO
		Departamento Ingenieria Telematica
      	email: francisco.fernandez@det.uvigo.es
      Asignatura:
		 Seguridad Curso  2015/2016

***********************************************************/
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;



public class FirmaServidor {

    public static void main(String[] args) throws Exception {
    String directorioRaiz="D:/git/seguridad/src/";
    FileInputStream fmensaje   = new    FileInputStream(directorioRaiz + "nada.txt");      

    String 		provider         = "SunJCE";
    String 		algoritmo        =  "SHA1withRSA";
    String 		algoritmo_base   =  "RSA";    
    int    		longitud_clave   =  1024;         
    int    		longbloque;
    byte   		bloque[]         = new byte[1024];
    long   		filesize         = 0;
    
    // Variables para el KeyStore

	KeyStore    ks;
	char[]      ks_password  	= "147258".toCharArray();
	char[]      key_password 	= "147258".toCharArray();
	String		ks_file			= directorioRaiz + "ServidorWeb/servidor.jce";	    
    
    
    // Obtener la clave privada del keystore
 			
	ks = KeyStore.getInstance("JCEKS");

	ks.load(new FileInputStream(ks_file),  ks_password);

	KeyStore.PrivateKeyEntry pkEntry = (KeyStore.PrivateKeyEntry)
 	      		   						ks.getEntry("servidor",
                                        new KeyStore.PasswordProtection(key_password));
 
    PrivateKey privateKey = pkEntry.getPrivateKey();
    
	System.out.println("************************************* ");
	System.out.println("***             FIRMA             *** ");
	System.out.println("************************************* ");

    // Visualizar clave privada
	System.out.println("*** CLAVE PRIVADA ***");	System.out.println(privateKey);

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
	
	double  v = firma.length;
	
	System.out.println("*** FIRMA: ****");
	for (int i=0; i<firma.length; i++)
	
		System.out.print(firma[i] + " ");
	System.out.println();
	System.out.println();

	fmensaje.close();
	
	/*******************************************************************
	 *       Verificacion
	 ******************************************************************/
	System.out.println("************************************* ");
	System.out.println("    VERIFICACION                    * ");
	System.out.println("************************************* ");

	FileInputStream fmensajeV   = new FileInputStream(directorioRaiz + "nada.txt");        

	// Creamos un objeto para verificar
	Signature verifier=Signature.getInstance(algoritmo);	
	

    // Obtener la clave publica del keystore
    PublicKey   publicKey  = ks.getCertificate("servidor").getPublicKey();

    System.out.println("*** CLAVE PUBLICA ***");	System.out.println(publicKey);
	
    // Obtener el usuario del Certificado tomado del KeyStrore
    byte []   certificadoRaw  = ks.getCertificate("servidor").getEncoded();
    
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
	if (resultado == true)
	    System.out.print("Firma CORRECTA");
	else
		System.out.print("Firma NO correcta");	    
	
	fmensajeV.close();

    }
}


