package ServidorWeb;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import Otros.Registrar_Documento_Request;

/************************************************************
 * ClassServer.java -- a simple file server that can serve
 * Http get request in both clear and secure channel
 *
 *  Basado en ClassServer.java del tutorial/rmi
 ************************************************************/
public abstract class ClassServer implements Runnable {

	private ServerSocket server = null;
	private int idRegistro=0;

	/**
	 * Constructs a ClassServer based on <b>ss</b> and
	 * obtains a file's bytecodes using the method <b>getBytes</b>.
	 *
	 */
	protected ClassServer(ServerSocket ss)
	{
		server = ss;
		newListener();
	}

	/****************************************************************
	 * getBytes -- Returns an array of bytes containing the bytes for
	 * the file represented by the argument <b>path</b>.
	 *
	 * @return the bytes for the file
	 * @exception FileNotFoundException if the file corresponding
	 * to <b>path</b> could not be loaded.
	 * @exception IOException if error occurs reading the class
	 ***************************************************************/
	public abstract 
	byte[] getBytes(String path)
			throws IOException, FileNotFoundException;

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

			Object recibido =  in.readObject();
			if (recibido instanceof Registrar_Documento_Request){
				Registrar_Documento_Request rdr=((Registrar_Documento_Request) recibido);
				timestamp=gettimestamp();
				System.out.println(rdr.getNombreDoc());
				if (Otros.VerificarFirma.Verificar(rdr.getDocumento(), "D:/git/seguridad/src/cacerts.jce", rdr.getFirmaDoc(),"SHA1withDSA",1024)){
					
					FirmaRegistrador fr = new FirmaRegistrador(idRegistro++, timestamp, rdr.getDocumento(), rdr.getFirmaDoc());
					Documento doc = new Documento(rdr.getDocumento(), rdr.getFirmaDoc(), idRegistro, timestamp, 
							Otros.Firma.Firmar(serialize(fr), "D:/git/seguridad/src/ServidorWeb/servidor.jce","servidor","SHA1withRSA",2048));
					FileOutputStream fout = new FileOutputStream("D:/git/seguridad/src/hola.hue");
					ObjectOutputStream oos = new ObjectOutputStream(fout);
					oos.writeObject(doc);
				}
				else {
					System.out.println("No");
					//Devolver respuesta de error
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	// Crea dos canales de salida, sobre el socket
	//		- uno binario  (rawOut)
	//		- uno de texto (out)

	/*OutputStream rawOut = socket.getOutputStream();

		    PrintWriter out = new PrintWriter(
										new BufferedWriter(
											new OutputStreamWriter(rawOut)));		    
		    try {
				// Obtener path to class file from header

		    	BufferedReader in =
				    new BufferedReader(
					new InputStreamReader(socket.getInputStream()));

				String path = obtenerPath(in);

				// Recuperar bytecodes

				byte[] bytecodes = getBytes(path);


				// send bytecodes in response (assumes HTTP/1.0 or later)

				try 
				{
				    out.print("HTTP/1.0 200 OK\r\n");
				    out.print("Content-Length: " + bytecodes.length +
						   "\r\n");
				    out.print("Content-Type: text/html\r\n\r\n");
				    out.flush();

				    rawOut.write(bytecodes);
				    rawOut.flush();
				} 
				catch (IOException ie) {
				    ie.printStackTrace();
				    return;
				}

		    } 
		    catch (Exception e) {
				e.printStackTrace();
				// write out error response
				out.println("HTTP/1.0 400 " + e.getMessage() + "\r\n");
				out.println("Content-Type: text/html\r\n\r\n");
				out.flush();
		    }

		} catch (IOException ex) {
		    // eat exception (could log error to log file, but
		    // write out to stdout for now).
		    System.out.println("error writing response: " + ex.getMessage());
		    ex.printStackTrace();

			 } finally {
				 try {
					 socket.close();
				 } catch (IOException e) {
				 }
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

	/*******************************************************
	 * 	obtenerPath 
	 * 			Returns the path to the file obtained from
	 * 			parsing the HTML header.
	 * @return 
	 *******************************************************/
	/*  private static String obtenerPath(BufferedReader in) throws IOException
      {
    		String line = in.readLine();
    		String path = "";

		    System.out.println ("*******" + line);

    		// extract class from GET line
    		if (line.startsWith("GET /")) {
    		    line = line.substring(5, line.length()-1).trim();
    		    int index = line.indexOf(' ');
    		    if (index != -1) {
    			path = line.substring(0, index);
    		    }
    		}

    		// eat the rest of header
    		do {
    		    line = in.readLine();
    		    System.out.println (line);
    		} while ((line.length() != 0) &&
    			 (line.charAt(0) != '\r') && (line.charAt(0) != '\n'));

    		if (path.length() != 0) {
    		    return path;
    		} else {
    		    throw new IOException("Cabecera incorrecta");
    		}
    }*/
	
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
}
