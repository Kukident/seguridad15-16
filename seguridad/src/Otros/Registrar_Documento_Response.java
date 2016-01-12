package Otros;

import java.io.Serializable;

public class Registrar_Documento_Response implements Serializable{
	private int idRegistro;
	private String selloTemporal;
	private byte [] firmaRegistrador;
	private int idError;
	
	public Registrar_Documento_Response(int idError, int idRegistro, String selloTemporal,
			byte[] firmaRegistrador) {
		this.idRegistro = idRegistro;
		this.selloTemporal = selloTemporal;
		this.firmaRegistrador = firmaRegistrador;
	}
	
	public Registrar_Documento_Response(int idError){
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

	public byte[] getFirmaRegistrador() {
		return firmaRegistrador;
	}

	public void setFirmaRegistrador(byte[] firmaRegistrador) {
		this.firmaRegistrador = firmaRegistrador;
	}
	
	
	
}
