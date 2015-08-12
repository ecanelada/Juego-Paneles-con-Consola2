
public class DatosJugador {
	private static int IDJugador, dificultadMax;
	private static String nomJugador;
	
	/**
	 * Método que devuelve el ID del jugador
	 * @return IDJugador
	 */
	public int getIDJugador(){
		return IDJugador;
	}
	
	public void setIDJugador(int IDJugador){
		this.IDJugador = IDJugador;
	}
	
	public int getDificultadMaxima(){
		return dificultadMax;
	}
	
	public void setDificultadMax(int dificultadMax){
		this.dificultadMax = dificultadMax;
	}
	
	public String getNomJugador(){
		return nomJugador;
	}
	
	public void setNomJugador(String nomJugador){
		this.nomJugador=nomJugador;
	}
	
}
