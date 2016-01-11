package Cliente;
/*************************************************************************
      SSLSocketClientWithClientAuth  
                Codigo para cliente autenticado

      SEG Curso 3 Plan Bolonia, Curso 2015/16
      Fecha: 10/11/2015
      Version: 1.0
 *************************************************************************/


import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Scanner;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import Otros.Recuperar_Documento_Request;
import Otros.Recuperar_Documento_Response;
import Otros.Registrar_Documento_Request;
import Otros.leerfichero;

/****************************************************************************
 * This example shows how to set up a key manager to do client
 * authentication if required by server.
 *
 * This program assumes that the client is not inside a firewall.
 * The application can be modified to connect to a server outside
 * the firewall by following SSLSocketClientWithTunneling.java.
 *
 ****************************************************************************/

public class SSLSocketClientWithClientAuth {

	private static String 	raizMios ="D:/git/seguridad/src/Cliente/";

	public static void main(String[] args) throws Exception {

		String 	host 		= null;
		int 	port 		= -1;
		String 	path 		= null;
		char[] 	contraseña 		  = "147258".toCharArray();
		String idPropietario = null;

		System.out.println("----------------Hola soy un cliente");
		definirKeyStores();

		for (int i = 0; i < args.length; i++)
			System.out.println(args[i]);

		if (args.length < 3) {
			System.out.println(
					"USAGE: java SSLSocketClientWithClientAuth " +
					"host port requestedfilepath");
			System.exit(-1);
		}

		try {
			host = args[0];
			port = Integer.parseInt(args[1]);
			path = args[2];
		} catch (IllegalArgumentException e) {
			System.out.println("USAGE: java SSLSocketClientWithClientAuth " +
					"host port requestedfilepath");
			System.exit(-1);
		}

		try {

			/*****************************************************************************
			 * Set up a key manager for client authentication if asked by the server.  
			 * Use the implementation's default TrustStore and secureRandom routines.
			 ****************************************************************************/
			SSLSocketFactory factory = null;
			try {
				SSLContext 			ctx;
				KeyManagerFactory 	kmf;
				KeyStore 			ks;

				ctx = SSLContext.getInstance("TLS");
				kmf = KeyManagerFactory.getInstance("SunX509");
				ks = KeyStore.getInstance("JCEKS");

				ks.load(new FileInputStream(raizMios + "cliente.jce"), contraseña);

				kmf.init(ks, contraseña);

				ctx.init(kmf.getKeyManagers(), null, null);

				factory = ctx.getSocketFactory();


				byte []   certificadoRaw  = ks.getCertificate("prueba").getEncoded();

				ByteArrayInputStream inStream = null;

				inStream = new ByteArrayInputStream(certificadoRaw);
				CertificateFactory cf = CertificateFactory.getInstance("X.509");
				X509Certificate cert1 = (X509Certificate)cf.generateCertificate(inStream);
				idPropietario=cert1.getIssuerDN().toString();
				System.out.println ("Usuario certificado " +
						idPropietario);  




				/*********************************************************************
				 * Suites SSL del contexto
				 *********************************************************************/
				// Suites disponibles

				/*System.out.println ("******** CypherSuites Disponibles **********");

	   	   	    String[] cipherSuites = factory.getSupportedCipherSuites();
	   	   	    for (int i=0; i<cipherSuites.length; i++)
	   	       		System.out.println (cipherSuites[i]);	    

	   	   	    // Suites habilitadas por defecto

	   	   	    System.out.println ("****** CypherSuites Habilitadas por defecto **********");

	   	   	    String[] cipherSuitesDef = factory.getDefaultCipherSuites();
	   	   	    for (int i=0; i<cipherSuitesDef.length; i++)
	   	       		System.out.println (cipherSuitesDef[i]);*/


			} catch (Exception e) {
				throw new IOException(e.getMessage());
			}

			SSLSocket socket = (SSLSocket)factory.createSocket(host, port);

			//socket.setEnabledCipherSuites(seleccionarsuite(socket)); //Dejar comentado para evitar seleccionar cada vez la suite

			/*********************************************************************
			 * send http request
			 *
			 * See SSLSocketClient.java for more information about why
			 * there is a forced handshake here when using PrintWriters.
			 ********************************************************************/


			System.out.println ("Comienzo SSL Handshake -- Cliente y Server Autenticados");

			socket.startHandshake();	    

			System.out.println ("Fin OK SSL Handshake");

			/*Menu del programa*/
			boolean correcto=false;
			Scanner entrada = new Scanner(System.in);
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			while (!correcto) {
				System.out.println("Escoge una opcion:");
				System.out.println("1. Registrar Documento");
				System.out.println("2. Recuperar Documento");
				System.out.println("3. Salir");
				if (entrada.hasNextInt()){
					switch (entrada.nextInt()) {
					case 1:
						System.out.println("Registrar Documento");
						try {
							Registrar_Documento_Request registrar = new Registrar_Documento_Request(idPropietario, "HUEHUEHUE", "publico", leerfichero.leer(raizMios+"imagen.jpg"),raizMios+"Cliente.jce");
							out.writeObject(registrar);
						} catch (Exception e) {
							e.printStackTrace();
						}
						break;

					case 2:
						System.out.println("Recuperar Documento");
						Recuperar_Documento_Request recuperar = new Recuperar_Documento_Request(idPropietario, 1);
						out.writeObject(recuperar);
						Recuperar_Documento_Response recibido =  (Recuperar_Documento_Response) in.readObject();
						System.out.println("Leyendo objeto recibido   "+recibido.getSelloTemporal());
						break;

					default:
						System.out.println("Salir");
						System.exit(1);
						break;
					}
					entrada.close(); //Cerramos flujo de entrada
					correcto=true;// y salimos del while de seleccion
				}
				else{
					entrada.nextLine();
				}
			}


			/*PrintWriter out = new PrintWriter(
					new BufferedWriter(
							new OutputStreamWriter(
									socket.getOutputStream())));
			out.println("GET " + "/" + path + " HTTP/1.0");
			out.println();
			out.flush();

			/*
			 * Make sure there were no surprises
			 */
			/*	if (out.checkError())
				System.out.println(
						"SSLSocketClient: java.io.PrintWriter error");

			/* read response */
			/*BufferedReader in = new BufferedReader(
					new InputStreamReader(
							socket.getInputStream()));

			String inputLine;

			while ((inputLine = in.readLine()) != null)
				System.out.println(inputLine);*/

			in.close();
			out.close();
			socket.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/******************************************************
		definirKeyStores()
	 *******************************************************/
	private static void definirKeyStores()
	{
		// Almacen de claves

		System.setProperty("javax.net.ssl.keyStore",         raizMios + "cliente.jce");
		System.setProperty("javax.net.ssl.keyStoreType",     "JCEKS");
		System.setProperty("javax.net.ssl.keyStorePassword", "147258");
		System.out.println("si");
		// Almacen de confianza

		System.setProperty("javax.net.ssl.trustStore",          "D:/git/seguridad/src/" + "cacerts.jce");
		System.setProperty("javax.net.ssl.trustStoreType",     "JCEKS");
		System.setProperty("javax.net.ssl.trustStorePassword", "147258");

	}

	private static String [] seleccionarsuite(SSLSocket socket){
		/*Seleccionamos una cipher suite*/
		boolean correcto=false;
		String suites[]= new String[1];
		while (!correcto) {
			System.out.println("Seleccionar cipher suite");
			for (int j = 0; j < socket.getEnabledCipherSuites().length; j++) {
				System.out.println(j+". "+socket.getEnabledCipherSuites()[j].toString());
			}
			Scanner entrada = new Scanner(System.in);
			if (entrada.hasNextInt()) {
				int seleccion=entrada.nextInt();
				if (seleccion>=0&&seleccion<=socket.getEnabledCipherSuites().length) {
					suites[0]=socket.getEnabledCipherSuites()[seleccion].toString();
					System.out.println("Suite escogida: "+suites[0]);
					entrada.close();
					break;
				}
			}
			System.out.println("Opcion no valida, vuelva a intentarlo");
		}
		return suites;
	}
}
