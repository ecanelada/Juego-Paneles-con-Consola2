import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.border.BevelBorder;

/**
 * 
 * @author Edu2
 *
 */
public class BotonJuego implements MouseListener {

	PanelJuegoPrueba v;
	
	//imágenes que debe cargar en función del nivel de dificultad en el que se juegue
	String [] imagenDificultad = {"/cresus.png", "/cresus.png", "/caballorobot.gif", "/caballorobot.gif", "/nivelfinal.gif"};
	String [] imagenDificultadMal = {"/explosion.png", "/explosion.png", "/explosion.png", "/explosion.png", "/explosion.png"};
	
	/**
	 * constructor sobrecargado
	 * @param vv
	 */
	BotonJuego(PanelJuegoPrueba vv){
		v=vv;
	}

	/**
	 * Al pulsa los paneles activados con un mouseListener, si ya había sido seleccionado cambia la imagen y para la partida
	 * @see cambiaImagen(), borrarBotonesActivos()
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		//buscar el panel al que est�� asociado el botón
		for (int i=0;i<v.botonPanel.length;i++){
			if (e.getSource()==v.botonPanel[i]){
				//comprueba que en alguna de las posiciones del array no existe el valor de la casilla que se selecciona, pero almacena el valor en su posici��n en el array, no en el orden de ejecuci��n
				if (v.cuadrosMarcados[i]==null){
					v.cuadrosMarcados[i]=i;
					
					//cambia la imagen 
					cambiaImagen(imagenDificultad, i);
					
					//almacena las casillas seleccionadas en orden, para guardar record de la partida
					if (v.seleccionadas.equals(","))
						v.seleccionadas = i+"," ;
					else
						v.seleccionadas += i+"," ;
				} else {
					//cambia la imagen
					cambiaImagen(imagenDificultadMal, i);
					
					v.botonPanel[i].setBackground(Color.RED);
					v.botonPanel[i].setBorder(new BevelBorder(BevelBorder.RAISED));
					v.botonPanel[i].removeMouseListener(this);
					//condici��n de parada del juego cuando se pulsa un bot��n anteriormente pulsado
					v.fin=true;
					
					//Hace que al seleccionar un panel repetido permita seguir interactuando con el resto de paneles de la pantalla
					if (v.getHandicapConsumido()<v.getHandicap())
						v.setHandicapConsumido(v.getHandicapConsumido()+1);
					else
					//evito que en la ejecuci��n actual se puedan seguir pulsando botones, ya que permitir��a sumar puntos despu��s de haberse parado la partida
					borrarBotonesActivos();
				}
				v.botonPanel[i].validate();
				v.botonPanel[i].repaint();
			}
		}
	}
	
	/**
	 * cambia la imagen del panel en función del nivel de dificultad
	 * @param imgD
	 * @param i
	 */
	public void cambiaImagen(String [] imgD, int i){
		v.botonPanel[i].removeAll();
		v.gif = new JLabel();
		URL imgURL = BotonJuego.class.getResource(imgD[v.dif]);
		ImageIcon imageIcon = new ImageIcon(imgURL);
		v.gif.setIcon(imageIcon); 
		v.botonPanel[i].add(v.gif);
	}
	
	/**
	 * elimina los mouselisteners
	 */
	public void borrarBotonesActivos(){
		for(int i=0; i<v.dificultad[v.dif]*v.dificultad[v.dif]; i++){
			if (v.botonPanel[i].getMouseListeners()!=null){
				v.botonPanel[i].removeMouseListener(this);
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
