/************************************
 * @(#)Tabla_Simbolos.java          *
 * @Joel A. Sanchez U.  8-810-2189 *
 * @version 1.00 2013/10/5          *
 ************************************/
 
import java.util.ArrayList;
 
class Tabla_Simbolos{
	
	private ArrayList<ArrayList<String>> tabla = new ArrayList<ArrayList<String>>();
	
	public void anadir_fila(int i){
		if(i>0)
			if(tabla.get(i-1).size() < 3)
				insertar_tabla(" ",i-1);
		tabla.add(new ArrayList<String>());
	}
	
	public void eliminar_fila(){
		int f = tabla.size() - 1;
		tabla.remove(f);
	}
	
	public void insertar_tabla(String simbolo, int i){
		tabla.get(i).add(simbolo);
	}
	
	public void ordenar_tabla(){
		int posMin = 0;
		String aux = "";
		for(int f = 0; f < tabla.size()-1; f++){
			posMin = f;
			for(int c = f+1; c < tabla.size(); c++)
				if(tabla.get(posMin).get(1).compareTo(tabla.get(c).get(1).toString()) > 0)
					posMin = c;
			if(posMin != f){
				for( int k = 0; k < tabla.get(f).size(); k++){
					aux = tabla.get(f).get(k).toString();
					tabla.get(f).add(k, tabla.get(posMin).get(k).toString());
					tabla.get(f).remove(k+1);
					tabla.get(posMin).add(k, aux);
					tabla.get(posMin).remove(k+1);
				}
			}
			aux = "";
		}
  	}
  	
  	public int buscar_simbolo(String dato){
  		int ini = 0, fin = tabla.size(), pos = 0, auxPos = 0;
  		while(ini <= fin){
  			pos = (ini + fin)/2;
  			if( tabla.get(pos).get(1).compareTo(dato) == 0 ){
  				auxPos = pos;
  				break;
  			}
  			else if(tabla.get(pos).get(1).compareTo(dato) < 0){
  				ini = pos+1;
  				auxPos = -1;
  			}
  			else if(tabla.get(pos).get(1).compareTo(dato) > 0){
  				fin = pos-1;
  				auxPos = -1;
  			}
  		}
  		return auxPos;
  	}
  	
  	public String getTipo(int pos){
  		return tabla.get(pos).get(0).toString();
  	}
  	
  	public void insertarValor(int pos, String val){
  		tabla.get(pos).add(2, val);
  		tabla.get(pos).remove(3);
  	}
  	
  	public String getValor(int pos){
  		return tabla.get(pos).get(2).toString();
  	}
		
}