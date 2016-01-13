package Otros;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Listar_Documentos_Response implements Serializable{
	HashMap<Integer, ArrayList> ListaDocPublicos = new HashMap<Integer, ArrayList>();
	HashMap<Integer, ArrayList> ListaDocPrivados = new HashMap<Integer, ArrayList>();
	public Listar_Documentos_Response(HashMap<Integer, ArrayList> listaDocPublicos,
			HashMap<Integer, ArrayList> listaDocPrivados) {
		ListaDocPublicos = listaDocPublicos;
		ListaDocPrivados = listaDocPrivados;
	}
	public HashMap<Integer, ArrayList> getListaDocPublicos() {
		return ListaDocPublicos;
	}
	public void setListaDocPublicos(HashMap<Integer, ArrayList> listaDocPublicos) {
		ListaDocPublicos = listaDocPublicos;
	}
	public HashMap<Integer, ArrayList> getListaDocPrivados() {
		return ListaDocPrivados;
	}
	public void setListaDocPrivados(HashMap<Integer, ArrayList> listaDocPrivados) {
		ListaDocPrivados = listaDocPrivados;
	}
	
}
