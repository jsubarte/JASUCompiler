/***********************************
 * @(#)Compilador.java             *
 * @Joel A. Sanchez U.  8-810-2189 *
 * @version 1.00 2013/8/21         *
 ***********************************/
 
import javax.swing.JOptionPane;
 
class Analizador_Lexico{
	private String instruccion, auxInst;
    public String tokens[][];
    private String lexemas[];
    Tabla_Simbolos objTS;
    private String palRes[] = {"booleano","caracter","double","sino","real","si","fin_mientras",
    						   "entero","mientras","cadena","inicio","fin","leer","imprimir"
    						  };
    
    public void asignar(String inst, Tabla_Simbolos o){
    	instruccion = inst;
    	auxInst = inst;
    	objTS = o;
    }
    
    public void llamarMetodos(){
    	Analizador_Sintaxtico objAS = new Analizador_Sintaxtico();
    	eliminarCometarios_y_Blancos();
    	insertarSeparador();
    	lexemas = instruccion.split(" ");
    	if( automata() ){
    		crearTokens();
    		objAS.asignar(tokens, auxInst, objTS);
    		objAS.llamarMetodos();
    	}
    }
    
    private void eliminarCometarios_y_Blancos(){
    	StringBuffer b = new StringBuffer(instruccion);
    	int aux = 0;
    	for(int x = 0; x < b.length(); x++)
    		if(b.charAt(x) == '#'){
    			aux = x;
    			while(b.charAt(aux) != '\n' && aux < b.length()-1)
    				aux++;
    			b.delete(x,aux+1);
    			x--;
    		}
    	for(int x = 0; x < b.length(); x++)
    		if(b.charAt(x) == '\t'){
    			b.delete(x,x+1);
    			x--;
    		}
    	for(int x = 0; x < b.length(); x++)
    		if(b.charAt(x) == '\n'){
    			b.delete(x,x+1);
    			if( x > 0 )
    				b.insert(x,' ');
    			x--;
    		}
    	for(int x = 0; x < b.length()-1; x++)
    		if(b.charAt(x) == ' ' && b.charAt(x+1) == ' '){
    			x++;
    			b.delete(x,x+1);
    			while(b.charAt(x) == ' ')
    				b.delete(x,x+1);
    		}
    	for(int x = 0; x < b.length()-1; x++)
    		if(b.charAt(x) == '"'){
    			x++;
    			while(b.charAt(x) != '"'){
    				if(b.charAt(x) == ' ')
    					b.replace(x,x+1,"~");
    				x++;
    			}
    		}
    	instruccion = "";
    	instruccion = b.toString();
    }
    
    private void insertarSeparador(){
    	StringBuffer buffer = new StringBuffer(instruccion);
    	char oper[] = {'+','-','*','/','=','<','>','!',';','(',')','{','}',',','"','\''};
    	boolean enc;
    	int comilla = -1;
    	for(int x = 0; x < buffer.length(); x++){
    		for(int g = 0; g < oper.length; g++)
    			if( buffer.charAt(x) == oper[g] ){
    				enc = false;
    				if(buffer.charAt(x) == oper[oper.length-1])
    					comilla++;
    				for(int u = 0; u <= 7; u++){
    					if( (buffer.charAt(x) == oper[u]) && (buffer.charAt(x+1) == oper[4]) ){
    						enc = true;
    						if(buffer.charAt(x-1) != ' '){
    							buffer.insert(x, ' ');
    							x+=2;
    							u=8;
    							g=oper.length;
    							if( x < buffer.length()-1 )
    								if(buffer.charAt(x+1) != ' ')
    									buffer.insert(x+1, ' ');
    						}
    						else{
    							x+=2;
    							u=8;
    							g=oper.length;
    						}
    					}
    				}
    				if( enc == false ){
    					if( x > 0 ){
    						if(buffer.charAt(x-1) != ' '){
    							buffer.insert(x, ' ');
    							x+=2;
    							if( x < buffer.length() ){
    								if(buffer.charAt(x) != ' ')
    									buffer.insert(x, ' ');
    							}
    							else
    								g=oper.length;
    						}
    						else{
    							x++;
    							if( x < buffer.length() ){
    								if(buffer.charAt(x) != ' ')
    									buffer.insert(x, ' ');
    							}
    							else
    								g=oper.length;
    						}
    						if(buffer.charAt(x-1) == oper[oper.length-1] && comilla % 2 == 0 ){
    							while(buffer.charAt(x) != oper[oper.length-1])
    								x++;
    							x--;
    						}
    					}
    					else{
    						if(buffer.charAt(x+1) != ' ')
    							buffer.insert(x+1, ' ');
    					}
    				}
    			}
    	}
    	for(int x = 2; x < buffer.length()-1; x++)
    		if(buffer.charAt(x) == '"' && buffer.charAt(x-2) == '=' ){
    			buffer.delete(x,x+2);
    			while(buffer.charAt(x-1)!='"')
    				x++;
    			buffer.delete(x-1,x+1);
    		}
    	for(int x = 2; x < buffer.length()-1; x++)
    		if(buffer.charAt(x) == '\'' && buffer.charAt(x-2) == '=' ){
    			buffer.delete(x,x+2);
    			while(buffer.charAt(x-1)!='\'')
    				x++;
    			buffer.delete(x-1,x+1);
    		}
    	instruccion = "";
    	instruccion = buffer.toString();
    }
    
    private boolean automata(){
		boolean c = true;
		for(int x = 0; x < lexemas.length; x++){
			if( verificarSimbolo(x) ){
				if(!lexemas[x].matches("[a-z]+[_][0-9]+|[a-z]+[0-9]*|[0-9]*[.]?[0-9]+|(\\w|\\d|\\W|\\s)(\\w|\\d|\\W|\\s)*") ){
					JOptionPane.showMessageDialog(null,"Error lexico\n" + lexemas[x] + " no es un identificador valido","Error",JOptionPane.ERROR_MESSAGE);
					c = false;
					x = lexemas.length;
				}
			}
		}
		return c;
	}
	
	private void crearTokens(){
		tokens = new String[lexemas.length][2];
    	for(int k = 0; k < lexemas.length; k++){
    		if( lexemas[k].matches("[0-9]*[.]?[0-9]+") ){
    			if(tokens[k-1][0].compareTo("=")==0&&tokens[k-2][0].compareTo("Id")==0&&(tokens[k-3][0].compareTo("TipoDato")==0||tokens[k-3][0].compareTo(",")==0))
    				tokens[k][0] = "Valor";
    			else
    				tokens[k][0] = "Num";
    			tokens[k][1] = lexemas[k];
    		}
    		else if( lexemas[k].matches("\\w+") ){
    			if(getPalRes(k)){
    				if( lexemas[k].compareTo("entero") == 0 || lexemas[k].compareTo("real") == 0 || lexemas[k].compareTo("booleano") == 0 || lexemas[k].compareTo("byte") == 0 || lexemas[k].compareTo("caracter") == 0 || lexemas[k].compareTo("cadena") == 0 )
    					tokens[k][0] = "TipoDato";
    				else
    					tokens[k][0] = lexemas[k];
    			}
    			else{
    				if( tokens[k-1][0].compareTo("=") == 0 && (tokens[k-3][0].compareTo("TipoDato") == 0 || tokens[k-3][0].compareTo(",") == 0) )
    					tokens[k][0] = "Valor";
    				else
    					tokens[k][0] = "Id";
    			}
    			tokens[k][1] = lexemas[k];
    		}
    		else if( lexemas[k].matches("\\W{1,2}") ){
    			if( lexemas[k].compareTo("'") == 0 || lexemas[k].compareTo("=") == 0 || lexemas[k].compareTo("+") == 0 || lexemas[k].compareTo("-") == 0 || lexemas[k].compareTo("*") == 0 || lexemas[k].compareTo("/") == 0 || lexemas[k].compareTo(";") == 0 || lexemas[k].compareTo(",") == 0 || lexemas[k].compareTo("(") == 0 || lexemas[k].compareTo(")") == 0 || lexemas[k].compareTo("{") == 0 || lexemas[k].compareTo("}") == 0 || lexemas[k].compareTo("\"") == 0 ){
    				tokens[k][0] = lexemas[k];
    				tokens[k][1] = lexemas[k];
    			}
    			else if( lexemas[k].compareTo("<=") == 0 || lexemas[k].compareTo(">=") == 0 || lexemas[k].compareTo("!=") == 0 || lexemas[k].compareTo("==") == 0 || lexemas[k].compareTo("<") == 0 || lexemas[k].compareTo(">") == 0 || lexemas[k].compareTo("!") == 0 ){
    				tokens[k][0] = "OpRel";
    				tokens[k][1] = lexemas[k];
    			}
    			else if( lexemas[k].compareTo("&&") == 0 || lexemas[k].compareTo("||") == 0 ){
    				tokens[k][0] = "OpLog";
    				tokens[k][1] = lexemas[k];
    			}
    		}
    		else if( lexemas[k].matches("(\\w|\\d|\\W|\\s|[ ])(\\w|\\d|\\W|\\s|[ ])*") ){
    			if(tokens[k-1][0].compareTo("=")==0&&tokens[k-2][0].compareTo("Id")==0&&(tokens[k-3][0].compareTo("TipoDato")==0||tokens[k-3][0].compareTo(",")==0))
    				tokens[k][0] = "Valor";
    			else
    				tokens[k][0] = "Cadena";
    			tokens[k][1] = lexemas[k];
    		}
    	}
    	eliminarTilde();
    }
    
    private boolean getPalRes( int k ){
    	boolean p = false;
    	for(int x = 0; x < palRes.length; x++)
    		if( palRes[x].equals(lexemas[k]) ){
    			p = true;
    			x = palRes.length + 1;
    		}
    	return p;
    }
    
    private boolean verificarSimbolo( int x ){
    	int k = 0;
    	boolean cont = true;
    	String simbolos[] = {"==","!=",">=","<=","!","<",">","+","-","*","/",";","=","(",")","\n","{","}",",","\"","&&","||","'"};
    	while( k < simbolos.length ){
    		if( lexemas[x].compareTo(simbolos[k]) == 0 ){
    			k = simbolos.length;
    			cont = false;
    		}
    		k++;
    	}
    	return cont;
    }
    
    private void eliminarTilde(){
    	StringBuffer temp = new StringBuffer("");
    	for( int f = 0; f < tokens.length; f++ ){
    		if(tokens[f][0].compareTo("Cadena") == 0 || tokens[f][0].compareTo("Valor") == 0){
    			temp.append(tokens[f][1].toString());
    			while(temp.indexOf("~") != -1){
    				temp.replace(temp.indexOf("~"),temp.indexOf("~")+1," ");
    				tokens[f][1] = temp.toString();
    			}
    			temp.delete(0,temp.length());
    		}
    	}
    }
    
}