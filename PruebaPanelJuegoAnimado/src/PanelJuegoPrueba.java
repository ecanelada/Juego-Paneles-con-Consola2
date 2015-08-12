import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;


public class PanelJuegoPrueba extends JPanel {

	
	String pintadas=",", seleccionadas=",";
	int comprobar=0;
	int delay=100;
	Timer timer1 = new Timer(delay, null);
	boolean something = false, fin=false;
	int i=0;
	BotonJuego bj = new BotonJuego(this); //todos los MouseListener sse generan con el mismo id para facilitar su eliminación y sobrecargar menos el sistema
	
	/**********************  --  DIFICULTAD  --  ************************/
	
	//array bidimensional para establecer niveles de dificultad (número de cuadros generados y número de botones por ronda)
	int [] dificultad = new int []{4,5,6,7,8};
	int dif=0;    //valor que recoge y establece el tamaño de la cuadrícula
	
	/********************************************************************/
	
	//attays de control de casillas:
	Integer [] cuadrosMarcados = new Integer[dificultad[dif]*dificultad[dif]];//declaro el array como Integer y no como int para poder comparar los valores del array vacío como null
	Integer [] cuadrosPintados = new Integer[dificultad[dif]*dificultad[dif]];
	Integer [] noRepeatBucle = new Integer[dificultad[dif]]; //hace referencia al número de casillas pintadas, para controlar que no se pintan repetidas en una misma ronda
	
	//paneles que harán de botón, la cantidad depende de la dificultad
	JPanel [] botonPanel = new JPanel[dificultad[dif]*dificultad[dif]];
	
	PanelJuegoPrueba() throws IOException{
		super();

		this.setLayout(new GridLayout(dificultad[dif],dificultad[dif], 2,2));
		
		for (int cuadro = 0 ; cuadro<dificultad[dif]*dificultad[dif] ; cuadro++ ){
			botonPanel[cuadro] = new JPanel();
			botonPanel[cuadro].setBackground(Color.WHITE);
			this.add(botonPanel[cuadro]);
		}
		
		printFileFecha(getFecha());
		ejecuta();
		
	}
	
	private void ejecuta(){
		timer1 = new Timer(delay, new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if (something) {
					pintar();
					something = !something;
					System.out.println("a "+i);
				} else {
					comprobar = comprobarPintados();
					if (comprobar==1){
						vaciarPaneles();
						something = !something;
						System.out.println("b "+i);
						i++;
					} else {
						boolean n = comprobarSiHayBotonesActivos();
						if (n==true){
							try {
								vaciarPaneles();
								almacenarDatosPartidaPrueba();
							} catch (ClassNotFoundException | SQLException | IOException e1) {
								e1.printStackTrace();
							}
							System.out.println("Partida acabada");
						}
						//System.out.println("Fin final");
						//timer1.stop();
						fin=true;
					}
				} 
			
				if (fin==false){
					if (something==false) 
						timer1.setInitialDelay(3000+(dificultad[dif]*1000)); 
					else 
						timer1.setInitialDelay(1000);
	            		
					timer1.stop();timer1.start();
				}else{
					timer1.stop();
					System.out.println("ERROR! - Tu memoria te traicionó");
					try {
						almacenarDatosPartidaPrueba();
					} catch (ClassNotFoundException | SQLException
							| IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}                   
		});                                             
		timer1.start();
		validate();
		repaint();
	}
	
	private void pintar(){
		Integer rand,cont=0;
		boolean repetido=false;
		if (comprobarPintados()==1)
		for (int i=0;i<dificultad[dif];i++){
			rand = 0 + (int) (Math.random()*dificultad[dif]*dificultad[dif]);
			//si la posici��n no ha salido en el rand pinto, sino creo otra posici��n. tengo que cambiarlo para permitir repeticiones, pero controladas
			
			//comprobar que las posiciones pintadas en esta ejecución del for no se repiten con noRepeatBucle
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
						System.out.println(rand+": cont=0");
					}else{
						cont++;
						System.out.println(rand+": cont++");
					}
			}
			
			//permite que solo se repitan las dos primeras casillas creadas mientras queden casillas vírgenes (nunca pintadas)
			if (cont<2  && repetido==false){ 
				botonPanel[rand].addMouseListener(bj);
				botonPanel[rand].setBackground(Color.LIGHT_GRAY);
				botonPanel[rand].setBorder(new BevelBorder(BevelBorder.LOWERED));
				botonPanel[rand].validate();
				botonPanel[rand].repaint();
				
				//cambio el valor de la posición para indicar que el número que corresponde a la posición ya ha salido
				cuadrosPintados[rand]=1;
				
				//almacena las casillas pintadas en orden, para guardar record de la partida
				if (pintadas.equals(","))
					pintadas = rand+"," ;
				else
					pintadas += rand+"," ;
				System.out.println(pintadas);
			}else{
				i--;
				cont--;
				repetido=false;
			}
		}
		
	}
	
	/**
	 * Devuelve 0 si se han pintado todas las casillas y si queda alguna casilla por pintar el número de casillas que quedan
	 * @return
	 */
	public int comprobarPintados(){
		int restantes=0;
		for (int i=0;i<cuadrosPintados.length && restantes == 0;i++)
			if (cuadrosPintados[i]==null)
				restantes=1;
		return restantes;
	}
	
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
	 * devuelve verdadero si hay algún MouseListener activo
	 * @return
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
	 * almacena los datos de la partida (orden de pintado de los cuadros y fecha y hora de inicio de partida)
	 * @throws SQLException
	 * @throws IOException
	 * @throws ClassNotFoundException 
	 */
	private void almacenarDatosPartidaPrueba() throws SQLException, IOException, ClassNotFoundException{
		/*Class.forName("com.mysql.jdbc.Driver");
		Connection c = DriverManager.getConnection("jdbc:mysql://localhost/JuegoCuadrosJava", "root", "1234");
		Statement stm = c.createStatement();
		stm.executeUpdate("INSERT INTO Partidas_prueba(secuencia_paneles, secuencia_seleccionadas, fecha_juego) VALUES ('"+pintadas+"', '"+seleccionadas+"', '"+readFileFecha()+"')");*/
	
		FileWriter fw = new FileWriter("RecordsPrueba", true);
		PrintWriter pw = new PrintWriter(fw);
		pw.println("Nivel: "+dificultad[dif]);
		pw.println("Pintadas: "+pintadas);
		pw.println("Seleccionadas: "+seleccionadas);
		pw.println("Inicio de partida: "+readFileFecha());
		pw.println("");
		pw.close();
		fw.close();
	}
	//Comando para iniciar mysql por si vuelve a fallar por culpa del yosemite
		/*sudo /Applications/XAMPP/xamppfiles/bin/mysql.server start*/
	//
	
	/**
	 * recoge la fecha del sistema
	 * @return dateFormat.format(cal.getTime()) : la fecha del momento en que se activa el método
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
	 * @see getFecha()
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
	 * @return
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
