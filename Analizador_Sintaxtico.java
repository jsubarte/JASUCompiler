/**********************************
 * @(#)Analizador_Sintaxtico.java *
 * @Joel Sanchez 8-810-2189       *
 * @version 1.00 2013/10/2        *
 **********************************/
 
import javax.swing.JOptionPane;
import java.util.LinkedList;

class Analizador_Sintaxtico{
	private String tokens[][], auxInst;
	private StringBuffer entrada = new StringBuffer("");
	private LinkedList<String> pila = new LinkedList<String>();
	Tabla_Simbolos objTS;
	private String reglasSintacticas[][] = {
											{"Prog","inicio","Cuerpo","fin"," "," "," "," "},
											{"Cuerpo","DeclVars","Instrs"," "," "," "," "," "},
											{"DeclVars","DeclVars","DeclVar"," "," "," "," "," "},
											{"DeclVars","DeclVar"," "," "," "," "," "," "},
											{"DeclVar","TipoDato","Decls",";"," "," "," "," "},
											{"Decls","Decls",",","Decl"," "," "," "," "},
											{"Decls","Decl"," "," "," "," "," "," "},
											{"Decl","Id"," "," "," "," "," "," "},
											{"Decl","Id","=","Valor"," "," "," "," "},
											{"Instrs","Instrs","Instr"," "," "," "," "," "},
											{"Instrs","Instr"," "," "," "," "," "," "},
											{"Instr","O",";"," "," "," "," "," "},
											{"Instr","C"," "," "," "," "," "," "},
											{"Instr","B"," "," "," "," "," "," "},
											{"O","A"," "," "," "," "," "," "},
											{"A","Id","=","E"," "," "," "," "},
											{"E","E","+","T"," "," "," "," "},
											{"E","E","-","T"," "," "," "," "},
											{"E","T"," "," "," "," "," "," "},
											{"T","T","*","F"," "," "," "," "},
											{"T","T","/","F"," "," "," "," "},
											{"T","F"," "," "," "," "," "," "},
											{"F","(","E",")"," "," "," "," "},
											{"F","Num"," "," "," "," "," "," "},
											{"F","Id"," "," "," "," "," "," "},
											{"C","mientras","(","Conds",")","{","Instrs","fin_mientras"},
											{"B","si","(","Conds",")","{","Instrs","}"},
											{"Conds","Conds","OpLog","Conds"," "," "," "," "},
											{"Conds","Cond"," "," "," "," "," "," "},
											{"Conds","(","Conds",")"," "," "," "," "},
											{"Cond","E","OpRel","E"," "," "," "," "},
											{"B","B","sino","{","Instrs","}"," "," "},
											{"E","leer","(",")"," "," "," "," "},
											{"O","imprimir","(","Parametros",")"," "," "," "},
											{"Parametros","Parametros","Parametro"," "," "," "," "," "},
											{"Parametro","Texto","+","E"," "," "," "," "},
											{"Parametro","Texto"," "," "," "," "," "," "},
											{"Parametro","E"," "," "," "," "," "," "},
											{"Parametro","Parametro","+","Parametro"," "," "," "," "},
											{"Parametros","Parametro"," "," "," "," "," "," "},
											{"Texto","\"","Cadena","\""," "," "," "," "}
										 };
	private int  w = -1;
	
	public void asignar(String t[][], String aux,  Tabla_Simbolos o){
		tokens = t;
		auxInst = aux;
		objTS = o;
	}
	
	public void llamarMetodos(){
		creaEntrada();
		analisis_Ascendente();
	}
	
	private void creaEntrada(){
		for( int z = 0; z < tokens.length; z++ ){
			entrada = entrada.append(tokens[z][0].toString());
			if( z != tokens.length - 1 )
				entrada = entrada.append(' ');
		}
	}
	
	private void analisis_Ascendente(){
		boolean roto = false;
		Analizador_Semantico objASem = new Analizador_Semantico();
		int vec[];
		desplazar();
		do{
			if(es_Parte()){
				if(esSimb()){
					if( pila.getFirst().toString().compareTo(";") == 0 || pila.getFirst().toString().compareTo(")") == 0 || pila.getFirst().toString().compareTo("}") == 0 || pila.getFirst().toString().compareTo("DeclVar") == 0 || pila.getFirst().toString().compareTo("Instrs") == 0 || pila.getFirst().toString().compareTo("Conds") == 0 || pila.getFirst().toString().compareTo("fin_mientras") == 0 ){
						if( pila.getFirst().toString().compareTo(")") == 0 ){
							if( nextSimbolo(false).compareTo("{") != 0 ){
								roto = reducir();
								if( roto )
									break;
							}
							else
								desplazar();
						}
						else{
							if( pila.getFirst().toString().compareTo("Instrs") == 0 ){
								if( nextSimbolo(false).compareTo("fin") == 0 ){
									roto = reducir();
									if( roto )
										break;
								}
								else
									desplazar();
							}
							else if( pila.getFirst().toString().compareTo("Conds") == 0 ){
								if( (nextSimbolo(false).compareTo(")") == 0 && pila.get(1).toString().compareTo("(") == 0) || nextSimbolo(false).compareTo("OpLog") == 0 ){
									desplazar();
								}
								else{
									roto = reducir();
									if( roto )
										break;
								}
							}
							else if( pila.getFirst().toString().compareTo("fin_mientras") == 0 ){
								roto = reducir();
								if( roto )
									break;
							}
							else{
								roto = reducir();
								if( roto )
									break;
							}
						}
					}
					else{
						if( entrada.length() > 0 )
							desplazar();
						else{
							roto = reducir();
							if( roto )
								break;
						}
					}
				}
				else{
					vec = new int[repetido(buscar())];
					getfilas(vec);
					if( decision(vec) )
						desplazar();
					else{
						roto = reducir();
						if( roto )
							break;
					}
				}
			}
			else{
				roto = true;
				break;
			}
		}while( reglasSintacticas[0][0].compareTo( (String) pila.getFirst() ) != 0 || entrada.length() > 0 );
		if( roto == false ){
			objASem.asignar(tokens, auxInst, objTS);
			objASem.llamarMetodos();
		}
		else
			JOptionPane.showMessageDialog(null,"Error Sintactico\nPila = "+pila.toString()+"\nEntrada: "+entrada.toString(),"Error",JOptionPane.ERROR_MESSAGE);
		pila = null;
	}
	
	private void desplazar(){
		pila.addFirst(nextSimbolo(true));
	}
	
	private boolean reducir(){
		boolean coinc[] = {false,false,false,false,false,false,false}, auxB = false;
		int k = 0, aux = 0;
		for (int f = 0; f < reglasSintacticas.length; f++)
			for (int c = reglasSintacticas[f].length - 1; c > 0; c--){
				if( f == w ){
					coinc[c-1] = true;
					f++;
				}
				if( reglasSintacticas[f][c].compareTo(" ") != 0 ){
					if( reglasSintacticas[f][c].compareTo( (String) pila.get(k) ) == 0 ){
						if(pila.size()>1)
							k++;
						coinc[c-1] = true;
						if( coinc[0] == true && coinc[1] == true && coinc[2] == true && coinc[3] == true && coinc[4] == true && coinc[5] == true && coinc[6] == true ){
							aux = f;
							f = reglasSintacticas.length + 1;
						}
					}
					else{
						k = 0;
						for( int h = 0; h < 7; h++)
							coinc[h] = false;
						break;
					}
				}
				else{
					coinc[c-1] = true;
				}
			}
		if( coinc[0] == true && coinc[1] == true && coinc[2] == true && coinc[3] == true && coinc[4] == true && coinc[5] == true && coinc[6] == true ){
			if( k > 0 ){
				for( int g = 0; g < k; g++ )
					pila.removeFirst();
			}
			else
				pila.removeFirst();
			pila.addFirst(reglasSintacticas[aux][0]);
			auxB = false;
		}
		else
			auxB = true;
		if( pila.getFirst().toString().compareTo("DeclVars") == 0 && nextSimbolo(false).compareTo("TipoDato") != 0 )
			w = 7;
		return auxB;
	}
	
	private boolean esSimb(){
		boolean t = false;
		String op[] = {"+","-","*","/","(",")","=",";",",","OpRel","OpLog","{","}","TipoDato","Decls","DeclVars","Instrs","DeclVar","inicio","fin","leer","imprimir","Parametros","Conds","fin_mientras"};
		for( int g = 0; g < op.length; g++ )
			if( pila.getFirst().toString().compareTo(op[g]) == 0 ){
				g = op.length + 1;
				t = true;
			}
		return t;
	}
	
	private int buscar(){
		int c = 1, aux = 0;
		while( c < 8 ){
			for( int l = 0; l < reglasSintacticas.length; l++ )
				if( reglasSintacticas[l][c].compareTo( (String) pila.getFirst() ) == 0 ){
					aux = c;
					l = reglasSintacticas.length + 1;
					c = 8;
				}
			c++;
		}
		return aux;
	}
	
	private int repetido(int k){
		int cont = 0;
		for( int f = 0; f < reglasSintacticas.length; f++ )
			if( pila.getFirst().toString().compareTo(reglasSintacticas[f][k].toString()) == 0 )
				cont++;
		return cont;
	}
	
	private boolean es_Parte(){
		boolean a = false;
		for( int c = 1; c < 8; c++ )
			for( int f = 0; f < reglasSintacticas.length; f++ )
				if ( reglasSintacticas[f][c].compareTo( (String) pila.getFirst() ) == 0 ){
					c = 8;
					f = reglasSintacticas.length + 1;
					a = true;
				}
		return a;
	}
	
	private void getfilas(int v[]){
		int c = buscar(), x = 0;
		for ( int f = 0; f < reglasSintacticas.length; f++ )
			if( reglasSintacticas[f][c].compareTo( (String) pila.getFirst() ) == 0 ){
				v[x] = f;
				x++;
			}
	}
	
	private boolean decision(int v[]){
		boolean y = false;
		int c = buscar();
		if( entrada.length() > 0 ){
			for (int x = 0; x < v.length; x++)
				if( nextSimbolo(false).compareTo( reglasSintacticas[v[x]][c+1] ) == 0 || ( nextSimbolo(false).compareTo( ")" ) == 0 && pila.getFirst().toString().compareTo("E") == 0 && pila.get(1).toString().compareTo("OpRel") != 0 && pila.get(1).toString().compareTo("+") != 0 ) || ( entrada.length() > 0 && reglasSintacticas[0][0].compareTo( (String) pila.getFirst() ) == 0 ) ){
					if( nextSimbolo(false).compareTo( "+" ) == 0 && pila.getFirst().toString().compareTo("E") == 0 && (pila.get(1).toString().compareTo("+") == 0||pila.get(1).toString().compareTo("(") == 0) )
						if(pila.get(1).toString().compareTo("(") == 0&&pila.get(2).toString().compareTo("imprimir") == 0)
							y = false;
						else if(pila.get(1).toString().compareTo("+") == 0)
							y = false;
						else
							y = true;
					else
						y = true;
					break;
				}
		}
		return y;
	}
	
	private String nextSimbolo( boolean s ){
		int k = 0;
		String aux = "";
		while( entrada.charAt(k) != ' ' ){
			aux += entrada.charAt(k);
			k++;
			if( k >= entrada.length() )
				break;
		}
		if( s )
			entrada = entrada.delete(0,k+1);
		return aux;
	}
	
}