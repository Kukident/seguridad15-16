package Otros;

import java.io.Serializable;

public class Recuperar_Documento_Request implements Serializable{
	private String idPropietario;
	private int idRegistro;
	
	public Recuperar_Documento_Request(String idPropietario, int idRegistro) {
		this.idPropietario = idPropietario;
		this.idRegistro = idRegistro;
	}

	public String getIdPropietario() {
		return idPropietario;
	}

	public void setIdPropietario(String idPropietario) {
		this.idPropietario = idPropietario;
	}

	public int getIdRegistro() {
		return idRegistro;
	}

	public void setIdRegistro(int idRegistro) {
		this.idRegistro = idRegistro;
	}
	
}
