/***********************************
 * @(#)Compilador.java             *
 * @Joel A. Sanchez U.  8-810-2189 *
 * @version 1.00 2013/8/21         *
 ***********************************/
 
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class Entorno_Grafico extends JFrame implements ActionListener{
	
	private JDesktopPane escritorio;
	private JTextArea hoja = new JTextArea();
	public String instruccion = "";
    Analizador_Lexico objAL = new Analizador_Lexico();
    Maquina_Virtual objMV = new Maquina_Virtual();
    Tabla_Simbolos objTS;
    JMenuItem nuevo = new JMenuItem("Nuevo archivo");
    JMenuItem compilar = new JMenuItem("Compilar");
    JMenuItem ejecutar = new JMenuItem("Ejecutar");
	
    public Entorno_Grafico() {
    	super("JASUCompiler");
		ImageIcon icono = new ImageIcon(ClassLoader.getSystemResource("logo.png"));
		JMenuBar barra = new JMenuBar();
    	JMenu menuAgregar = new JMenu("Agregar");
    	JMenu menuCompilar = new JMenu("Compilar");
    	JMenu menuEjecutar = new JMenu("Ejecutar");
    	menuAgregar.add(nuevo);
    	menuCompilar.add(compilar);
    	menuEjecutar.add(ejecutar);
    	barra.add(menuAgregar);
    	barra.add(menuCompilar);
    	barra.add(menuEjecutar);
    	setJMenuBar(barra);
    	escritorio = new JDesktopPane();
    	getContentPane().add(escritorio);
    	nuevo.addActionListener(this);
    	compilar.addActionListener(this);
    	ejecutar.addActionListener(this);
		setIconImage(icono.getImage());
    	setSize(600,480);
    	setVisible(true);
    }
    
    public void actionPerformed(ActionEvent e){
    	JInternalFrame marco = new JInternalFrame("Codigo Fuente",true,true,true,true);
    	Container contenedor = marco.getContentPane();
    	if(e.getSource() == nuevo){
    		contenedor.add(new JScrollPane(hoja));
    		marco.setSize(escritorio.getSize());
    		escritorio.add(marco);
    		marco.setVisible(true);
    	}
    	else if(e.getSource() == compilar){
    		instruccion = hoja.getText();
    		objTS = new Tabla_Simbolos();
    		objAL.asignar(instruccion, objTS);
    		objAL.llamarMetodos();
    	}
    	else if(e.getSource() == ejecutar){
    		instruccion = hoja.getText();
    		objMV.asignar(objAL.tokens,objTS);
    	}
    }
    
}