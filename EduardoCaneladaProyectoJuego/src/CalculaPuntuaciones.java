import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * 
 * @author Edu2
 *
 */
public class CalculaPuntuaciones extends DatosJugador {

	Statement st;
	private int puntosIndividuales=0, handicap=0, puntosUltimoMes;
	private static int [] partidasXnivel = {0,0,0,0,0};
	//puntos = (nivel + 4)*(nivel + 4) * 5 * ((nivel+4)+5+nivel*2)
	private int [] puntosMinNiveles = {720, (1500+720), (1500+720+2700), (1500+720+2700+5145), (1500+720+2700+5145+9280)};
	ConsolaInicial c;
	
	/**
	 * Método que devuelve los puntos del jugador
	 * @return puntosIndividuales
	 */
	public int getPuntosIndividuales (){
		return puntosIndividuales;
	}
	
	/**
	 * método que devuelve los puntos del último mes
	 * @return puntosUltimoMes
	 */
	public int getPuntosUltimoMes (){
		return puntosUltimoMes;
	}
	
	/**
	 * constructor sobrecargado
	 * @param ci
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws IOException
	 */
	CalculaPuntuaciones(ConsolaInicial ci) throws ClassNotFoundException, SQLException, IOException{
		super();
		c=ci;
		st=conectarBD();
	}
	
	CalculaPuntuaciones(){
		super();
		try {
			st=conectarBD();
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Almacena los puntos individuales y en partidasXnivel las partidas de cada nivel que ha jugado
	 * @see #getID()
	 * @see #procesarPartida()
	 * @throws SQLException
	 * @throws NumberFormatException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void calculaPuntosTotalJugador(int id) throws SQLException, NumberFormatException, IOException, ClassNotFoundException{
		puntosIndividuales=0;
		ResultSet rs = st.executeQuery("select * from partidas where IDJugador = '"+id+"'");
		while (rs.next()){
			int dif = rs.getInt("Dificultad");
			puntosIndividuales += procesarPartida(rs.getString("Secuencia_paneles"), rs.getString("Secuencia_seleccionados"), dif);
			//suma el número de partidas de cada nivel
			partidasXnivel[dif]=dif;
		}
		rs.close();
	}
	
	/**
	 * Calcula los puntos obtenidos en las partidas del mes actual
	 * @throws NumberFormatException
	 * @throws SQLException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void calculaPuntosUltimoMes() throws NumberFormatException, SQLException, IOException, ClassNotFoundException{
		puntosUltimoMes=0;
		GregorianCalendar gcal = new GregorianCalendar();
		int mes;
		mes=gcal.get(Calendar.MONTH)+1;
		String dobleDigito;
		if (mes<10) dobleDigito = "0"+mes;
		else dobleDigito =""+mes;
		if (st==null)
			st=conectarBD();
		ResultSet rs = st.executeQuery("select * from partidas where IDJugador like '"+getIDJugador()+"' and Fecha_juego like '%/"+dobleDigito+"/%'");
		while (rs.next()){
			int dif = rs.getInt("Dificultad");
			puntosUltimoMes += procesarPartida(rs.getString("Secuencia_paneles"), rs.getString("Secuencia_seleccionados"), dif);
		}
		rs.close();
	}
	
	/**
	 * Calcula los puntos obtenidos en cada partida
	 * @param pintadas
	 * @param seleccionadas
	 * @param dificultad
	 * @return s.length*pantallasDesbloqueadas*5, los puntos obtenidos
	 */
	private int procesarPartida(String pintadas, String seleccionadas, int dificultad){
		String [] p = pintadas.split(",");
		String [] s = seleccionadas.split(",");
		//divido el total de casillas por el total de casillas que aparecen según el nivel. en función de las casillas pintadas
		int pantallasDesbloqueadas = p.length/(dificultad+4);
		//los puntos de cada partida se suman: elementos desbloqueados * pantallas desbloqueadas
		return s.length*pantallasDesbloqueadas*5;
	}
	
	/**
	 * Calcula a qué nivel pertenece la puntuación que posee el jugador y actualiza la base de datos
	 * @throws NumberFormatException
	 * @throws SQLException
	 * @throws IOException
	 */
	public void desbloqueaNivel() throws NumberFormatException, SQLException, IOException{
		boolean fin=false;
		int i;
		for (i=0;i<partidasXnivel.length && fin==false;i++){
			//si no ha jugado partidas en ese nivel es que no lo ha desbloqueado
			if (partidasXnivel[i]==0){
				fin=true;
			}
		}
		if (puntosIndividuales > puntosMinNiveles[i]){
			//desbloquea nuevo nivel si tienes los puntos necesarios. dependiendo de los que gastes puedes perder niveles
			st.executeUpdate("update user set Nivel = '"+(i+1)+"' where ID like '"+getIDJugador()+"'");
		}
	}
	
	/**
	 * Saca los puntos de handicap gastados para mostrarlos en la gráfica
	 * @return handicap
	 * @throws NumberFormatException
	 * @throws SQLException
	 * @throws IOException
	 */
	public int getHandicapGastado() throws NumberFormatException, SQLException, IOException{
		int handicap=0;
		ResultSet rs = st.executeQuery("select Puntos_handicap_usados from user where ID like '"+getIDJugador()+"'");
		while(rs.next()){
			handicap = rs.getInt("Puntos_handicap_usados");
		}
		return handicap;
	}
	
	/**
	 * Actualiza los puntos del jugador
	 */
	public void almacenaPuntos (){
		try {
			st.executeUpdate("update user set Nivel_descompresion = '"+puntosIndividuales+"' where ID like '"+getIDJugador()+"'");
		} catch (NumberFormatException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * CRea una conexión con la base de datos
	 * @return c.createStatement() objeto que permite conectarse a la base de datos
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public Statement conectarBD() throws ClassNotFoundException, SQLException{
		Class.forName("com.mysql.jdbc.Driver");
		Connection c = DriverManager.getConnection("jdbc:mysql://localhost/JuegoCuadros", "root", "1234");
		return c.createStatement();
	}

	/**
	 * Método que devuelve un String con los nombres de todos los jugadores ordenados por ID, con su ID
	 * @return info cadena con los nombres de los jugadores registrados y su correspondiente ID
	 * @throws SQLException
	 */
	public String getNombreEIdJugadores() throws SQLException{
		String nombres="";
		ResultSet rs = st.executeQuery("select * from user order by Nivel_descompresion desc");
		while (rs.next()){
			nombres+=rs.getString("Nombre")+"-"+rs.getInt("ID")+",";
		}
		rs.close();
		return nombres;
	}
	
	/**
	 * Vectoriza la información que recibe en un array bidimensional
	 * @param nomId
	 * @return info array bidimensional con la información del string que recibe vectorizada
	 */
	public String [][] procesarNombreEId(String nomId){
		String [] p = nomId.split(",");
		String [] s;
		String [][] info = new String[p.length][2];
		for(int i=0;i<p.length;i++){
			s = p[i].split("-");
			//info[i] = s;
			info[i][0] = s[0]; //nombre
			info[i][1] = s[1]; //id
		}
		return info;
	}
	
	/**
	 * Calcula la efectividad del jugador, que se pasa por array en una lista y se indica mediante la posición de este
	 * @param info
	 * @param pos
	 * @return porcentaje/dif, el porcentaje final, teniendo en cuenta el número de niveles jugados
	 * @throws SQLException
	 */
	public double calculaPorcentajeEfectividad(String [][] info, int pos) throws SQLException{
		double porcentaje=0;
		int dif=0;
		int [] cont={0,0,0,0,0}; //contador de partidas por nivel
		double [] mediaAciertosXDificultad = {0,0,0,0,0}; //porcentaje de aciertos que obtiene por cada nivel
		ResultSet rs2 = st.executeQuery("select * from partidas where IDJugador = '"+info[pos][1]+"' order by Fecha_juego asc");
		while(rs2.next()){
			dif = rs2.getInt("Dificultad");
			String [] p = rs2.getString("Secuencia_seleccionados").split(",");
			cont[dif]++;
			//System.out.println(info[pos][0]+" dif: "+dif+" casillas: "+(p.length)+" cont:"+cont[dif]);
			//por cada nivel sumo la porción de casillas desbloqueadas con respecto al total de ese nivel
			//p.length-1 porque acaba en coma y añade una posición vacía
			mediaAciertosXDificultad[dif] += (p.length-1)/(dif+4)*(dif+4);
			//System.out.println(mediaAciertosXDificultad[dif]);
		}
		rs2.close();
		for (int i=0;i<dif;i++){
			porcentaje += mediaAciertosXDificultad[i]/cont[i];
		}
		return porcentaje;
	}
}
