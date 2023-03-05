/***********************************
 * @(#)Analizador_Semantico.java   *
 * @ Joel A. Sanchez U. 8-810-2189 *
 * @version 1.00 2013/10/22        *
 ***********************************/

import javax.swing.JOptionPane;
import java.util.LinkedList;
import java.util.StringTokenizer;

class Analizador_Semantico {
	private String tokens[][];
	private boolean e = false, err = false;
	public Tabla_Simbolos objTS;
	private String tablas_semanticas[][][]={
										{
										 {"+","entero","real","caracter","cadena"},
										 {"entero","entero","real","cadena","cadena"},
										 {"real","real","real","cadena","cadena"},
										 {"caracter","entero","real","cadena","cadena"},
										 {"cadena","cadena","cadena","cadena","cadena"}
										},
										{
										 {"-","entero","real"},
										 {"entero","entero","real"},
										 {"real","real","real"}
										},
										{
										 {"*","entero","real"},
										 {"entero","entero","real"},
										 {"real","real","real"}
										},
										{
										 {"/","entero","real"},
										 {"entero","real","real"},
										 {"real","real","real"}
										},
										{
										 {"opRel","entero","real","caracter","cadena"},
										 {"entero","booleano","booleano"," "," "},
										 {"real","booleano","booleano"," "," "},
										 {"caracter"," "," ","booleano"," "},
										 {"cadena"," "," "," ","booleano"}
										},
										{
										 {"opLog","booleano"},
										 {"booleano","booleano"}
										},
										{
										 {"=","entero","real","caracter","cadena"},
										 {"entero","entero","real"," "," "},
										 {"real"," ","real"," "," "},
										 {"caracter","entero","real","caracter"," "},
										 {"cadena"," "," "," ","cadena"}
										}
									   };
	
	public void asignar(String t[][], String aux, Tabla_Simbolos o){
		tokens = t;
		objTS = o;
	}
	
	public void llamarMetodos(){
		analisis_estatico();
	}
	
	private void analisis_estatico(){
		segundaPasada(primeraPasada(creartabla()));
	}
	
	private int creartabla(){
		String td = "";
		int f = 0, cont = -1;
		while( f < tokens.length ){
			if( tokens[f][0].compareTo(";") == 0 && tokens[f+1][0].compareTo("TipoDato") != 0 || e ){
				if(tokens[f-1][0].compareTo("Valor") != 0)
					objTS.insertar_tabla(" ",cont);
				break;
			}
			else{
				if( tokens[f][0].compareTo("TipoDato") == 0 ){
					td = tokens[f][1].toString();
					cont++;
					objTS.anadir_fila(cont);
					objTS.insertar_tabla(td, cont);
					while(tokens[f][0].compareTo(";") != 0){
						if(tokens[f][0].compareTo(",") == 0){
							cont++;
							objTS.anadir_fila(cont);
							objTS.insertar_tabla(td, cont);
						}
						else if(tokens[f][0].compareTo("Id") == 0 || tokens[f][0].compareTo("Valor") == 0){
							if(tokens[f][0].compareTo("Id") == 0){
								e = false;
								for(int x = f-1; x > 0; x--)
									if( tokens[f][1].compareTo(tokens[x][1].toString()) == 0 ){
										e = true;
										break;
									}
							}
							if(e == false){
								if(tokens[f][0].compareTo("Valor") == 0){
									if(td.compareTo("entero") == 0){
										if(!tokens[f][1].matches("\\d+|-\\d+"))
												e = true;
									}
									else if(td.compareTo("real") == 0){
										if(!tokens[f][1].matches("\\d+.\\d+|-\\d+.\\d+|\\d+|-\\d+"))
												e = true;
									}
									else if(td.compareTo("caracter") == 0){
										if(!tokens[f][1].matches("\\d?|\\w?|\\s?"))
												e = true;
									}
									else if(td.compareTo("cadena") == 0){
										if(!tokens[f][1].matches("[A-Za-z0-9 ]*"))
												e = true;
									}
									else if(td.compareTo("booleano") == 0){
										if(tokens[f][1].compareTo("verdadero") != 0 && tokens[f][1].compareTo("falso") != 0)
												e = true;
									}
									if(e==false)
										objTS.insertar_tabla(tokens[f][1].toString(), cont);
									else
										JOptionPane.showMessageDialog(null,"1. Tipos de datos incompatibles","Error Semantico",JOptionPane.ERROR_MESSAGE);
								}
								else
									objTS.insertar_tabla(tokens[f][1].toString(), cont);
							}
							else{
								JOptionPane.showMessageDialog(null,"La variable \""+ tokens[f][1].toString() +"\" ya esta declarada","ERROR SEMANTICO",JOptionPane.ERROR_MESSAGE);
								objTS.eliminar_fila();
								cont--;
								break;
							}
						}
						f++;
					}
					f--;
				}
			}
			f++;
		}
		if(e == false){
			objTS.ordenar_tabla();
		}
		return f+1;
	}
	
	private int primeraPasada( int ind_T ){
		int aux = ind_T, pos = 0;
		if(e == false){
			while(ind_T < tokens.length){
				if(tokens[ind_T][0].compareTo("Id") == 0){
					pos = objTS.buscar_simbolo(tokens[ind_T][1].toString());
					if(pos == -1){
						err = true;
  						JOptionPane.showMessageDialog(null,"La variable "+ tokens[ind_T][1].toString() +" no esta declarada","ERROR SEMANTICO",JOptionPane.ERROR_MESSAGE);
  						break;
					}
  				}
				ind_T++;
			}
		}
		return aux;
	}
	
	private void segundaPasada( int ind_T ){
		String inst = "";
		int x = ind_T, pos = 0;
		if(e == false && err == false){
			while(x < tokens.length){
				for( ; x < tokens.length && tokens[x][1].compareTo(";") != 0 && tokens[x][1].compareTo("{") != 0 && tokens[x][1].compareTo("}") != 0 && tokens[x][1].compareTo("sino") != 0 && tokens[x][1].compareTo("fin") != 0 ; x++ ){
					if(tokens[x][0].compareTo("Id") == 0){
						pos = objTS.buscar_simbolo(tokens[x][1].toString());
						inst += objTS.getTipo(pos);
					}
					else{
						if(tokens[x][1].compareTo("leer") == 0 || tokens[x][1].compareTo("imprimir") == 0){
							inst = "";
							if(tokens[x][1].compareTo("imprimir") == 0)
								while(tokens[x][1].compareTo(";") != 0)
									x++;
							else
								x += 2;
							break;
						}
						if(tokens[x][1].matches("\\d+"))
							inst += " entero ";
						else if(tokens[x][1].matches("\\d+.\\d+"))
							inst += " real ";
						else if( tokens[x][1].compareTo("==") == 0 || tokens[x][1].compareTo("!=") == 0 || tokens[x][1].compareTo(">=") == 0 || tokens[x][1].compareTo("<=") == 0 || tokens[x][1].compareTo(">") == 0 || tokens[x][1].compareTo("<") == 0 )
							inst += " opRel ";
						else if( tokens[x][1].compareTo("&&") == 0 || tokens[x][1].compareTo("||") == 0 )
							inst += " opLog ";
						else
							inst += ' ' + tokens[x][1].toString() + ' ';
					}
				}
				if(inst.compareTo("") != 0)
					verificar(aPostFijo(inst));
				if(e == true && err == true)
					break;
				inst = "";
				x++;
			}
		}
		if(e == false && err == false)
			JOptionPane.showMessageDialog(null,"Proceso completado con exito","Analizador Semantico",JOptionPane.INFORMATION_MESSAGE);
	}
	
	private String[] aPostFijo(String inst){
		LinkedList<String> pila = new LinkedList<String>();
		StringTokenizer infijo = new StringTokenizer(inst.toString() + " )");
		String postfijo = "", aux = "";
		pila.addFirst("(");
		while(pila.size() > 0){
			aux = infijo.nextToken();
			if( aux.compareTo("entero") == 0 || aux.compareTo("real") == 0 || aux.compareTo("cadena") == 0 || aux.compareTo("caracter") == 0 || aux.compareTo("booleano") == 0 )
				postfijo += aux + ' ';
			else{
				if(aux.compareTo("(") == 0)
					pila.addFirst(aux);
				else{
					if(aux.compareTo("+") == 0 || aux.compareTo("-") == 0 || aux.compareTo("*") == 0 || aux.compareTo("/") == 0 || aux.compareTo("=") == 0 || aux.compareTo("opLog") == 0 || aux.compareTo("opRel") == 0 ){
						if(aux.compareTo("+") == 0 || aux.compareTo("-") == 0)
							while(pila.getFirst().toString().compareTo("+") == 0 || pila.getFirst().toString().compareTo("-") == 0 || pila.getFirst().toString().compareTo("*") == 0 || pila.getFirst().toString().compareTo("/") == 0){
								postfijo += pila.getFirst().toString() + ' ';
								pila.removeFirst();
							}
						else if(aux.compareTo("*") == 0 || aux.compareTo("/") == 0)
							while(pila.getFirst().toString().compareTo("*") == 0 || pila.getFirst().toString().compareTo("/") == 0){
								postfijo += pila.getFirst().toString() + ' ';
								pila.removeFirst();
							}
						else if(aux.compareTo("opRel") == 0)
							while(pila.getFirst().toString().compareTo("+") == 0 || pila.getFirst().toString().compareTo("-") == 0 || pila.getFirst().toString().compareTo("*") == 0 || pila.getFirst().toString().compareTo("/") == 0 || pila.getFirst().toString().compareTo("opRel") == 0){
								postfijo += pila.getFirst().toString() + ' ';
								pila.removeFirst();
							}
						else if(aux.compareTo("opLog") == 0)
							while(pila.getFirst().toString().compareTo("+") == 0 || pila.getFirst().toString().compareTo("-") == 0 || pila.getFirst().toString().compareTo("*") == 0 || pila.getFirst().toString().compareTo("/") == 0 || pila.getFirst().toString().compareTo("opRel") == 0 || pila.getFirst().toString().compareTo("opLog") == 0){
								postfijo += pila.getFirst().toString() + ' ';
								pila.removeFirst();
							}
						else
							while(pila.getFirst().toString().compareTo("(") != 0){
								postfijo += pila.getFirst().toString() + ' ';
								pila.removeFirst();
							}
						pila.addFirst(aux);
					}
					else{
						if(aux.compareTo(")") == 0){
							while(pila.getFirst().toString().compareTo("(") != 0){
								postfijo += pila.getFirst().toString() + ' ';
								pila.removeFirst();
							}
							pila.removeFirst();
						}
					}
				}
			}
			aux = "";
		}
		return postfijo.split(" ");
	}
	
	private void verificar(String dato[]){
		int t = 0, f = 0, c = 0, x = 0, ini = 0;
		LinkedList<String> lista = new LinkedList<String>();
		invertir(dato);
		for( ; x < dato.length; x++)
			lista.add(dato[x].toString());
		x = 0;
		while(lista.size() > 1){
			if( esOperador(lista.get(x).toString()) ){
				t = getTabla(lista.get(x).toString());
				x++;
				if( esOperando(lista.get(x).toString()) ){
					f = getFila(t,lista.get(x).toString());
					x++;
					if( esOperando(lista.get(x).toString()) ){
						c = getColum(t,lista.get(x).toString());
						if( f != -1 && c != -1 ){
							if(tablas_semanticas[t][f][c].compareTo(" ") != 0){
								lista.subList(ini,x+1).clear();
								lista.add(ini,tablas_semanticas[t][f][c].toString());
							}
							else{
								e = true;
								err = true;
								JOptionPane.showMessageDialog(null,"3. Tipos de datos incompatibles","Error Semantico",JOptionPane.ERROR_MESSAGE);
								break;
							}
						}
						else{
							e = true;
							err = true;
							JOptionPane.showMessageDialog(null,"2. Tipos de datos incompatibles","Error Semantico",JOptionPane.ERROR_MESSAGE);
							break;
						}
						x = 0;
						ini = x;
					}
					else{
						ini++;
						x = ini;
					}	
				}
				else{
					ini++;
					x = ini;
				}
			}
			else{
				ini++;
				x = ini;
			}
		}
	}
	
	private void invertir(String arr[]){
		int f = 0;
		String aux = "";
		for(int f1 = arr.length-1; f1 > f; f1--){
			aux = arr[f1];
			arr[f1] = arr[f];
			arr[f] = aux;
			f++;
		}
	}
	
	private int getTabla(String dato){
		int numT = 0;
		for( ; numT < tablas_semanticas.length; numT++)
			if(tablas_semanticas[numT][0][0].compareTo(dato.toString()) == 0)
				break;
		return numT;
	}
	
	private boolean esOperador(String dato){
		boolean op = false;
		String ope[] = {"+","-","*","/","=","opRel","opLog"};
		for( int i = 0; i < ope.length; i++ )
			if(ope[i].compareTo(dato) == 0){
				op = true;
				break;
			}
		return op;
	}
	
	private boolean esOperando(String dato){
		boolean op = false;
		String ope[] = {"entero","real","caracter","cadena","booleano"};
		for( int i = 0; i < ope.length; i++ )
			if(ope[i].compareTo(dato) == 0){
				op = true;
				break;
			}
		return op;
	}
	
	private int getFila(int t, String dato){
		int f = 0, aux = -1;
		for( ; f < tablas_semanticas[t].length; f++ )
			if( tablas_semanticas[t][f][0].compareTo(dato) == 0 ){
				aux = f;
				break;
			}
		return aux;
	}
	
	private int getColum(int t, String dato){
		int c = 0, aux = -1;
		for( ; c < tablas_semanticas[t][0].length; c++ )
			if( tablas_semanticas[t][0][c].compareTo(dato) == 0 ){
				aux = c;
				break;
			}
		return aux;
	}
	
}