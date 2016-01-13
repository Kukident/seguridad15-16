package ServidorWeb;

import java.io.*;
import java.net.*;
import java.security.KeyStore;

import javax.net.*;
import javax.net.ssl.*;
import javax.security.cert.X509Certificate;

/*********************************************************************
 * ClassFileServer.java -- a simple file server that can server
 * Http get request in both clear and secure channel
 *
 * The ClassFileServer implements a ClassServer that
 * reads files from the file system. See the
 * doc for the "Main" method for how to run this
 * server.
 ********************************************************************/

public class ClassFileServer extends ClassServer {

    protected ClassFileServer(ServerSocket ss) {
		super(ss);
		// TODO Auto-generated constructor stub
	}

    private static int 		port = 9001;
	private static String 	raizServidor = "D:/git/seguridad/raizServidor/";
	private static String	keyStoreFile;
	private static String	contraseñaKeystore;
	private static String	truststoreFile;
	private static String	contraseñaTruststore;


    public static void main(String args[])
    {
    	System.out.println("+++++++++++++++++++++Hola soy un servidor");
    	
		System.out.println(
		    "USAGE: java Registrador keyStoreFile contraseñaKeystore truststoreFile contraseñaTruststore algoritmoCifrado");
	
		try {
			keyStoreFile = args[0];
			contraseñaKeystore = args[1];
			truststoreFile = args[2];
			contraseñaTruststore = args[3];
		} catch (IllegalArgumentException e) {
			System.out.println("USAGE: java SSLSocketClientWithClientAuth " +
					"keyStoreFile contraseñaKeystore truststoreFile contraseñaTruststore");
			System.exit(-1);
		}
		
		definirKeyStores();
	

	
		try {
		    ServerSocketFactory ssf =
		    		ClassFileServer.getServerSocketFactory("TLS");
	
		    ServerSocket ss = ssf.createServerSocket(port);
		    ((SSLServerSocket)ss).setNeedClientAuth(true);
		    /*for (int j = 0; j < ((SSLServerSocket)ss).getEnabledCipherSuites().length; j++) {
	        	   System.out.println(((SSLServerSocket)ss).getEnabledCipherSuites()[j].toString());}*/
		
		    new ClassFileServer(ss);
		
		} catch (IOException e) {
		    System.out.println("Unable to start ClassServer: " +
				       e.getMessage());
		    e.printStackTrace();
		}
    }

    /******************************************************
    	getServerSocketFactory(String type) {}
    *****************************************************/
    private static ServerSocketFactory getServerSocketFactory(String type) {

    if (type.equals("TLS")) 
    {
    	SSLServerSocketFactory ssf = null;
	    
    	try {
			
    		// Establecer el keymanager para la autenticacion del servidor

    		SSLContext 			ctx;
			KeyManagerFactory 	kmf;
			KeyStore 			ks;
	
			ctx = SSLContext.getInstance("TLS");
			kmf = KeyManagerFactory.getInstance("SunX509");

			ks  = KeyStore.getInstance("JCEKS");
			ks.load(new FileInputStream(raizServidor + keyStoreFile), contraseñaKeystore.toCharArray());
			

			kmf.init(ks, contraseñaKeystore.toCharArray());
			
			ctx.init(kmf.getKeyManagers(), null, null);
	
			ssf = ctx.getServerSocketFactory();
			return ssf;
	    } 
	    catch (Exception e) {

	    	   e.printStackTrace();
	    }
	
    }  
    else 
    {
    	System.out.println("Usando la Factoria socket por defecto (no SSL)");

    	return ServerSocketFactory.getDefault();
	}
	
    return null;
    }

    /******************************************************
		definirKeyStores()
    *******************************************************/
	private static void definirKeyStores()
	{
		// Almacen de claves
		System.setProperty("javax.net.ssl.keyStore",         raizServidor + keyStoreFile);
		System.setProperty("javax.net.ssl.keyStoreType",     "JCEKS");
	    System.setProperty("javax.net.ssl.keyStorePassword", contraseñaKeystore);
	    // Almacen de confianza
	    
	    System.setProperty("javax.net.ssl.trustStore",          raizServidor + truststoreFile);
		System.setProperty("javax.net.ssl.trustStoreType",     "JCEKS");
	    System.setProperty("javax.net.ssl.trustStorePassword", contraseñaTruststore);
	}
}


