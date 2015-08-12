import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;

/**
 * 
 * @author Edu2
 *
 */
public class PanelJuegoPrueba extends JPanel {

	
	String pintadas=",", seleccionadas=",";
	int comprobar=0;
	int delay=100;
	Timer timer1 = new Timer(delay, null);
	boolean something = false, fin=false;
	int i=0;
	BotonJuego bj; //todos los MouseListener sse generan con el mismo id para facilitar su eliminaci��n y sobrecargar menos el sistema
	VentanaJuego vj;
	
	/**********************  --  DIFICULTAD  --  ************************/
	
	//array bidimensional para establecer niveles de dificultad (número de cuadros generados y n��mero de botones por ronda)
	int [] dificultad = new int []{4,5,6,7,8};
	int dif;    //valor que recoge y establece el tama��o de la cuadr��cula
	
	/********************************************************************/
	
	//arrays de control de casillas:
	Integer [] cuadrosMarcados;//declaro el array como Integer y no como int para poder comparar los valores del array vac��o como null
	Integer [] cuadrosPintados;
	Integer [] noRepeatBucle; //hace referencia al n��mero de casillas pintadas, para controlar que no se pintan repetidas en una misma ronda
	
	//paneles que har��n de bot��n, la cantidad depende de la dificultad
	JPanel [] botonPanel;
	
	//etiqueta en la que se carga el gif
	JLabel gif;
	
	static int handicap, handicapConsumido=0;
	
	/**
	 * Devuelve el valor de la variable handicap
	 * @return handicap
	 */
	public int getHandicap() {
		return handicap;
	}

	/**
	 * Devuelve el valor de la variable handicapConsumido
	 * @return handicapConsumido
	 */
	public int getHandicapConsumido() {
		return handicapConsumido;
	}

	/**
	 * Permite modificar el valor de la variable handcapConsumido
	 * @param handicapConsumido
	 */
	public void setHandicapConsumido(int handicapConsumido) {
		this.handicapConsumido = handicapConsumido;
	}

	/**
	 * Constructor sobrecargado
	 * @param v
	 * @throws IOException
	 * @see #prinFileFecha()
	 * @see #getFecha()
	 * @see #ejecuta()
	 */
	PanelJuegoPrueba(VentanaJuego v) throws IOException{
		super();
		
		vj = v;
		//almacena el nivel de dificultad seleccionado
		dif=Integer.parseInt(vj.lb.l.niveles.getSelectedItem().toString());
		//almacena la cantidad de handicap seleccionada
		if (vj.lb.l.getHayHandicap())
			handicap = Integer.parseInt(vj.lb.l.handicap.getSelectedItem().toString().substring(0, 1));
		else handicap = 0;
		
		bj = new BotonJuego(this);
		
		//crea la matriz en función de los parámetros seleccionados de dificultad y prepara los arrays de almacenamiento de información
		this.setLayout(new GridLayout(dificultad[dif],dificultad[dif], 2,2));
		botonPanel = new JPanel[dificultad[dif]*dificultad[dif]];
		noRepeatBucle = new Integer[dificultad[dif]];
		cuadrosPintados = new Integer[dificultad[dif]*dificultad[dif]];
		cuadrosMarcados = new Integer[dificultad[dif]*dificultad[dif]];
		
		//crea el panel
		for (int cuadro = 0 ; cuadro<dificultad[dif]*dificultad[dif] ; cuadro++ ){
			botonPanel[cuadro] = new JPanel(); //no sé!!!
			botonPanel[cuadro].setBackground(Color.WHITE);
			this.add(botonPanel[cuadro]);
		}
		
		//almacena hora de comienzo de partida
		printFileFecha(getFecha());
		
		//motor de la partida
		ejecuta();
		
	}
	
	/**
	 * Mediante un temporizador crea la animación que ejecuta el juego, en periodos de tiempo determinados desbloquea un número de casillas adecuado al nivel,
	 * y posteriormente los limpia. Repite la acción tantas veces como haga falta hasta que se hayan desbloqueado todas las casillas al menos una vez.
	 * Para si se selecciona una casilla previamente seleccionada y este proceso se repite hasta sobrepasar el límite permitido por el handicap
	 * @see #pintar()
	 * @see #comprobarPintados()
	 * @see #vaciarPaneles()
	 * @see #comprobarSiHayBotonesActivos()
	 * @see #almacenarDatosPartidaPrueba()
	 */
	private void ejecuta(){
		timer1 = new Timer(delay, new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if (something) {
					pintar();
					something = !something;
				} else {
					comprobar = comprobarPintados();
					if (comprobar==1){
						vaciarPaneles();
						something = !something;
						i++;
					} else {
						boolean n = comprobarSiHayBotonesActivos();
						if (n==true){
							vaciarPaneles();
						}
						fin=true;
					}
				} 
			
				if (fin==false){
					if (something==false) 
						timer1.setInitialDelay(3000+(dificultad[dif]*1000)); 
					else 
						timer1.setInitialDelay(1000);
	            		
					timer1.stop();
					timer1.start();
				}else{
					if (handicapConsumido<handicap){
						handicapConsumido++;
						fin=false;
					}else{
						timer1.stop();
						try {
							almacenarDatosPartidaPrueba();
						} catch (ClassNotFoundException | SQLException
								| IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						vj.lb.l.ci.juego.setVisible(false);
						vj.lb.l.ci.login.setVisible(true);
						vj.lb.l.ci.login.validate();
						vj.lb.l.ci.login.repaint();
						vj.lb.l.ci.displaySTATS.setVisible(true);
						vj.lb.l.ci.displaySTATS.setEnabled(true);
						vj.lb.l.ci.setPreferredSize(new Dimension(370, 400));
						vj.lb.l.ci.toolbar2.setVisible(true);
						vj.lb.l.ci.cp.almacenaPuntos();
						vj.lb.l.ci.validate();
						vj.lb.l.ci.repaint();
						vj.lb.l.ci.pack();
					}
				}
			}                   
		});
		//da comienzo a la animaci�n
		if (this.isVisible())
			timer1.start();
		validate();
		repaint();
	}
	
	/**
	 * Método que pinta las casillas
	 * @see #comprobarPintados()
	 */
	private void pintar(){
		Integer rand,cont=0;
		boolean repetido=false;
		if (comprobarPintados()==1)
		for (int i=0;i<dificultad[dif];i++){
			rand = 0 + (int) (Math.random()*dificultad[dif]*dificultad[dif]);
			//si la posici�n no ha salido en el rand pinto, sino creo otra posici�n. tengo que cambiarlo para permitir repeticiones, pero controladas
			
			//comprobar que las posiciones pintadas en esta ejecuci��n del for no se repiten con noRepeatBucle
			for (int j=0;j<dificultad[dif] && j<=i && repetido==false;j++){
				if (noRepeatBucle[j]!=rand || noRepeatBucle[j]==null){
					repetido = false;
				}else
					repetido=true;
			}
			if (repetido==false){
				noRepeatBucle[i]=rand;
				if (cuadrosPintados[rand]!=null)
					if (comprobarPintados()==0){
						cont=0;
					}else{
						cont++;
					}
			}
			
			//permite que solo se repitan las dos primeras casillas creadas mientras queden casillas v��rgenes (nunca pintadas)
			if (cont<2 && repetido==false){ 
				gif = new JLabel();
				URL imgURL = PanelJuegoPrueba.class.getResource("/candado.gif");
				ImageIcon imageIcon = new ImageIcon(imgURL);
				gif.setIcon(imageIcon); 
				botonPanel[rand].add(gif);
				botonPanel[rand].addMouseListener(bj);
				botonPanel[rand].setBackground(Color.LIGHT_GRAY);
				botonPanel[rand].setBorder(new BevelBorder(BevelBorder.LOWERED));
				botonPanel[rand].validate();
				botonPanel[rand].repaint();
				
				//cambio el valor de la posici��n para indicar que el n��mero que corresponde a la posici��n ya ha salido
				cuadrosPintados[rand]=1;
				
				//almacena las casillas pintadas en orden, para guardar record de la partida
				if (pintadas.equals(","))
					pintadas = rand+"," ;
				else
					pintadas += rand+"," ;
			}else{
				i--;
				cont--;
				repetido=false;
			}
		}
		
	}
	
	/**
	 * Devuelve 0 si se han pintado todas las casillas y si queda alguna casilla por pintar el n��mero de casillas que quedan
	 * @return restantes devuelve si hay paneles sin pintar
	 */
	public int comprobarPintados(){
		int restantes=0;
		for (int i=0;i<cuadrosPintados.length && restantes == 0;i++)
			if (cuadrosPintados[i]==null)
				restantes=1;
		return restantes;
	}
	
	/**
	 * almacena los datos de la partida (orden de pintado de los cuadros y fecha y hora de inicio de partida)
	 * @throws SQLException
	 * @throws IOException
	 * @throws ClassNotFoundException 
	 */
	private void almacenarDatosPartidaPrueba() throws SQLException, IOException, ClassNotFoundException{
		Statement stm = vj.lb.l.ci.cp.conectarBD();
		stm.executeUpdate("INSERT INTO Partidas(Secuencia_paneles, Secuencia_seleccionados, Fecha_juego, IDJugador, Dificultad) VALUES ('"+pintadas+"', '"+seleccionadas+"', '"+readFileFecha()+"', '"+vj.lb.getIDJugador()+"', '"+Integer.parseInt(vj.lb.l.niveles.getSelectedItem().toString())+"')");
	}
	
	/**
	 * elimina los elementos añadidos a los paneles
	 */
	public void vaciarPaneles(){
		for(int i=0; i<dificultad[dif]*dificultad[dif]; i++){
			if (botonPanel[i].getBorder()!=null){
				botonPanel[i].removeAll();
				botonPanel[i].removeMouseListener(bj);
				botonPanel[i].setBorder(null);
				botonPanel[i].validate();
				botonPanel[i].repaint();
				botonPanel[i].setBackground(Color.WHITE);
			}
		}
	}
	
	/**
	 * devuelve verdadero si hay alg��n MouseListener activo
	 * @return ret booleano que devuelve si hay o no MouseListener
	 */
	public boolean comprobarSiHayBotonesActivos(){
		boolean ret=false;
		for(int i=0; i<dificultad[dif]*dificultad[dif] && ret==false; i++){
			if (botonPanel[i].getMouseListeners()==null){
				ret = false;
			} else ret = true;
		}
		return ret;
	}
	
	/**
	 * recoge la fecha del sistema
	 * @return dateFormat.format(cal.getTime()) : la fecha del momento en que se activa el m��todo
	 */
	private String getFecha(){
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		return dateFormat.format(cal.getTime());
	}
	
	/**
	 * almacena en un fichero la fecha de inicio de la partida
	 * @param fechaInicio la fecha que recoge del sistema
	 * @throws IOException
	 * @see #getFecha()
	 */
	private void printFileFecha(String fechaInicio) throws IOException{
		FileWriter fw = new FileWriter("FechaInicioPartidaPrueba");
		PrintWriter pw = new PrintWriter(fw);
		pw.println(fechaInicio);
		pw.close();
		fw.close();
	}
	
	/**
	 * recoge los datos almacenados en el txt, la fecha de inicio de partida
	 * @return fecha
	 * @throws IOException
	 */
	private String readFileFecha() throws IOException{
		FileReader fr = new FileReader("FechaInicioPartidaPrueba");
		BufferedReader br = new BufferedReader(fr);
		String fecha = br.readLine();
		br.close();
		fr.close();
		return fecha;
	}
}
