package Cliente;
/*************************************************************************
      SSLSocketClientWithClientAuth  
                Codigo para cliente autenticado

      SEG Curso 3 Plan Bolonia, Curso 2015/16
      Fecha: 10/11/2015
      Version: 1.0
 *************************************************************************/


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import Otros.Listar_Documentos_Request;
import Otros.Listar_Documentos_Response;
import Otros.Recuperar_Documento_Request;
import Otros.Recuperar_Documento_Response;
import Otros.Registrar_Documento_Request;
import Otros.Registrar_Documento_Response;
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

public class Cliente {

	private static String 	raizCliente ="D:/git/seguridad/raizCliente/";
	private static String	keyStoreFile;
	private static String	contraseñaKeystore;
	private static String	truststoreFile;
	private static String	contraseñaTruststore;
	private static String	ksentry="prueba";
	private static String 	ksentrypass="147258";

	public static void main(String[] args) throws Exception {

		String 	host 		= "127.0.0.1";
		int 	port 		= 9001;
		String idPropietario = null;
		HashMap<Integer, ArrayList> BD = new HashMap<Integer, ArrayList>();

		System.out.println("/****Hola soy un cliente****\\");

		if (args.length != 4) {
			System.out.println(
					"USAGE: java Cliente " +
					"keyStoreFile contraseñaKeystore truststoreFile contraseñaTruststore");
			System.exit(-1);
		}

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

				ks.load(new FileInputStream(raizCliente + keyStoreFile), contraseñaKeystore.toCharArray());

				kmf.init(ks, contraseñaKeystore.toCharArray());

				ctx.init(kmf.getKeyManagers(), null, null);

				factory = ctx.getSocketFactory();


				byte []   certificadoRaw  = ks.getCertificate(ksentry).getEncoded();

				ByteArrayInputStream inStream = null;

				inStream = new ByteArrayInputStream(certificadoRaw);
				CertificateFactory cf = CertificateFactory.getInstance("X.509");
				X509Certificate cert1 = (X509Certificate)cf.generateCertificate(inStream);
				idPropietario=cert1.getIssuerDN().toString();

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
			FileOutputStream fout;
			while (!correcto) {
				System.out.println("Escoge una opcion:");
				System.out.println("1. Registrar Documento");
				System.out.println("2. Recuperar Documento");
				System.out.println("3. Listar Documentos");
				System.out.println("4. Salir");
				if (entrada.hasNextInt()){
					switch (entrada.nextInt()) {
					case 1:
						String [] datos = null;
						System.out.println("Registrar Documento:\nUso: fichero.ext privado|publico");
						entrada.nextLine();
						if (entrada.hasNextLine()) {
							datos=entrada.nextLine().split(" ");
							if (datos.length!=2) {
								System.out.println("Comando incorrecto");
								break;
							}
							else {
								if (!datos[1].equals("privado")) {
									if (!datos[1].equals("publico")) {
										System.out.println("Confidencialidad incorrecta");
										break;		
									}
								}
							}
						}
						try {
							byte [] fichero = leerfichero.leer(raizCliente+"Enviar/"+datos[0]);
							byte [] firma = Otros.Firma.Firmar(fichero, raizCliente+keyStoreFile,contraseñaKeystore,ksentry,ksentrypass,"SHA1withDSA",1024);
							Registrar_Documento_Request registrar = new Registrar_Documento_Request(idPropietario, datos[0], datos[1], fichero,raizCliente+keyStoreFile,firma);
							out.writeObject(registrar);

							Registrar_Documento_Response recibido = (Registrar_Documento_Response) in.readObject();

							if (recibido.getIdError()==0) {


								ByteArrayOutputStream ops = new ByteArrayOutputStream();
								byte fr [];
								ops.write(registrar.getDocumento());
								ops.write(recibido.getIdRegistro());
								ops.write(recibido.getSelloTemporal().getBytes());
								ops.write(registrar.getFirmaDoc());
								fr = ops.toByteArray();
								ops.close();

								if (Otros.VerificarFirma.Verificar(fr, raizCliente+truststoreFile,contraseñaTruststore, recibido.getFirmaRegistrador(), "SHA1withRSA",2048,"servidor")) {
									System.out.println("Documento correctamente registrado");
									MessageDigest digest = MessageDigest.getInstance("SHA-256");
									byte[] hash = digest.digest(registrar.getDocumento());
									ArrayList<byte []> arraylist = new ArrayList<byte []>();
									arraylist.add(hash);
									arraylist.add(firma);
									arraylist.add(registrar.getNombreDoc().getBytes());
									BD.put(recibido.getIdRegistro(), arraylist);
									System.out.println(String.format("Hash documento enviado: "+"%064x", new java.math.BigInteger(1, hash)));
									System.out.println("Borramos el documento enviado del directorio: "+new File(raizCliente+"Enviar/"+datos[0]).delete());
								}
								else{
									System.out.println("Firma registrador incorrecta");
								}
							}
							else{
								System.out.println("ID de error: "+recibido.getIdError());
							}
						}catch (FileNotFoundException e) {
							System.out.println("File not found " + e);
						}
						catch (IOException ioe) {
							System.out.println("Exception while reading file " + ioe);
						}
						catch (Exception e) {
							e.printStackTrace();
						}
						break;

					case 2:
						System.out.println("Recuperar Documento");
						System.out.println("Introduce el idRegistro que quieras recuperar: ");
						int idRegistro = entrada.nextInt();
						Recuperar_Documento_Request recuperar = new Recuperar_Documento_Request(idPropietario, idRegistro);
						out.writeObject(recuperar);
						Recuperar_Documento_Response recibido =  (Recuperar_Documento_Response) in.readObject();
						System.out.println("Leyendo objeto recibido   ");

						if (recibido.getIdError()==0) {

							ByteArrayOutputStream ops = new ByteArrayOutputStream();
							byte fr [];
							ops.write(recibido.getDocumento());
							ops.write(recibido.getIdRegistro());
							ops.write(recibido.getSelloTemporal().getBytes());
							ops.write((byte[]) BD.get(recibido.getIdRegistro()).get(1));
							fr = ops.toByteArray();
							ops.close();


							MessageDigest digest = MessageDigest.getInstance("SHA-256");
							byte[] hash = digest.digest(recibido.getDocumento());
							System.out.println(String.format("Hash documento recibido: "+"%064x", new java.math.BigInteger(1, hash)));


							if (Otros.VerificarFirma.Verificar(fr, raizCliente+truststoreFile,contraseñaTruststore, recibido.getFirmaRegistrador(), "SHA1withRSA",2048,"servidor")) {
								if (Arrays.equals(hash, (byte[]) BD.get(recibido.getIdRegistro()).get(0))) {
									System.out.println("Documento recuperado correctamente");
									fout = new FileOutputStream(raizCliente+"Recibir/"+new String((byte[]) BD.get(recibido.getIdRegistro()).get(2), StandardCharsets.UTF_8));
									fout.write(recibido.getDocumento());
									fout.close();
								}
								else {
									System.out.println("Documento alterado por el registrador");
								}
							}
							else{
								System.out.println("Fallo de firma de registrador");
							}
						}else{
							System.out.println("ID de error: "+recibido.getIdError());
						}
						break;

					case 3:
						System.out.println("Listar Documentos");
						Listar_Documentos_Request listar = new Listar_Documentos_Request(idPropietario);
						out.writeObject(listar);
						Listar_Documentos_Response recibido1 = (Listar_Documentos_Response) in.readObject();
						System.out.println("Publicos: "+recibido1.getListaDocPublicos().toString());
						System.out.println("Privados: "+recibido1.getListaDocPrivados().toString());
						break;

					default:
						System.out.println("Salir");
						System.exit(1);
						break;
					}
					//entrada.close(); //Cerramos flujo de entrada
					//correcto=true;// y salimos del while de seleccion
				}
				else{
					entrada.nextLine();
				}
			}

			in.close();
			out.close();
			socket.close();

		} catch (SSLException e) {
			System.out.println("Conexion con el servidor caida "+e);
			//e.printStackTrace();
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

		System.setProperty("javax.net.ssl.keyStore",         raizCliente + keyStoreFile);
		System.setProperty("javax.net.ssl.keyStoreType",     "JCEKS");
		System.setProperty("javax.net.ssl.keyStorePassword", contraseñaKeystore);
		// Almacen de confianza

		System.setProperty("javax.net.ssl.trustStore",          raizCliente + truststoreFile);
		System.setProperty("javax.net.ssl.trustStoreType",     "JCEKS");
		System.setProperty("javax.net.ssl.trustStorePassword", contraseñaTruststore);

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
