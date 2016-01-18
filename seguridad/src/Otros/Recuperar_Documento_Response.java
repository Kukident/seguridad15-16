package Otros;

import java.io.Serializable;

public class Recuperar_Documento_Response implements Serializable{
	private int idRegistro;
	private String selloTemporal;
	private byte [] documento;
	private byte [] firmaRegistrador;
	private int idError;
	
	public Recuperar_Documento_Response(int idError, int idRegistro, String selloTemporal, byte[] documento,
			byte[] firmaRegistrador) {
		this.idRegistro = idRegistro;
		this.selloTemporal = selloTemporal;
		this.documento = documento;
		this.firmaRegistrador = firmaRegistrador;
	}

	public Recuperar_Documento_Response(int idError) {
		this.idError=idError;
	}

	public int getIdError() {
		return idError;
	}

	public void setIdError(int idError) {
		this.idError = idError;
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

	public byte[] getFirmaRegistrador() {
		return firmaRegistrador;
	}

	public void setFirmaRegistrador(byte[] firmaRegistrador) {
		this.firmaRegistrador = firmaRegistrador;
	}
	
	
	
}
