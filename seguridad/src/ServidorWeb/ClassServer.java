package ServidorWeb;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;

import Otros.Listar_Documentos_Request;
import Otros.Listar_Documentos_Response;
import Otros.Recuperar_Documento_Request;
import Otros.Recuperar_Documento_Response;
import Otros.Registrar_Documento_Request;
import Otros.Registrar_Documento_Response;

/************************************************************
 * ClassServer.java -- a simple file server that can serve
 * Http get request in both clear and secure channel
 *
 *  Basado en ClassServer.java del tutorial/rmi
 ************************************************************/
public abstract class ClassServer implements Runnable {

	private ServerSocket server = null;
	private String algoritmoCifrado = null;
	private String path = null;
	private String keyStoreFile = null;
	private String contraseñaKeystore = null;
	private String truststoreFile=null;
	private String contraseñaTruststore=null;
	private int idRegistro=0;
	private String ksentry="servidor";
	private String ksentrypass="147258";
	private static HashMap<Integer, Fichero> BD = new HashMap<Integer, Fichero>();

	/**
	 * Constructs a ClassServer based on <b>ss</b> and
	 * obtains a file's bytecodes using the method <b>getBytes</b>.
	 *
	 */
	protected ClassServer(ServerSocket ss, String algoritmoCifrado, String path,
			String keyStoreFile, String contraseñaKeystore,String truststoreFile,String contraseñaTruststore)
	{
		server = ss;
		this.algoritmoCifrado=algoritmoCifrado;
		this.path=path;
		this.keyStoreFile=keyStoreFile;
		this.contraseñaKeystore=contraseñaKeystore;
		this.truststoreFile=truststoreFile;
		this.contraseñaTruststore=contraseñaTruststore;
		newListener();
	}

	/***************************************************************
	 * run() -- The "listen" thread that accepts a connection to the
	 * server, parses the header to obtain the file name
	 * and sends back the bytes for the file (or error
	 * if the file is not found or the response was malformed).
	 **************************************************************/
	public void run()
	{
		Socket socket;
		String timestamp;


		// accept a connection
		try 
		{
			socket = server.accept();

		} 
		catch (IOException e) {
			System.out.println("Class Server died: " + e.getMessage());
			e.printStackTrace();
			return;
		}

		// create a new thread to accept the next connection
		newListener();

		try {

			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			FileOutputStream fout;
			while (true){
				Object recibido =  in.readObject();
				if (recibido instanceof Registrar_Documento_Request){
					try{
						Registrar_Documento_Request rdr=((Registrar_Documento_Request) recibido);
						timestamp=gettimestamp();
						if (Otros.VerificarFirma.Verificar(rdr.getDocumento(), path+truststoreFile,contraseñaTruststore, rdr.getFirmaDoc(),"SHA1withDSA",1024,rdr.getIdPropietario())){


							ByteArrayOutputStream ops = new ByteArrayOutputStream();
							byte fr [];
							ops.write(rdr.getDocumento());
							ops.write(idRegistro);
							ops.write(timestamp.getBytes());
							ops.write(rdr.getFirmaDoc());
							fr = ops.toByteArray();
							ops.close();


							Fichero doc = new Fichero(rdr.getDocumento(), rdr.getFirmaDoc(), idRegistro, timestamp, 
									Otros.Firma.Firmar(fr, path+keyStoreFile,contraseñaKeystore,ksentry,ksentrypass,"SHA1withRSA",2048), rdr.getIdPropietario(),false);
							if (rdr.getTipoConfidencialidad().toLowerCase().equals("privado")) {
								CifradoDescifrado.cifrar(doc,algoritmoCifrado);//La clase cifrado ya se encarga de obtener el documento
								fout = new FileOutputStream(path+idRegistro+"_"+rdr.getIdPropietario()+".cif");
							}
							else{
								fout = new FileOutputStream(path+idRegistro+"_"+rdr.getIdPropietario()+".sig");
							}
							ObjectOutputStream oos = new ObjectOutputStream(fout);
							oos.writeObject(doc);
							doc.setDocumento(null);//Dejamos vacio el doc para no ocupar memoria??
							doc.setNombreDoc(rdr.getNombreDoc());
							BD.put(idRegistro, doc);

							Registrar_Documento_Response response = new Registrar_Documento_Response(0, doc.getIdRegistro(), doc.getSelloTemporal(), doc.getFirmaRegistrador());
							out.writeObject(response);

							fout.close();
							oos.close();
							idRegistro++;
						}
						else {
							//Devolver respuesta de error
							Registrar_Documento_Response response = new Registrar_Documento_Response(1);
							out.writeObject(response);
						}
					}catch (FileNotFoundException e){
						Registrar_Documento_Response response = new Registrar_Documento_Response(2);
						out.writeObject(response);
					}catch (IOException e){
						Registrar_Documento_Response response = new Registrar_Documento_Response(3);
						out.writeObject(response);
					}catch (KeyStoreException e){
						Registrar_Documento_Response response = new Registrar_Documento_Response(4);
						out.writeObject(response);
					}catch (NoSuchAlgorithmException e){
						Registrar_Documento_Response response = new Registrar_Documento_Response(5);
						out.writeObject(response);
					}catch (CertificateException e){
						Registrar_Documento_Response response = new Registrar_Documento_Response(6);
						out.writeObject(response);
					}catch (InvalidKeyException e){
						Registrar_Documento_Response response = new Registrar_Documento_Response(7);
						out.writeObject(response);
					}catch (SignatureException e){
						Registrar_Documento_Response response = new Registrar_Documento_Response(8);
						out.writeObject(response);
					}catch (UnrecoverableEntryException e){
						Registrar_Documento_Response response = new Registrar_Documento_Response(9);
						out.writeObject(response);
					}catch (NoSuchProviderException e){
						Registrar_Documento_Response response = new Registrar_Documento_Response(10);
						out.writeObject(response);
					}catch (NoSuchPaddingException e){
						Registrar_Documento_Response response = new Registrar_Documento_Response(11);
						out.writeObject(response);
					}catch (ShortBufferException e){
						Registrar_Documento_Response response = new Registrar_Documento_Response(12);
						out.writeObject(response);
					}catch (IllegalBlockSizeException e){
						Registrar_Documento_Response response = new Registrar_Documento_Response(13);
						out.writeObject(response);
					}catch (BadPaddingException e){
						Registrar_Documento_Response response = new Registrar_Documento_Response(14);
						out.writeObject(response);
					}
					//out.close();
				}
				if (recibido instanceof Recuperar_Documento_Request) {
					try {
						Fichero doc;
						Recuperar_Documento_Request rdr = (Recuperar_Documento_Request) recibido;
						if (BD.containsKey(rdr.getIdRegistro())) {
							if (BD.get(rdr.getIdRegistro()).isPrivado()) {
								if (BD.get(rdr.getIdRegistro()).getIdPropietario().equals(rdr.getIdPropietario())){
									//Desciframos y respondemos con el fichero
									System.out.println("Descifrando el documento...");
									doc = (Fichero) deserialize(Otros.leerfichero.leer(path+rdr.getIdRegistro()+"_"+rdr.getIdPropietario()+".cif"));
									Recuperar_Documento_Response response = new Recuperar_Documento_Response(0, rdr.getIdRegistro(), BD.get(rdr.getIdRegistro()).getSelloTemporal(),
											CifradoDescifrado.descifrar(doc.getDocumento(), doc.getParamCifrado(),algoritmoCifrado),BD.get(rdr.getIdRegistro()).getFirmaRegistrador());
									out.writeObject(response);
									System.out.println("Documento descifrado y enviado al cliente");
								}
								else{
									System.out.println("Acceso no permitido");
								}				
							}else{
								//Respondemos con el fichero de vuelta
								doc = (Fichero) deserialize(Otros.leerfichero.leer(path+rdr.getIdRegistro()+"_"+rdr.getIdPropietario()+".sig"));
								Recuperar_Documento_Response response = new Recuperar_Documento_Response(0, rdr.getIdRegistro(), BD.get(rdr.getIdRegistro()).getSelloTemporal(),
										doc.getDocumento(),BD.get(rdr.getIdRegistro()).getFirmaRegistrador());
								out.writeObject(response);
								System.out.println("Documento sin cifrar enviado");
							}
						}
						else{
							System.out.println("Documento no existente");
						}
					}catch (FileNotFoundException e){
						Recuperar_Documento_Response response = new Recuperar_Documento_Response(1);
						out.writeObject(response);
					}catch (IOException e){
						Recuperar_Documento_Response response = new Recuperar_Documento_Response(2);
						out.writeObject(response);
					}catch (ClassNotFoundException e){
						Recuperar_Documento_Response response = new Recuperar_Documento_Response(3);
						out.writeObject(response);
					}catch (KeyStoreException e){
						Recuperar_Documento_Response response = new Recuperar_Documento_Response(4);
						out.writeObject(response);
					}catch (NoSuchAlgorithmException e){
						Recuperar_Documento_Response response = new Recuperar_Documento_Response(5);
						out.writeObject(response);
					}catch (CertificateException e){
						Recuperar_Documento_Response response = new Recuperar_Documento_Response(6);
						out.writeObject(response);
					}catch (UnrecoverableEntryException e){
						Recuperar_Documento_Response response = new Recuperar_Documento_Response(7);
						out.writeObject(response);
					}catch (NoSuchProviderException e){
						Recuperar_Documento_Response response = new Recuperar_Documento_Response(8);
						out.writeObject(response);
					}catch (NoSuchPaddingException e){
						Recuperar_Documento_Response response = new Recuperar_Documento_Response(9);
						out.writeObject(response);
					}catch (InvalidKeyException e){
						Recuperar_Documento_Response response = new Recuperar_Documento_Response(10);
						out.writeObject(response);
					}catch (ShortBufferException e){
						Recuperar_Documento_Response response = new Recuperar_Documento_Response(11);
						out.writeObject(response);
					}catch (IllegalBlockSizeException e){
						Recuperar_Documento_Response response = new Recuperar_Documento_Response(12);
						out.writeObject(response);
					}catch (BadPaddingException e){
						Recuperar_Documento_Response response = new Recuperar_Documento_Response(13);
						out.writeObject(response);
					}catch (InvalidAlgorithmParameterException e){
						Recuperar_Documento_Response response = new Recuperar_Documento_Response(14);
						out.writeObject(response);
					}
				}
				if (recibido instanceof Listar_Documentos_Request) {
					Listar_Documentos_Request ldr = (Listar_Documentos_Request) recibido;
					HashMap<Integer, ArrayList> ListaDocPublicos = new HashMap<Integer, ArrayList>();
					HashMap<Integer, ArrayList> ListaDocPrivados = new HashMap<Integer, ArrayList>();

					for (Entry<Integer, Fichero> entry : BD.entrySet()) {
						Integer key = entry.getKey();
						Fichero value = entry.getValue();
						if (value.isPrivado()) {//Fichero privado
							ArrayList<Object> arraylist = new ArrayList<>();
							arraylist.add(value.getNombreDoc());
							arraylist.add(value.getSelloTemporal());
							ListaDocPrivados.put(key,arraylist);
						}
						else {
							ArrayList<Object> arraylist = new ArrayList<>();
							arraylist.add(value.getNombreDoc());
							arraylist.add(value.getSelloTemporal());
							ListaDocPublicos.put(key,arraylist);
						}

					}

					Listar_Documentos_Response response = new Listar_Documentos_Response(ListaDocPublicos, ListaDocPrivados);
					out.writeObject(response);
				}
			}
		} catch (SocketException e) {
			System.out.println("Conexion con el cliente cerrada " + e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}


	/********************************************************
	 * newListener()
	 * 			Create a new thread to listen.
	 *******************************************************/
	private void newListener()
	{
		(new Thread(this)).start();
	}

	private String gettimestamp(){
		Date timestamp = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy 'at' HH:mm:ss z");
		return sdf.format(timestamp);
	}

	public static byte[] serialize(Object obj) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream os = new ObjectOutputStream(out);
		os.writeObject(obj);
		return out.toByteArray();
	}
	public static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		ObjectInputStream is = new ObjectInputStream(in);
		return is.readObject();
	}
}
