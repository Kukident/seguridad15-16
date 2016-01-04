package Otros;

import java.io.Serializable;

public class Registrar_Documento_Request implements Serializable {
	private String idPropietario;
	private String nombreDoc;
	private String tipoConfidencialidad;
	private byte [] documento;
	private byte [] firmaDoc;
	
	public Registrar_Documento_Request(String idPropietario, String nombreDoc, String tipoConfidencialidad, byte [] documento, String path){
		this.idPropietario=idPropietario;
		this.nombreDoc=nombreDoc;
		this.tipoConfidencialidad=tipoConfidencialidad;
		this.documento=documento;
		try {
			this.firmaDoc=Otros.Firma.Firmar(documento, path,"prueba","SHA1withDSA",1024);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			//ServidorWeb.CifradoDescifrado.main(documento);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getIdPropietario() {
		return idPropietario;
	}

	public void setIdPropietario(String idPropietario) {
		this.idPropietario = idPropietario;
	}

	public String getNombreDoc() {
		return nombreDoc;
	}

	public void setNombreDoc(String nombreDoc) {
		this.nombreDoc = nombreDoc;
	}

	public String getTipoConfidencialidad() {
		return tipoConfidencialidad;
	}

	public void setTipoConfidencialidad(String tipoConfidencialidad) {
		this.tipoConfidencialidad = tipoConfidencialidad;
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
