package Otros;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class leerfichero {
	
	public static byte [] leer(String path) throws Exception {
		
		File file = new File(path);
		FileInputStream fin = null;
		try {
			// create FileInputStream object
			fin = new FileInputStream(file);

			byte fileContent[] = new byte[(int)file.length()];
			
			// Reads up to certain bytes of data from this input stream into an array of bytes.
			fin.read(fileContent);
			return fileContent;
		}
		catch (FileNotFoundException e) {
			System.out.println("File not found " + e);
			throw e;
		}
		catch (IOException ioe) {
			System.out.println("Exception while reading file " + ioe);
			throw ioe;
		}
		finally {
			// close the streams using close method
			try {
				if (fin != null) {
					fin.close();
				}
			}
			catch (IOException ioe) {
				System.out.println("Error while closing stream: " + ioe);
			}
		}
	}
}