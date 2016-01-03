package ServidorWeb;

import java.io.Serializable;

public class FirmaRegistrador implements Serializable{
	private int idRegistro;
	private String selloTemporal;
	private byte [] documento;
	private byte [] firmaDoc;
	
	public FirmaRegistrador(int idRegistro, String selloTemporal, byte[] documento, byte[] firmaDoc) {
		this.idRegistro = idRegistro;
		this.selloTemporal = selloTemporal;
		this.documento = documento;
		this.firmaDoc = firmaDoc;
	}

	public int getIdRegistro() {
		return idRegistro;
	}

	public void setIdRegistro(int idRegistro) {
		this.idRegistro = idRegistro;
	}

	public String getSelloTemporal() {
		return selloTemporal;
	}

	public void setSelloTemporal(String selloTemporal) {
		this.selloTemporal = selloTemporal;
	}

	public byte[] getDocumento() {
		return documento;
	}

	public void setDocumento(byte[] documento) {
		this.documento = documento;
	}

	public byte[] getFirmaDoc() {
		return firmaDoc;
	}

	public void setFirmaDoc(byte[] firmaDoc) {
		this.firmaDoc = firmaDoc;
	}
	
	
	
}
