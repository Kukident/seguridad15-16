package Otros;

import java.io.Serializable;

public class Listar_Documentos_Request implements Serializable{
private String idPropietario;

public Listar_Documentos_Request(String idPropietario) {
	this.idPropietario = idPropietario;
}

public String getIdPropietario() {
	return idPropietario;
}

public void setIdPropietario(String idPropietario) {
	this.idPropietario = idPropietario;
}


}
