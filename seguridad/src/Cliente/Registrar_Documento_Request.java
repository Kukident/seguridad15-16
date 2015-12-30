package Cliente;

public class Registrar_Documento_Request {
	private int idPropietario;
	private String nombreDoc;
	private String tipoConfidencialidad;
	private String [] documento;
	private String [] firmaDoc;
	
	public Registrar_Documento_Request(){
		
	}

	public int getIdPropietario() {
		return idPropietario;
	}

	public void setIdPropietario(int idPropietario) {
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

	public String[] getDocumento() {
		return documento;
	}

	public void setDocumento(String[] documento) {
		this.documento = documento;
	}

	public String[] getFirmaDoc() {
		return firmaDoc;
	}

	public void setFirmaDoc(String[] firmaDoc) {
		this.firmaDoc = firmaDoc;
	}
}
