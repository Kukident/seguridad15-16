package ServidorWeb;

import java.io.Serializable;

public class Documento implements Serializable{
	private byte [] documento;
	private byte [] firmaDoc;
	private int idRegistro;
	private String selloTemporal;
	private byte [] firmaRegistrador;
	
	public Documento(byte[] documento, byte[] firmaDoc, int idRegistro, String selloTemporal, byte[] firmaRegistrador) {
		this.documento = documento;
		this.firmaDoc = firmaDoc;
		this.idRegistro = idRegistro;
		this.selloTemporal = selloTemporal;
		this.firmaRegistrador = firmaRegistrador;
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

	public byte[] getFirmaRegistrador() {
		return firmaRegistrador;
	}

	public void setFirmaRegistrador(byte[] firmaRegistrador) {
		this.firmaRegistrador = firmaRegistrador;
	}
	
	
}
