import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.border.BevelBorder;


public class BotonJuego implements MouseListener {

	PanelJuegoPrueba v;
	
	BotonJuego(PanelJuegoPrueba vv){
		v=vv;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		//buscar el panel al que está asociado el botón
		for (int i=0;i<v.botonPanel.length;i++){
			if (e.getSource()==v.botonPanel[i]){
				//comprueba que en alguna de las posiciones del array no existe el valor de la casilla que se selecciona, pero almacena el valor en su posición en el array, no en el orden de ejecución
				if (v.cuadrosMarcados[i]==null){
					v.cuadrosMarcados[i]=i;
					System.out.println(v.cuadrosMarcados[i]+" marcado "+i);
					//almacena las casillas seleccionadas en orden, para guardar record de la partida
					if (v.seleccionadas.equals(","))
						v.seleccionadas = i+"," ;
					else
						v.seleccionadas += i+"," ;
				} else {
					v.botonPanel[i].setBackground(Color.RED);
					v.botonPanel[i].setBorder(new BevelBorder(BevelBorder.RAISED));
					v.botonPanel[i].removeMouseListener(this);
					v.botonPanel[i].validate();
					v.botonPanel[i].repaint();
					//condición de parada del juego cuando se pulsa un botón anteriormente pulsado
					v.fin=true;
					
					//incluir handicap, que según el handicap (que costará un precio en puntos) permitirá un mayor número de casillas rojas antes de que se pare la aplicación en función de la dificultad
					
					//evito que en la ejecución actual se puedan seguir pulsando botones, ya que permitiría sumar puntos después de haberse parado la partida
					borrarBotonesActivos();
				}
			}
		}
	}
	
	/**
	 * elimina los muselisteners
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
