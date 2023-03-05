/**********************************
 * @(#)Maquina_Virtual.java       *
 * @Joel A. Sanchez U. 8-810-2189 *
 * @version 1.00 2013/11/27       *
 **********************************/

import javax.swing.JOptionPane;
import java.util.LinkedList;
import java.util.StringTokenizer;

class Maquina_Virtual {

    private String instrucciones[][];
    private Tabla_Simbolos objTS;
    private LinkedList<String> pila = new LinkedList<String>();
    private boolean sigI = false, sigW = false;
    
    public void asignar(String[][] progFuente, Tabla_Simbolos o){
    	objTS = o;
    	instrucciones = progFuente;
    	ejecutar();
    }
    
    private void ejecutar(){
    	int aux = 0;
    	while(aux < instrucciones.length){
    		if(instrucciones[aux][0].compareTo(";") == 0 && instrucciones[aux+1][0].compareTo("TipoDato") != 0)
    			break;
    		aux++;
    	}
    	for (int i = aux+1; instrucciones[i][1].compareTo("fin") != 0; i++){
    		if(sigI && instrucciones[i][0].compareTo("sino") == 0)
    			while(instrucciones[i][1].compareTo("fin") != 0){
    				if(instrucciones[i][0].compareTo("}") == 0 && instrucciones[i+1][0].compareTo("sino") != 0){
    					sigI = false;
    					break;
    				}
    				i++;
    			}
    		if(sigW && instrucciones[i][0].compareTo("fin_mientras") == 0){
    			while(instrucciones[i][0].compareTo("mientras") != 0){
    				sigW = false;
    				i--;
    			}
    		}
    		if(instrucciones[i][1].compareTo("leer") == 0)
    			leer(i-2);
    		else if(instrucciones[i][1].compareTo("imprimir") == 0)
    			i = imprimir(i+2);
    		else if(instrucciones[i][1].compareTo("si") == 0)
    			i = si(i+2);
    		else if(instrucciones[i][1].compareTo("mientras") == 0)
    			i = mientras(i+2);
    		else if(instrucciones[i][0].compareTo("Id") == 0 && instrucciones[i+1][0].compareTo("=") == 0  && (instrucciones[i+2][0].compareTo("Id") == 0 || instrucciones[i+2][0].compareTo("Valor") == 0))
    			i = expresion(i);
    	}
    }
    
    private void leer(int i){
    	int a = -1;
    	boolean er = false;
    	int varEntero;
    	char varCaracter;
    	double varReal;
    	String tipo = "", var = "", varCadena = "";
    	a = objTS.buscar_simbolo(instrucciones[i][1].toString());
    	if(a != -1){
    		tipo = objTS.getTipo(a);
    		do{
    			try{
    				if(tipo.compareTo("real") == 0){
    					varReal = Double.parseDouble(JOptionPane.showInputDialog("Ingrese un n�mero real para la variable "+instrucciones[i][1].toString()));
    					var += varReal;
    				}
    				else if(tipo.compareTo("entero") == 0){
    					varEntero = Integer.parseInt(JOptionPane.showInputDialog("Ingrese un n�mero entero para la variable "+instrucciones[i][1].toString()));
    					var += varEntero;
    				}
    				else if(tipo.compareTo("caracter") == 0){
    					varCaracter = (JOptionPane.showInputDialog("Ingrese un caracter para la variable "+instrucciones[i][1].toString())).charAt(0);
    					var += varCaracter;
    				}
    				else if(tipo.compareTo("cadena") == 0){
    					varCadena = JOptionPane.showInputDialog("Ingrese una cadena para la variable "+instrucciones[i][1].toString());
    					var = varCadena;
    				}
    				er = false;
    				objTS.insertarValor(objTS.buscar_simbolo(instrucciones[i][1].toString()),var);
    			}
    			catch(Exception e){
    				JOptionPane.showMessageDialog(null,"Ingreso un tipo de dato invalido.\nPor favor ingrese un dato tipo: "+tipo,"Error",JOptionPane.ERROR_MESSAGE);
    				er = true;
    				var = "";
    			}
    		}while(er);
    	}
    }
    
    private int imprimir(int i){
    	String mensaje = "";
    	while(instrucciones[i][0].compareTo(")") != 0){
    		if(instrucciones[i][0].compareTo("\"") != 0 && instrucciones[i][0].compareTo("+") != 0){
    			if(instrucciones[i][0].compareTo("Id") == 0)
    				mensaje += objTS.getValor(objTS.buscar_simbolo(instrucciones[i][1].toString()));
    			else
    				mensaje += instrucciones[i][1].toString();
    		}
    		i++;
    	}
    	JOptionPane.showMessageDialog(null,mensaje,"Maquina Virtual",JOptionPane.INFORMATION_MESSAGE);
    	return i;
    }
    
    private int si(int i){
    	String cond = "";
    	int valor = -1;
    	while(instrucciones[i][0].compareTo("{") != 0){
    		cond += instrucciones[i][1].toString() + ' ';
    		i++;
    	}
    	valor = evaluador(aPostFijo(cond)+")");
    	if(valor == 0){
    		while(instrucciones[i][0].compareTo("sino") != 0)
    			i++;
    	}
    	else if(valor == 1)
    		sigI = true;
    	return i;
    }
    
    private int mientras(int i){
    	String cond = "";
    	int valor = -1;
    	while(instrucciones[i][0].compareTo("{") != 0){
    		cond += instrucciones[i][1].toString() + ' ';
    		i++;
    	}
    	valor = evaluador(aPostFijo(cond)+")");
    	if(valor == 0){
    		while(instrucciones[i][0].compareTo("fin_mientras") != 0)
    			i++;
    	}
    	else if(valor == 1)
    		sigW = true;
    	return i;
    }
    
    private int expresion(int i){
    	String exp = "";
    	while(instrucciones[i][0].compareTo(";") != 0){
    		exp += instrucciones[i][1].toString() + ' ';
    		i++;
    	}
    	evaluador(aPostFijo(exp)+")");
    	return i;
    }
    
    private int evaluador(String inst){
    	int v = -1, i = 0;
    	String postfijo[] = inst.split(" "), x = "", y = "";
    	while(postfijo[i].compareTo(")") != 0){
    		if(postfijo[i].matches("[A-Za-z]+") || postfijo[i].matches("\\d+|\\d+.\\d+")){
    			if(postfijo[i].matches("[A-Za-z]+")){
    				if(i == 0 && ( postfijo[postfijo.length-2].compareTo("=") == 0 || postfijo[postfijo.length-2].compareTo("+=") == 0 || postfijo[postfijo.length-2].compareTo("-=") == 0 || postfijo[postfijo.length-2].compareTo("*=") == 0 || postfijo[postfijo.length-2].compareTo("/=") == 0 ) )
    					pila.addFirst(postfijo[i]);
    				else
    					pila.addFirst(objTS.getValor(objTS.buscar_simbolo(postfijo[i])));
    			}
    			else
    				pila.addFirst(postfijo[i]);
    		}
    		else if(postfijo[i].compareTo("+") == 0 || postfijo[i].compareTo("-") == 0 || postfijo[i].compareTo("*") == 0 || postfijo[i].compareTo("/") == 0 || postfijo[i].compareTo("==") == 0 || postfijo[i].compareTo("!=") == 0 || postfijo[i].compareTo("<") == 0 || postfijo[i].compareTo(">") == 0 || postfijo[i].compareTo("<=") == 0 || postfijo[i].compareTo(">=") == 0 || postfijo[i].compareTo("=") == 0 || postfijo[i].compareTo("&&") == 0 || postfijo[i].compareTo("||") == 0){
    			x = pila.getFirst().toString();
    			pila.removeFirst();
    			y = pila.getFirst().toString();
    			pila.removeFirst();
    			if(postfijo[i].compareTo("+") == 0 || postfijo[i].compareTo("-") == 0 || postfijo[i].compareTo("*") == 0 || postfijo[i].compareTo("/") == 0)
    				calcular(x,y,postfijo[i].toString());
    			else if(postfijo[i].compareTo("==") == 0 || postfijo[i].compareTo("!=") == 0 || postfijo[i].compareTo("<") == 0 || postfijo[i].compareTo(">") == 0 || postfijo[i].compareTo("<=") == 0 || postfijo[i].compareTo(">=") == 0)
    				v = calcularLog(x,y,postfijo[i].toString(),v);
    			else if(postfijo[i].compareTo("=") == 0)
    				objTS.insertarValor(objTS.buscar_simbolo(y),x);
    			else if(postfijo[i].compareTo("&&") == 0)
    				v = verif_and(x,y,v);
    			else if(postfijo[i].compareTo("||") == 0)
    				v = verif_or(x,y,v);
    		}
    		i++;
    	}
    	return v;
    }
    
    private void calcular(String x, String y, String op){
    	if(x.matches("\\d+|\\d+.\\d+|-\\d+|-\\d+.\\d+") && y.matches("\\d+|\\d+.\\d+|-\\d+|-\\d+.\\d+")){
    		if(op.compareTo("+") == 0){
    			if(x.matches("\\d+") && y.matches("\\d+"))
    				sumar(Integer.parseInt(x),Integer.parseInt(y));
    			else if(x.matches("\\d+") && y.matches("\\d+.\\d+"))
    				sumar(Integer.parseInt(x),Double.parseDouble(y));
    			else if(x.matches("\\d+.\\d+") && y.matches("\\d+"))
    				sumar(Integer.parseInt(y),Double.parseDouble(x));
    			else if(x.matches("\\d+.\\d+") && y.matches("\\d+.\\d+"))
    				sumar(Double.parseDouble(x),Double.parseDouble(y));
    		}
    		else if(op.compareTo("*") == 0){
    			if(x.matches("\\d+") && y.matches("\\d+"))
    				multiplicar(Integer.parseInt(x),Integer.parseInt(y));
    			else if(x.matches("\\d+") && y.matches("\\d+.\\d+"))
    				multiplicar(Integer.parseInt(x),Double.parseDouble(y));
    			else if(x.matches("\\d+.\\d+") && y.matches("\\d+"))
    				multiplicar(Integer.parseInt(y),Double.parseDouble(x));
    			else if(x.matches("\\d+.\\d+") && y.matches("\\d+.\\d+"))
    				multiplicar(Double.parseDouble(x),Double.parseDouble(y));
    		}
    		else if(op.compareTo("-") == 0){
    			if(x.matches("\\d+") && y.matches("\\d+"))
    				restar(Integer.parseInt(x),Integer.parseInt(y));
    			else if(x.matches("\\d+") && y.matches("\\d+.\\d+"))
    				restar(Integer.parseInt(x),Double.parseDouble(y));
    			else if(x.matches("\\d+.\\d+") && y.matches("\\d+"))
    				restar(Double.parseDouble(x),Integer.parseInt(y));
    			else if(x.matches("\\d+.\\d+") && y.matches("\\d+.\\d+"))
    				restar(Double.parseDouble(x),Double.parseDouble(y));
    		}
    		else if(op.compareTo("/") == 0){
    			if(x.matches("\\d+") && y.matches("\\d+"))
    				division(Integer.parseInt(x),Integer.parseInt(y));
    			else if(x.matches("\\d+") && y.matches("\\d+.\\d+"))
    				division(Integer.parseInt(x),Double.parseDouble(y));
    			else if(x.matches("\\d+.\\d+") && y.matches("\\d+"))
    				division(Double.parseDouble(x),Integer.parseInt(y));
    			else if(x.matches("\\d+.\\d+") && y.matches("\\d+.\\d+"))
    				division(Double.parseDouble(x),Double.parseDouble(y));
    		}
    	}
    	else if(x.matches("\\w+") && y.matches("\\w+")){
    		if(op.compareTo("+") == 0){
    			concatenar(x,y);
    		}
    	}
    }
    
    private int calcularLog(String x, String y, String op, int v){
    	if(x.matches("\\d+|\\d+.\\d+|-\\d+|-\\d+.\\d+") && y.matches("\\d+|\\d+.\\d+|-\\d+|-\\d+.\\d+")){
    		if(op.compareTo(">") == 0){
    			v = mayorQue(Double.parseDouble(x),Double.parseDouble(y),v);
    		}
    		else if(op.compareTo("<") == 0){
    			v = menorQue(Double.parseDouble(x),Double.parseDouble(y),v);
    		}
    		else if(op.compareTo(">=") == 0){
    			v = mayor_igualQue(Double.parseDouble(x),Double.parseDouble(y),v);
    		}
    		else if(op.compareTo("<=") == 0){
    			v = menor_igualQue(Double.parseDouble(x),Double.parseDouble(y),v);
    		}
    		else if(op.compareTo("==") == 0){
    			v = igualQue(Double.parseDouble(x),Double.parseDouble(y),v);
    		}
    		else if(op.compareTo("!=") == 0){
    			v = distintoQue(Double.parseDouble(x),Double.parseDouble(y),v);
    		}
    	}
    	else if(x.matches("\\w+") && y.matches("\\w+")){
    		if(op.compareTo(">") == 0){
    			v = mayorCad(x,y,v);
    		}
    		else if(op.compareTo("<") == 0){
    			v = menorCad(x,y,v);
    		}
    		else if(op.compareTo("==") == 0){
    			v = igualCad(x,y,v);
    		}
    		else if(op.compareTo("!=") == 0){
    			v = distintoCad(x,y,v);
    		}
    	}
    	return v;
    }
    
    private String aPostFijo(String inst){
		LinkedList<String> pila = new LinkedList<String>();
		StringTokenizer infijo = new StringTokenizer(inst.toString() + " )");
		String postfijo = "", aux = "";
		pila.addFirst("(");
		while(pila.size() > 0){
			aux = infijo.nextToken();
			if( aux.matches("\\d*|\\w*|\\s*|\\d*.\\d+") )
				postfijo += aux + ' ';
			else{
				if(aux.compareTo("(") == 0)
					pila.addFirst(aux);
				else{
					if(aux.compareTo("+") == 0 || aux.compareTo("-") == 0 || aux.compareTo("*") == 0 || aux.compareTo("/") == 0 || aux.compareTo("=") == 0 || aux.compareTo("&&") == 0 || aux.compareTo("||") == 0 || aux.compareTo("==") == 0 || aux.compareTo("!=") == 0 || aux.compareTo(">=") == 0 || aux.compareTo("<=") == 0 || aux.compareTo(">") == 0 || aux.compareTo("<") == 0 || aux.compareTo("+=") == 0 || aux.compareTo("-=") == 0 || aux.compareTo("*=") == 0 || aux.compareTo("/=") == 0){
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
						else if(aux.compareTo("==") == 0 || aux.compareTo("!=") == 0 || aux.compareTo(">=") == 0 || aux.compareTo("<=") == 0 || aux.compareTo(">") == 0 || aux.compareTo("<") == 0)
							while(pila.getFirst().toString().compareTo("+") == 0 || pila.getFirst().toString().compareTo("-") == 0 || pila.getFirst().toString().compareTo("*") == 0 || pila.getFirst().toString().compareTo("/") == 0 || pila.getFirst().toString().compareTo("==") == 0 || pila.getFirst().toString().compareTo("!=") == 0 || pila.getFirst().toString().compareTo(">=") == 0 || pila.getFirst().toString().compareTo("<=") == 0 || pila.getFirst().toString().compareTo(">") == 0 || pila.getFirst().toString().compareTo("<") == 0){
								postfijo += pila.getFirst().toString() + ' ';
								pila.removeFirst();
							}
						else if(aux.compareTo("&&") == 0 || aux.compareTo("||") == 0)
							while(pila.getFirst().toString().compareTo("+") == 0 || pila.getFirst().toString().compareTo("-") == 0 || pila.getFirst().toString().compareTo("*") == 0 || pila.getFirst().toString().compareTo("/") == 0 || pila.getFirst().toString().compareTo("==") == 0 || pila.getFirst().toString().compareTo("!=") == 0 || pila.getFirst().toString().compareTo(">=") == 0 || pila.getFirst().toString().compareTo("<=") == 0 || pila.getFirst().toString().compareTo(">") == 0 || pila.getFirst().toString().compareTo("<") == 0 || pila.getFirst().toString().compareTo("&&") == 0 || pila.getFirst().toString().compareTo("||") == 0){
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
		return postfijo;
	}
    
    private void sumar(int x, int y){
    	pila.addFirst(String.valueOf(y+x));
    }
    
    private void sumar(double x, double y){
    	pila.addFirst(String.valueOf(y+x));
    }
    
    private void sumar(int x, double y){
    	pila.addFirst(String.valueOf(y+x));
    }
    
    private void multiplicar(int x, int y){
    	pila.addFirst(String.valueOf(y*x));
    }
    
    private void multiplicar(double x, double y){
    	pila.addFirst(String.valueOf(y*x));
    }
    
    private void multiplicar(int x, double y){
    	pila.addFirst(String.valueOf(y*x));
    }
    
    private void restar(int x, int y){
    	pila.addFirst(String.valueOf(y-x));
    }
    
    private void restar(double x, double y){
    	pila.addFirst(String.valueOf(y-x));
    }
    
    private void restar(int x, double y){
    	pila.addFirst(String.valueOf(y-x));
    }
    
    private void restar(double x, int y){
    	pila.addFirst(String.valueOf(y-x));
    }
    
    private void division(int x, int y){
    	pila.addFirst(String.valueOf(y/x));
    }
    
    private void division(double x, double y){
    	pila.addFirst(String.valueOf(y/x));
    }
    
    private void division(int x, double y){
    	pila.addFirst(String.valueOf(y/x));
    }
    
    private void division(double x, int y){
    	pila.addFirst(String.valueOf(y/x));
    }
    
    private int	mayorQue(double x, double y, int v){
    	if( y > x ){
    		v = 1;
    		pila.addFirst("verdadero");
    	}
    	else{
    		v = 0;
    		pila.addFirst("falso");
    	}
    	return v;
    }
    
    private int	menorQue(double x, double y, int v){
    	if( y < x ){
    		v = 1;
    		pila.addFirst("verdadero");
    	}
    	else{
    		v = 0;
    		pila.addFirst("falso");
    	}
    	return v;
    }
    
    private int	mayor_igualQue(double x, double y, int v){
    	if( y >= x ){
    		v = 1;
    		pila.addFirst("verdadero");
    	}
    	else{
    		v = 0;
    		pila.addFirst("falso");
    	}
    	return v;
    }
    
    private int	menor_igualQue(double x, double y, int v){
    	if( y <= x ){
    		v = 1;
    		pila.addFirst("verdadero");
    	}
    	else{
    		v = 0;
    		pila.addFirst("falso");
    	}
    	return v;
    }
    
    private int	igualQue(double x, double y, int v){
    	if( y == x ){
    		v = 1;
    		pila.addFirst("verdadero");
    	}
    	else{
    		v = 0;
    		pila.addFirst("falso");
    	}
    	return v;
    }
    
    private int	distintoQue(double x, double y, int v){
    	if( y != x ){
    		v = 1;
    		pila.addFirst("verdadero");
    	}
    	else{
    		v = 0;
    		pila.addFirst("falso");
    	}
    	return v;
    }
    
    private void concatenar(String x, String y){
    	pila.addFirst(y.concat(x));
    }
    
    private int mayorCad(String x, String y, int v){
    	if(y.compareTo(x) > 0){
    		v = 1;
    		pila.addFirst("verdadero");
    	}
    	else{
    		v = 0;
    		pila.addFirst("falso");
    	}
    	return v;
    }
    
    private int menorCad(String x, String y, int v){
    	if(y.compareTo(x) < 0){
    		v = 1;
    		pila.addFirst("verdadero");
    	}
    	else{
    		v =0;
    		pila.addFirst("falso");
    	}
    	return v;
    }
    
    private int igualCad(String x, String y, int v){
    	if(y.compareTo(x) == 0){
    		v = 1;
    		pila.addFirst("verdadero");
    	}
    	else{
    		v = 0;
    		pila.addFirst("falso");
    	}
    		v = 0;
    	return v;
    }
    
    private int distintoCad(String x, String y, int v){
    	if(y.compareTo(x) != 0){
    		v = 1;
    		pila.addFirst("verdadero");
    	}
    	else{
    		v = 0;
    		pila.addFirst("falso");
    	}
    		v = 0;
    	return v;
    }
    
    private int verif_and(String x, String y, int v){
    	if(y.compareTo("verdadero")==0 && x.compareTo("verdadero") == 0){
    		v = 1;
    		pila.addFirst("verdadero");
    	}
    	else{
    		v = 0;
    		pila.addFirst("falso");
    	}
    	return v;
    }
    
    private int verif_or(String x, String y, int v){
    	if(y.compareTo("verdadero")==0 || x.compareTo("verdadero") == 0){
    		v = 1;
    		pila.addFirst("verdadero");
    	}
    	else{
    		v = 0;
    		pila.addFirst("falso");
    	}
    	return v;
    }
    
}